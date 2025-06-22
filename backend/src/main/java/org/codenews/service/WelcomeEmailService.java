package org.codenews.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WelcomeEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    /**
     * Envia e-mail de boas-vindas ao assinante com banner e botões de ícones.
     */
    public void sendWelcomeEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, "CodeNews");
            helper.setTo(toEmail);
            helper.setSubject("Bem-vindo ao CodeNews!");

            String html = "" +
                    "<!DOCTYPE html>" +
                    "<html lang='pt-BR'>" +
                    "<head>" +
                    "<meta charset='UTF-8'/><meta name='viewport' content='width=device-width, initial-scale=1.0'/>" +
                    "<style>" +
                    "  body{margin:0;padding:0;font-family:Arial,sans-serif;background:#f6f6f6;color:#333;}" +
                    "  .banner{width:100%;height:auto;}" +
                    "  .container{max-width:600px;margin:20px auto;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);}" +
                    "  .header{text-align:center;padding:20px;background:#004080;color:#fff;}" +
                    "  .header h1{margin:0;font-size:24px;}" +
                    "  .content{padding:20px;line-height:1.6;}" +
                    "  .buttons{text-align:center;margin:20px 0;}" +
                    "  .buttons a{display:inline-block;margin:0 10px;text-decoration:none;}" +
                    "  .buttons img{width:40px;height:40px;}" +
                    "  .footer{text-align:center;padding:15px;font-size:12px;color:#777;background:#f0f0f0;}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    // Banner Image from imgur
                    "  <img src='https://i.imgur.com/flBR7Ab.jpeg alt='CodeNews Banner' class='banner'/>" +
                    "  <div class='header'>" +
                    "    <h1>Bem-vindo ao CodeNews!</h1>" +
                    "  </div>" +
                    "  <div class='content'>" +
                    "    <p>Você está dentro! 🚀</p>" +
                    "    <p>Obrigado por se inscrever na nossa newsletter de tecnologia. A partir de agora você receberá as melhores notícias, tendências e insights diariamente.</p>" +
                    "    <p>Enquanto isso, siga-nos nas redes sociais e confira nosso código-fonte:</p>" +
                    "    <div class='buttons'>" +
                    "      <a href='https://www.linkedin.com/in/rafabrh/' target='_blank'>" +
                    "        <img src='https://i.imgur.com/lmSRhsY.jpeg' alt='LinkedIn'/>" +
                    "      </a>" +
                    "      <a href='https://github.com/rafabrh/CodeNews' target='_blank'>" +
                    "        <img src='https://i.imgur.com/Gqg3RJy.jpeg' alt='GitHub'/>" +
                    "      </a>" +
                    "    </div>" +
                    "    <p>Se tiver dúvidas, sugestões ou bugs, responda a este e-mail. Estamos construindo juntos!</p>" +
                    "  </div>" +
                    "  <div class='footer'>" +
                    "    © 2025 CodeNews · Desenvolvido por Rafael Alvarenga Braghittoni" +
                    "  </div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(html, true);
            mailSender.send(message);
            log.info("Welcome email enviado para {}", toEmail);
        } catch (Exception ex) {
            log.error("Erro ao enviar welcome email para {}: {}", toEmail, ex.getMessage());
        }
    }
}