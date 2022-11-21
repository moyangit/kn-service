package com.tsn.serv.pub.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.pub.service.mem.MemFeignService;

@Controller
@RequestMapping("page")
public class CustomerViewController {
	
	@Autowired
	private MemFeignService memFeignService;
	
/*	@GetMapping("redirect")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> redirect() {
		String userId = JwtLocal.getJwt().getSubject();
		String customerUrl = Env.getVal("customer_redirect_url");
		customerUrl = StringUtils.isEmpty(customerUrl) ? "https://mapi.heibaohouduan.com/website/page/service/customer.html" : customerUrl;
		//目前只会绑定一个，数组里面只会有一个
		return Response.ok(customerUrl + "?uId=" + userId);
	}*/
	
	/**
	 * 
	 * @param model
	 * @param linkCode 和你要跳转的目标地址进行绑定
	 * @return
	 */
	@GetMapping("/customer")
	public ModelAndView toFHPage(Model model, String uId){
		
		MemInfo memInfoTmp = new MemInfo();
		if (StringUtils.isEmpty(uId) || "null".equals(uId)) {
			model.addAttribute("user", memInfoTmp);
			return new ModelAndView("customer");
		}
		
		MemInfo memInfo = memFeignService.selectMemById(uId);
		
		if (memInfo == null) {
			memInfoTmp.setMemId(uId);
			model.addAttribute("user", memInfoTmp);
			return new ModelAndView("customer");
		}
		
		if (StringUtils.isEmpty(memInfo.getMemPhone())) {
			memInfo.setMemPhone("游客");
		}
		
		model.addAttribute("user", memInfo);
		
		return new ModelAndView("customer");
	}

}
