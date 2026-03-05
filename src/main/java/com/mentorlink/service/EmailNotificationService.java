package com.mentorlink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendRegistrationWelcome(String toEmail, String role) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Mail not configured, skipping welcome email");
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("Welcome to MentorLink");
            msg.setText("You have successfully registered on the MentorLink portal as " + role + ".\n\nYou can now login and start using the portal.");
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    public void sendDeadlineReminder(String toEmail, String deadlineName, String dueDate) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Mail not configured, skipping email to {}", toEmail);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("MentorLink: Deadline Reminder - " + deadlineName);
            msg.setText("Reminder: " + deadlineName + " is due on " + dueDate + ".\n\nPlease complete your tasks before the deadline.");
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }

    public void sendApprovalNotification(String toEmail, String projectTitle, boolean approved) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Mail not configured, skipping email to {}", toEmail);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("MentorLink: Faculty Request " + (approved ? "Approved" : "Rejected"));
            msg.setText("Your faculty mentorship request for project \"" + projectTitle + "\" has been " + (approved ? "approved" : "rejected") + ".");
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }
}
