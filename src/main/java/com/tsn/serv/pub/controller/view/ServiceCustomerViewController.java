package com.tsn.serv.pub.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.serv.pub.entity.customer.ServiceCustomer;
import com.tsn.serv.pub.service.customer.ServCustomerService;

@RestController
@RequestMapping("page")
public class ServiceCustomerViewController {
	
	@Autowired
	private ServCustomerService servCustomerService;
	
	@GetMapping("/service/customer.html")
	public ModelAndView toServiceCustomerPage(Model model, String uId) {
		
		List<ServiceCustomer> customerList = servCustomerService.getServCustomerListByCusType(uId);
		
		model.addAttribute("customerList", customerList);
		
		return new ModelAndView("customer");
	}

}
