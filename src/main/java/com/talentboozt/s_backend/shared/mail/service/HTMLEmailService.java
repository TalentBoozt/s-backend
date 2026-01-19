package com.talentboozt.s_backend.shared.mail.service;

import com.talentboozt.s_backend.shared.utils.EmailValidator;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class HTMLEmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public HTMLEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        if (!EmailValidator.isValid(to)) {
            throw new IllegalArgumentException("Invalid email format: " + to);
        }
        sendHtmlEmailWithAttachment(to, subject, htmlContent, null, null);
    }

    public void sendHtmlEmailWithAttachment(String to, String subject, String htmlContent,
                                            String attachmentName, DataSource attachmentDataSource)
            throws MessagingException {
        if (!EmailValidator.isValid(to)) {
            throw new IllegalArgumentException("Invalid email format: " + to);
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(Objects.requireNonNull(to));
        helper.setSubject(Objects.requireNonNull(subject));
        helper.setText(Objects.requireNonNull(htmlContent), true);

        if (attachmentName != null && attachmentDataSource != null) {
            helper.addAttachment(attachmentName, attachmentDataSource);
        }

        javaMailSender.send(message);
    }
}
