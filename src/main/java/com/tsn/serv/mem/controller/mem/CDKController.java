package com.tsn.serv.mem.controller.mem;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsn.common.db.mysql.page.PageResult;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.pojo.Response;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.mem.entity.mem.CDK;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.mem.param.CDKParam;
import com.tsn.serv.mem.service.mem.CDKService;
import com.tsn.serv.mem.service.mem.MemService;

@RestController
@RequestMapping("cdk")
public class CDKController {
	
	@Autowired
	private CDKService cdkService;
	
	@Autowired
	private MemService memService;
	
	@PostMapping("/convert/{cdkCode}")
	@AuthClient(client = AuthEnum.bea_us)
	@ApiOperation(value = "兑换",notes = "测试")
	public Response<CDK> convert(@PathVariable String cdkCode) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		cdkService.convert(userId, cdkCode);
		return Response.ok();
	}
	
	@PostMapping("/convert")
	@AuthClient(client = AuthEnum.bea_us)
	@ApiOperation(value = "兑换",notes = "测试")
	public Response<CDK> convert1(String cdkCode) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		cdkService.convert(userId, cdkCode);
		return Response.ok();
	}
	
	@PostMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	@ApiOperation(value = "添加",notes = "测试")
	public Response<CDK> add(@RequestBody CDK cdk) {
		cdkService.saveByMemName(cdk);
		return Response.ok();
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除",notes = "测试")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<CDK> add(@PathVariable String id) {
		
		CDK cdk = cdkService.getById(id);
		
		if (1 == cdk.getStatus()) {
			
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "已经兑换 无法删除");
			
		}
		
		cdkService.removeById(id);
		return Response.ok();
	}
	
	@GetMapping("page/{page}/{rows}")
	@ApiOperation(value = "分页接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<PageResult<CDK>> getPage(@PathVariable int page, @PathVariable int rows, CDKParam param) {
		
		//构建条件
	    QueryWrapper<CDK> wrapper = new QueryWrapper<CDK>();

	    String yyyyMMdd = param.getYyyyMMdd();
	    if (!StringUtils.isEmpty(yyyyMMdd)){
	    	wrapper.le("create_time",yyyyMMdd + " 23:59:59");
	        wrapper.ge("create_time",yyyyMMdd + " 00:00:00");
	    }
	    
	    //获取条件是否为空
	    Integer status = param.getStatus();
	    if (!StringUtils.isEmpty(status)){
	        wrapper.eq("status",status);
	    }
	    
	    String cdkNo = param.getCdkNo();
	    if (!StringUtils.isEmpty(cdkNo)){
	    	wrapper.eq("cdk_no", cdkNo);
	    }
	    
	    String memName = param.getMemName();
	    if (!StringUtils.isEmpty(memName)){
	    	
	    	MemInfo memInfo = memService.queryMemByPhone(memName);
	    	
	    	if (memInfo == null) {
	    		
	    		memInfo = memService.selectMemByInviCode(memName);
	    		
	    	}
	    	
	    	wrapper.eq("user_id", memInfo == null ? null : memInfo.getMemId());
	    	
	    }
	    
	    
	    String convertMemName = param.getConvertMemName();
	    if (!StringUtils.isEmpty(convertMemName)){
	    	
	    	MemInfo memInfo = memService.queryMemByPhone(convertMemName);
	    	
	    	if (memInfo == null) {
	    		
	    		memInfo = memService.selectMemByInviCode(convertMemName);
	    		
	    	}
	    	
	    	wrapper.eq("convert_user_id", memInfo == null ? null : memInfo.getMemId());
	    	
	    }
	    
	    wrapper.orderByDesc("create_time");
	    
	    Page<CDK> pageObj = new Page<CDK>(page, rows);
	    cdkService.page(pageObj, wrapper);
		
		return Response.ok(PageResult.build(pageObj));
	}
	
	
	@GetMapping("/my/page/{page}/{rows}")
	@ApiOperation(value = "分页接口",notes = "测试")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<PageResult<CDK>> myCDK(@PathVariable int page, @PathVariable int rows, CDKParam param) {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		//构建条件
	    QueryWrapper<CDK> wrapper = new QueryWrapper<CDK>();
	    wrapper.eq("convert_user_id", userId);
	    wrapper.orderByDesc("create_time");
	    
	    Page<CDK> pageObj = new Page<CDK>(page, rows);
	    cdkService.page(pageObj, wrapper);
		
		return Response.ok(PageResult.build(pageObj));
	}

}
