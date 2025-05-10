package com.ss.design8or.service.communication;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ss.design8or.model.Pool;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.service.PoolService;
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
    
    private final ServiceProperties properties;

    private final JavaMailSenderImpl javaMailSender;

    private final SpringTemplateEngine templateEngine;

    private final PoolService poolService;

    
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
        String content = templateEngine.process("designation", getContext(designation, candidate));
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
        return UriComponentsBuilder.fromUriString(properties.getDesignationEmail().getServiceBaseUrl())
                .path("/designations/{id}/response")
                .queryParam("emailAddress", candidateEmailAddress)
                .buildAndExpand(Map.of("id", designationId))
                .toUriString();
    }

    private Context getContext(Designation designation, User candidate) {
        Pool currentPool = poolService.getCurrent();
        Context context = new Context();
        context.setVariable("user", candidate);
        context.setVariable("responseUrl", getResponseUrl(designation.getId(), candidate.getEmailAddress()));
        context.setVariable("serviceBaseUrl", properties.getDesignationEmail().getServiceBaseUrl());
        context.setVariable("year", LocalDate.now().getYear());
        context.setVariable("lead", designation.getUser());
        context.setVariable("isDeclined", DesignationStatus.DECLINED.equals(designation.getStatus()));
        context.setVariable("candidatesCount", poolService.getCandidates(currentPool.getId()).size());
        context.setVariable("participantsCount", poolService.getParticipants(currentPool.getId()).size());
        context.setVariable("poolStartDate", currentPool.getStartDate());
        return context;
    }
    
}