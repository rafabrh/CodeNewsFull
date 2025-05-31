package org.codenews.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public void sendNewsletter(String to, String content) {
        log.info("Enviando newsletter para {}", to);
        MimeMessage mime = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom("rafinhalvarenga@gmail.com", "Code News");
            helper.setTo(to);
            helper.setSubject("Code News - Notícias do Dia");
            helper.setText(content, false);

            mailSender.send(mime);
            log.info("E-mail enviado com sucesso para {}", to);

        } catch (MessagingException | UnsupportedEncodingException ex) {
            log.error("Falha ao enviar e-mail para " + to, ex);
        }
    }
}