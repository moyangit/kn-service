package com.tsn.serv.pub.controller.complaint;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.pub.entity.complaint.Complaint;
import com.tsn.serv.pub.entity.complaint.ComplaintType;
import com.tsn.serv.pub.service.complaint.ComplaintService;

@RestController
@RequestMapping("complaint")
public class ComplaintController {

	@Autowired
	private ComplaintService complaintService;

	/**
	 * 获取所有投诉类型
	 * @return
	 */
	@GetMapping("typeList")
	public Response<?> getComplaintTypeList() {
		List<ComplaintType> complaintTypeList = complaintService.selectAllComplaintType();
		return Response.ok(complaintTypeList);
	}

	/**
	 * 客服投诉新增
	 * @param complaint
	 * @return
	 */
	@PostMapping("comp")
	public Response<?> addComplaint(@RequestBody Complaint complaint) {
		complaintService.addComplaint(complaint);
		return Response.ok();
	}

	/**
	 * 客服投诉分页查询
	 * @param page
	 * @return
	 */
	@GetMapping("/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getComplaintPage(Page page) {
		complaintService.getComplaintPage(page);
		return Response.ok(page);
	}

	/**
	 * 客服投诉修改
	 * @param complaint
	 * @return
	 */
	@PutMapping("/comp")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> upComplaint(@RequestBody Complaint complaint) {
		complaintService.upComplaint(complaint);
		return Response.ok();
	}

	/**
	 * 投诉类型分页查询
	 * @param page
	 * @return
	 */
	@GetMapping("/typePage")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getComplaintTypePage(Page page) {
		complaintService.getComplaintTypePage(page);
		return Response.ok(page);
	}

	/**
	 * 投诉类型新增
	 * @param complaintType
	 * @return
	 */
	@PostMapping("/type")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addComplaintType(@RequestBody ComplaintType complaintType) {
		complaintService.addComplaintType(complaintType);
		return Response.ok();
	}

	/**
	 * 投诉类型编辑
	 * @param complaintType
	 * @return
	 */
	@PutMapping("/type")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> upComplaintType(@RequestBody ComplaintType complaintType) {
		complaintService.upComplaintType(complaintType);
		return Response.ok();
	}
}
