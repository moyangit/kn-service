package com.tsn.serv.mem.service.mem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.utils.CommUtils;
import com.tsn.common.utils.utils.cons.FHCons;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.security.Base64Utils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.code.mem.MemCode;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.comm.EnableStatus;
import com.tsn.serv.common.enm.credits.convert.ConvertDurationEum;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.MemInvitorTypeEum;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.common.enm.order.OrderStatusEum;
import com.tsn.serv.common.enm.order.PayTypeEum;
import com.tsn.serv.common.enm.path.PathGrade;
import com.tsn.serv.common.entity.device.Device;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.common.mq.ChannelMsgProducter;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.common.utils.DeviceUtils;
import com.tsn.serv.common.utils.valid.ShareUtils;
import com.tsn.serv.mem.entity.account.MemAccount;
import com.tsn.serv.mem.entity.device.MemDeviceOline;
import com.tsn.serv.mem.entity.flow.FlowLimit;
import com.tsn.serv.mem.entity.mem.CDK;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.entity.mem.MemAccessInfo;
import com.tsn.serv.mem.entity.mem.MemCashoutInfo;
import com.tsn.serv.mem.entity.mem.MemExtInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.mem.MemInviterCode;
import com.tsn.serv.mem.entity.mem.NewUser;
import com.tsn.serv.mem.entity.order.ChargeOrder;
import com.tsn.serv.mem.entity.proxy.ProxyGroup;
import com.tsn.serv.mem.entity.proxy.ProxyInfo;
import com.tsn.serv.mem.mapper.account.MemAccountMapper;
import com.tsn.serv.mem.mapper.credits.CreditsTaskMapper;
import com.tsn.serv.mem.mapper.device.MemDeviceOlineMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.mapper.mem.MemInviterCodeMapper;
import com.tsn.serv.mem.mapper.mem.NewUserMapper;
import com.tsn.serv.mem.mapper.order.ChargeOrderMapper;
import com.tsn.serv.mem.mapper.proxy.ProxyGroupMapper;
import com.tsn.serv.mem.mapper.proxy.ProxyInfoMapper;
import com.tsn.serv.mem.mapper.source.MemSourceInviterMapper;
import com.tsn.serv.mem.service.channel.ChannelStatisService;
import com.tsn.serv.mem.service.credits.CreditsService;
import com.tsn.serv.mem.service.flow.FlowLimitService;
import com.tsn.serv.mem.service.order.ChargeOrderService;
import com.tsn.serv.mem.service.sys.ISysService;

@Service
public class MemService {
	
	@Autowired
	private MemInfoMapper memMapper;
	
	@Autowired
	public ISysService sysService;
	
	@Autowired
	public RedisHandler redisHandler;

	@Autowired
	public ProxyGroupMapper proxyGroupMapper;

	@Autowired
	public MemInviterCodeMapper memInviterCodeMapper;

	@Autowired
	public NewUserMapper newUserMapper;

	@Autowired
	public ProxyInfoMapper proxyInfoMapper;

	@Autowired
	public MemAccountMapper accountMapper;

	@Autowired
	public ChargeOrderService chargeOrderService;

	@Autowired
	public ChargeOrderMapper chargeOrderMapper;

	@Autowired
	public FlowLimitService flowLimitService;

	@Autowired
	private MemDeviceOlineMapper memDeviceOlineMapper;
	
	@Autowired
	private DurationRecordService durationRecordService;

	@Autowired
	private MemSourceInviterMapper memSourceInviterMapper;

	@Autowired
	private CreditsService creditsService;
	
	@Autowired
	private CreditsTaskMapper creditsTaskMapper;
	
	@Autowired
	private MemGuestInfoService memGuestInfoService;
	
	@Autowired
	private MemAccountMapper memAccountMapper;
	
	@Autowired
	private MemExtInfoService memExtInfoService;
	
	@Autowired
	private MemActiviService memActiviService;
	
	@Autowired
	private SerialNoService serialNoService;
	
	@Autowired
	private CDKService cdkService;
	
	@Autowired
	private ChannelStatisService channelStatisService;

	private SnowFlakeManager snowFlakeManager = SnowFlakeManager.build();

	//private static final String guest = "guest:memInfo:";
	
	public int getInvitationMemCount(String memId) {
		return memMapper.getInvitationMemCount(memId);
	}
	
	public MemInfo getMemInfoByInviCode(String inviCode) {
		return memMapper.selectMemByInviCode(inviCode);
	}
	
	public MemInfo queryMemByEmail(String email) {
		return memMapper.selectMemByEmail(email);
	}
	
	@Transactional
	public void move(String guestInviCode, String memId) {
		
		MemInfo inviMemInfo = memMapper.selectMemByInviCode(guestInviCode);
		
		if (inviMemInfo == null || !MemTypeEum.guest_mem.getCode().equals(inviMemInfo.getMemType())) {
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "cannot start this operation, inviCode no exists or is not guest user");
		}
		
		//如果是过期的，就不执行下面，防止把没有过期的mem，覆盖
		if (inviMemInfo.isExpire()) {
			throw new BusinessException(ResultCode.UNKNOW_ERROR, "guest is expire,can not move");
		}
		
		MemInfo memInfoTemp2 = memMapper.selectByPrimaryKey(memId);
		
		MemInfo memInfoTemp = new MemInfo();
		memInfoTemp.setMemId(memId);
		memInfoTemp.setMemType(MemTypeEum.general_mem.getCode());
		memInfoTemp.setSuspenDate(inviMemInfo.getSuspenDate());
		memInfoTemp.setChangeSuspenDate(memInfoTemp.getSuspenDate());
		memInfoTemp.setIsPay("1");
		//更新用户信息
		memMapper.updateByPrimaryKeySelective(memInfoTemp);
		
		MemInfo guestMemInfo = new MemInfo();
		guestMemInfo.setMemId(inviMemInfo.getMemId());
		guestMemInfo.setSuspenDate(inviMemInfo.getRegDate());
		guestMemInfo.setIsPay("0");
		memMapper.updateByPrimaryKeySelective(guestMemInfo);
		
		
		
