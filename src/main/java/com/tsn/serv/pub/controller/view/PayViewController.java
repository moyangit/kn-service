package com.tsn.serv.pub.controller.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.sign.SignatureUtil;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.common.cons.key.KeyCons;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.mem.entity.charge.MemCharge;
import com.tsn.serv.mem.service.charge.MemChargeService;
import com.tsn.serv.pub.service.mem.MemFeignService;

@RestController
@RequestMapping("page")
public class PayViewController {
	
	private static Logger log = LoggerFactory.getLogger(PayViewController.class);

	@Autowired
	private MemFeignService memFeignService;
	
	@Autowired
	private MemChargeService chargeService;
	
	@Autowired
	private RedisHandler redisHandler;

	/**
	 * return new ModelAndView("redirect:/users");
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/pay.html")
	public ModelAndView toFastPayPage(Model model, String mId, String code, String sign, String time, HttpServletRequest request) {
		try {
			//String payUrl = request.getRequestURL().toString();
			
			List<?> chargeList = memFeignService.getChargeList(mId);
			model.addAttribute("chargeList", chargeList);
			String reToken = memFeignService.getReToken();
			model.addAttribute("reToken", reToken);
			
			String apyAPi = Env.getVal("pay_api");
			model.addAttribute("payApi", StringUtils.isEmpty(apyAPi)? "https://cmem.api.heibaohouduan.com/mem_api/" : apyAPi);
			
			//String payUrl = request.getRequestURL().toString();
			/*JwtInfo jwtInfo = JwtLocal.getJwt();
			String payUrl = (String) jwtInfo.getExt("originPath");
			
			log.info("payURl : {} ", payUrl);
			
			if (payUrl.indexOf(":443") > -1) {
				payUrl = payUrl.replace(":443", "");
			}*/
			/*String payUrl = request.getRequestURL().toString();
			
			if (!"dev".equals(Env.getVal("spring.profiles.active"))){
				payUrl = payUrl.replaceAll("http:", "https:");
			}
			
			//如果是http先验证下，如果不行，再替换https验证一下
			boolean flag = signValidate(payUrl, mId, code, time, sign);
			
			if (!flag) {
				payUrl = payUrl.replaceAll("http:", "https:");
				log.error("sign valite error");
				boolean flagTmp = signValidate(payUrl, mId, code, time, sign);
				
				if (!flagTmp) {
					log.error("sign valite error");
					return new ModelAndView("pay");
				}
			}*/
			
			String token = (String) redisHandler.get(RedisKey.USER_TOKEN + mId);
			
			if (StringUtils.isEmpty(token)) {
				log.error("no valid user, token is not exists");
				//return new ModelAndView(sysType + "/invalid");
				return new ModelAndView("pay");
			}
			
			model.addAttribute("mId", mId);
			model.addAttribute("token", "bea_us " + token);
			
