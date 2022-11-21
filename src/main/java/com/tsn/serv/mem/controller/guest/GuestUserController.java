package com.tsn.serv.mem.controller.guest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.AuthCode;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.credits.TaskExceptionCode;
import com.tsn.serv.common.code.mem.MemCode;
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.mem.DeviceTypeEum;
import com.tsn.serv.common.entity.device.Device;
import com.tsn.serv.common.utils.DeviceUtils;
import com.tsn.serv.mem.entity.credits.CreditsTaskOrder;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.service.credits.CreditsTaskOrderService;
import com.tsn.serv.mem.service.mem.MemGuestInfoService;

/**
 * 游客
 * @author work
 *
 */
@RestController
@RequestMapping("guest")
public class GuestUserController {

	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private CreditsTaskOrderService creditsTaskOrderService;
	
	@Autowired
	private MemGuestInfoService memGuestInfoService;
	
	private static Logger logger = LoggerFactory.getLogger(GuestUserController.class);
	
	private static final String SALT = "db908c3f";
	
	@PostMapping(ApiCons.PRIVATE_PATH)
	public Response<?> addGuest(@RequestBody GuestInfo guestInfo){
		memGuestInfoService.insertGuestInfo(guestInfo);
		return Response.ok();
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/{deviceNo}")
	public Response<?> getGuest(@PathVariable String deviceNo){
		
		Device device = DeviceUtils.getDeviceInfo(deviceNo);
		
		GuestInfo guestInfo = memGuestInfoService.getGuestByDeviceNo(device.getDeviceNo());
		return Response.ok(guestInfo);
	}
	
	/**
	 * 获取使用时间
	 * @param deviceNo
	 * @return
	 */
	@GetMapping("{memId}")
	public Response<?> getGuestUseTime(@PathParam("memId") String memId){
		GuestInfo guestInfo = memGuestInfoService.getGuestById(memId);
		
		if(guestInfo == null) {
			throw new BusinessException(MemCode.MEM_DEVICE_IS_NOT_EXISTS, "deviceNo nothingness");
		}
		
		return Response.ok(guestInfo);
	}
	
	/**
	 * 免费使用获取15分钟时长
	 * @return
	 */
	@PostMapping("continue/{sign}")
	@AuthClient(client = {AuthEnum.bea_us,AuthEnum.guest_us})
	public Response<?> guestUseContinueSpeedUp(@RequestBody CreditsTaskOrder creditsTaskOrder, @PathVariable("sign") String sign){
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
 		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		if(!userId.equals(creditsTaskOrder.getMemId())) {
			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "jwtinfo no exist user info");
		}
		
		String ciphertext = creditsTaskOrder.getMemId()+creditsTaskOrder.getOrderNo() + SALT;
		ciphertext = MD5Utils.digest(ciphertext);
		
		if(!ciphertext.equals(sign)) {
			throw new AuthException(TaskExceptionCode.SIGN_VALID_ERROR, "sign valid error");
		}
		
		//1.根据任务ID查出任务
		CreditsTaskOrder taskOrder = creditsTaskOrderService.selectCreditsTaskOrderByOrderNo(creditsTaskOrder.getOrderNo());
		if(taskOrder == null || taskOrder.getTaskStatus() == 1) {
			throw new BusinessException(TaskExceptionCode.TASK_NOTHINGNESS, "任务不存在或者已完成");
		}
		
		String tokenType = jwtInfo.getTokenType();
		
		creditsTaskOrderService.updateStimulateToReward(creditsTaskOrder,sign,tokenType,userId);
		
		
		return Response.ok();
	}
	
	
	/**
	 * 游客总人数
	 * @return
	 */
	@GetMapping("totelNum")
	public Response<?> selectGuestTotelNum(){
		
		Map<String, Object> map = new HashMap<String, Object>();
		Set<String> keys = redisHandler.keys(RedisKey.GUEST_KEY+"*");
		int guestTotelNum = keys.size();
		map.put("total", guestTotelNum);
		
		int adNum = 0;
		int iosNum = 0;
		int winNum = 0;
		int macNum = 0;
		
		Iterator<String> it = keys.iterator() ; 
		while(it.hasNext()){  
			String deviceNo = it.next().split(":")[3];
			
			Device device = DeviceUtils.getDeviceInfo(deviceNo);
			
			if(DeviceTypeEum.ad.getCode().equals(device.getDeviceType())) {
				adNum = adNum + 1;
			}else if(DeviceTypeEum.ios.getCode().equals(device.getDeviceType())) {
				iosNum = iosNum + 1;
			}else if(DeviceTypeEum.win.getCode().equals(device.getDeviceType())) {
				winNum = winNum + 1;
			}else if(DeviceTypeEum.mac.getCode().equals(device.getDeviceType())) {
				macNum = macNum + 1;
			}
        }
		
		map.put("Android", adNum);
		map.put("IOS", iosNum);
		map.put("PC", winNum);
		map.put("Mac", macNum);
		
		
		return Response.ok(map);
	}
	
