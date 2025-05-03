package com.ss.design8or.service.notification;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ss.design8or.model.DesignationStatus;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.CharEncoding;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;

import com.ss.design8or.config.properties.ServiceProperties;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.User;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * 
 * @author ezerbo
 * 
 * Service for sending e-mails.
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String USER = "user";

    private static final String LEAD = "lead";

    private static final String RESPONSE_URL = "responseUrl";

    private static final String YEAR = "year";
    
    private final ServiceProperties properties;

    private final JavaMailSenderImpl javaMailSender;

    private final SpringTemplateEngine templateEngine;

    
    @Async
    public void sendEmail(String to, String subject, String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(properties.getDesignationEmail().getFrom());
            message.setSubject(subject);
            message.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}'", to, e);
        }
    }
    
    @Async
    public void sendEmail(Designation designation) {
       sendEmail(designation, designation.getUser());
    }
    
    @Async
    public void sendEmail(Designation designation, User candidate) {
        Context context = new Context();
        context.setVariable(USER, candidate);
		context.setVariable(RESPONSE_URL, getResponseUrl(designation.getId(), candidate.getEmailAddress()));
        context.setVariable(YEAR, LocalDate.now().getYear());
        context.setVariable(LEAD, designation.getUser());
        context.setVariable("isDeclined", DesignationStatus.DECLINED.equals(designation.getStatus()));
        String content = templateEngine.process("designation", context);
        String subject = properties.getDesignationEmail().getSubject();
        sendEmail(candidate.getEmailAddress(), subject, content);
    }

    public void broadcastDeclinationEmail(Designation designation, List<User> candidates) {
        candidates.forEach(candidate -> sendEmail(designation, candidate));
    }

    public void sendReassignmentEmail(User currentAssignee) {
        String subject = "Designation Reassignment Notification";
        String content = "Dear " + currentAssignee.getFirstName() + ",\n\n" +
                "Your designation has been successfully reassigned.\n" +
                "Best regards,\n" +
                "The Design8or Team";
        sendEmail(currentAssignee.getEmailAddress(), subject, content);
    }
    
    private String getResponseUrl(long designationId, String candidateEmailAddress) {
        return UriComponentsBuilder.fromUriString(properties.getDesignationEmail().getResponseBaseUrl())
                .path("/designations/{id}/response")
                .queryParam("emailAddress", candidateEmailAddress)
                .buildAndExpand(Map.of("id", designationId))
                .toUriString();
    }
    
}