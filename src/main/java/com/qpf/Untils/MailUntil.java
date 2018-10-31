package com.qpf.Untils;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.sun.mail.util.MailSSLSocketFactory;

@Component
public class MailUntil {
	private static final Logger logger = LoggerFactory.getLogger(MailUntil.class);

	@Autowired
	private Environment environment;

	private static String auth;
	private static String host;
	private static String protocol;
	private static int port;
	private static String authName;
	private static String password;
	private static boolean isSSL;
	private static String chartSet;
	private static String timeout;

	@PostConstruct
	public void initParm() {
		auth = environment.getProperty("mail.smtp.auth");
		host = environment.getProperty("mail.host");
		protocol = environment.getProperty("mail.transport.protocol");
		port = environment.getProperty("mail.smtp.port", Integer.class);
		authName = environment.getProperty("mail.auth.name");
		password = environment.getProperty("mail.auth.password");
		isSSL = environment.getProperty("mail.is.ssl", Boolean.class);
		chartSet = environment.getProperty("mail.send.charset");
		timeout = environment.getProperty("mail.smtp.timeout");
	}

	public static boolean sendMail(String mainSubject, String[] toUsers, String[] ccUsers, String content,
			List<Map<String, String>> attachfiles) {
		boolean flag = true;
		// JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);
		javaMailSender.setPort(port);
		javaMailSender.setUsername(authName);
		javaMailSender.setPassword(password);
		javaMailSender.setDefaultEncoding(chartSet);
		javaMailSender.setProtocol(protocol);

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", auth);
		properties.setProperty("mail.smtp.timeout", timeout);
		if (isSSL) {
			try {
				MailSSLSocketFactory msf = null;
				msf = new MailSSLSocketFactory();
				msf.setTrustAllHosts(true);
				properties.put("mail.smtp.ssl.socketFactory", msf);
				properties.put("mail.smtp.ssl.enable", "true");
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			javaMailSender.setJavaMailProperties(properties);

			MimeMessage message = javaMailSender.createMimeMessage();

			// 发送邮件的对象
			MimeMessageHelper messageHelper = new MimeMessageHelper(message);
			try {
				messageHelper.setTo(toUsers);
				if (ccUsers != null && ccUsers.length > 0) {
					messageHelper.setCc(ccUsers);
				}
				messageHelper.setFrom(authName);
				messageHelper.setSubject(mainSubject);
				messageHelper.setText(content, true);
				if (attachfiles != null && attachfiles.size() > 0) {
					for (Map<String, String> attachfile : attachfiles) {
						String attachfileName = attachfile.get("name");
						File file = new File(attachfile.get("file"));
						messageHelper.addAttachment(attachfileName, file);
					}
				}
				javaMailSender.send(message);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				logger.error("发送邮件失败!", e);
				flag = false;
			}
		}
		return flag;
	}
}
