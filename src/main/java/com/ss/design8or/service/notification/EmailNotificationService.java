package com.ss.design8or.service.notification;
import java.time.LocalDate;

import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.CharEncoding;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.ss.design8or.config.ServiceProperties;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.MailConfig;
import com.ss.design8or.model.User;

import lombok.extern.slf4j.Slf4j;

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
class EmailNotificationService {

    private static final String USER = "user";
    private static final String DECLINED_DESIGNATION_USER = "declinedDesignationUser";
    private static final String DESIGNATION_RESPONSE_URL = "reponseUrl";
    private static final String YEAR = "year";
    
    private ServiceProperties properties;
    private JavaMailSenderImpl javaMailSender;
    private SpringTemplateEngine templateEngine;
    
    public EmailNotificationService(ServiceProperties properties, JavaMailSenderImpl javaMailSender,
    		SpringTemplateEngine templateEngine) {
    	this.javaMailSender = javaMailSender;
    	this.templateEngine = templateEngine;
    	this.properties = properties;
	}
    
    @Async
    private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(properties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}'", to, e);
        }
    }
    
    @Async
    public void sendDesignationEvent(Designation designation) {
       sendDesignationEvent(designation, designation.getUser());
    }
    
    @Async
    public void sendDesignationEvent(Designation designation, User candidate) {
        Context context = new Context();
        context.setVariable(USER, candidate);
        String responseUrl = computeDesignationResponseUrl(designation.getToken(), candidate.getEmailAddress());
		context.setVariable(DESIGNATION_RESPONSE_URL, responseUrl);
        context.setVariable(YEAR, LocalDate.now().getYear());
        String content = null;
        if(designation.isDeclined()) {
        	context.setVariable(DECLINED_DESIGNATION_USER, designation.getUser());
        	content = templateEngine.process("designation-broadcast", context);
        } else {
        	content = templateEngine.process("designation", context);
        }
        String subject = properties.getMail().getDesignationEmailSubject();
        sendEmail(candidate.getEmailAddress(), subject, content, false, true);
    }
    
    private String computeDesignationResponseUrl(String token, String emailAddress) {
    	MailConfig mailConfig = properties.getMail();
    	String responseBaseUrl = mailConfig.getDesignationResponseBaseUrl();
    	return String.format("%s/designation-response?token=%s&email=%s", responseBaseUrl, token, emailAddress);
    }
    
}