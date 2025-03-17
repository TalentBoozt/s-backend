package com.talentboozt.s_backend.Service.common.mail;

import com.talentboozt.s_backend.DTO.common.mail.BankPaymentDTO;
import com.talentboozt.s_backend.DTO.common.mail.CVRequestDTO;
import com.talentboozt.s_backend.DTO.common.mail.ContactUsDTO;
import com.talentboozt.s_backend.DTO.common.mail.PersonalContactDTO;
import com.talentboozt.s_backend.Service.common.security.ValidateTokenService;
import com.talentboozt.s_backend.Utils.ConfigUtility;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    ConfigUtility configUtil;

    @Autowired
    ValidateTokenService validateTokenService;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSimpleEmail(String to, String subject, String text) {
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

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "Password Reset Request";
        String resetUrl = configUtil.getProperty("PASSWORD_REST_URL") + resetToken;
        String body = "Dear User,\n\nYou requested to reset your password. Please click the following link to reset it:\n"
                + resetUrl + "\n\nThis link will expire in 30 minutes.\n\nIf you did not request this, please ignore this email.";

        sendSimpleEmail(toEmail, subject, body);
    }

    public void contactMe(String email) {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String subject = "Request For Information";
        String body = "Hi, I would like to know more about your services. I'm, \n\n" + email + "\n\n Thank you.";

        sendSimpleEmail(to, subject, body);
    }

    public void contactUs(ContactUsDTO contactUsDTO) {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String mailSubject = contactUsDTO.getSubject() + " - " + contactUsDTO.getName();
        String body = "Name: " + contactUsDTO.getName() + "\nEmail: " + contactUsDTO.getEmail() + "\n\n" + contactUsDTO.getMessage();

        sendSimpleEmail(to, mailSubject, body);
    }

    public void personalContact(PersonalContactDTO personalContactDTO) {
        String to = personalContactDTO.getToEmail();
        String mailSubject = personalContactDTO.getSubject() + " - " + personalContactDTO.getName();
        String body = "Dear Valued User,\n\n" + "You have received a new message from " + personalContactDTO.getName() + ".\n\n" + personalContactDTO.getMessage() + "\nContact Sender: " + personalContactDTO.getFromEmail() + "\n\nBest regards,\nTeam Talent Boozt.";

        sendSimpleEmail(to, mailSubject, body);
    }

    public void sendRejectionNotification(String to, String candidateName) {
        String subject = "Application Status";
        String body = "Dear " + candidateName + ",\n\nWe appreciate your interest in using Talent Boozt. Unfortunately, you applied company has decided to move forward with other candidates at this time. \nBut don't stop now. You can apply unlimited jobs free of charge from Talent Boozt. We wish you the best of luck in your future endeavors.\n\nBest regards,\nTeam Talent Boozt.";
        sendSimpleEmail(to, subject, body);
    }

    public void sendSelectionNotification(String to, String candidateName) {
        String subject = "Application Status";
        String body = "Dear " + candidateName + ",\n\nWe are happy to inform your application was selected in first round. Applied company will contact you shortly.\n\nBest regards,\nTeam Talent Boozt.";
        sendSimpleEmail(to, subject, body);
    }

    public void subscribedNewsLatter(String to) {
        String subject = "Talent Boozt Newsletter";
        String body = "Dear valuable user,\n\nWe are happy to inform you that you have subscribed to our newsletter. We promise not to spam your inbox :) \n\nBest regards,\nTeam Talent Boozt.";
        sendSimpleEmail(to, subject, body);
    }

    public void sendInterviewPreparationQuestionAccess(String to) throws UnsupportedEncodingException {
        String userName = to.split("@")[0];
        String token = validateTokenService.generateToken(userName);
        String link = "https://talentboozt.com/private/interview-questions?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String subject = "Your Interview Question Access Link";
        String body = "Dear " + userName +",\n\n" + "Click on the link below to access the interview preparation questions. \n\n" + link + "\nNote: This link will expire in 24 hours and only be used once(if you reload the page the link will be expired). Don't share this link with anyone.\nIf you did not request this, please ignore this email.\n\nThank You.\nBest regards,\nTeam Talent Boozt.";

        sendSimpleEmail(to, subject, body);
    }

    public void sendNotificationToken(String to) throws UnsupportedEncodingException {
        String userName = to.split("@")[0];
        String token = validateTokenService.generateToken(userName);
        String link = "https://talentboozt.com/private/system-notifications?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String subject = "Your System Notification Management Link";
        String body = "Dear " + userName +",\n\n" + "Click on the link below to access the system notification management panel. \n\n" + link + "\nNote: This link will expire in 24 hours and only be used once(if you reload the page the link will be expired). Don't share this link with anyone.\nIf you did not request this, please ignore this email.\n\nThank You.\nBest regards,\nTeam Talent Boozt.";

        sendSimpleEmail(to, subject, body);
    }

    public void sendPreOrderSuccess(String to) {
        String subject = "Pre-Order Success";
        String body = "Dear valuable user,\n\nYou have successfully placed your pre-order. If you have any further questions, please don't hesitate to contact us.\n\nPlease ignore this mail if it does not apply to you. \n\nThank You.\nBest regards,\nTeam Talent Boozt.";
        sendSimpleEmail(to, subject, body);
    }

    public void bankPayment(BankPaymentDTO bankPaymentDTO) {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String mailSubject = "Bank Payment Request - " + bankPaymentDTO.getName();
        String message = bankPaymentDTO.getCompanyId()+" Requested Bank Payment! \n\nSlip Url: " + bankPaymentDTO.getSlipUrl();
        String body = "Name: " + bankPaymentDTO.getName() + "\nCountry: " + bankPaymentDTO.getCountry() + "\nPhone: " + bankPaymentDTO.getPhone() + "\n\n" + message;

        sendSimpleEmail(to, mailSubject, body);
    }

    public void requestResume(CVRequestDTO cvRequestDTO) {
        String to = configUtil.getProperty("CONTACT_ME_EMAIL");
        String mailSubject = "CV Request - " + cvRequestDTO.getName();
        String body = "Name: " + cvRequestDTO.getName() + "\nEmail: " + cvRequestDTO.getEmail() + "\nDOB:" + cvRequestDTO.getDob() + "\nCareer Stage: " + cvRequestDTO.getCareerStage() + "\nJob Title: " + cvRequestDTO.getJobTitle() + "\nJob Link: " + cvRequestDTO.getLink() + "\n\n" + cvRequestDTO.getMessage();

        sendSimpleEmail(to, mailSubject, body);
    }
}