			return new ModelAndView("pay");
		} catch (Exception e) {
			log.error("toFastPayPage, excep, e = {}", e);
			return new ModelAndView("pay");
		}
	}
	
	@GetMapping("/pay_n.html")
	public ModelAndView toFastPayV2Page(Model model, String mId, String code, String sign, String time, HttpServletRequest request) {
		try {
			//String payUrl = request.getRequestURL().toString();
			
			List<?> chargeList = memFeignService.getChargeList(mId);
			model.addAttribute("chargeList", chargeList);
			String reToken = memFeignService.getReToken();
			model.addAttribute("reToken", reToken);
			
			String apyAPi = Env.getVal("pay_api");
			model.addAttribute("payApi", StringUtils.isEmpty(apyAPi)? "https://mapi.heibaohouduan.com/mem_api/" : apyAPi);
			
			String token = (String) redisHandler.get(RedisKey.USER_TOKEN + mId);
			
			if (StringUtils.isEmpty(token)) {
				log.error("no valid user, token is not exists");
				//return new ModelAndView(sysType + "/invalid");
				return new ModelAndView("pay_v2");
			}
			
			model.addAttribute("mId", mId);
			model.addAttribute("token", "bea_us " + token);
			
			return new ModelAndView("pay_v2");
		} catch (Exception e) {
			log.error("toFastPayPage, excep, e = {}", e);
			return new ModelAndView("pay_v2");
		}
	}
	
	@RequestMapping("pay_web.html")
	public ModelAndView toPay(Model model, HttpServletRequest request){
		
		String memId = null;
		if (StringUtils.isEmpty(memId)) {
			List<MemCharge> chargeList = chargeService.selectChargeByMemType(MemTypeEum.trial_mem.getCode());
			model.addAttribute("chargeList", chargeList);
		}else {
			/*List<Object> chargeList = memFeignService.getChargeByMemId(mId);
			model.addAttribute("chargeList", chargeList);*/
		}
		
		String reToken = memFeignService.getReToken();
		model.addAttribute("reToken", reToken);
		return new ModelAndView("pay_web");
	}
	
	private boolean signValidate(String payUrl, String mId, String code, String time, String sign) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("payUrl", payUrl);
		map.put("mId", mId);
		map.put("code", code);
		map.put("time", time);
		map.put("sign", sign);
		
		boolean flag = SignatureUtil.validateSign(map, KeyCons.PAY_SING_KEY);
		
		return flag;
	}
	
	/*@GetMapping("/xm/pay.html")
	public ModelAndView toXmPayPage(Model model, String mId, String code, String sign, String time, HttpServletRequest request) {
		try {
			
			String payUrl = request.getRequestURL().toString();
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("payUrl", payUrl);
			map.put("mId", mId);
			map.put("code", code);
			map.put("time", time);
			map.put("sign", sign);
			
			boolean flag = SignatureUtil.validateSign(map, "28376683914140677710");
			
			if (!flag) {
				log.error("sign valite error");
				return new ModelAndView("xm/error");
			}
			
			String token = (String) redisHandler.get("xm:user:token:" + mId);
			
			if (StringUtils.isEmpty(token)) {
				log.error("no valid user, token is not exists");
				return new ModelAndView("xm/error");
			}
			
			List<?> chargeList = chargeService.getXmChargeList(mId);
			model.addAttribute("chargeList", chargeList);
			model.addAttribute("mId", "mId");
			model.addAttribute("token", "bea_us " + token);
			//model.addAttribute("pk", "pk_test_51IVCxbIr3XyPoDgh2yCY233KmSdOr9u22eBZrchqKB2dP70PJcq0mN15LUS4n930J6pHSw1e14kR2HBOFYexyMAZ00UcUz3vhK");
			model.addAttribute("payApi", "http://192.168.0.105:2100/fast_api/order/charge");
			
			String reToken = chargeService.getXmReToken();
			model.addAttribute("reToken", reToken);

			return new ModelAndView("xm/pay");
		} catch (Exception e) {
			log.error("toXmPayPage, excep, e = {}", e);
			return new ModelAndView("xm/error");
		}
	}*/
	
	@GetMapping("/{sysType}/cancel.html")
	public ModelAndView toCancelPage(@PathVariable String sysType) {
		return new ModelAndView(sysType + "/cancel");
	}
	
	@GetMapping("/{sysType}/success.html")
	public ModelAndView toSuccessPage(@PathVariable String sysType) {
		return new ModelAndView(sysType + "/success");
	}
	
	@GetMapping("/{sysType}/invalid.html")
	public ModelAndView toInvalidPage(@PathVariable String sysType) {
		return new ModelAndView(sysType + "/invalid");
	}
	
	@GetMapping("/{sysType}/alipay.html")
	public ModelAndView toAlipayPage(@PathVariable String sysType, String qrcode, String orderNo, Model model) {
		model.addAttribute("qrcode", qrcode);
		model.addAttribute("orderNo", orderNo);
		return new ModelAndView(sysType + "/alipay");
	}

}
