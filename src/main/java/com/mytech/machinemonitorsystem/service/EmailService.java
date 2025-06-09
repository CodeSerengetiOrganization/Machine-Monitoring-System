package com.mytech.machinemonitorsystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int smtpTimeout;

//    @Async  //execute this method in a separate thread
    public void sendTemplatedHtmlEmail(String[] to, String subject, String templateName, Context variables){
        try {
            String htmlContent = templateEngine.process(templateName, variables);
//            System.out.println("Processed email content: " + htmlContent);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("qiang.serengeti@gmail.com");    // Must match configured username
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            System.out.println("smtpTimeout:"+smtpTimeout);//for debug
            mailSender.send(message);
            System.out.println("Async HTML alert email sent to " + String.join(", ", to) + " at " + System.currentTimeMillis());
//        }catch ()

        } catch (MessagingException me){
            // error handling
            System.out.println("Error happens when create message for mail to :"+String.join(",",to)+".Message: "+me.getMessage());
        }
        catch (MailAuthenticationException mae){
//            System.out.println(mae.toString());
            System.out.println("CRITICAL: Email Authentication error for mailing to:  "+String.join(",",to)+"Check App password for sender.Message: "+mae.getMessage());
        }catch(MailSendException mse){
//            System.out.println(mse.toString());
            System.out.println("Error: Error when sending mail to:  "+String.join(",",to)+".Message: "+mse.getMessage());
        }catch(Exception e){
            System.out.println("UNEXPECTED ERROR: Failed to send mail to : "+String.join(",",to)+".Message:"+e.getMessage());
        }
    }
}
