package com.mytech.machinemonitorsystem;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class GmailSmtpTest {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "20000");
        props.put("mail.smtp.timeout", "20000");
        props.put("mail.smtp.writetimeout", "20000");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("qiang.serengeti@gmail.com", "fhafngngyzvivont"); // Use App Password
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("qiang.serengeti@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("jonathanwq.wang@gmail.com"));
        msg.setSubject("Test Mail");
        msg.setText("This is a test mail.");

        Transport.send(msg);
        System.out.println("Mail sent successfully!");
    }
}
