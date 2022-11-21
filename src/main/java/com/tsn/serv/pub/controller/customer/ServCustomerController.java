package com.tsn.serv.pub.controller.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.pub.entity.customer.ServiceCustomer;
import com.tsn.serv.pub.service.customer.ServCustomerService;

@RestController
@RequestMapping("customer")
public class ServCustomerController {
	
	@Autowired
	private ServCustomerService servCustomerService;
	
	@GetMapping("/redirect")
	public Response<?> getFeedbackPage() {
		//String cusServiceUrl = "http://chat.ahcdialogchat.com/chat/h5/chatLink.html?channelId=2O9dNy";
		
		String addr = Env.getVal("cus.serv.addr");
		
		if (!StringUtils.isEmpty(addr)) {
			return Response.ok(addr);
		}
		
		String cusServiceUrl = "https://chat.aiheconglink.com/chat/h5/chatLink.html?channelId=2O9dNy";
		
		return Response.ok(cusServiceUrl);
	}
	
	/**
	 * 获取客服联系方式
	 * @param page
	 * @return
	 */
	@GetMapping("contact")
	public Response<?> getComplaintPage() {
		String userId = JwtLocal.getJwt().getSubject();
		List<ServiceCustomer> customerList = servCustomerService.getServCustomerListByCusType(userId);
		//目前只会绑定一个，数组里面只会有一个
		return Response.ok(customerList);
	}
	
	@GetMapping("list")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getList(ServiceCustomer serviceCustomer) {
		List<ServiceCustomer> customerList = servCustomerService.getServCustomerList();
		return Response.ok(customerList);
	}
	
	@PostMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> add(@RequestBody ServiceCustomer serviceCustomer) {
		servCustomerService.addServCustomer(serviceCustomer);
		return Response.ok();
	}
	
	@PutMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> update(@RequestBody ServiceCustomer serviceCustomer) {
		servCustomerService.updateServCustomer(serviceCustomer);
		return Response.ok();
	}
	
	@DeleteMapping("/{id}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> update(@PathVariable long id) {
		servCustomerService.deleteServCustomerById(id);
		return Response.ok();
	}

}
