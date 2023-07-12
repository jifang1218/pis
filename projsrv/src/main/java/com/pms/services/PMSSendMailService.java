package com.pms.services;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PMSSendMailService {
	
	public void sendmail(String title, String body, String to) throws AddressException, MessagingException, IOException {
		   Properties props = new Properties();
		   props.put("mail.smtp.auth", "true");
		   props.put("mail.smtp.starttls.enable", "true");
		   props.put("mail.smtp.host", "smtp.gmail.com");
		   props.put("mail.smtp.port", "587");
		   
		   String from = "jifang1218@gmail.com";
		   String googleAppPasswd = "uuyizlfynoojfjug";
		   
		   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		      protected PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication(from, googleAppPasswd);
		      }
		   });
		   Message msg = new MimeMessage(session);
		   
		   log.debug("from: " + from);
		   msg.setFrom(new InternetAddress(from, false));

		   log.debug("to: " + to);
		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		   
		   log.debug("title: " + title);
		   msg.setSubject(title);
		   
		   log.debug("content: " + body);
		   msg.setContent(body, "text/html");
		   
		   Date date = new Date();
		   log.debug("send date: " + date);
		   msg.setSentDate(date);

		   MimeBodyPart messageBodyPart = new MimeBodyPart();
		   messageBodyPart.setContent(body, "text/html");

		   Multipart multipart = new MimeMultipart();
		   multipart.addBodyPart(messageBodyPart);
		   //MimeBodyPart attachPart = new MimeBodyPart();

//		   attachPart.attachFile("/home/jifang/Desktop/api.txt");
//		   multipart.addBodyPart(attachPart);
		   msg.setContent(multipart);
		   Transport.send(msg);   
		}
}
