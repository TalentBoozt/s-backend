package com.talentboozt.s_backend.shared.mail.service;

import com.talentboozt.s_backend.shared.mail.cfg.EmailQueueService;
import com.talentboozt.s_backend.shared.mail.cfg.EmailTemplateLoader;
import com.talentboozt.s_backend.shared.mail.dto.*;
import com.talentboozt.s_backend.shared.security.service.ValidateTokenService;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import com.talentboozt.s_backend.shared.utils.EmailValidator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.Map;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailTemplateLoader emailTemplateLoader;

    private final ConfigUtility configUtil;
    private final ValidateTokenService validateTokenService;
    private final EmailQueueService emailQueueService;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, EmailTemplateLoader emailTemplateLoader, ConfigUtility configUtil, ValidateTokenService validateTokenService, EmailQueueService emailQueueService) {
        this.javaMailSender = javaMailSender;
        this.emailTemplateLoader = emailTemplateLoader;
        this.configUtil = configUtil;
        this.validateTokenService = validateTokenService;
        this.emailQueueService = emailQueueService;
    }

//    @Autowired
//    public EmailService(JavaMailSender javaMailSender, EmailTemplateLoader emailTemplateLoader) {
//        this.javaMailSender = javaMailSender;
//        this.emailTemplateLoader = emailTemplateLoader;
//    }

    public void sendSimpleEmail(String to, String subject, String text) {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    private void sendEmail(Session session, String toEmail, String subject, String body) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        //message.setFrom(new InternetAddress(FROM_EMAIL));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) throws IOException {
        if (!EmailValidator.isValid(toEmail)) {
            System.out.println("Invalid email skipped: " + toEmail);
            return;
        }
        String subject = "Password Reset Request";
        String resetUrl = configUtil.getProperty("PASSWORD_REST_URL") + resetToken;
        Map<String, String> variables = Map.of(
                "resetLink", resetUrl,
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("password-reset.html", variables);
        EmailJob job = new EmailJob(toEmail, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void contactMe(String email) {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String subject = "Request For Information";
        String body = "Hi, I would like to know more about your services. I'm, \n\n" + email + "\n\n Thank you.";

        sendSimpleEmail(to, subject, body);
    }

    public void contactUs(ContactUsDTO contactUsDTO) throws IOException {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String mailSubject = contactUsDTO.getSubject() + " - " + contactUsDTO.getName();
        Map<String, String> variables = Map.of(
                "name", contactUsDTO.getName(),
                "email", contactUsDTO.getEmail(),
                "subject", contactUsDTO.getSubject(),
                "message", contactUsDTO.getMessage(),
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("contact-us.html", variables);
        EmailJob job = new EmailJob(to, mailSubject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void personalContact(PersonalContactDTO personalContactDTO) throws IOException {
        String to = personalContactDTO.getToEmail();
        String mailSubject = personalContactDTO.getSubject() + " - " + personalContactDTO.getName();
        Map<String, String> variables = Map.of(
                "name", personalContactDTO.getName(),
                "fromEmail", personalContactDTO.getFromEmail(),
                "toEmail", personalContactDTO.getToEmail(),
                "subject", personalContactDTO.getSubject(),
                "message", personalContactDTO.getMessage(),
                "year", String.valueOf(Year.now().getValue())
        );

        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }

        String htmlContent = emailTemplateLoader.loadTemplate("personal-contact.html", variables);
        EmailJob job = new EmailJob(to, mailSubject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendRejectionNotification(String to, String candidateName) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String subject = "Application Status";
        Map<String, String> variables = Map.of(
                "candidateName", candidateName,
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("rejection-notice.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendSelectionNotification(String to, String candidateName) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String subject = "Application Status";
        Map<String, String> variables = Map.of(
                "candidateName", candidateName,
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("selection-notice.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void subscribedNewsLatter(String to) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String subject = "Talent Boozt Newsletter";
        Map<String, String> variables = Map.of(
                "name", "Team Talent Boozt",
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("newsletter.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendInterviewPreparationQuestionAccess(String to) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String userName = to.split("@")[0];
        String token = validateTokenService.generateToken(userName);
        String link = "https://talentboozt.com/private/interview-questions?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String subject = "Your Interview Question Access Link";

        Map<String, String> variables = Map.of(
                "username", userName,
                "accessLink", link,
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("interview-access.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendNotificationToken(String to) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String userName = to.split("@")[0];
        String token = validateTokenService.generateToken(userName);
        String link = "https://talentboozt.com/private/system-notifications?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String subject = "Your System Notification Management Link";

        Map<String, String> variables = Map.of(
                "userName", userName,
                "notificationLink", link,
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("system-token.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendPreOrderSuccess(String to) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String subject = "Pre-Order Success";
        Map<String, String> variables = Map.of(
                "name", "Team Talent Boozt",
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("pre-order.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void bankPayment(BankPaymentDTO bankPaymentDTO) throws IOException {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String mailSubject = "Bank Payment Request - " + bankPaymentDTO.getName();

        Map<String, String> variables = Map.of(
                "name", bankPaymentDTO.getName(),
                "country", bankPaymentDTO.getCountry(),
                "phone", bankPaymentDTO.getPhone(),
                "companyId", bankPaymentDTO.getCompanyId(),
                "slipUrl", bankPaymentDTO.getSlipUrl(),
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("bank-payment.html", variables);
        EmailJob job = new EmailJob(to, mailSubject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void requestResume(CVRequestDTO cvRequestDTO) throws IOException {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String mailSubject = "CV Request - " + cvRequestDTO.getName();
        Map<String, String> variables = Map.of(
                "name", cvRequestDTO.getName(),
                "email", cvRequestDTO.getEmail(),
                "dob", cvRequestDTO.getDob(),
                "careerStage", cvRequestDTO.getCareerStage(),
                "jobTitle", cvRequestDTO.getJobTitle(),
                "link", cvRequestDTO.getLink(),
                "message", cvRequestDTO.getMessage(),
                "year", String.valueOf(Year.now().getValue())
        );

        String htmlContent = emailTemplateLoader.loadTemplate("cv-request.html", variables);
        EmailJob job = new EmailJob(to, mailSubject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendCourseBatchStartEmail(String to, String subject, Map<String, String> variables) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String htmlContent = emailTemplateLoader.loadTemplate("course-batch-start.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendCourseReminderEmail(String to, String subject, Map<String, String> variables) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String htmlContent = emailTemplateLoader.loadTemplate("course-reminder.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendCourseCompletionEmail(String to, String subject, Map<String, String> variables) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String htmlContent = emailTemplateLoader.loadTemplate("course-completion.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void sendRecCourseRejectEmail(String to, String subject, Map<String, String> variables) throws IOException {
        if (!EmailValidator.isValid(to)) {
            System.out.println("Invalid email skipped: " + to);
            return;
        }
        String htmlContent = emailTemplateLoader.loadTemplate("course-reject.html", variables);
        EmailJob job = new EmailJob(to, subject, htmlContent);
        emailQueueService.queueEmail(job);
    }

    public void leads(LeadsDTO leadsDTO) throws IOException {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String mailSubject = "Lead - " + leadsDTO.getCtaType();
        String text = "New Lead received from " + leadsDTO.getName() + " - " + leadsDTO.getEmail() + "\n\n" +
                "ctaType: " + leadsDTO.getCtaType() + "\nserviceType: " + leadsDTO.getServiceType() + "\nfocusArea: " + leadsDTO.getFocusArea() + "\nmessage: " + leadsDTO.getMessage();

        sendSimpleEmail(to, mailSubject, text);

        switch (leadsDTO.getCtaType()) {
            case "contact":
                leadsAck(leadsDTO);
                break;
            case "booking":
                //todo: implement booking
                break;
        }
    }

    private void leadsAck(LeadsDTO leadsDTO) throws IOException {
        String contactSubject = "Contact Us - " + leadsDTO.getServiceType();
        Map<String, String> contactVariables = Map.of(
                "name", leadsDTO.getName(),
                "serviceName", leadsDTO.getEmail()
        );

        String htmlContent = emailTemplateLoader.loadTemplate("leads.html", contactVariables);
        EmailJob job = new EmailJob(leadsDTO.getEmail(), contactSubject, htmlContent);
        emailQueueService.queueEmail(job);
    }
}
