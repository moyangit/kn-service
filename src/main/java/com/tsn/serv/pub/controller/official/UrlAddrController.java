package com.tsn.serv.pub.controller.official;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;

@RestController
@RequestMapping("official")
public class UrlAddrController {

	@GetMapping("addrs")
	public Response<?> addrs(){
		String addr = Env.getVal("official.url.addrs");
		String[] addrs = addr.split(",");
		return Response.ok(addrs);
	}
	
	@GetMapping("addr")
	public Response<?> addr(){
		String addr = Env.getVal("official.url.addr");
		return Response.ok(addr);
	}
	
	/**
	 * 取微信公众号
	 * @return
	 */
	@GetMapping("wx")
	public Response<?> wecharNo(){
		String wechatNo = Env.getVal("official.wechat.no");
		return Response.ok(wechatNo);
	}

	/**
	 * 问题反馈地址
	 * @return
	 */
	@GetMapping("feedbackUrl")
	public Response<?> feedbackUrl(){
		String feedbackUrl = Env.getVal("problem.feedback.url");
		return Response.ok(feedbackUrl);
	}

	/**
	 * 快加速官网地址
	 * @return
	 */
	@GetMapping("official")
	public Response<?> officialUrl(){
		String officialUrl = Env.getVal("official.website.url");
		return Response.ok(officialUrl);
	}
}
