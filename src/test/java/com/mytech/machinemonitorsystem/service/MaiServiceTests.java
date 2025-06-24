package com.mytech.machinemonitorsystem.service;

import com.mytech.machinemonitorsystem.helper.MimeMessageHelperFactory;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.tomcat.websocket.AuthenticationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.github.stefanbirkner.systemlambda.SystemLambda;

@ExtendWith(MockitoExtension.class)
public class MaiServiceTests {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private SpringTemplateEngine templateEngine;
    @Mock
    private MimeMessageHelperFactory mimeMessageHelperFactory;
    @Mock
    private MimeMessage mockedMimeMessage;
    @InjectMocks
    EmailService emailService;

    String[] mockedTo = {"mockedEmailReceiver@gmail.com"};
    String mockedMailUserName = "mockedMailUserName@gmail.com";
    String mockedSubject = "mockedSubject";
    String mockedTemplateName = "emails/alert-template";
    Context mockedContext =  new Context();
    MimeMessageHelper mockedMessageHelper = mock(MimeMessageHelper.class);
    //prepare
    String mockedHtmlContent = "Mocked htmlContent";

    @BeforeEach
    public void setup() throws MessagingException {
//        emailService.setSmtpTimeout(20000);
//        emailService.setMailUserName(mockedMailUserName);   //need it?--no, do not store state
//        when(mailSender.createMimeMessage()).thenReturn(mockedMimeMessage);
        Mockito.doReturn(mockedHtmlContent).when(templateEngine).process(mockedTemplateName,mockedContext);
        Mockito.doReturn(mockedMimeMessage).when(mailSender).createMimeMessage();
        Mockito.doReturn(mockedMessageHelper).when(mimeMessageHelperFactory).createMimeMessageHelper(any(MimeMessage.class),eq(true),anyString());//moved to setup()

    }

    //1. should send email in normal scenario
    @Test
    public void shouldSendEmailNormalScenario() throws MessagingException {

        //given-
        Mockito.doNothing().when(mailSender).send((MimeMessage) any());

        //when
        emailService.sendTemplatedHtmlEmail(mockedTo,mockedSubject,mockedTemplateName,mockedContext);
        //then
        Mockito.verify(templateEngine,Mockito.times(1)).process(eq(mockedTemplateName),eq(mockedContext));
        Mockito.verify(mimeMessageHelperFactory,Mockito.times(1)).createMimeMessageHelper(eq(mockedMimeMessage),eq(true),eq("UTF-8"));
        Mockito.verify(mockedMessageHelper,times(1)).setFrom((String) any());
        Mockito.verify(mailSender,Mockito.times(1)).createMimeMessage();
    }
    //2.should handle checked exception:MessagingException
    @Test
    public void shouldHandleMessagingException() throws Exception {

        //given
        MessagingException mockedMessagingException = new MessagingException("mocked MessagingException");
        Mockito.doThrow(mockedMessagingException).when(mockedMessageHelper).setFrom((String) any());//helper will eventually call setFrom in MimeMessage
        //when & then
        //here is only printed by the code under test, will check log in future
        String printedExceptionString = SystemLambda.tapSystemOut(() -> {
            emailService.sendTemplatedHtmlEmail(mockedTo, mockedSubject, mockedTemplateName, mockedContext);
        });
        //assert the exception
        Assertions.assertTrue(printedExceptionString.contains(mockedMessagingException.getMessage().trim()));
        //assert the interactions up to the method which throw exception
        verify(templateEngine,times(1)).process((String) any(),any());
        verify(mimeMessageHelperFactory,times(1)).createMimeMessageHelper(any(),eq(true),any());
        //assert the method is called(and for sure it throw exception)
        verify(mockedMessageHelper,times(1)).setFrom((String) any());
    }
    //3.should handle checked exception:MailAuthenticationException
    @Test
    public void shouldHandleMailAuthenticationException() throws Exception {
        //given(arrange)

        MailAuthenticationException mockedMailAuthenticationException = new MailAuthenticationException("mocked MailAuthenticationException");
        Mockito.doThrow(mockedMailAuthenticationException)
                .when(mailSender).send(any(MimeMessage.class));
        //when(act) & then(assert)
        //here is only printed by the code under test, will check log in future
        String printedExceptionString = SystemLambda.tapSystemOut(() -> {
            emailService.sendTemplatedHtmlEmail(mockedTo, mockedSubject, mockedTemplateName, mockedContext);
        });
        //assert the exception
        Assertions.assertTrue(printedExceptionString.contains(mockedMailAuthenticationException.getMessage().trim()));
        //assert the interactions up to the method which throw exception
        verify(templateEngine,times(1)).process((String) any(),any());
        verify(mimeMessageHelperFactory,times(1)).createMimeMessageHelper(any(),eq(true),any());
        verify(mockedMessageHelper,times(1)).setFrom((String) any());
        //assert the method is called(and for sure it throw exception)
        verify(mailSender,times(1)).send((MimeMessage) any());
    }

    //4.should handle checked exception:MailSendException
    @Test
    public void shouldHandleMailSendException() throws Exception {
        //given--arrange
        MailSendException mockedMailSendException = new MailSendException("mocked MailSendException");
        doThrow(mockedMailSendException).when(mailSender).send((MimeMessage) any());
        //when&assert
        String systemOut = SystemLambda.tapSystemOut(() -> {
            emailService.sendTemplatedHtmlEmail(mockedTo,mockedSubject, mockedTemplateName, mockedContext);
        });
        Assertions.assertTrue(systemOut.contains(mockedMailSendException.getMessage().trim()));
        //assert the interactions up to the method which throw exception
        verify(templateEngine,times(1)).process((String) any(),any());
        verify(mimeMessageHelperFactory,times(1)).createMimeMessageHelper(any(),eq(true),any());
        verify(mockedMessageHelper,times(1)).setFrom((String) any());
        //assert the method is called(and for sure it throw exception)
        verify(mailSender,times(1)).send((MimeMessage) any());
    }

    //5.should handle unexpected exception
    @Test
    public void shouldHandledUnexpectedException() throws Exception {
        //given--arrange
        Exception mockedUnexpectedException = new RuntimeException("Mocked unexpected exception");
        doThrow(mockedUnexpectedException).when(mailSender).send((MimeMessage) any());
        //when&assert
        String systemOut = SystemLambda.tapSystemOut(() -> {
            emailService.sendTemplatedHtmlEmail(mockedTo,mockedSubject, mockedTemplateName, mockedContext);
        });
        Assertions.assertTrue(systemOut.contains(mockedUnexpectedException.getMessage().trim()));
        //assert the interactions up to the method which throw exception
        verify(templateEngine,times(1)).process((String) any(),any());
        verify(mimeMessageHelperFactory,times(1)).createMimeMessageHelper(any(),eq(true),any());
        verify(mockedMessageHelper,times(1)).setFrom((String) any());
        //assert the method is called(and for sure it throw exception)
        verify(mailSender,times(1)).send((MimeMessage) any());
    }

}
