package com.tsn.serv.mem.controller.credits;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.mem.entity.credits.CreditsRecord;
import com.tsn.serv.mem.entity.credits.CreditsRecordStatistics;
import com.tsn.serv.mem.service.credits.CreditsRecordService;

/**
 * 积分记录
 * @author work
 *
 */
@RestController
@RequestMapping("credits/record")
public class CreditsRecordCotroller {
	
	@Autowired
	private CreditsRecordService creditsRecordService;
	
	/**
	 * 积分记录获取
	 * @return
	 */
	@GetMapping("list")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> selectCreditsRecordByPage(Page page){
		JwtInfo info = JwtLocal.getJwt();
		String userId = info.getSubject();
		page.setParamObj("memId", userId);
		creditsRecordService.selectCreditsRecordByPage(page);
		return Response.ok(page);
	}
	
	/**
	 * 积分记录获取
	 * @return
	 */
	@GetMapping("")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> selectCreditsRecord(CreditsRecord creditsRecord){
		List<CreditsRecord> creditsRecords = creditsRecordService.selectCreditsRecordByEntity(creditsRecord);
		return Response.ok(creditsRecords);
	}
	
	/**
	 * 积分统计
	 * @return
	 */
	@GetMapping("statistics")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> selectCreditsStatistics(String createTime){
		
		JwtInfo info = JwtLocal.getJwt();
		
		String userId = info.getSubject();
		
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		CreditsRecordStatistics map = creditsRecordService.selectCreditsStatistics(createTime,userId);
		return Response.ok(map);
	}

}
