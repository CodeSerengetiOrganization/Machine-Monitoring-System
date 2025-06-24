package com.mytech.machinemonitorsystem.helper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

// Interface for the factory
/*
* This interface is used to crate MimeMessageHelper, to avoid new a MimeMessageHelper, so that in unit test MimeMessageHelper can be mocked and intercept the creating process of MimeMessage
* */
public interface MimeMessageHelperFactory {
    MimeMessageHelper createMimeMessageHelper(MimeMessage mimeMessage, boolean multipart, String encoding) throws MessagingException;
}