	/**
	 * 根据天查询游客数
	 * @param day
	 * @return
	 */
	@GetMapping("day/{day}")
	public Response<?> selectGuestNumByDay(@PathVariable String day){
		Long map = this.getGuestNumByDay(day);
		return Response.ok(map);
	}
	
	@GetMapping("month/{month}")
	public Response<?> selectGuestNumByMonth(@PathVariable String month){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		List<Long> numbers = new ArrayList<Long>();
		try {
			Date date = sdf.parse(month);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			for (int j = 1; j <= i; j++) {
				String m = "";
				if(j < 10) {
					m = "0" + j;
				}else {
					m = j+"";
				}
				String day = month + m;
				long size = this.getGuestNumByDay(day);
				numbers.add(size);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Response.ok(numbers);
	}
	
	
	public Long getGuestNumByDay(String day){
		//return redisHandler.zSize(RedisKey.GUEST_DAY + day);
		return redisHandler.pfcount(RedisKey.GUEST_DAY_PF + day);
	}
	
	/*@GetMapping("syncpf/{yyyyMM}")
	public Response<?> syncRedis2Pf(@PathVariable String yyyyMM) {
		
		if (yyyyMM.length() != 6) {
			throw new BusinessException(ResultCode.PARAM_VALID_ERROR, "yyyyMM valid fail");
		}
		
		//通过年月获取日
		int maxDate = DateUtils.getMaxDayNumByMonth(yyyyMM);
		
		String year = yyyyMM.substring(0,4);
		String month = yyyyMM.substring(4, 6);
		
		Calendar cal = Calendar.getInstance();   
		cal.set(Calendar.YEAR, Integer.parseInt(year));   
		cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		int maxDate = cal.getActualMaximum(Calendar.DATE);
		
		for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
			String day = yyyyMM + (dateNum < 10 ? "0" + dateNum : dateNum);
			memGuestInfoService.syncRedis2Pf(day);
		}
		
		return Response.ok();
		
	}
	
	@GetMapping("clear/{yyyyMM}")
	public Response<?> clearRedisGuest2DB(@PathVariable String yyyyMM) {
		
		if (yyyyMM.length() != 6) {
			throw new BusinessException(ResultCode.PARAM_VALID_ERROR, "yyyyMM valid fail");
		}
		
		//通过年月获取日
		int maxDate = DateUtils.getMaxDayNumByMonth(yyyyMM);
		
		String year = yyyyMM.substring(0,4);
		String month = yyyyMM.substring(4, 6);
		
		Calendar cal = Calendar.getInstance();   
		cal.set(Calendar.YEAR, Integer.parseInt(year));   
		cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		int maxDate = cal.getActualMaximum(Calendar.DATE);
		
		for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
			String day = yyyyMM + (dateNum < 10 ? "0" + dateNum : dateNum);
			memGuestInfoService.clearRedisGuest(day);
		}
		
		return Response.ok();
		
	}
	
	@GetMapping("sync/{yyyyMM}")
	public Response<?> syncGuest2DB(@PathVariable String yyyyMM) {
		
		if (yyyyMM.length() != 6) {
			throw new BusinessException(ResultCode.PARAM_VALID_ERROR, "yyyyMM valid fail");
		}
		
		//通过年月获取日
		int maxDate = DateUtils.getMaxDayNumByMonth(yyyyMM);
		
		String year = yyyyMM.substring(0,4);
		String month = yyyyMM.substring(4, 6);
		
		Calendar cal = Calendar.getInstance();   
		cal.set(Calendar.YEAR, Integer.parseInt(year));   
		cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		int maxDate = cal.getActualMaximum(Calendar.DATE);
		
		for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
			String day = yyyyMM + (dateNum < 10 ? "0" + dateNum : dateNum);
			memGuestInfoService.syncGuest2DB(day);
		}
		
		return Response.ok();
		
	}
	
	@GetMapping("syncpf/day/{yyyyMMdd}")
	public Response<?> syncGuestpfDay2DB(@PathVariable String yyyyMMdd) {
		
		if (yyyyMMdd.length() != 8) {
			throw new BusinessException(ResultCode.PARAM_VALID_ERROR, "yyyyMM valid fail");
		}
		
		memGuestInfoService.syncRedis2Pf(yyyyMMdd);
		
		return Response.ok();
		
	}
	
	@GetMapping("sync/day/{yyyyMMdd}")
	public Response<?> syncGuestDay2DB(@PathVariable String yyyyMMdd) {
		
		if (yyyyMMdd.length() != 8) {
			throw new BusinessException(ResultCode.PARAM_VALID_ERROR, "yyyyMM valid fail");
		}
		
		memGuestInfoService.syncGuest2DB(yyyyMMdd);
		
		return Response.ok();
		
	}*/
	
}
