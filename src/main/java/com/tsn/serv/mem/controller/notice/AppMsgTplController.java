package com.tsn.serv.mem.controller.notice;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsn.common.db.mysql.page.PageResult;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.pojo.Response;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.mem.entity.notice.AppMsgTempl;
import com.tsn.serv.mem.entity.notice.param.AppMsgTplParam;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.notice.AppMsgTemplService;

@RestController
@RequestMapping("msg/tpl")
public class AppMsgTplController{
	
	@Autowired
	private AppMsgTemplService appMsgTemplService;
	
	@Autowired
	private MemService memService;
	
	@PostMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	@ApiOperation(value = "添加",notes = "测试")
	public Response<?> add(@RequestBody AppMsgTempl appMsgTempl) {
		appMsgTemplService.save(appMsgTempl);
		return Response.ok();
	}
	
	@PutMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	@ApiOperation(value = "添加",notes = "测试")
	public Response<?> update(@RequestBody AppMsgTempl appMsgTempl) {
		appMsgTemplService.updateById(appMsgTempl);
		return Response.ok();
	}
	
	@GetMapping("page/{page}/{rows}")
	@ApiOperation(value = "分页接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<PageResult<AppMsgTempl>> getPage(@PathVariable int page, @PathVariable int rows, AppMsgTplParam param) {
		
		//构建条件
	    QueryWrapper<AppMsgTempl> wrapper = new QueryWrapper<AppMsgTempl>();
	    
	    String msgType = param.getMsgType();
	    if (!StringUtils.isEmpty(msgType)){
	    	wrapper.eq("msg_type", msgType);
	    }
	    
	    Page<AppMsgTempl> pageObj = new Page<AppMsgTempl>(page, rows);
	    appMsgTemplService.page(pageObj, wrapper);
		
		return Response.ok(PageResult.build(pageObj));
	}
	
}
