package com.tsn.serv.mem.controller.page;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.core.norepeat.impl.AtomiRedisNoRepeatService;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.utils.CookieUtils;
import com.tsn.common.web.utils.WebUtils;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.mem.entity.env.enm.EnvKeyEnum;
import com.tsn.serv.mem.entity.flow.FlowDay;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.mem.MemInfoConfig;
import com.tsn.serv.mem.service.charge.MemChargeService;
import com.tsn.serv.mem.service.env.EnvParamsService;
import com.tsn.serv.mem.service.flow.FlowService;
import com.tsn.serv.mem.service.mem.MemInfoConfigService;
import com.tsn.serv.mem.service.mem.MemService;

@Controller
@RequestMapping("page")
public class PageViewController {
	
	private static Logger log = LoggerFactory.getLogger(PageViewController.class);
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private AtomiRedisNoRepeatService atomiRedisNoRepeatService;
	
	@Autowired
	private MemChargeService memChargeService;
	
	@Autowired
	private FlowService flowService;
	
	@Autowired
	private MemInfoConfigService memInfoConfigService;
	
	@Autowired
	private EnvParamsService envParamsService;
	
	@GetMapping("/login.html")
	public ModelAndView toLoginPage() {
		try {
			return new ModelAndView("user/login");
		} catch (Exception e) {
			log.error("toInvitFriendListPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	@GetMapping("/reg.html")
	public ModelAndView toRegPage() {
		try {
			return new ModelAndView("user/reg");
		} catch (Exception e) {
			log.error("toInvitFriendListPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	@GetMapping("/updatepwd.html")
	@AuthClient()
	public ModelAndView toUpdatepwdPage() {
		try {
			JwtInfo jwtInfo = JwtLocal.getJwt();
			if (jwtInfo == null) {
				return new ModelAndView("redirect:/page/login.html");
			}
			
			String userId = jwtInfo.getSubject();
			if (StringUtils.isEmpty(userId)) {
				return new ModelAndView("redirect:/page/login.html");
			}
			
			return new ModelAndView("user/updatepwd");
		} catch (Exception e) {
			log.error("toInvitFriendListPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	@GetMapping("/subscribe.html")
	@AuthClient()
	public ModelAndView toSubPage(Model model) {
		try {
			JwtInfo jwtInfo = JwtLocal.getJwt();
			if (jwtInfo == null) {
				return new ModelAndView("redirect:/page/login.html");
			}
			
			String userId = jwtInfo.getSubject();
			if (StringUtils.isEmpty(userId)) {
				return new ModelAndView("redirect:/page/login.html");
			}
			
			Page page = new Page();
			page.setParamObj("memId", userId);
			memService.inviteList(page);
			model.addAttribute("invitNum", page.getTotalRecord());
			
			MemInfo memInfo = memService.queryMemById(userId);
			model.addAttribute("user", memInfo);
			FlowDay flowDay = flowService.queryDayByMemId(userId);
			model.addAttribute("flowDay", flowDay);
			
			MemInfoConfig memInfoConfig = memInfoConfigService.getMemConfigAndAddByMemId(userId);
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("sr", "https://user.kuainiaojsq.xyz/path/sub/all/srvmess/" + memInfoConfig.getSubKey());
			result.put("general", "https://user.kuainiaojsq.xyz/path/sub/all/generalvmess/" + memInfoConfig.getSubKey());
			model.addAttribute("subInfo", result);
			
			//获取appsotre账号
			List<Map<String, String>> accList = envParamsService.getAppStoreAcc();
			model.addAttribute("accList", accList);
			
			//获取小火箭教程链接
			String swrLink = envParamsService.getValByKey(EnvKeyEnum.link_swr_help);
			model.addAttribute("swrLink", swrLink);
			
			return new ModelAndView("user/subscribe");
		} catch (Exception e) {
			log.error("toInvitFriendListPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	@GetMapping("/menu.html")
	@AuthClient()
	public ModelAndView tomenuPage(Model model) {
		try {
			JwtInfo jwtInfo = JwtLocal.getJwt();
			if (jwtInfo == null) {
				model.addAttribute("userName", "未登录");
				model.addAttribute("memType", "请登录");
				return new ModelAndView("user/menu");
			}
			
			String userId = jwtInfo.getSubject();
			if (StringUtils.isEmpty(userId)) {
				model.addAttribute("userName", "未登录");
				model.addAttribute("memType", "请登录");
				return new ModelAndView("user/menu");
			}
			
			MemInfo memInfo = memService.queryMemById(userId);
			
			model.addAttribute("userName", "ID:" + memInfo.getMemNo());
			model.addAttribute("memType", "00".equals(memInfo.getMemType()) ? "游客" : ("01".equals(memInfo.getMemType()) ? "普通用户" : "会员用户"));
			return new ModelAndView("user/menu");
		} catch (Exception e) {
			log.error("toInvitFriendListPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	/**
	 * 这个接口是app访问的，不加auth
	 * @param model
	 * @param uId
	 * @param sign
	 * @param time
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/invitfriend.html")
	@AuthClient()
	public ModelAndView toInvitFriendPage(Model model, String uId, String sign, String time, HttpServletRequest request, HttpServletResponse response) {
		try {
			//String payUrl = request.getRequestURL().toString();
			
			if (!StringUtils.isEmpty(uId)) {
				
				String token = (String) redisHandler.get(RedisKey.USER_TOKEN + uId);
				
				//token = StringUtils.isEmpty(token) ? "test" : token;
				// 创建一个 cookie对象
				
			    String domain = WebUtils.getBaseUrl(request);
				if (!"dev".equals(Env.getVal("spring.profiles.active"))){
					domain = domain.replaceAll("http:", "https:");
				}
			    //将cookie对象加入response响应
			    
				if (!StringUtils.isEmpty(token)) {
					 //将cookie对象加入response响应
				    CookieUtils.setCookie(request, response, "v-token", token, true);
				    CookieUtils.setCookie(request, response, "v-token-type", "bea_us", true);
				    CookieUtils.setCookie(request, response, "v-domain", domain, true);
				}
			}else {
				JwtInfo jwtInfo = JwtLocal.getJwt();
				if (jwtInfo == null) {
					return new ModelAndView("redirect:/page/login.html");
				}
				
				String userId = jwtInfo.getSubject();
				if (StringUtils.isEmpty(userId)) {
					return new ModelAndView("redirect:/page/login.html");
				}
				
				uId = userId;
			}
			
			MemInfo memInfo = memService.queryMemById(uId);
			String shareUrl = memService.shareOwnLink(uId);
			model.addAttribute("memInfo", memInfo);
			model.addAttribute("shareUrl", shareUrl);
			Page page = new Page();
			page.setParamObj("memId", uId);
			memService.inviteList(page);
			model.addAttribute("invitNum", page.getTotalRecord());
		    
			return new ModelAndView("user/invitfriend");
		} catch (Exception e) {
			log.error("toInvitFriendPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	/**
	 * app打开后 内置链接
	 * @param uId
	 * @return
	 */
	@GetMapping("/invitfriendlist.html")
	public ModelAndView toInvitFriendListPage() {
		try {
			return new ModelAndView("invitfriendlist");
		} catch (Exception e) {
			log.error("toInvitFriendListPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	@GetMapping("/linkcustomer.html")
	public ModelAndView toLinkCustomerPage(Model model, String mId, String sign, String time, HttpServletRequest request) {
		try {
			
			return new ModelAndView("linkcustomer");
		} catch (Exception e) {
			log.error("toCustomerPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	@GetMapping("/msgcenter.html")
	@AuthClient()
	public ModelAndView tomsgcenterPage(String uId, Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			
			if (!StringUtils.isEmpty(uId)) {
				String token = (String) redisHandler.get(RedisKey.USER_TOKEN + uId);
				
				//token = StringUtils.isEmpty(token) ? "test" : token;
			    String domain = WebUtils.getBaseUrl(request);
				if (!"dev".equals(Env.getVal("spring.profiles.active"))){
					domain = domain.replaceAll("http:", "https:");
				}
			    
				if (!StringUtils.isEmpty(token)) {
					 //将cookie对象加入response响应
				    CookieUtils.setCookie(request, response, "v-token", token, true);
				    CookieUtils.setCookie(request, response, "v-token-type", "bea_us", true);
				    CookieUtils.setCookie(request, response, "v-domain", domain, true);
				}
			}
			
		   
			return new ModelAndView("user/msgcenter");
		} catch (Exception e) {
			log.error("toCustomerPage, excep, e = {}", e);
			return new ModelAndView("error");
		}
	}
	
	@GetMapping("/paycenter.html")
	public ModelAndView toFastPayV2Page(Model model, String mId, String code, String sign, String time, HttpServletRequest request, HttpServletResponse response) {
		try {
			//String payUrl = request.getRequestURL().toString();
			if (!StringUtils.isEmpty(mId)) {
				
				String token = (String) redisHandler.get(RedisKey.USER_TOKEN + mId);
				
				//token = StringUtils.isEmpty(token) ? "test" : token;
			    String domain = WebUtils.getBaseUrl(request);
				if (!"dev".equals(Env.getVal("spring.profiles.active"))){
					domain = domain.replaceAll("http:", "https:");
				}
			    
			    //将cookie对象加入response响应
			    CookieUtils.setCookie(request, response, "v-token", token, true);
			    CookieUtils.setCookie(request, response, "v-token-type", "bea_us", true);
			    CookieUtils.setCookie(request, response, "v-domain", domain, true);
			    
			    if (StringUtils.isEmpty(token)) {
					log.error("no valid user, token is not exists");
					//return new ModelAndView(sysType + "/invalid");
					return new ModelAndView("user/pay");
				}
				
				model.addAttribute("mId", mId);
				model.addAttribute("token", "bea_us " + token);
			}else {
				
			}
			
			List<?> chargeList = memChargeService.selectChargeByMemType("01");
			model.addAttribute("chargeList", chargeList);
			
			return new ModelAndView("user/pay");
		} catch (Exception e) {
			log.error("toFastPayPage, excep, e = {}", e);
			return new ModelAndView("user/pay");
		}
	}
	
	
	@GetMapping("/paysuccess.html")
	@AuthClient()
	public ModelAndView paySuccess() {
		return new ModelAndView("user/pay_success");
	}

}