		chargeOrderMapper.updateOrderGuest2Mem(inviMemInfo.getMemId(), memId, memInfoTemp2.getMemPhone(), memInfoTemp2.getMemNickName(),memInfoTemp2.getMemType());
		
	}
	
	public MemInfo getMemInfoByDeviceNo(String deviceNo) {
		return memMapper.getMemInfoByDeviceNo(deviceNo);
	}
	
	public MemInfo queryMemByPhone(String phone) {
		return memMapper.selectMemByPhone(phone);
	}
	
	public void updateMemInfoSelect(MemInfo memInfo) {
		memMapper.updateByPrimaryKey(memInfo);
	}
	
	public MemInfo queryMemCacheById(String id) {
		
		MemInfo memInfo = (MemInfo) redisHandler.get(RedisKey.USER_ID_USERINFO + id);
		
		if (memInfo == null) {
			MemInfo memInfoFromDB = memMapper.selectByPrimaryKey(id);
			
			MemExtInfo memExtInfo = memExtInfoService.getById(id);
			
			if (memExtInfo != null) {
				memInfoFromDB.setDeviceNo(memExtInfo.getDeviceNo());
			}
			
			//设置过期30分钟
			redisHandler.set(RedisKey.USER_ID_USERINFO + id, memInfoFromDB, 30 * 60);
			
			return memInfoFromDB;
		}
		
		return memInfo;
		
		
	}
	
	/**
	 * 添加防洪链接
	 * @param userId
	 * @return
	 */
	public String shareOwnLink(String userId) {
		
		String shareUrl = Env.getVal("mem.share.reg.url");
		
		//分享防洪链接配置
		String fhUrl = Env.getVal("mem.share.fh.url");
		fhUrl = StringUtils.isEmpty(fhUrl) ? "https://mapi.heibaohouduan.com/website/" : fhUrl;
		
		MemInfo memInfo = memMapper.selectByPrimaryKey(userId);
		
		if (memInfo == null) {
			String webUrls = Env.getVal("official.url.addrs");
			if (StringUtils.isEmpty(webUrls)) {
				return "";
			}
			return webUrls.split(",")[0];
		}
		
		String sign = ShareUtils.getSign(userId, memInfo.getInviterCode(), shareUrl);
		
		String signShareUrl = new StringBuffer(shareUrl).append("?").append(sign).toString();
		
		String r = Base64Utils.encodeToString(signShareUrl);
    	
    	String linkUrl = fhUrl + "/link?r=" + r + "&ivCode=" + memInfo.getInviterCode() + "&s=" + FHCons.sign(signShareUrl);
		
		//String linkCode = Long.toHexString(Long.valueOf(userId));
		
		//redisHandler.set(RedisKey.SHARE_LINK_CODE + linkCode, signShareUrl, 1 * 365 * 24 * 60 * 60);
		
		//return new StringBuffer(fhUrl).append("/link/share/").append(linkCode).toString();
    	return linkUrl;
		
		
		/*String linkCode = Long.toHexString(Long.valueOf(userId));
		
		redisHandler.set(RedisKey.SHARE_LINK_CODE + linkCode, signShareUrl, 1 * 365 * 24 * 60 * 60);
		
		return new StringBuffer(fhUrl).append("/link/share/").append(linkCode).toString();*/
	}
	
	public void updateMemCashInfo(String memId, MemCashoutInfo cashout) {
		MemInfo memInfo = new MemInfo();
		memInfo.setMemId(memId);
		memInfo.setMemCashDetail(JsonUtils.objectToJson(cashout));
		memMapper.updateByPrimaryKeySelective(memInfo);
	}
	
	public MemInfo queryMemById(String userId) {
		MemInfo memInfo = memMapper.selectByPrimaryKey(userId);
		return memInfo;
	}
	
	public MemInfo queryAllMemById(String userId) {
		MemInfo memInfo = memMapper.selectAllByPrimaryKey(userId);
		return memInfo;
	}
	
	public int updateMemInfo(MemInfo memInfo) {
		// 密码加密
		if (!StringUtils.isEmpty(memInfo.getMemPwd())) {
			String md5Password = DigestUtils.md5Hex(memInfo.getMemPwd().getBytes());
			memInfo.setMemPwd(md5Password);
		}
		
		return memMapper.updateByPrimaryKeySelective(memInfo);
	}
	
	public int updateSelMemInfo(MemInfo memInfo) {

		if (!StringUtils.isEmpty(memInfo.getParentInvCode())) {

			//先获取用户是否已经填写过邀请码
			MemInfo memInfoTemp = memMapper.selectByPrimaryKey(memInfo.getMemId());

			// 判断用户是否充值过，充值过就不允许填写邀请码了
	/*if (memInfoTemp.getLastRechargeDate() != null){
		throw new BusinessException(MemCode.MEM_IS_INVIT_CODE_ERROR, "mem is invit code error!");
	}*/

			//如果没有填写
			if (StringUtils.isEmpty(memInfoTemp.getInviterUserId())) {

				MemInfo inviMemInfo = memMapper.selectMemByInviCode(memInfo.getParentInvCode());

				if (inviMemInfo == null) {
					throw new BusinessException(MemCode.MEM_INVIT_CODE_NOT_EXISTS, "mem input invit code not exists!");
				}

				if (memInfo.getParentInvCode().equals(memInfoTemp.getInviterCode())) {
					throw new BusinessException(MemCode.MEM_INVIT_CODE_AVLID_ERROR, "mem invit code avlid error!");
				}

				if (inviMemInfo != null) {
					memInfo.setInviterUserId(inviMemInfo.getMemId());
					memInfo.setInviterUserType(MemInvitorTypeEum.mem.name());
					memInfo.setInviterTime(new Date());
				}
			}
			
		}
		
		int result = memMapper.updateByPrimaryKeySelective(memInfo);
		
		return result;
	}
	
	@Transactional
	public void updateMemInviCode(String userId, String invicode) {
		
		if (StringUtils.isEmpty(invicode)) {
			
			throw new RequestParamValidException("inviterCode can not be null");
			
		}

		//先获取用户是否已经填写过邀请码
		MemInfo memInfoTemp = memMapper.selectByPrimaryKey(userId);
		if (!StringUtils.isEmpty(memInfoTemp.getInviterUserId())) {
			throw new BusinessException(MemCode.MEM_REPEAT_INVIT_CODE_ERROR, "已经填写过邀请码!");
		}
		/*
		// 判断用户是否充值过，充值过就不允许填写邀请码了
		if (memInfoTemp.getLastRechargeDate() != null){
			throw new BusinessException(MemCode.MEM_IS_INVIT_CODE_ERROR, "mem is invit code error!");
		}*/

        MemInfo memInfo = new MemInfo();

		/*// 优先查询推广来源邀请码信息
		MemSourceInviter memSourceInviter = memSourceInviterMapper.selectByInviterCode(invicode);
		// 如果填写的是推广邀请码，则发放奖励
		if (memSourceInviter != null) {
			// 当奖励类型为时长时，发放时长
			if (memSourceInviter.getRewardType().equals("0")) {
				// 新增时长记录
				DurationRecord durationRecord = new DurationRecord();
				durationRecord.setMemId(userId);
				durationRecord.setConvertCardType(ConvertDurationEum.source.name());
				durationRecord.setConvertDuration(memSourceInviter.getRewardVal());
				durationRecord.setDurationSources(ConvertDurationEum.source.name());
				durationRecordService.insert(durationRecord);

                updateSuspenDateByMinute(userId, memSourceInviter.getRewardVal());
			} else {
				// 发放积分暂时不做操作
			}
			memInfo.setMemId(userId);
			memInfo.setInviterUserId(memSourceInviter.getId().toString());
			memInfo.setInviterUserType(MemInvitorTypeEum.source.name());
			memInfo.setInviterTime(new Date());

			memSourceInviter.setNum(memSourceInviter.getNum() + 1);
			memSourceInviterMapper.updateByPrimaryKeySelective(memSourceInviter);
		} else {

			MemInfo parentMemInfo = memMapper.selectMemByInviCode(invicode);

			if (parentMemInfo == null) {

				throw new BusinessException(MemCode.MEM_INVIT_CODE_NOT_EXISTS, "input invi code is not exists");

			}

			if (parentMemInfo.getMemId().equals(userId)) {

				throw new BusinessException(MemCode.MEM_INVIT_CODE_AVLID_ERROR, "mem invit code avlid error!");
			}
			
			//新增时长记录
			durationRecordService.addDurationOfInvite(parentMemInfo.getMemId());
			//修改邀请人使用的时间
            updateSuspenDateByMinute(parentMemInfo.getMemId(), Integer.valueOf(ConvertDurationEum.friend_invite.getCode()));

			durationRecordService.addDurationOfInvite(userId);
			//修改被邀请人的使用时间
            updateSuspenDateByMinute(userId, Integer.valueOf(ConvertDurationEum.friend_invite.getCode()));

			memInfo.setMemId(userId);
            memInfo.setInviterUserId(parentMemInfo.getMemId());
            memInfo.setInviterUserType(MemInvitorTypeEum.mem.name());
            memInfo.setInviterTime(new Date());
		}*/
        
        MemInfo parentMemInfo = memMapper.selectMemByInviCode(invicode);

		if (parentMemInfo == null) {

			throw new BusinessException(MemCode.MEM_INVIT_CODE_NOT_EXISTS, "input invi code is not exists");

		}

		if (parentMemInfo.getMemId().equals(userId)) {

			throw new BusinessException(MemCode.MEM_INVIT_CODE_AVLID_ERROR, "mem invit code avlid error!");
		}
		
		//新增时长记录
		//durationRecordService.addDurationOfInvite(parentMemInfo.getMemId());
		//修改邀请人使用的时间
        //updateSuspenDateByMinute(parentMemInfo.getMemId(), Integer.valueOf(ConvertDurationEum.friend_invite.getCode()));
        //memActiviService.inviteUpdateMemTimeByPeopleNum(parentMemInfo.getMemId());
		memActiviService.inviteUpdateMemTimeByRechargeUser(parentMemInfo.getMemId());
		
		durationRecordService.addDurationOfInvite(userId);
		//修改被邀请人的使用时间
        //updateSuspenDateByMinute(userId, Integer.valueOf(ConvertDurationEum.friend_invite.getCode()));

		memInfo.setMemId(userId);
        memInfo.setInviterUserId(parentMemInfo.getMemId());
        memInfo.setInviterUserType(MemInvitorTypeEum.mem.name());
        memInfo.setInviterTime(new Date());

        memMapper.updateByPrimaryKeySelective(memInfo);
	}
	
	public MemAccessInfo accessByUserId(String userId) {
		MemInfo memInfo = memMapper.selectByPrimaryKey(userId);
		
		if (memInfo == null) {
			return null;
		}
		
		MemAccessInfo accessInfo = new MemAccessInfo();
		accessInfo.setMemId(memInfo.getMemId());
		accessInfo.setMemStatus(memInfo.getStatus());
		accessInfo.setMemType(memInfo.getMemType());
		
		Map<String, Object> nodekey = getNodeKey();
		accessInfo.setNodeKey(String.valueOf(nodekey.get("id")));
		accessInfo.setAlterId((Integer)nodekey.get("alterId"));
		accessInfo.setEmail(String.valueOf(nodekey.get("email")));
		accessInfo.setInBoundTag(String.valueOf(nodekey.get("inBoundTag")));
		accessInfo.setLevel((Integer)(nodekey.get("level")));
		
		return accessInfo;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getNodeKey() {
		
		Response<?> res = sysService.getNodeKey();
		
		if (ResultCode.OPERATION_IS_SUCCESS.equals(res.getCode())) {
			return null;
		}
		
		Map<String, Object> v2Info = (Map<String, Object>) res.getData();
		
		return v2Info;
	}
	
	public int updateTime(MemInfo mem) {
		mem.setLastLoginTime(new Date());
		int result = memMapper.updateByPrimaryKeySelective(mem);
		return result;
	}
	
	@Transactional
	public int addMem(MemInfo memInfo) {
		
		int userAddTime = Integer.valueOf(ConvertDurationEum.reg.getCode());
		
		if (!StringUtils.isEmpty(memInfo.getParentInvCode())) {
			
			MemInfo inviMemInfo = memMapper.selectMemByInviCode(memInfo.getParentInvCode());
			
			//这个地方如果出现问题，不要影响用户注册， 放到了充值回调流程
			/*try {
				memActiviService.inviteUpdateMemTimeByPeopleNum(inviMemInfo.getMemId());
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			/*//新增时长记录
			durationRecordService.addDurationOfInvite(inviMemInfo.getMemId());
			//修改邀请人使用的时间
            updateSuspenDateByMinute(inviMemInfo.getMemId(), Integer.valueOf(ConvertDurationEum.friend_invite.getCode()));*/

            memInfo.setInviterUserId(inviMemInfo.getMemId());
            memInfo.setInviterUserType(MemInvitorTypeEum.mem.name());
            memInfo.setInviterTime(new Date());
			
            //userAddTime = Integer.valueOf(ConvertDurationEum.reg.getCode());
			
		}
		
		memInfo.setStatus(String.valueOf(EnableStatus.enable.getCode()));
		//默认男
		memInfo.setMemSex("0");
		
		//密码不是空
		if (!StringUtils.isEmpty(memInfo.getMemPwd())) {
			String md5Password = DigestUtils.md5Hex(memInfo.getMemPwd().getBytes());
			memInfo.setMemPwd(md5Password);
		}
		//默认连接设备数：5
		memInfo.setDeviceNum("5");
		memInfo.setIsPay("0");
		
		if (StringUtils.isEmpty(memInfo.getMemNickName())) {
			memInfo.setMemNickName(CommUtils.getStringRandom(5));
		}
		
		//这个地方获取vm等平台传递得clickId 和  exId，用于分析回调充值金额给vm 和 广告平台
		String yyyyMMdd = DateUtils.getCurrYMD("yyyyMMdd");
		Map<String, String> channelBehaviorData = channelStatisService.getAndSetChannelClickIdAndExId(memInfo.getChannelCode(), yyyyMMdd);
		String clickId = channelBehaviorData.get("cid");
		String exId = channelBehaviorData.get("eid");
		
		//获取设备详情信息
		String deviceAllInfo = memInfo.getDeviceNo();
		Device device = DeviceUtils.getDeviceInfo(deviceAllInfo);
		
		int res = 0;
		//如果是游客
		if (MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType())) {
			String inviterCode = synMethod();
			memInfo.setInviterCode(inviterCode);
			memInfo.setMemNo(serialNoService.getSerialNo());
			memInfo.setGuestRegDate(new Date());
			memInfo.setRegDate(memInfo.getGuestRegDate());
			memInfo.setMemType(MemTypeEum.guest_mem.getCode());
			//默认不是代理
			memInfo.setIsProxy("0");
			//默认1天
			memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(memInfo.getRegDate(), userAddTime));
			res = memMapper.insert(memInfo);
			//同时添加扩展表
			MemExtInfo memExtInfoTemp = new MemExtInfo();
			memExtInfoTemp.setId(memInfo.getMemId());
			memExtInfoTemp.setDeviceNo(device.getDeviceNo());
			memExtInfoTemp.setDeviceType(device.getDeviceType());
			memExtInfoTemp.setDeviceName(device.getDeviceName());
			memExtInfoTemp.setChannelClickId(clickId);
			memExtInfoTemp.setChannelExId(exId);
			memExtInfoService.save(memExtInfoTemp);
			
			//触发游客注册事件,统计总游客 按天统计
			
			if (res > 0) {
				EventMsgProducter.build().sendEventMsg(EventMsg.createGuestRegMsg(memInfo.getMemId(), memInfo.getRegDate()));
			}
			
		}else {//如果是正常注册用户
			memInfo.setMemType(MemTypeEum.trial_mem.getCode());
			
			if (StringUtils.isEmpty(memInfo.getMemName())) {
				memInfo.setMemName(memInfo.getMemNickName());
			}
			
			//如果是正常注册用户，根据设备号查询是否存在游客用户，，如果是游客，获取游客的MemId，进行更新
			MemInfo memInfoCache = memExtInfoService.getMemInfoByDeviceId(device.getDeviceNo());
			//如果根据device查询用户是游客，就更新
			if (memInfoCache != null && MemTypeEum.guest_mem.getCode().equals(memInfoCache.getMemType())) {
				//使用游客的Id
				memInfo.setMemId(memInfoCache.getMemId());
				memInfo.setRegDate(new Date());
				//不能更新过期时间
				if (memInfoCache.isExpire()) {
					Date currDate = new Date();
					memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(currDate, userAddTime));
				}else {
					Date date = DateUtils.getCurrDateAddMinTime(memInfoCache.getSuspenDate(), userAddTime);
					memInfo.setSuspenDate(date);
				}
				
				memMapper.updateByPrimaryKeySelective(memInfo);
				
				//同时清空缓存
				redisHandler.del(RedisKey.USER_DEVICE_NO_USERINFO + device.getDeviceNo());
				redisHandler.del(RedisKey.USER_ID_USERINFO + memInfo.getMemId());
				
			}else{
				String memId = SnowFlakeManager.build().create(GenIDEnum.MEM_ID.name()).getIdByPrefix(GenIDEnum.MEM_ID.getPreFix());
				memInfo.setMemId(memId);
				memInfo.setRegDate(new Date());
				String inviterCode = synMethod();
				memInfo.setInviterCode(inviterCode);
				memInfo.setMemNo(serialNoService.getSerialNo());
				//默认不是代理
				memInfo.setIsProxy("0");
				//默认1天
				memInfo.setSuspenDate(DateUtils.getCurrDateAddMinTime(memInfo.getRegDate(), userAddTime));
				res = memMapper.insert(memInfo);
				//同时添加扩展表
				MemExtInfo memExtInfoTemp = new MemExtInfo();
				memExtInfoTemp.setId(memInfo.getMemId());
				
				//如果存在其他会员或者使用用户
				if (memInfoCache != null && (MemTypeEum.trial_mem.getCode().equals(memInfoCache.getMemType()) || MemTypeEum.general_mem.getCode().equals(memInfoCache.getMemType()) )) {
					memExtInfoTemp.setDeviceNo(null);
				}else {
					memExtInfoTemp.setDeviceNo(device.getDeviceNo());
					memExtInfoTemp.setDeviceType(device.getDeviceType());
					memExtInfoTemp.setDeviceName(device.getDeviceName());
				}
				memExtInfoTemp.setChannelClickId(clickId);
				memExtInfoTemp.setChannelExId(exId);
				memExtInfoService.save(memExtInfoTemp);
			}
			
			if (res > 0) {
				//添加注册人数事件
				ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createRegPeopleCount(memInfo.getChannelCode(), memInfo.getMemId()));
				
			}
		}
				
		return res;
	}
	
	public void updateMemByPhone(MemInfo memInfo) {
		// 密码加密
		String md5Password = DigestUtils.md5Hex(memInfo.getMemPwd().getBytes());
		memInfo.setMemPwd(md5Password);
		memMapper.updateMemByPhone(memInfo);
	}

	public void userDataTableList(Page page) {
		List<Map<String,Object>> memInfoList = memMapper.getUserDataList(page);
		page.setData(memInfoList);
	}
	
	public void getUserList(Page page) {
		List<Map<String,Object>> memInfoList = memMapper.getUserList(page);
		page.setData(memInfoList);
	}

	public int updateMemInfoTo(MemInfo memInfo) {
		return memMapper.updateByPrimaryKeySelective(memInfo);
	}

	/**
	 * 组装树形table
	 * @param page
	 * @return
	 */
	public void getProxyTreeList(Page page){
		// 获取一级代理
		List<Map<String,Object>> proxyMemList = memMapper.getOneProxyMemList(page);
		List<Map<String, Object>> list = new ArrayList<>();
		proxyMemList.stream()
				.forEach(proxyMap -> {
					List<Map<String, Object>> listChildren;
					String id = proxyMap.get("memId").toString();
					// 根据ID计算总返利
					String sumMoney = memMapper.selectSumMoneyByMemId(id);
					proxyMap.put("sumMoney",(!StringUtils.isEmpty(sumMoney) ? new BigDecimal(sumMoney) : BigDecimal.ZERO));
					listChildren = proxyTreeLower(id);
					if (listChildren.size()>0) {
						proxyMap.put("children", listChildren);
					}
					// 为一级代理增加标识
					proxyMap.put("treeLevel", "1");
					list.add(proxyMap);
				});

		page.setData(list);
	}
	
	public void queryProxyPage(Page page){
		List<Map<String, Object>> result = memMapper.queryProxyAllPage(page);
		page.setData(result);
	}
	
	public void queryProxyAndInvitPage(Page page){
		List<Map<String,Object>> memInfoList = memMapper.getUserList(page);
		
		List<String> memIds = new ArrayList<String>();
		for (Map<String, Object> memInfo : memInfoList) {
			memIds.add(String.valueOf(memInfo.get("memId")));
		}
		
		Map<String, Object> paramObj = (Map<String, Object>) page.getParamObj();
		String currTime = String.valueOf(paramObj.get("currTime"));
		
		List<Map<String,Object>> memInvitProxyList = memMapper.queryMemInvitAndProxyListByIds(currTime, memIds);
		
		for (Map<String, Object> memInfo : memInfoList) {
			
			String memId = String.valueOf(memInfo.get("memId"));
			for (Map<String, Object> memInviProxyInfo : memInvitProxyList ) {
				
				String memIdTmp = String.valueOf(memInviProxyInfo.get("memId"));
				
				if (memId.equals(memIdTmp)) {
					memInfo.put("memName", memInviProxyInfo.get("memName"));
					memInfo.put("proxyCreateTime", memInviProxyInfo.get("proxyCreateTime"));
					memInfo.put("accountMoney", memInviProxyInfo.get("accountMoney"));
					memInfo.put("invitTotalNum", memInviProxyInfo.get("invitTotalNum"));
					memInfo.put("invitRechargeNum", memInviProxyInfo.get("invitRechargeNum"));
					memInfo.put("currInvitNum", memInviProxyInfo.get("currInvitNum"));
				}
				
			}
		}
		
		page.setData(memInfoList);
	}
	
	public void queryProxyAndInvitList(Page page){
		
		Map<String, Object> paramObj = (Map<String, Object>) page.getParamObj();
		String invitStartTime = String.valueOf(paramObj.get("invitStartTime"));
		String invitEndTime = String.valueOf(paramObj.get("invitEndTime"));
		String regTime = String.valueOf(paramObj.get("time"));
		String isProxy = String.valueOf(paramObj.get("isProxy"));
		String memType = String.valueOf(paramObj.get("memType"));
		String memPhone = String.valueOf(paramObj.get("memPhone"));
		
		List<Map<String,Object>> memInvitProxyList = memMapper.queryMemInvitAndProxyListByTime(invitStartTime, invitEndTime, regTime, memPhone, isProxy, memType);
		
		/*for (Map<String, Object> memInfo : memInfoList) {
			
			String memId = String.valueOf(memInfo.get("memId"));
			for (Map<String, Object> memInviProxyInfo : memInvitProxyList ) {
				
				String memIdTmp = String.valueOf(memInviProxyInfo.get("memId"));
				
				if (memId.equals(memIdTmp)) {
					memInfo.put("memName", memInviProxyInfo.get("memName"));
					memInfo.put("proxyCreateTime", memInviProxyInfo.get("proxyCreateTime"));
					memInfo.put("accountMoney", memInviProxyInfo.get("accountMoney"));
					memInfo.put("invitTotalNum", memInviProxyInfo.get("invitTotalNum"));
					memInfo.put("invitRechargeNum", memInviProxyInfo.get("invitRechargeNum"));
					memInfo.put("currInvitNum", memInviProxyInfo.get("currInvitNum"));
				}
				
			}
		}*/
		page.setTotalRecord(memInvitProxyList.size());
		page.setData(memInvitProxyList);
	}
	

	public List<Map<String, Object>> proxyTreeLower(String parentId){
		List<Map<String, Object>> list = new ArrayList<>();

		List firstIdList = new ArrayList();
		firstIdList.add(parentId);
		// 根据ID获取下级代理信息
		List<Map<String, Object>> memInfoList = memMapper.getSubordinateProxy(firstIdList);
		memInfoList.stream().forEach(item -> {
			Map<String, Object> map = new HashMap<>();
			List<Map<String, Object>> listChildren;
			String id = item.get("memId").toString();
			map.put("memId",id);
			map.put("memPhone",item.get("memPhone"));
			map.put("memNickName",item.get("memNickName"));
			map.put("lastRechargeDate",item.get("lastRechargeDate"));
			map.put("suspenDate",item.get("suspenDate"));
			map.put("accountMoney",item.get("accountMoney"));
			map.put("status",item.get("status"));
			// 根据ID计算总返利
			String sumMoney = memMapper.selectSumMoneyByMemId(id);
			map.put("sumMoney",(!StringUtils.isEmpty(sumMoney) ? new BigDecimal(sumMoney) : BigDecimal.ZERO));
			listChildren = proxyTreeLower(id);
			if (listChildren.size()>0) {
				map.put("children", listChildren);
			}
			list.add(map);
		});

		return list;
	}



	/**
	 * 根据会员ID获取所有下级会员
	 * @param memInfo
	 * @return
	 */
	public List userGrade(MemInfo memInfo){
		List<Map<String, Object>> list = new ArrayList<>();

		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> listChildren = new ArrayList<>();
		String id = memInfo.getMemId();
		String sumMoney = memMapper.selectSumMoneyByMemId(id);
		String typeName = "";
		if ("01".equals(memInfo.getMemType())){
			typeName = "试用会员";
		}else if("02".equals(memInfo.getMemType())){
			if ("0".equals(memInfo.getIsProxy())){
				typeName = "普通会员";
			}else if("1".equals(memInfo.getIsProxy())){
				typeName = "代理会员";
			}
		}
		map.put("phone",memInfo.getMemPhone());
		map.put("label",memInfo.getMemPhone() + "(" + memInfo.getMemNickName() + ")" + " - " + typeName + " - 累计返利：" + (!StringUtils.isEmpty(sumMoney) ? new BigDecimal(sumMoney) : BigDecimal.ZERO));
		listChildren = userGradeTo(id);
		if (listChildren.size()>0) {
			map.put("children", listChildren);
		}
		list.add(map);

		return list;
	}

	public List<Map<String, Object>> userGradeTo(String parentId){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

		List firstIdList = new ArrayList();
		firstIdList.add(parentId);
		// 根据ID获取下级会员信息
		List<MemInfo> memInfoList = memMapper.getSubordinateMember(firstIdList);
		memInfoList.stream().forEach(item -> {
			Map<String, Object> map = new HashMap<>();
			List<Map<String, Object>> listChildren;
			String id = item.getMemId();
			String sumMoney = memMapper.selectSumMoneyByMemId(id);
			String typeName = "";
			if ("01".equals(item.getMemType())){
				typeName = "试用会员";
			}else if("02".equals(item.getMemType())){
				if ("0".equals(item.getIsProxy())){
					typeName = "普通会员";
				}else if("1".equals(item.getIsProxy())){
					typeName = "代理会员";
				}
			}
			map.put("phone",item.getMemPhone());
			map.put("label",item.getMemPhone() + "(" + item.getMemNickName() + ")" + " - " + typeName + " - 累计返利：" + (!StringUtils.isEmpty(sumMoney) ? new BigDecimal(sumMoney) : BigDecimal.ZERO));
			listChildren = userGradeTo(id);
			if (listChildren.size()>0) {
				map.put("children", listChildren);
			}
			list.add(map);
		});

		return list;
	}

	public void proxyGroupList(Page page) {
		List<ProxyGroup> proxyGroupList = proxyGroupMapper.proxyGroupList(page);
		page.setData(proxyGroupList);
	}

	public int updateProxyGroup(ProxyGroup proxyGroup) {
		return proxyGroupMapper.updateByPrimaryKeySelective(proxyGroup);
	}


	/*
	* 单独将同步代码块提出来
	* 同时使用事务和锁无法保证同步
	* */
	public synchronized String synMethod(){
		return getInviterCode();
	}
	/**
	 * 获取邀请码
	 * @return
	 */
	public String getInviterCode(){
		// 获取一条邀请码信息
		MemInviterCode memInviterCode = memInviterCodeMapper.getInviterCode();
		String inviterCode = memInviterCode.getInviterCode();
		// 删除邀请码信息
		int delete = memInviterCodeMapper.delete(memInviterCode.getId());
		if (delete > 0){
			return inviterCode;
		}else {
			throw new BusinessException(MemCode.MEM_INVIT_CODE_NOT_EXISTS, "input invi code is not exists");
		}
	}

	public Response<?> bindingAccount(Map<String, String> newUserMap) {
		NewUser newUser = new NewUser();
		newUser.setNewAccount(newUserMap.get("newAccount"));
		newUser.setOldAccount(newUserMap.get("oldAccount"));
		newUser.setWxCode(newUserMap.get("wxCode"));

		// 查询新老用户绑定表，判断当前老用户邮箱或者手机号是否被绑定(一个老账户只允许被绑定一次)
		NewUser newUserTo = newUserMapper.selectNewUserByOldAccount(newUser.getOldAccount());
		if (newUserTo != null){
			return Response.retn("000001");
		}

		// 查询新老用户绑定表，判断当前新用户手机号是否被绑定(一个手机号只允许被绑定一次)
		NewUser newUserToTo = newUserMapper.selectNewUserByNewAccount(newUser.getNewAccount());
		if (newUserToTo != null){
			return Response.retn("000002");
		}

		// 查询会员表，当前需要绑定手机号是否被注册
		/*MemInfo memInfo = memMapper.selectMemByPhone(newUser.getNewAccount());
		if (memInfo != null){
			return Response.retn("000003");
		}*/

		newUser.setCreateDate(new Date());
		newUser.setStatus("0");
		newUserMapper.insertDynamic(newUser);
		return Response.ok();
	}

	public void newUserList(Page page) {
		List<NewUser> newUserList = newUserMapper.newUserList(page);
		page.setData(newUserList);
	}

	public int updateNewUser(NewUser newUser) {
		return newUserMapper.updateDynamic(newUser);
	}

	public int deleteNewUser(NewUser newUser) {
		return newUserMapper.delete(newUser.getId());
	}

	public MemInfo queryProxyByPhone(String phone) {
		return memMapper.queryProxyByPhone(phone);
	}

	public void updateInviter(MemInfo memInfo) {
		// 验证邀请码是否存在，不存在返回错误信息，存在则填入邀请人信息
		MemInfo inviMemInfo = memMapper.selectMemByInviCode(memInfo.getInviterCode());

		if (inviMemInfo == null) {
			throw new BusinessException(MemCode.MEM_INVIT_CODE_NOT_EXISTS, "mem input invit code not exists!");
		}

		// 获取被修改会员信息
		MemInfo memInfoTemp = memMapper.selectByPrimaryKey(memInfo.getMemId());

		if (memInfo.getInviterCode().equals(memInfoTemp.getInviterCode())) {
			throw new BusinessException(MemCode.MEM_INVIT_CODE_AVLID_ERROR, "mem invit code avlid error!");
		}

		if (inviMemInfo != null) {
			memInfoTemp.setInviterUserId(inviMemInfo.getMemId());
			memInfoTemp.setInviterUserType(MemInvitorTypeEum.mem.name());
			memInfoTemp.setInviterTime(new Date());
		}

		memMapper.updateByPrimaryKeySelective(memInfoTemp);
	}

	public List<Map> todayMemInfoList(Page page) {
		return memMapper.todayMemInfoList(page);
	}

	public Map<String,Object> memInfoQuery(String memPhone) {
		// 查询用户信息
		MemInfo memInfo = memMapper.selectMemByPhone(memPhone);
		if (memInfo == null) {
			return null;
		}
		// 查询用户代理信息
		ProxyInfo proxyInfo = proxyInfoMapper.selectByPrimaryKey(memInfo.getMemId());
		// 查询用户账户信息
		MemAccount memAccount = accountMapper.queryMemAccountByMemId(memInfo.getMemId());

		// 查询用户流量使用信息
		FlowLimit flowLimit = flowLimitService.getCurrMemFlow(memInfo.getMemId());

		Map<String,Object> respMap = new HashMap<>();
		memInfo.setMemPwd("");
		respMap.put("memInfo", memInfo);
		respMap.put("proxyInfo", proxyInfo);
		respMap.put("memAccount", memAccount);
		respMap.put("flowLimit", flowLimit);

		return respMap;
	}
	
	public MemInfo selectMemByInviCode(String inviCode) {
		return memMapper.selectMemByInviCode(inviCode);
	}
	
	public Map<String,Object> queryMemInfoByInviCode(String inviCode) {
		// 查询用户信息
		MemInfo memInfo = memMapper.selectMemByInviCode(inviCode);
		if (memInfo == null) {
			return null;
		}
		// 查询用户代理信息
		ProxyInfo proxyInfo = proxyInfoMapper.selectByPrimaryKey(memInfo.getMemId());
		// 查询用户账户信息
		MemAccount memAccount = accountMapper.queryMemAccountByMemId(memInfo.getMemId());

		// 查询用户流量使用信息
		FlowLimit flowLimit = flowLimitService.getCurrMemFlow(memInfo.getMemId());

		QueryWrapper<CDK> queryWapperTmp = new QueryWrapper<CDK>();
		queryWapperTmp.eq("user_id", memInfo.getMemId());
		//查询所有获取的sdk卡
		List<CDK> cdkList = cdkService.list(queryWapperTmp);
		
		Map<String,Object> respMap = new HashMap<>();
		if (cdkList !=null && !cdkList.isEmpty()) {
			//获取每类型对应获取的总数
			Map<String,Long> totalCdk=cdkList.stream().collect(Collectors.groupingBy(CDK::getCdkType,Collectors.counting()));
			
			Map<String,Map<Object, Long>> totalCdkTempMap = cdkList.stream().collect(Collectors.groupingBy(CDK::getCdkType,Collectors.groupingBy(b -> {
	            int status = b.getStatus();
	            return status == 1 ? "convert" : "unconvert";
	        }, Collectors.counting())));
			
			respMap.put("cdkInfo", totalCdk);
			respMap.put("cdkConvertInfo", totalCdkTempMap);
		}
		
		//Map<String,Object> respMap = new HashMap<>();
		memInfo.setMemPwd("");
		respMap.put("memInfo", memInfo);
		respMap.put("proxyInfo", proxyInfo);
		respMap.put("memAccount", memAccount);
		respMap.put("flowLimit", flowLimit);

		return respMap;
	}

	public void upMemOrder(JSONObject requestJson) {

		MemInfo memInfo = JsonUtils.jsonToPojo(JSON.toJSONString(requestJson.get("memInfo")), MemInfo.class);
		ChargeOrder chargeOrder = JsonUtils.jsonToPojo(JSON.toJSONString(requestJson.get("chargeOrder")), ChargeOrder.class);

		// 生成订单信息
		String orderNo = snowFlakeManager.create(GenIDEnum.BALANCE_ORDER_NO.name()).getIdByPrefix(GenIDEnum.BALANCE_ORDER_NO.getPreFix());
		chargeOrder.setOrderNo(orderNo);
		chargeOrder.setMemId(memInfo.getMemId());
		chargeOrder.setMemName(memInfo.getMemNickName());
		chargeOrder.setMemPhone(memInfo.getMemPhone());
		chargeOrder.setMemRealName(memInfo.getMemReallyName());
		chargeOrder.setMemType(memInfo.getMemType());
		// 获取用户修改前的到期日期
		MemInfo memInfoOld = memMapper.selectByPrimaryKey(memInfo.getMemId());
		if (memInfoOld == null) {
			throw new AuthException(MemCode.MEM_IS_NOT_EXISTS, "member is not exists");
		}
		chargeOrder.setMemBeforeSuspenDate(memInfoOld.getSuspenDate());
		// 系统充值：sys
		chargeOrder.setPayType(PayTypeEum.sys.name());
		// 订单状态:成功:pay_success
		chargeOrder.setOrderStatus(OrderStatusEum.pay_success.getStatus());
		chargeOrder.setCreateTime(new Date());
		chargeOrder.setUpdateTime(new Date());

		chargeOrderMapper.insert(chargeOrder);

		// 更新
		memMapper.updateByPrimaryKeySelective(memInfo);
	}

	public void updateDeviceNum(MemInfo memInfo) {
		// 只有一种情况下修改实时设备信息表
		MemInfo memInfoOld = memMapper.selectByPrimaryKey(memInfo.getMemId());
		// 当设备数小于修改之前的设备数时，直接修改
		if (Integer.parseInt(memInfo.getDeviceNum()) < Integer.parseInt(memInfoOld.getDeviceNum())){
			MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(memInfo.getMemId());
			List<Map> deviceList = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
			int size = deviceList.size() - Integer.parseInt(memInfo.getDeviceNum());
			for ( int i = 0; i < size; i++){
				deviceList.remove(i);
			}
			memDeviceOline.setMemId(memInfo.getMemId());
			memDeviceOline.setDeviceJson(JsonUtils.objectToJson(deviceList));
			memDeviceOline.setUpdateTime(new Date());
			memDeviceOlineMapper.updateDynamic(memDeviceOline);
		}

		memMapper.updateByPrimaryKeySelective(memInfo);
	}

	public void inviteList(Page page) {
		List<MemInfo> invitationRecord = memMapper.getInvitationRecord(page);
		page.setData(invitationRecord);
	}
	
	public void updateSuspenDateByMinute(String memId,int duration) {
		MemInfo memInfo = this.queryMemById(memId);
		
		MemInfo memInfoTmp = new MemInfo();
		memInfoTmp.setMemId(memId);
		
		if (memInfo.isExpire()) {
			memInfoTmp.setLastRechargeDate(new Date());
			memInfoTmp.setSuspenDate(DateUtils.getCurrDateAddMinTime(new Date(), duration));
		}else {
			memInfoTmp.setSuspenDate(DateUtils.getCurrDateAddMinTime(memInfo.getSuspenDate(), duration));
		}
		memMapper.updateByPrimaryKeySelective(memInfoTmp);
	}
	
	
	public static Date addDate(Date date,long minute) {
		long time = date.getTime(); // 得到指定日期的毫秒数
		minute = minute*60*1000; // 要加上的天数转换成毫秒数
		time+=minute; // 相加得到新的毫秒数
		return new Date(time); // 将毫秒数转换成日期
	}


	public void recentUseMem(Page page) {
		List<Map<String, Object>> memInfoList = memMapper.recentUseMem(page);
		page.setData(memInfoList);
	}

	/**
	 * 更新用户线路状态
	 * 1.游客 和注册前三天，充值的， 都是要让他们用最快的，不限速
	 * 2.不充值每天做任务的用垃圾的，不限速
	 * 3.充了值没到期但是还在做任务的，用最快的，不限速
	 * 4.充了值到期的还在做任务使用的，用垃圾的，不限速
	 */

	public String getMemPathGrade(String memId) {
		
		return PathGrade.lev_0.getGrade();
		
		// 默认路线为普通路线
		/*String pathGrade = PathGrade.lev_1.getGrade();
		boolean guest = isGuest(memId);
		
		if (!guest)  {
			
			// 当该用户为注册用户
			MemInfo memInfo = memMapper.selectByPrimaryKey(memId);
			
			if (memInfo == null) {
				throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user is not exsit");
			}
			// 根据用户充值到期日期判断 changeSuspenDate
			if (memInfo.getChangeSuspenDate() != null && memInfo.getChangeSuspenDate().getTime() > new Date().getTime()) {
				pathGrade = PathGrade.lev_0.getGrade();
			}
		}

		return pathGrade;*/
	}
	
	/**
		//L0 不限速(),L1 50M,L2 限速30M ,L3限速20M,L4限速10M,L5限速5M 没有默认50M，防止用户使用过慢
		this.limitConfigMap.put("L0", "393216000,393216000");
		this.limitConfigMap.put("L1", "6553600,6553600");
		this.limitConfigMap.put("L2", "3932160,3932160");
		this.limitConfigMap.put("L3", "2621440,2621440");
		this.limitConfigMap.put("L4", "1310720,1310720");
		this.limitConfigMap.put("L5", "655360,655360");
		this.limitConfigMap.put("other", "655360,655360");
	 * @param memId
	 * @return
	 */
	public String getLimitLvlByMemId(String memId) {
		//判断是游客还是快加速
		//用户类型，2是游客， 1是注册用户
		/*String userType = memId.substring(0, 1);
		
		//如果是会员
		if (GenIDEnum.MEM_ID.getPreFix().equals(userType)) {
			// 当该用户为注册用户
			MemInfo memInfo = memMapper.selectByPrimaryKey(memId);
			
			//如果是新游客
			if (MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType())) {
				Long refDate = memInfo.getRegDate().getTime();
				
				//如果3天已经过期，限速5M
				if ((new Date().getTime() - refDate) > 3 * 24 * 60 * 60 * 1000) {
					//限速5M
					return "L6";
				}else {
					return "L5";
				}
			}
			
			//如果过期
			if (memInfo.isExpire()) {
				//限速50M
				return "L5";
			}
			
			// 根据用户充值到期日期判断 changeSuspenDate， 充值用户，没过期，不限速
			if (memInfo.getChangeSuspenDate() != null && memInfo.getChangeSuspenDate().getTime() > new Date().getTime()) {
				return "L0";
			}
			
			
			
			
		} else if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType)) {
			//String key = RedisKey.GUEST_INFO + memId;
			//Map<Object, Object> guestMap = redisHandler.hGetMap(key);
			GuestInfo guestInfo = memGuestInfoService.getGuestById(memId);
			//Long refDate = (Long) guestMap.get("regDate");
			Long refDate = guestInfo.getRegDate().getTime();
			
			//如果3天已经过期，限速5M
			if ((new Date().getTime() - refDate) > 3 * 24 * 60 * 60 * 1000) {
				//限速5M
				return "L6";
			}
			
			//限速10M
			return "L5";
		}
		
		return "L6";*/
		
		return "L0";
	}
	
	public Date getRegTimeByMemId(String memId) {
		//判断是游客还是快加速
		//用户类型，2是游客， 1是注册用户
		String userType = memId.substring(0, 1);
		
		//如果是会员
		if (GenIDEnum.MEM_ID.getPreFix().equals(userType)) {
			// 当该用户为注册用户
			MemInfo memInfo = queryMemCacheById(memId);
			
			return memInfo.getRegDate();
			
		} else if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType)) {
			//String key = RedisKey.GUEST_INFO + memId;
			//Map<Object, Object> guestMap = redisHandler.hGetMap(key);
			GuestInfo guestInfo = memGuestInfoService.getCacheGuestById(memId);
			//Long refDate = (Long) guestMap.get("regDate");
			
			return guestInfo.getRegDate();
			
		}
		
		return null;
	}
	
	public boolean isGuest(String memId) {
		//判断是游客还是快加速
		//用户类型，2是游客， 1是注册用户
		String userType = memId.substring(0, 1);
		
		//如果是会员
		if (GenIDEnum.MEM_ID.getPreFix().equals(userType)) {
			
			MemInfo memInfo = queryMemById(memId);
			
			if (memInfo != null && MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType())) {
				return true;
			}

			return false;
			
		} else if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType)) {

			return true;
			
		}
		
		return false;
	}
	
	/**
	 * 只通过Id判断是否游客
	 * @param memId
	 * @return
	 */
	public boolean isGuestById(String memId) {
		//判断是游客还是快加速
		//用户类型，2是游客， 1是注册用户
		String userType = memId.substring(0, 1);
		
		//如果是会员
		if (GenIDEnum.MEM_ID.getPreFix().equals(userType)) {

			return false;
			
		} else if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType)) {

			return true;
			
		}
		
		return false;
	}
	
	/**
	 * 只通过类型判断是否游客
	 * @param memId
	 * @return
	 */
	public boolean isGuestByType(String memId) {
		
		MemInfo memInfo = queryMemById(memId);
		
		if (memInfo != null && MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取每日新增的用户数，以及活跃用户数
	 * @param month
	 * @return
	 */
	public List<Map<String, Object>> getUserRegStatisGroupByDay(String month) {
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultCache = (List<Map<String, Object>>) redisHandler.get(RedisKey.REG_EFFECT_MONTH + month);
		
		if (resultCache != null && !resultCache.isEmpty()) {
			return resultCache;
		}
		
		String yyyy = month.substring(0, 4);
		String mm = month.substring(4, 6);
		String formatMonth = yyyy + "-" + mm;
		int dayNum = DateUtils.getMaxDayNumByMonth(month);
		
		List<Map<String,Object>> useRegList = memMapper.getUserRegStatisGroupByDay(formatMonth);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (int i = 1; i <= dayNum; i++) {
			String dayStr =String.format("%02d", i);
			dayStr = yyyy + "-" + mm + "-" + dayStr;
			
			Map<String, Object> mapList = new HashMap<String, Object>();
			mapList.put("day", dayStr);
			mapList.put("total", "0");
			mapList.put("zxguestCount", "0");
			mapList.put("zxzcCount", "0");
			mapList.put("byqCount", "0");
			
			//获取今日游客总数
			long todayGuestCount = redisHandler.pfcount(RedisKey.GUEST_DAY_PF + dayStr.replace("-", ""));
			mapList.put("guestCount", String.valueOf(todayGuestCount));
			
			/*long count = redisHandler.pfcount(RedisKey.USER_EFFECTIVE_REG_COUNT + dayStr.replace("-", ""));
			mapList.put("eftotal", String.valueOf(count));*/
			long guestcount = redisHandler.pfcount(RedisKey.GUEST_EFFECTIVE_REG_COUNT + dayStr.replace("-", ""));
			mapList.put("efguestCount", String.valueOf(guestcount));
			long yqcount = redisHandler.pfcount(RedisKey.YQUSER_EFFECTIVE_REG_COUNT + dayStr.replace("-", ""));
			mapList.put("efbyqCount", String.valueOf(yqcount));
			long xzcount = redisHandler.pfcount(RedisKey.ZXUSER_EFFECTIVE_REG_COUNT + dayStr.replace("-", ""));
			mapList.put("efzxzcCount", String.valueOf(xzcount));
			
			mapList.put("eftotal", String.valueOf(yqcount + xzcount));
			
			boolean flag = false;
			for (Map<String, Object> orderMap : useRegList) {
				String day = String.valueOf(orderMap.get("day"));
				String total = String.valueOf(orderMap.get("total"));
				String zxguestCount = String.valueOf(orderMap.get("zxguestCount"));
				String zxzcCount = String.valueOf(orderMap.get("zxzcCount"));
				String byqCount = String.valueOf(orderMap.get("byqCount"));
				
				if (dayStr.equals(day)) {
					flag = true;
					mapList.put("total", total);
					mapList.put("zxguestCount", zxguestCount);
					mapList.put("zxzcCount", zxzcCount);
					mapList.put("byqCount", byqCount);
					result.add(mapList);
				} 
			}
			
			if (!flag) {
				result.add(mapList);
			}
			
		}
		
		redisHandler.set(RedisKey.REG_EFFECT_MONTH + month, result, 2 * 60);
		
		return result;
		
	}
	
}
