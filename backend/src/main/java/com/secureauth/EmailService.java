package com.secureauth;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.*;
import org.springframework.beans.factory.annotation.Value;
@Service
public class EmailService {
 private final JavaMailSender sender; private final String from;
 public EmailService(JavaMailSender sender,@Value("${spring.mail.username}") String from){this.sender=sender;this.from=from;}
 public void send(String email,String code) throws jakarta.mail.MessagingException {
 var message=sender.createMimeMessage();var helper=new MimeMessageHelper(message,false,"UTF-8");helper.setFrom(from);helper.setTo(email);helper.setSubject("SecureAuth | Your verification code");helper.setText("<div style='font-family:Arial;background:#eef2ff;padding:32px'><div style='background:white;padding:32px;border-radius:16px;max-width:420px;margin:auto'><h2 style='color:#5945d6'>SecureAuth</h2><p>Your verification code is</p><h1 style='letter-spacing:8px;color:#312e81'>"+code+"</h1><p>Expires in five minutes. Never share this code.</p><p>If you did not request it, ignore this email.</p></div></div>",true);sender.send(message);
 }
}
