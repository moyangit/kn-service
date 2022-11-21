package com.tsn.serv.mem.service.mem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.AuthCode;
import com.tsn.serv.common.code.cdk.CDKCode;
import com.tsn.serv.common.utils.CDKRuleUtils;
import com.tsn.serv.mem.entity.mem.CDK;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.notice.AppMsg;
import com.tsn.serv.mem.mapper.mem.CDKMapper;
import com.tsn.serv.mem.service.notice.AppMsgService;
import com.tsn.serv.mem.service.notice.AppMsgTemplService;

@Service
public class CDKService extends ServiceImpl<CDKMapper, CDK>{
	
	@Autowired
	private MemService memService;
	
	private int codeLength = 6;
	
	@Autowired
	private AppMsgService appMsgService;
	
	@Autowired
	private DurationRecordService durationRecordService;
	
	@Autowired
	private AppMsgTemplService appMsgTemplService;
	
	private Map<String, Integer> CDKMap = new HashMap<String, Integer>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			
			put("day", 24 * 60);
			put("week", 7*24*60);
			put("month", 30*24*60);
			
		}
	};
	
	private Map<String, Integer> CDKCountMap = new HashMap<String, Integer>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			
			put("day", 10);
			put("week", 1);
			put("month", 1);
			
		}
	};
	
	@Transactional
	public void convert(String convertUserId, String cdkCode) {
		
		QueryWrapper<CDK> queryWapper = new QueryWrapper<CDK>();
		queryWapper.eq("cdk_no", cdkCode);
		CDK cdk = super.getOne(queryWapper);
		if (cdk == null) {
			throw new BusinessException(CDKCode.CDK_NOT_ISEXISTS_ERROR, "cdk no exits");
		}
		
		if (1 == cdk.getStatus()) {
			throw new BusinessException(CDKCode.CDK_USED_ERROR, "beyand limit");
		}
		
		String cdkType = cdk.getCdkType();
		//通过兑换人查询所有兑换的信息，包括日，月，年
		QueryWrapper<CDK> queryWapperTmp = new QueryWrapper<CDK>();
		queryWapperTmp.eq("convert_user_id", convertUserId);
		queryWapperTmp.eq("cdk_type", cdkType);
		long count = super.count(queryWapperTmp);
		
		if (count == CDKCountMap.get(cdkType)) {
			throw new BusinessException(CDKCode.BEYAND_LIMIT_ERROR, "beyand limit");
		}
		
		MemInfo memInfo = memService.queryMemById(convertUserId);
		
		if (memInfo == null) {
			throw new BusinessException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user no exists");
		}
		
		CDK cdkTmp = new CDK();
		//cdkTmp.setId(cdk.getId());
		cdkTmp.setConvertUserId(convertUserId);
		cdkTmp.setConvertUserName(memInfo == null || StringUtils.isEmpty(memInfo.getMemPhone()) ? memInfo.getInviterCode() : memInfo.getMemPhone());
		cdkTmp.setStatus(1);
		UpdateWrapper<CDK> updateWrapper = new UpdateWrapper<CDK>();
		updateWrapper.eq("id", cdk.getId());
		updateWrapper.eq("status", 0);
		boolean res = super.update(cdkTmp, updateWrapper);
		
		if (!res) {
			throw new BusinessException(CDKCode.CDK_USED_ERROR, "cdk always be used");
		}
		
		durationRecordService.addDurationCDK(convertUserId, cdkCode, CDKMap.get(cdkType));
	}
	
	/**
	 * 
	 * @param userId
	 * @param cdkType 类型
	 * @param cdkNum 个数
	 * @param duration 时长
	 * @return
	 */
	@Transactional
	public void insertCDK(String userId, String cdkType, Integer cdkNum, String sourceType) {
		
		if (cdkNum == null) {
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "cdkNum is null");
		}
		
		MemInfo memInfo = memService.queryMemCacheById(userId);
		
		Date regDate = memInfo.getRegDate();
		String id = userId.substring(userId.length() - 3);
		int actId = Integer.valueOf(id + DateUtils.getCurrYMD(regDate, "hhmmss"));
		
		Set<String> cdkCodeList = CDKRuleUtils.generateCodes(null, cdkNum, codeLength, actId);
		
		List<CDK> cdkList = new ArrayList<CDK>();
		
		int duration = CDKMap.get(cdkType);
		
		for (String cdkCode : cdkCodeList) {
			CDK cdk = new CDK();
			cdk.setCdkNo(cdkCode);
			cdk.setCdkType(cdkType);
			cdk.setDuration(duration);
			cdk.setUserId(userId);
			cdk.setUserName(StringUtils.isEmpty(memInfo.getMemPhone()) ? memInfo.getInviterCode() : memInfo.getMemPhone());
			cdk.setStatus(0);
			cdk.setSourceType(sourceType);
			cdkList.add(cdk);
		}
		
		super.saveBatch(cdkList);
		
		for (CDK cdk : cdkList) {
			cdk.setUserId(null);
			cdk.setUserName(null);
		}
		
		AppMsg appMsg = new AppMsg();
		appMsg.setMsgType("CDK");
		//获取标题模板
		String title = appMsgTemplService.getCDKMsgTitleTpl(appMsg.getMsgType(), cdkList.size());
		appMsg.setMsgTitle(title);
		appMsg.setMsgContent(JsonUtils.objectToJson(cdkList));
		appMsg.setReceiverId(userId);
		appMsg.setReceiverName(memInfo.getMemName());
		appMsg.setReceiverPhone(memInfo.getMemPhone());
		appMsg.setSenderId("system");
		appMsg.setSenderName("系统");
		appMsgService.save(appMsg);
		
	}
	
	@Transactional
	public void saveByMemName(CDK cdk) {
		
		if (StringUtils.isEmpty(cdk.getUserName())) {
			
			throw new BusinessException(AuthCode.AUTH_PHONE_CODE_NOT_EMPTY_ERROR, "phone can not be empty");
		}
		
		MemInfo memInfo = memService.queryMemByPhone(cdk.getUserName());
		
		if (memInfo == null) {
			
			if (memInfo == null) {
	    		
	    		memInfo = memService.selectMemByInviCode(cdk.getUserName());
	    		
	    	}
			
			if (memInfo == null) {
				throw new BusinessException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user is no exists");
			}

		}
		
		Date regDate = memInfo.getRegDate();
		String id = memInfo.getMemId().substring(memInfo.getMemId().length() - 3);
		int actId = Integer.valueOf(id + DateUtils.getCurrYMD(regDate, "hhmmss"));
		
		Set<String> cdkCodeList = CDKRuleUtils.generateCodes(null, 1, codeLength, actId);
		for (String code : cdkCodeList) {
			cdk.setCdkNo(code);
			cdk.setUserId(memInfo.getMemId());
		}
		
		if (StringUtils.isEmpty(cdk.getCdkNo())) {
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "cdk.getCdkNo() is null");
		}
		
		super.save(cdk);
		
		if (1 == cdk.getMsg()) {
			
			AppMsg appMsg = new AppMsg();
			appMsg.setMsgType("CDK");
			//获取标题模板
			String title = appMsgTemplService.getCDKMsgTitleTpl(appMsg.getMsgType(), 1);
			appMsg.setMsgTitle(title);
			List<CDK> cdkList = new ArrayList<CDK>();
			cdkList.add(cdk);
			appMsg.setMsgContent(JsonUtils.objectToJson(cdkList));
			appMsg.setReceiverId(memInfo.getMemId());
			appMsg.setReceiverName(memInfo.getMemName());
			appMsg.setReceiverPhone(memInfo.getMemPhone());
			
			JwtInfo jwtInfo = JwtLocal.getJwt();
			appMsg.setSenderId(jwtInfo.getSubject());
			appMsg.setSenderName(jwtInfo.getSubName());
			appMsgService.save(appMsg);
		}
	}

}
