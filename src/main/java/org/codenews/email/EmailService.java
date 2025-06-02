package org.codenews.service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final SubscriberRepository subscriberRepository;
    private final JavaMailSender mailSender;

    /**
     * Constrói o HTML completo da newsletter a partir de uma lista de notícias.
     */
    /**
     * Constrói o HTML completo da newsletter a partir de uma lista de notícias,
     * incluindo banner, notícias e seção de parcerias profissional.
     */
    private String buildNewsletterHtml(List<News> newsList) {
        StringBuilder sb = new StringBuilder();

        // Container geral (fundo cinza-claro) e centralização
        sb.append("<div style=\"font-family:Arial, sans-serif; background:#f6f8fc; padding:0; margin:0; text-align:center;\">");
        sb.append("  <div style=\"max-width:600px; margin:32px auto; background:white; border-radius:16px; ")
                .append("box-shadow:0 4px 16px rgba(0,0,0,0.05); padding:32px;\">");

        // Banner principal (link corrigido para imagem direta do Imgur)
        sb.append("    <img src=\"https://i.imgur.com/Jf4DhGG.png\" alt=\"CodeNews Banner\" ")
                .append("style=\"width:100%; border-radius:12px; margin-bottom:16px; display:block;\"/>");

        // Cabeçalho
        sb.append("    <h1 style=\"font-size:1.9em; color:#7048e8; margin-bottom:4px;\">")
                .append("🚀 <span style=\"font-family:Montserrat, sans-serif;\">CodeNews</span>")
                .append("</h1>");
        sb.append("    <p style=\"color:#4d4d4d; font-size:1.1em; margin:8px 0 18px 0;\">")
                .append("Sua dose diária de inovação, tendências e o melhor do universo dev, direto na sua caixa de entrada.")
                .append("</p>");
        sb.append("    <p style=\"color:#23a455; font-weight:bold; margin-bottom:20px;\">")
                .append("🔥 Você mantém sua streak de leitura ativa!")
                .append("</p>");

        // Mini-sumário / Destaque
        sb.append("    <div style=\"padding:16px; background:#f0f2f6; border-radius:12px; margin-bottom:24px;\">");
        sb.append("      <span style=\"font-weight:bold; letter-spacing:.5px; color:#333; font-size:1.2em;\">")
                .append("🌐 Principais Notícias de Tecnologia")
                .append("</span>");
        sb.append("    </div>");

        // Listagem de notícias
        for (News news : newsList) {
            sb.append("    <div style=\"margin-bottom:24px; border-bottom:1px solid #eee; padding-bottom:24px; text-align:left;\">");

            // Imagem (se houver) ou placeholder cinza
            if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                sb.append("      <img src=\"")
                        .append(news.getImageUrl())
                        .append("\" alt=\"Imagem da notícia\" style=\"width:100%; max-height:200px; object-fit:cover; border-radius:8px; margin-bottom:12px;\"/>");
            } else {
                sb.append("      <div style=\"width:100%; height:200px; border-radius:8px; background:#ececec; ")
                        .append("display:flex; align-items:center; justify-content:center; font-size:14px; color:#999; margin-bottom:12px;\">")
                        .append("Sem imagem")
                        .append("</div>");
            }

            // Título (com link)
            sb.append("      <h2 style=\"font-family:Montserrat, sans-serif; font-size:1.3em; color:#7048e8; margin:0 0 8px 0;\">")
                    .append("<a href=\"").append(news.getUrl())
                    .append("\" style=\"text-decoration:none; color:#7048e8;\" target=\"_blank\">")
                    .append(news.getTitle())
                    .append("</a>")
                    .append("</h2>");

            // Fonte e data
            sb.append("      <p style=\"color:#888; font-size:0.95em; margin:0 0 12px 0;\">")
                    .append("<b>").append(news.getSource()).append("</b> &middot; ")
                    .append(news.getPublishDate().toLocalDate())
                    .append("</p>");

            // Resumo (até o primeiro ponto final)
            String fullSummary = news.getSummary() != null ? news.getSummary().trim() : "";
            String shortSummary = "";
            if (!fullSummary.isEmpty()) {
                int pos = fullSummary.indexOf(".");
                if (pos > 0) {
                    shortSummary = fullSummary.substring(0, pos + 1).trim();
                } else {
                    shortSummary = fullSummary;
                }
            }
            if (!shortSummary.isEmpty()) {
                sb.append("      <p style=\"color:#333; font-size:1em; line-height:1.5; margin:0 0 12px 0;\">")
                        .append(shortSummary)
                        .append("</p>");
            }

            // Botão “Ler matéria”
            sb.append("      <div style=\"text-align:center; margin-top:12px;\">")
                    .append("<a href=\"").append(news.getUrl())
                    .append("\" style=\"display:inline-block; padding:8px 24px; border-radius:8px; background:#7048e8; ")
                    .append("color:#fff; font-weight:bold; font-size:0.96em; text-decoration:none; transition:all .2s;\" ")
                    .append("target=\"_blank\">Ler matéria</a>")
                    .append("</div>");

            sb.append("    </div>");
        }

        // === Seção de Parcerias / Mídia Kit (reformulada para parecer mais profissional) ===
        sb.append("    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff; padding:0; margin:0;\">");

        // Container principal da caixa de parcerias
        sb.append("      <tr>");
        sb.append("        <td align=\"center\" style=\"padding:0 16px;\">");
        sb.append("          <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"")
                .append("background-color:#f9f9f9;")
                .append("border:2px dashed #23a455;")
                .append("border-radius:12px;")
                .append("padding:24px;")
                .append("font-family:Arial, sans-serif;")
                .append("\">");

        // Título do bloco de parcerias
        sb.append("            <tr>");
        sb.append("              <td align=\"center\" style=\"padding-bottom:16px;\">");
        sb.append("                <span style=\"")
                .append("font-family:Montserrat, sans-serif;")
                .append("font-size:1.4em;")
                .append("color:#006622;")
                .append("font-weight:bold;")
                .append("\">")
                .append("🚀 Espaço Premium para Parcerias Tech!")
                .append("</span>");
        sb.append("              </td>");
        sb.append("            </tr>");

        // Alcance estimado
        sb.append("            <tr>");
        sb.append("              <td align=\"center\" style=\"padding-bottom:12px;\">");
        sb.append("                <span style=\"")
                .append("font-size:1em;")
                .append("color:#23a455;")
                .append("font-weight:bold;")
                .append("\">")
                .append("Alcance mensal estimado: +2.000 devs, gestores e decisores tech do Brasil")
                .append("</span>");
        sb.append("              </td>");
        sb.append("            </tr>");

        // Perfil do público e formatos aceitos
        sb.append("            <tr>");
        sb.append("              <td align=\"center\" style=\"padding-bottom:16px;\">");
        sb.append("                <span style=\"")
                .append("font-size:0.95em;")
                .append("color:#333333;")
                .append("line-height:1.5;")
                .append("\">")
                .append("Perfil do público: tecnologia, inovação, startups, SaaS, produto digital, mercado financeiro.")
                .append("</span><br>");
        sb.append("                <span style=\"")
                .append("font-size:0.95em;")
                .append("color:#333333;")
                .append("line-height:1.5;")
                .append("\">")
                .append("Formatos aceitos: ")
                .append("<span style=\"color:#006622; font-weight:bold;\">")
                .append("banner, cupom, branded content, divulgação de eventos, cursos, produtos digitais e lançamentos.")
                .append("</span>")
                .append("</span>");
        sb.append("              </td>");
        sb.append("            </tr>");

        // Chamada “Aqui sua marca pode aparecer!”
        sb.append("            <tr>");
        sb.append("              <td align=\"center\" style=\"padding-bottom:16px;\">");
        sb.append("                <span style=\"")
                .append("font-size:1em;")
                .append("color:#23a455;")
                .append("font-weight:bold;")
                .append("\">")
                .append("Aqui sua marca pode aparecer! 📢")
                .append("</span>");
        sb.append("              </td>");
        sb.append("            </tr>");

        // Descrição “Seja parceiro do CodeNews…”
        sb.append("            <tr>");
        sb.append("              <td align=\"center\" style=\"padding-bottom:16px;\">");
        sb.append("                <span style=\"")
                .append("font-size:0.95em;")
                .append("color:#333333;")
                .append("line-height:1.5;")
                .append("\">")
                .append("Seja parceiro do CodeNews e posicione sua empresa, plataforma, fintech ou edtech neste espaço estratégico.")
                .append("</span>");
        sb.append("              </td>");
        sb.append("            </tr>");

        // Botão “Solicite o media kit”
        sb.append("            <tr>");
        sb.append("              <td align=\"center\">");
        sb.append("                <a href=\"https://codenews.com.br/midia-kit\" target=\"_blank\" style=\"")
                .append("display:inline-block;")
                .append("background-color:#23a455;")
                .append("color:#ffffff;")
                .append("font-family:Montserrat, sans-serif;")
                .append("font-size:1em;")
                .append("font-weight:bold;")
                .append("text-decoration:none;")
                .append("padding:12px 28px;")
                .append("border-radius:8px;")
                .append("transition: background-color 0.2s ease-in-out;")
                .append("\">")
                .append("Solicite o media kit")
                .append("</a>");
        sb.append("              </td>");
        sb.append("            </tr>");

        sb.append("          </table>");
        sb.append("        </td>");
        sb.append("      </tr>");

        // Bloco de “Indique a edição” em destaque (estilo caixa sólida)
        sb.append("      <tr>");
        sb.append("        <td align=\"center\" style=\"padding-top:24px; padding-bottom:24px;\">");
        sb.append("          <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"")
                .append("background-color:#1a237e;")
                .append("border-radius:8px;")
                .append("padding:12px 16px;")
                .append("\">");
        sb.append("            <tr>");
        sb.append("              <td align=\"center\">");
        sb.append("                <span style=\"")
                .append("font-family:Montserrat, sans-serif;")
                .append("font-size:1em;")
                .append("color:#ffffff;")
                .append("font-weight:bold;")
                .append("\">")
                .append("🔥 Curtiu a edição? Indique CodeNews para seus amigos e ajude a crescer a comunidade tech!")
                .append("</span>");
        sb.append("              </td>");
        sb.append("            </tr>");
        sb.append("          </table>");
        sb.append("        </td>");
        sb.append("      </tr>");

        // Rodapé com assinatura, links e direitos autorais
        sb.append("      <tr>");
        sb.append("        <td align=\"center\" style=\"padding-bottom:32px;\">");
        sb.append("          <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding-top:24px; border-top:1px solid #eeeeee;\">");
        sb.append("            <tr>");
        sb.append("              <td align=\"center\" style=\"font-family:Arial, sans-serif; color:#555555; font-size:0.9em; line-height:1.5;\">");
        sb.append("                Newsletter idealizada, desenvolvida e mantida por ")
                .append("<span style=\"color:#006622; font-weight:bold;\">Rafael Alvarenga Braghittoni</span>.")
                .append("<br>");
        sb.append("                <a href=\"https://www.linkedin.com/in/rafabrh/\" target=\"_blank\" style=\"")
                .append("font-family:Montserrat, sans-serif;")
                .append("font-size:0.9em;")
                .append("color:#006622;")
                .append("font-weight:bold;")
                .append("text-decoration:none;")
                .append("\">")
                .append("Conecte-se comigo no LinkedIn")
                .append("</a>")
                .append("<br><br>");
        sb.append("                Recebeu este e-mail por engano? ")
                .append("<a href=\"https://codenews.com.br/unsubscribe\" style=\"color:#006622; text-decoration:none;\" target=\"_blank\">")
                .append("Cancele a inscrição aqui")
                .append("</a>")
                .append("<br>");
        sb.append("                © 2025 CodeNews &middot; ")
                .append("<a href=\"https://codenews.com.br/privacidade\" style=\"color:#006622; text-decoration:none;\" target=\"_blank\">")
                .append("Política de Privacidade")
                .append("</a>")
                .append(" &middot; ")
                .append("<a href=\"https://codenews.com.br/contato\" style=\"color:#006622; text-decoration:none;\" target=\"_blank\">")
                .append("Fale conosco")
                .append("</a>");
        sb.append("              </td>");
        sb.append("            </tr>");
        sb.append("          </table>");
        sb.append("        </td>");
        sb.append("      </tr>");

        sb.append("    </table>");

        // Fecha container interno e externo
        sb.append("  </div>");
        sb.append("</div>");

        return sb.toString();
    }

    /**
     * Envia o HTML para todos os subscribers cadastrados no BD.
     */
    public void sendNewsletter(List<News> newsList) {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        String subject = "CodeNews: Últimas Notícias de Tecnologia";
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
                        ex.getMessage());
            }
        }
    }
}
