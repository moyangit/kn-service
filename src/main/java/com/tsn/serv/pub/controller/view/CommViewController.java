package com.tsn.serv.pub.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.pub.entity.customer.ServiceCustomer;
import com.tsn.serv.pub.service.customer.ServCustomerService;

@RestController
@RequestMapping("page")
public class CommViewController {
	
	@Autowired
	private ServCustomerService servCustomerService;
	
	@GetMapping("/box.html")
    public ModelAndView toSharePayPage(Model model, String uId){
		
		model.addAttribute("uId", uId);
		
		EventMsgProducter.build().sendEventMsg(EventMsg.createClickBoxPeopleMsg(uId));
		
	    return new ModelAndView("box");
	}
	
	@GetMapping("/guide.html")
    public ModelAndView toQuidePayPage(Model model, String uId){
		
		//同时获取客服
		List<ServiceCustomer> customerList = servCustomerService.getServCustomerListByCusType(uId);
		
		model.addAttribute("customer", customerList.isEmpty() ? null : customerList.get(0));
		
		if (StringUtils.isEmpty(uId)) {
			model.addAttribute("uId", "");
		    return new ModelAndView("guide");
		}
				
		model.addAttribute("uId", uId);
	    return new ModelAndView("guide");
	}

}
