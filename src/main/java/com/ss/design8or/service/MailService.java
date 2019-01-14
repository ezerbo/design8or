package com.ss.design8or.service;
import java.time.LocalDate;

import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.CharEncoding;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.ss.design8or.config.ServiceProperties;
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
public class MailService {

    private static final String USER = "user";
    private static final String DESIGNATION_RESPONSE_URL = "reponseUrl";
    private static final String YEAR = "year";

    private ServiceProperties properties;
    private JavaMailSenderImpl javaMailSender;
    private SpringTemplateEngine templateEngine;
    private BasicTextEncryptor basicTextEncryptor;
    
    public MailService(ServiceProperties properties, JavaMailSenderImpl javaMailSender,
    		SpringTemplateEngine templateEngine, BasicTextEncryptor basicTextEncryptor) {
    	this.javaMailSender = javaMailSender;
    	this.templateEngine = templateEngine;
    	this.properties = properties;
    	this.basicTextEncryptor = basicTextEncryptor;
	}

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);
        // Prepare message using a Spring helper
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
    public void sendDesignationEventAsEmail(User user) {
        log.debug("Sending activation e-mail to '{}'", user.getEmailAddress());
        Context context = new Context();
        context.setVariable(USER, user);
        String designationResponseUrl = computeDesignationResponseUrl(user.getEmailAddress());
        context.setVariable(DESIGNATION_RESPONSE_URL, designationResponseUrl);
        context.setVariable(YEAR, LocalDate.now().getYear());
        String content = templateEngine.process("designation-email", context);
        String subject = properties.getMail().getDesignationEmailSubject();
        sendEmail(user.getEmailAddress(), subject, content, false, true);
    }
    
    private String computeDesignationResponseUrl(String emailAddress) {
    	MailConfig mailConfig = properties.getMail();
    	String responseBaseUrl = mailConfig.getDesignationResponseBaseUrl();
    	//designated or there's declined designation.
    	String encryptedEmail = basicTextEncryptor.encrypt(emailAddress);
    	return String.format("%s/designation-response?token=%s", responseBaseUrl, encryptedEmail);
    }

}