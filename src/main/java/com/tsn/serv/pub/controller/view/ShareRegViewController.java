package com.tsn.serv.pub.controller.view;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.common.utils.web.utils.env.Env;

@RestController
@RequestMapping("page")
public class ShareRegViewController {
	
	//private static Logger log = LoggerFactory.getLogger(PayViewController.class);
	
	@GetMapping("/share/reg.html")
    public ModelAndView toSharePayPage(Model model, String uId, String ivCode, String ctime, String sign, HttpServletRequest request){
		
		//String payUrl = request.getRequestURL().toString();
		/*JwtInfo jwtInfo = JwtLocal.getJwt();
		String requestURl = (String) jwtInfo.getExt("originPath");
		requestURl = requestURl.replaceAll("http:", "https:");
		requestURl = requestURl.replaceAll(":443", "");
		log.info("toSharePayPage: {}", requestURl);*/
		//String requestURl = request.getRequestURL().toString();
		//String requestURl = request.getRequestURI();
		/*Map<String, String> validParams = new HashMap<String, String>();
		validParams.put("uId", uId);
		validParams.put("ivCode",ivCode);
		validParams.put("ctime", ctime);
		validParams.put("url", requestURl);*/
		//String newSign = SignatureUtil.generateSign(validParams, "share_vip@xx.com");
		//String reqSign = params.get("sign");
		/*if (!newSign.equals(sign)) {
			return new ModelAndView("error");
		}*/
		
		String smsApi = Env.getVal("submit_mem_api");
		model.addAttribute("smsApi", StringUtils.isEmpty(smsApi)? "https://mapi.heibaohouduan.com/auth_api/" : smsApi);
	    return new ModelAndView("shareReg", "ivCode", ivCode);
	}

}
