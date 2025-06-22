package org.codenews.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codenews.model.News;
import org.codenews.model.Subscriber;
import org.codenews.repository.SubscriberRepository;
import org.codenews.service.MetricsService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final SubscriberRepository subscriberRepository;
    private final JavaMailSender mailSender;
    private final MetricsService metricsService;

    private String buildNewsletterHtml(List<News> newsList) {
        StringBuilder sb = new StringBuilder();

        // Container geral (fundo cinza-claro) e centralização
        sb.append("<div style=\"font-family:Arial, sans-serif; background:#f6f8fc; padding:0; margin:0; text-align:center;\">")
                .append("<div style=\"max-width:600px; margin:32px auto; background:white; border-radius:16px; ")
                .append("box-shadow:0 4px 16px rgba(0,0,0,0.05); padding:32px;\">");

        // Banner principal (link direto do Imgur)
        sb.append("<img src=\"https://i.imgur.com/Jf4DhGG.png\" alt=\"CodeNews Banner\" ")
                .append("style=\"width:100%; border-radius:12px; margin-bottom:16px; display:block;\"/>");

        // Cabeçalho da newsletter
        sb.append("<h1 style=\"font-size:1.9em; color:#7048e8; margin-bottom:4px;\">")
                .append("🚀 <span style=\"font-family:Montserrat, sans-serif;\">CodeNews</span></h1>");
        sb.append("<p style=\"color:#4d4d4d; font-size:1.1em; margin:8px 0 18px 0;\">")
                .append("Sua dose diária de inovação, tendências e o melhor do universo dev, direto na sua caixa de entrada.")
                .append("</p>");
        sb.append("<p style=\"color:#23a455; font-weight:bold; margin-bottom:20px;\">")
                .append("🔥 Você mantém sua streak de leitura ativa!")
                .append("</p>");

        // Mini-sumário / Destaque do bloco de notícias
        sb.append("<div style=\"padding:16px; background:#f0f2f6; border-radius:12px; margin-bottom:24px;\">")
                .append("<span style=\"font-weight:bold; letter-spacing:.5px; color:#333; font-size:1.2em;\">")
                .append("🌐 Principais Notícias de Tecnologia")
                .append("</span></div>");

        // Listagem iterativa de cada notícia
        for (News news : newsList) {
            sb.append("<div style=\"margin-bottom:24px; border-bottom:1px solid #eee; padding-bottom:24px; text-align:left;\">");

            // Imagem (se houver) ou placeholder cinza
            if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                sb.append("<img src=\"")
                        .append(news.getImageUrl())
                        .append("\" alt=\"Imagem da notícia\" style=\"width:100%; max-height:200px; object-fit:cover; border-radius:8px; margin-bottom:12px;\"/>");
            } else {
                sb.append("<div style=\"width:100%; height:200px; border-radius:8px; background:#ececec; ")
                        .append("display:flex; align-items:center; justify-content:center; font-size:14px; color:#999; margin-bottom:12px;\">")
                        .append("Sem imagem")
                        .append("</div>");
            }

            // Título (com link clicável)
            sb.append("<h2 style=\"font-family:Montserrat, sans-serif; font-size:1.3em; color:#7048e8; margin:0 0 8px 0;\">")
                    .append("<a href=\"").append(news.getUrl()).append("\" style=\"text-decoration:none; color:#7048e8;\" target=\"_blank\">")
                    .append(news.getTitle())
                    .append("</a></h2>");

            // Fonte e data de publicação
            sb.append("<p style=\"color:#888; font-size:0.95em; margin:0 0 12px 0;\">")
                    .append("<b>").append(news.getSource()).append("</b> &middot; ")
                    .append(news.getPublishDate().toLocalDate())
                    .append("</p>");

            // Resumo: até o primeiro ponto final
            String fullSummary = news.getSummary() != null ? news.getSummary().trim() : "";
            String shortSummary = "";
            if (!fullSummary.isEmpty()) {
                int pos = fullSummary.indexOf(".");
                shortSummary = pos > 0
                        ? fullSummary.substring(0, pos + 1).trim()
                        : fullSummary;
            }
            if (!shortSummary.isEmpty()) {
                sb.append("<p style=\"color:#333; font-size:1em; line-height:1.5; margin:0 0 12px 0;\">")
                        .append(shortSummary)
                        .append("</p>");
            }

            // Botão “Ler matéria”
            sb.append("<div style=\"text-align:center; margin-top:12px;\">")
                    .append("<a href=\"").append(news.getUrl())
                    .append("\" style=\"display:inline-block; padding:8px 24px; border-radius:8px; background:#7048e8; ")
                    .append("color:#fff; font-weight:bold; font-size:0.96em; text-decoration:none; transition:all .2s;\" ")
                    .append("target=\"_blank\">Ler matéria</a>")
                    .append("</div>");

            sb.append("</div>");
        }

        // === Seção de Parcerias / Mídia Kit ===
        sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff; padding:0; margin:0;\">");
        sb.append("<tr><td align=\"center\" style=\"padding:0 16px;\">");
        sb.append("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"")
                .append("background-color:#f9f9f9;")
                .append("border:2px dashed #23a455;")
                .append("border-radius:12px;")
                .append("padding:24px;")
                .append("font-family:Arial, sans-serif;")
                .append("\">");

        // Título do bloco de parcerias
        sb.append("<tr><td align=\"center\" style=\"padding-bottom:16px;\">")
                .append("<span style=\"font-family:Montserrat, sans-serif; font-size:1.4em; color:#006622; font-weight:bold;\">")
                .append("🚀 Espaço Premium para Parcerias Tech!")
                .append("</span></td></tr>");

        // Alcance estimado
        sb.append("<tr><td align=\"center\" style=\"padding-bottom:12px;\">")
                .append("<span style=\"font-size:1em; color:#23a455; font-weight:bold;\">")
                .append("Alcance mensal estimado: +2.000 devs, gestores e decisores tech do Brasil")
                .append("</span></td></tr>");

        // Público e formatos aceitos
        sb.append("<tr><td align=\"center\" style=\"padding-bottom:16px;\">")
                .append("<span style=\"font-size:0.95em; color:#333333; line-height:1.5;\">")
                .append("Perfil do público: tecnologia, inovação, startups, SaaS, produto digital, mercado financeiro.")
                .append("</span><br>")
                .append("<span style=\"font-size:0.95em; color:#333333; line-height:1.5;\">")
                .append("Formatos aceitos: <span style=\"color:#006622; font-weight:bold;\">")
                .append("banner, cupom, branded content, divulgação de eventos, cursos, produtos digitais e lançamentos.")
                .append("</span></span></td></tr>");

        // Chamada “Aqui sua marca pode aparecer!”
        sb.append("<tr><td align=\"center\" style=\"padding-bottom:16px;\">")
                .append("<span style=\"font-size:1em; color:#23a455; font-weight:bold;\">")
                .append("Aqui sua marca pode aparecer! 📢")
                .append("</span></td></tr>");

        // Texto de convite a parceiros
        sb.append("<tr><td align=\"center\" style=\"padding-bottom:16px;\">")
                .append("<span style=\"font-size:0.95em; color:#333333; line-height:1.5;\">")
                .append("Seja parceiro do CodeNews e posicione sua empresa, plataforma, fintech ou edtech neste espaço estratégico.")
                .append("</span></td></tr>");

        // Botão “Solicite o media kit”
        sb.append("<tr><td align=\"center\">")
                .append("<a href=\"https://codenews.com.br/midia-kit\" target=\"_blank\" style=\"")
                .append("display:inline-block; background-color:#23a455; color:#ffffff; font-family:Montserrat, sans-serif;")
                .append("font-size:1em; font-weight:bold; text-decoration:none; padding:12px 28px; border-radius:8px;")
                .append("transition: background-color 0.2s ease-in-out;")
                .append("\">Solicite o media kit</a>")
                .append("</td></tr>");

        sb.append("</table></td></tr>");

        // Bloco “Indique a edição” em destaque
        sb.append("<tr><td align=\"center\" style=\"padding-top:24px; padding-bottom:24px;\">");
        sb.append("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#1a237e; border-radius:8px; padding:12px 16px;\">");
        sb.append("<tr><td align=\"center\">")
                .append("<span style=\"font-family:Montserrat, sans-serif; font-size:1em; color:#ffffff; font-weight:bold;\">")
                .append("🔥 Curtiu a edição? Indique CodeNews para seus amigos e ajude a crescer a comunidade tech!")
                .append("</span></td></tr>");
        sb.append("</table></td></tr>");

        // Rodapé com assinatura, links e direitos autorais
        sb.append("<tr><td align=\"center\" style=\"padding-bottom:32px;\">");
        sb.append("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding-top:24px; border-top:1px solid #eeeeee;\">");
        sb.append("<tr><td align=\"center\" style=\"font-family:Arial, sans-serif; color:#555555; font-size:0.9em; line-height:1.5;\">")
                .append("Newsletter idealizada, desenvolvida e mantida por <span style=\"color:#006622; font-weight:bold;\">Rafael Alvarenga Braghittoni</span>.<br>")
                .append("<a href=\"https://www.linkedin.com/in/rafabrh/\" target=\"_blank\" style=\"font-family:Montserrat, sans-serif; font-size:0.9em; color:#006622; font-weight:bold; text-decoration:none;\">Conecte-se comigo no LinkedIn</a><br><br>")
                .append("Recebeu este e-mail por engano? <a href=\"https://codenews.com.br/unsubscribe\" style=\"color:#006622; text-decoration:none;\" target=\"_blank\">Cancele a inscrição aqui</a><br>")
                .append("© 2025 CodeNews &middot; <a href=\"https://codenews.com.br/privacidade\" style=\"color:#006622; text-decoration:none;\" target=\"_blank\">Política de Privacidade</a> &middot; <a href=\"https://codenews.com.br/contato\" style=\"color:#006622; text-decoration:none;\" target=\"_blank\">Fale conosco</a>")
                .append("</td></tr>");
        sb.append("</table></td></tr>");

        sb.append("</table>");
        sb.append("</div></div>");

        return sb.toString();
    }

    /**
     * Envia a newsletter (HTML) para todos os assinantes cadastrados.
     */
    public void sendNewsletter(List<News> newsList) {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        String subject = "🔥 CodeNews: 🔥 Últimas Notícias de Tecnologia - 🔥🔥🔥Teste Schedulado das 4h20🔥🔥🔥";
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

                metricsService.logAndRecordEmailDelivery(subscriber.getEmail(), true, null);

                log.info("[EMAIL_SERVICE] Newsletter enviada para {}", subscriber.getEmail());
            } catch (Exception ex) {
                metricsService.logAndRecordEmailDelivery(subscriber.getEmail(), false, ex.getMessage());

                log.error("[EMAIL_SERVICE] Erro ao enviar newsletter para {}: {}", subscriber.getEmail(), ex.getMessage());
            }
        }
    }
}
