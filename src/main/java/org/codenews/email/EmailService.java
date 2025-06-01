package org.codenews.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.model.News;
import org.codenews.model.Subscriber;
import org.codenews.repository.SubscriberRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável por montar o HTML da newsletter
 * e enviar e-mail para todos os assinantes cadastrados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final SubscriberRepository subscriberRepository;
    private final JavaMailSender mailSender;

    /**
     * Constrói o HTML completo da newsletter a partir de uma lista de notícias.
     */
    private String buildNewsletterHtml(List<News> newsList) {
        StringBuilder sb = new StringBuilder();

        // Container geral e banner
        sb.append("<div style='font-family:Roboto, Arial, sans-serif; background:#f6f8fc; padding:0; margin:0;'>");
        sb.append("<div style='max-width:600px;margin:32px auto;background:white;border-radius:16px;box-shadow:0 4px 16px rgba(0,0,0,0.05);padding:32px;'>");
        sb.append("<img src='https://i.imgur.com/CODEBANNER.png' alt='CodeNews Banner' style='width:100%;border-radius:12px;margin-bottom:16px;'/>");

        sb.append("<h1 style='font-size:1.9em;color:#7048e8;margin-bottom:0;'>🚀 <span style='font-family:Montserrat,sans-serif;'>CodeNews</span></h1>");
        sb.append("<p style='color:#4d4d4d;font-size:1.1em;margin:8px 0 18px 0;'>Sua dose diária de inovação, tendências e o melhor do universo dev, direto na sua caixa de entrada.</p>");
        sb.append("<p style='color:#23a455;font-weight:bold;margin-bottom:20px;'>🔥 Você mantém sua streak de leitura ativa!</p>");

        // Mini sumário / destaque
        sb.append("<div style='padding:16px;background:#f0f2f6;border-radius:12px;margin-bottom:24px;'>");
        sb.append("<span style='font-weight:bold;letter-spacing:.5px;color:#333;font-size:1.2em;'>🌐 Principais Notícias de Tecnologia</span>");
        sb.append("</div>");

        // Listagem de notícias
        for (News news : newsList) {
            sb.append("<div style='display:flex;align-items:flex-start;margin-bottom:20px;border-bottom:1px solid #eee;padding-bottom:14px;'>");
            // Imagem (se houver)
            if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                sb.append("<img src='").append(news.getImageUrl()).append("' alt='Imagem da notícia' style='width:62px;height:62px;border-radius:8px;object-fit:cover;margin-right:16px;border:1px solid #eee;'/>");
            } else {
                sb.append("<div style='width:62px;height:62px;border-radius:8px;background:#ececec;display:flex;align-items:center;justify-content:center;font-size:12px;color:#999;margin-right:16px;'>Sem imagem</div>");
            }
            // Conteúdo da notícia
            sb.append("<div>");
            sb.append("<div style='color:#7048e8;font-size:1em;font-weight:600;'>").append(news.getTitle()).append("</div>");
            sb.append("<div style='color:#888;font-size:.98em;margin-bottom:6px;'><b>")
                    .append(news.getSource())
                    .append("</b> · ")
                    .append(news.getPublishDate().toLocalDate())
                    .append("</div>");
            if (news.getSummary() != null && !news.getSummary().isEmpty()) {
                sb.append("<div style='color:#333;font-size:1em;margin-bottom:6px;'>")
                        .append(news.getSummary())
                        .append("</div>");
            }
            sb.append("<a href='").append(news.getUrl())
                    .append("' style='display:inline-block;padding:8px 20px;border-radius:8px;background:#7048e8;color:#fff;font-weight:bold;font-size:.96em;text-decoration:none;transition:all .2s;'>Ler matéria</a>");
            sb.append("</div>");
            sb.append("</div>");
        }

        // Bloco “Premium / mídia kit”
        sb.append("<div style='padding:16px 0;margin:20px 0 0 0;border-top:1px solid #eee;'>");
        sb.append("<span style='color:#333;font-weight:bold;font-size:1.08em;'>🌟 Sua marca no epicentro da inovação tech.</span><br>");
        sb.append("<span style='font-size:.99em;'>Espaço Premium da CodeNews conecta sua empresa, evento ou plataforma aos profissionais tech mais engajados do Brasil.</span><br>");
        sb.append("<a href='https://codenews.com.br/midia-kit' style='display:inline-block;color:#7048e8;font-weight:bold;margin-top:6px;'>Solicite o media kit</a>");
        sb.append("</div>");

        // Rodapé
        sb.append("<div style='font-size:.93em;color:#888;margin-top:32px;border-top:1px solid #eee;padding-top:18px;'>");
        sb.append("Newsletter idealizada, desenvolvida e mantida por Rafael Alvarenga Braghittoni.<br>");
        sb.append("<a href='https://www.linkedin.com/in/rafaelbraghittoni' style='color:#7048e8;font-weight:bold;'>Conecte-se comigo no LinkedIn</a><br><br>");
        sb.append("<span style='font-size:.93em;'>Recebeu este e-mail por engano? <a href='https://codenews.com.br/unsubscribe' style='color:#7048e8;'>Cancele sua inscrição aqui</a><br>© 2025 CodeNews | <a href='https://codenews.com.br/privacidade' style='color:#7048e8;'>Política de Privacidade</a> | <a href='https://codenews.com.br/contato' style='color:#7048e8;'>Fale conosco</a></span>");
        sb.append("</div>");

        sb.append("</div></div>");
        return sb.toString();
    }

    /**
     * Envia o HTML para todos os subscribers cadastrados no BD.
     */
    public void sendNewsletter(List<News> newsList) {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        String subject = "CodeNews: Newsletter com as Últimas de Tecnologia";
        String htmlContent = buildNewsletterHtml(newsList);

        for (Subscriber subscriber : subscribers) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(subscriber.getEmail());
                helper.setFrom("rafinhalvarenga@gmail.com", "CodeNews");
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(message);
                log.info("[EMAIL_SERVICE] Newsletter enviada para {}", subscriber.getEmail());
            } catch (Exception ex) {
                log.error("[EMAIL_SERVICE] Erro ao enviar newsletter para {}: {}",
                        subscriber.getEmail(),
                        ex.getMessage()
                );
            }
        }
    }
}
