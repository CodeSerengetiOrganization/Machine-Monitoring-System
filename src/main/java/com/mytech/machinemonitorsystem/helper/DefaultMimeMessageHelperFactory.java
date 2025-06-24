package com.mytech.machinemonitorsystem.helper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

// Default implementation
/*
 * This implementation is used to crate MimeMessageHelper, to avoid new a MimeMessageHelper, so that in unit test MimeMessageHelper can be mocked and intercept the creating process of MimeMessage
 * */
@Component
public class DefaultMimeMessageHelperFactory implements MimeMessageHelperFactory {

    @Override
    public MimeMessageHelper createMimeMessageHelper(MimeMessage mimeMessage, boolean multipart, String encoding) throws MessagingException {
        return new MimeMessageHelper(mimeMessage, multipart, encoding);
    }
}
