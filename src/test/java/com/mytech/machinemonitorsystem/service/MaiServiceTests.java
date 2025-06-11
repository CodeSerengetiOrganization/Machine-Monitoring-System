package com.mytech.machinemonitorsystem.service;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

@SpringBootTest
public class MaiServiceTests {
    @Mock
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    public void testSendTemplatedHtmlEmailManualCheck(){}

}
