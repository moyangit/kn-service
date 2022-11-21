package com.tsn.serv.auth.service.email;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.tsn.common.utils.utils.tools.http.HttpPostReq;
import com.tsn.common.utils.utils.tools.http.entity.HttpRes;

@Service("emailService")
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")  
	private String from;
	
	@Value("${spring.mail.password}")  
	private String password;
	
	@Value("${spring.mail.ownserver}")  
	private String ownServer;
	
	private Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	private String produceName = "黑豹";
	
	public void sendMailGun(MailParam mail) {
		
		Client client = Client.create();
		//client.addFilter(new HTTPBasicAuthFilter("api", "dfc35b8406996907a20c8784a7f0515d-90346a2d-5555c122"));
		client.addFilter(new HTTPBasicAuthFilter("api", "dfc35b8406996907a20c8784a7f0515d-90346a2d-5555c122"));
		
		if (StringUtils.isEmpty(ownServer)) {
			ownServer = "https://api.mailgun.net/v3/ralexmail.com/messages";
		}
		
		WebResource webResource = client.resource(ownServer);
		MultivaluedMapImpl formData = new MultivaluedMapImpl();
		formData.add("from", produceName + " <"+from+">");
		formData.add("to", mail.getTo());
		formData.add("subject", StringUtils.isEmpty(mail.getSubject()) ? produceName + "发来验证码请查看" : "");
		formData.add("template", "kjs-valid-code");
		formData.add("h:X-Mailgun-Variables", "{\"smsCode\": \"" + mail.getContent() + "\"}");
		ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
			post(ClientResponse.class, formData);
		logger.info("send mailgun email, res = {}",response.toString());
		
		
//		Client client = Client.create();
//		client.addFilter(new HTTPBasicAuthFilter("api", "dfc35b8406996907a20c8784a7f0515d-90346a2d-5555c122"));
//		WebResource webResource = client.resource("https://api.mailgun.net/v3/sandbox4e599710d9244c95a4bd14c34fbaa675.mailgun.org/messages");
//		MultivaluedMapImpl formData = new MultivaluedMapImpl();
//		formData.add("from", "Mailgun Sandbox <postmaster@sandbox4e599710d9244c95a4bd14c34fbaa675.mailgun.org>");
//		formData.add("to", mail.getTo());
//		formData.add("subject", mail.getSubject());
//		formData.add("template", "kjs-valid-code");
//		formData.add("h:X-Mailgun-Variables", "{'smsCode': '" + mail.getContent() +"'}");
//		ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
//			post(ClientResponse.class, formData);
//		logger.info("send mailgun email, res = {}",response.toString());
	}
	
	public void sendEmail(MailParam mail){
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		
		if (StringUtils.isEmpty(from)) {
			from = mail.getFrom();
		}
		
		simpleMailMessage.setFrom(from); // 发送人,从配置文件中取得
		simpleMailMessage.setTo(mail.getTo()); // 接收人
		simpleMailMessage.setSubject(mail.getSubject());
		simpleMailMessage.setText(mail.getContent());
		mailSender.send(simpleMailMessage);
	}
	
	public void sendOwnMail(MailParam mail) {
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("mail_from", from));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("mail_to", mail.getTo()));
		params.add(new BasicNameValuePair("subject", mail.getSubject()));
		params.add(new BasicNameValuePair("content", mail.getContent()));
		params.add(new BasicNameValuePair("subtype", ""));
		try {
			HttpRes req = new HttpPostReq(ownServer, null, params).excuteReturnObj();
			logger.info("sendOwnMail success, {}",req);
		} catch (Exception e) {
			logger.error("{}",e);
		}
	}
	
	
	
	
	public static class MailParam {

		/** 发件人 **/
		private String from;
		/** 收件人 **/
		private String to;
		/** 主题 **/
		private String subject;
		/** 邮件内容 **/
		private String content;

		public MailParam() {
		}

		public MailParam(String to, String subject, String content) {
			this.to = to;
			this.subject = subject;
			this.content = content;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

}