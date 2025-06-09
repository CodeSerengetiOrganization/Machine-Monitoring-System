package com.mytech.machinemonitorsystem.controller;

import com.mytech.machinemonitorsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

@RestController
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @Value("${app.alert.recipients}")
    private String[] defaultRecipients;
    @GetMapping("/trigger-email")
    public void triggerEmailSending(
            @RequestParam @Nullable String machineCode
    ){
        String[] to = defaultRecipients;
        String subject = "Alert for Machine "+machineCode;
        String templateName = "emails/alert-template";
        Context context = new Context();
        context.setVariable("machineCode",machineCode);

        try {
            emailService.sendTemplatedHtmlEmail(to,subject,templateName,context);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
    }
}
