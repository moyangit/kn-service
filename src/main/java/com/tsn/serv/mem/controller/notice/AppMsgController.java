package com.tsn.serv.mem.controller.notice;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsn.common.db.mysql.page.PageResult;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.pojo.Response;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.notice.AppMsg;
import com.tsn.serv.mem.entity.notice.param.AppMsgParam;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.notice.AppMsgService;

@RestController
@RequestMapping("msg")
public class AppMsgController{
	
	@Autowired
	private AppMsgService appMsgService;
	
	@Autowired
	private MemService memService;
	
	@GetMapping("page/{page}/{rows}")
	@ApiOperation(value = "分页接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<PageResult<AppMsg>> getPage(@PathVariable int page, @PathVariable int rows, AppMsgParam param) {
		
		//构建条件
	    QueryWrapper<AppMsg> wrapper = new QueryWrapper<AppMsg>();
	    
	    String receiverPhone = param.getReceiverPhone();
	    if (!StringUtils.isEmpty(receiverPhone)){
	    	wrapper.eq("receiver_phone", receiverPhone);
	    }

	    //获取条件是否为空
	    Integer status = param.getStatus();
	    if (!StringUtils.isEmpty(status)){
	        wrapper.eq("status",status);
	    }
	    
	    String yyyyMMdd = param.getYyyyMMdd();
	    if (!StringUtils.isEmpty(yyyyMMdd)){
	    	wrapper.le("create_time",yyyyMMdd + " 23:59:59");
	        wrapper.ge("create_time",yyyyMMdd + " 00:00:00");
	    }
	    
	    String userId = param.getUserId();
	    if (!StringUtils.isEmpty(userId)){
	    	wrapper.eq("receiver_id", userId);
	    }
	    
	    String recevierUserPhone = param.getConvertUserPhone();
	    if (!StringUtils.isEmpty(receiverPhone)){
	    	MemInfo memInfo = memService.queryMemByPhone(recevierUserPhone);
	    	if (memInfo != null) {
	    		wrapper.eq("receiver_id", memInfo.getMemId());
	    	}
	    }
	    
	    wrapper.orderByDesc("create_time");
	    
	    Page<AppMsg> pageObj = new Page<AppMsg>(page, rows);
	    appMsgService.page(pageObj, wrapper);
		
		return Response.ok(PageResult.build(pageObj));
	}
	
	@GetMapping("my/list/{page}/{rows}")
	@ApiOperation(value = "分页接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<PageResult<AppMsg>> getMyPage(@PathVariable int page, @PathVariable int rows, AppMsgParam param) {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		//构建条件
	    QueryWrapper<AppMsg> wrapper = new QueryWrapper<AppMsg>();
	    wrapper.eq("receiver_id", userId);
	    wrapper.orderByDesc("create_time");
	    
	    Page<AppMsg> pageObj = new Page<AppMsg>(page, rows);
	    appMsgService.page(pageObj, wrapper);
		
		return Response.ok(PageResult.build(pageObj));
	}
	
	@GetMapping("sys/list/{page}/{rows}")
	@ApiOperation(value = "分页接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<PageResult<AppMsg>> getSysPage(@PathVariable int page, @PathVariable int rows, AppMsgParam param) {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		//构建条件
	    QueryWrapper<AppMsg> wrapper = new QueryWrapper<AppMsg>();
	    wrapper.eq("msg_type", "sys");
	    wrapper.orderByDesc("create_time");
	    
	    Page<AppMsg> pageObj = new Page<AppMsg>(page, rows);
	    appMsgService.page(pageObj, wrapper);
		
		return Response.ok(PageResult.build(pageObj));
	}
	

	@GetMapping("/{id}")
	@ApiOperation(value = "消息状态接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<AppMsg> getMsgById(@PathVariable String id) {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		AppMsg appMsg = new AppMsg();
		appMsg.setId(id);
		appMsg.setStatus(1);
		appMsgService.updateById(appMsg);
		
		
		return Response.ok(appMsgService.getById(id));
	}

	@GetMapping("status")
	@ApiOperation(value = "消息状态接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<Long> status() {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		//构建条件
	    QueryWrapper<AppMsg> wrapper = new QueryWrapper<AppMsg>();
	    wrapper.eq("receiver_id", userId);
	    wrapper.eq("status", 0);
	    long count = appMsgService.count(wrapper);
		
		return Response.ok(count);
	}
	
	/**
	 * 获取紧急消息
	 * @param id
	 * @return
	 */
	@GetMapping("/urgent")
	@ApiOperation(value = "消息状态接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<AppMsg> getMsgUrgent(@PathVariable String id) {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		QueryWrapper<AppMsg> queryWrapper = new QueryWrapper<AppMsg>();
		queryWrapper.eq("msg_type", "sys");
		queryWrapper.eq("urgent", 1);
		
		AppMsg appMsg = appMsgService.getOne(queryWrapper);
		
		return Response.ok(appMsg);
	}
	
}
