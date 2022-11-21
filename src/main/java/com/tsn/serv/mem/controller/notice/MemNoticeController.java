package com.tsn.serv.mem.controller.notice;

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

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.notice.MemNotice;
import com.tsn.serv.mem.service.notice.MemNoticeService;

@RestController
@RequestMapping("memNotice")
public class MemNoticeController {

	@Autowired
	private MemNoticeService memNoticeService;

	/**
	 * 安卓、IOS获取轮播公告信息
	 * @param type
	 * @return
	 */
	@GetMapping("type/{type}")
	public Response<?> getMemNoticeByType(@PathVariable String type){
		List<MemNotice> memNoticeList = memNoticeService.getMemNoticeByType(type);
		return Response.ok(memNoticeList);
	}

	// 列表分页展示
	@GetMapping("page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> memNoticeList(Page page){
		memNoticeService.memNoticeList(page);
		return Response.ok(page);
	}

	// 新增
	@PostMapping("/addMemNotice")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addMemNotice(@RequestBody MemNotice memNotice) {
		memNoticeService.addMemNotice(memNotice);
		return Response.ok();
	}

	// 修改
	@PutMapping("/updateMemNotice")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateMemNotice(@RequestBody MemNotice memNotice) {
		memNoticeService.updateMemNotice(memNotice);
		return Response.ok();
	}

	// 删除
	@DeleteMapping("/deleteMemNotice")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteMemNotice(@RequestBody List<MemNotice> memNoticeList) {
		memNoticeService.deleteMemNotice(memNoticeList);
		return Response.ok();
	}

}
