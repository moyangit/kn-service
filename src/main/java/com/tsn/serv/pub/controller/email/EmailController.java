package com.tsn.serv.pub.controller.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.serv.auth.service.email.EmailService;
import com.tsn.serv.pub.entity.emailuser.EmailUser;
import com.tsn.serv.pub.service.email.EmailUserService;

@RestController
@RequestMapping("email")
public class EmailController {
	
	@Autowired
	private EmailUserService emailUserService;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private EmailService emailService;
	
	@Value("${spring.mail.batchusername}")  
	private String batchFrom;
	
	private Logger logger = LoggerFactory.getLogger(EmailController.class);
	
	@GetMapping("select")
	public Response<?> selectEmail() {
		String[] arr = batchFrom.split(",");
		return Response.ok(arr);
	}
	
	/*@GetMapping("batch/{number}")
	public void sendBatchEmail(@PathVariable Integer number) {
		QueryWrapper<EmailUser> queryWrapper = new QueryWrapper<EmailUser>();
		queryWrapper.orderByAsc("id");
		List<EmailUser> emailUserList = emailUserService.list(queryWrapper);
		
		int count = 0;
		List<String> userList = new ArrayList<String>();
		for (EmailUser emailUser : emailUserList) {
			userList.add(emailUser.getEmail());
			if (count == number - 1) {
				break;
			}
		}
		
		Context context = new Context();
		context.setVariable("name", "");
		String subject = "欢迎使用快鸟vpn";
		String content = templateEngine.process("/email/", context);
		
		String subject = "尊敬的vpn用户您好：";
	    // true 表示启动HTML格式的邮件
	    String html = "<html>" +
	            " <body>" +
	            "  <h1>欢迎使用快鸟加速器:</h1>" +
	            "  <p>立即加入体验价格仅需1元,分享给其他朋友更是可以获得永久vip</p>" +
	            "  <p>高速稳定的网络加速工具</p>" +
	            "  <p>多平台兼容，随时一键连接</p>" +
	            "  <p>流量加密，协助您匿名隐身</p>" +
	            "  <p>一键解锁流媒体</p>" +
	            "  <p>遍布全球的高速服务器</p>" +
	            "  <P>官网网址：<a href='https://knjs.com'>https://knjs.com</p>" +
	            "  <P>安卓：<a href='https://down.knsj.xyz/down/ad'>点击下载</p>" +
	            "  <P>windows：<a href='https://down.knsj.xyz/down/pc'>点击下载</p>" +
	            "  <P>mac：<a href='https://down.knsj.xyz/down/mac'>点击下载</p>" +
	            "  <P>苹果：<a href='https://user.kuainiaojsq.xyz/page/subscribe.html'>点击下载</p>" +
	            " </body>" +
	            "</html>";
	    
	    String[] userArr = userList.toArray(new String[]{});
	    
	    logger.info("send email user List : {}", JsonUtils.objectToJson(userArr));
		emailService.sendBatchEmail(userArr, subject, html);;
	}*/
	
	@GetMapping("batch/{minId}/{maxId}")
	public void sendBatchEmail(@PathVariable Integer minId, @PathVariable Integer maxId, String fromEmail) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				QueryWrapper<EmailUser> queryWrapper = new QueryWrapper<EmailUser>();
				queryWrapper.le("id",maxId)//小于等于
				.ge("id", minId).orderByAsc("id");
				List<EmailUser> emailUserList = emailUserService.list(queryWrapper);
				String[] arr = batchFrom.split(",");
				List<String> userList = new ArrayList<String>();
				
				for (EmailUser emailUser : emailUserList) {
					
					String fromEmailTemp = "";
					if (StringUtils.isEmpty(fromEmail)) {
						int index = new Random().nextInt(arr.length);
						fromEmailTemp = arr[index];
					}else {
						fromEmailTemp = fromEmail;
					}
					
					userList.add(emailUser.getEmail());
					String subject = "尊敬的V.P.N用户您好：";
				    // true 表示启动HTML格式的邮件
				    String html = "<html>" +
				            " <body>" +
				            "  <h1>欢迎使用快鸟加速器:</h1>" +
				            "  <p>立即加入体验价格仅需1元,分享给其他朋友更是可以获得永久vip</p>" +
				            "  <p>高速稳定的网络加速工具</p>" +
				            "  <p>多平台兼容，随时一键连接</p>" +
				            "  <p>流量加密，协助您匿名隐身</p>" +
				            "  <p>一键解锁流媒体</p>" +
				            "  <p>遍布全球的高速服务器</p>" +
				            "  <p>官网网址：<a href='https://knjs.com'>https://knjs.xyz</a></p>" +
				            "  <p>安卓：<a href='https://down.knsj.xyz/down/ad'>https://down.knsj.xyz/down/ad</a>(若无法打开，复制到浏览器打开)</p>" +
				            "  <p>windows：<a href='https://down.knsj.xyz/down/pc'>https://down.knsj.xyz/down/pc</a>(若无法打开，复制到浏览器打开)</p>" +
				            "  <p>mac：<a href='https://down.knsj.xyz/down/mac'>https://down.knsj.xyz/down/mac</a>(若无法打开，复制到浏览器打开)</p>" +
				            "  <p>苹果：<a href='https://user.kuainiaojsq.xyz/page/subscribe.html'>https://user.kuainiaojsq.xyz/page/subscribe.html</a>(若无法打开，复制到浏览器打开)</p>" +
				            "  <p>联系tg客服:https://t.me/knjs_xyz</p>" +
				            " </body>" +
				            "</html>";
				    
				    String[] userArr = userList.toArray(new String[]{});
				    logger.info("batch/{minId}/{maxId} send email user List : {}", JsonUtils.objectToJson(userArr));
					emailService.sendBatchEmail(fromEmailTemp, userArr, subject, html);
					userList.clear();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
		
	}
	
}
