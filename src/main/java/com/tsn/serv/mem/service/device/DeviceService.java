package com.tsn.serv.mem.service.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.serv.common.code.mem.MemCode;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.mem.entity.device.MemDevice;
import com.tsn.serv.mem.entity.device.MemDeviceOline;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.device.MemDeviceMapper;
import com.tsn.serv.mem.mapper.device.MemDeviceOlineMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.service.mem.MemGuestInfoService;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.path.PathService;


@Service
public class DeviceService {
	
	@Autowired
	private MemDeviceMapper memDeviceMapper;

	@Autowired
	private PathService pathService;

	@Autowired
	private MemInfoMapper memInfoMapper;

	@Autowired
	private MemDeviceOlineMapper memDeviceOlineMapper;
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private MemGuestInfoService memGuestInfoService;
	
    private static Logger log = LoggerFactory.getLogger(DeviceService.class);
	
	/*private static final String key = "guest:deviceNo:memId:";
	
	private static final String guest = "guest:memInfo:";
	
	private static final String guestDay = "guest:day:";*/

	public List<Map> getOlineDevice(String userId, String dId) {
		
		//触发实时活跃online用户
		//EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(userId));
		
		if (!StringUtils.isEmpty(dId)) {
			EventMsgProducter.build().sendEventMsg(EventMsg.createDeviceUserRegMsg(dId));
		}
		
	    // 每次轮询更新设备表信息
        MemDevice memDevice = memDeviceMapper.selectByMemIdAndDeviceNo(userId, dId);
        if (memDevice != null) {
            memDevice.setUseTime(new Date());
            // 更新
            memDeviceMapper.updateDynamic(memDevice);
        }

        MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
        return JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
	}

	public Map<String, Object> deviceDetails(String userId) {

        MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
        List<MemDevice> memDeviceList = new ArrayList<>();
        if(memDeviceOline != null) {
            List<Map> memDevices = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
            memDevices.stream()
                    .forEach(devcie -> {
                        MemDevice device = memDeviceMapper.selectByMemIdAndDeviceNo(userId, devcie.get("deviceNo").toString());
                        if (devcie != null) {
                            memDeviceList.add(device);
                        }
                    });
            for(int i = memDeviceList.size() - 1; i >= 0; i--){
                String pollingTime = Env.getVal("mem.device.polling.time");
                // 当轮询时间取不到时默认
                if(StringUtils.isEmpty(pollingTime)){
                    pollingTime = "10";
                }
                long useTime = Long.valueOf(memDeviceList.get(i).getUseTime().getTime());
                long date = new Date().getTime() - 1000 * Integer.parseInt(pollingTime);
                if (useTime < date) {
                    memDeviceList.remove(i);
                }
            }
        }
        MemInfo memInfo = memInfoMapper.selectByPrimaryKey(userId);
        Map<String, Object> deviceNumMap = new HashMap<>();
        deviceNumMap.put("deviceNum", memInfo.getDeviceNum());

        Map<String, Object> retnMap = new HashMap<>();
        retnMap.put("deviceList", memDeviceList);
        retnMap.put("deviceNumMap", deviceNumMap);
        return retnMap;
	}

	public Map<String, Object> deviceDetails(String userId, String tokenType) {
		
		Map<String, Object> result = (Map<String, Object>) redisHandler.get("device:num:" + userId + tokenType);
		
		if (result != null && !result.isEmpty()) {
			return result;
		}
		
		MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
        List<MemDevice> memDeviceList = new ArrayList<>();
        if(memDeviceOline != null) {
            List<Map> memDevices = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
            memDevices.stream()
                    .forEach(devcie -> {
                        MemDevice device = memDeviceMapper.selectByMemIdAndDeviceNo(userId, devcie.get("deviceNo").toString());
                        if (devcie != null) {
                            memDeviceList.add(device);
                        }
                    });
            for(int i = memDeviceList.size() - 1; i >= 0; i--){
                String pollingTime = Env.getVal("mem.device.polling.time");
                // 当轮询时间取不到时默认
                if(StringUtils.isEmpty(pollingTime)){
                    pollingTime = "10";
                }
                long useTime = Long.valueOf(memDeviceList.get(i).getUseTime().getTime());
                long date = new Date().getTime() - 1000 * Integer.parseInt(pollingTime);
                if (useTime < date) {
                    memDeviceList.remove(i);
                }
            }
        }

        Map<String, Object> deviceNumMap = new HashMap<>();
        if(AuthEnum.guest_us.name().equals(tokenType)) {
        	deviceNumMap.put("deviceNum", 1);
        }else {
        	MemInfo memInfo = memInfoMapper.selectByPrimaryKey(userId);
        	deviceNumMap.put("deviceNum", memInfo.getDeviceNum());
        }

        Map<String, Object> retnMap = new HashMap<>();
        retnMap.put("deviceList", memDeviceList);
        retnMap.put("deviceNumMap", deviceNumMap);

        redisHandler.set("device:num:" + userId + tokenType, retnMap, 60);
        
        return retnMap;
	}

    public void deviceList(Page page) {
        List<Map<String, Object>> memDeviceList = memDeviceMapper.deviceList(page);
        page.setData(memDeviceList);
    }

    /**
     * 重置用户在线设备信息
     * @param memId
     */
    public void resetDeviceOline(String memId) {
        MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(memId);
        if (memDeviceOline == null) {
            log.warn("mem has no on line device");
            // throw new AuthException(MemCode.MEM_IS_NOT_EXISTS, "mem has no on line device");
        } else {
            memDeviceOline = new MemDeviceOline();
            memDeviceOline.setDeviceJson("[]");
            int num = memDeviceOlineMapper.updateDynamic(memDeviceOline);
            if(num == 0) {
                memDeviceOline.setMemId(memId);
                memDeviceOline.setUpdateTime(new Date());
                memDeviceOlineMapper.insertDynamic(memDeviceOline);
            }
        }
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> getOlineDeviceV1(String userId, String dId, String tokenType) {
		
		//触发实时活跃online用户
		//EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(userId));
		
		if (!StringUtils.isEmpty(dId)) {
			EventMsgProducter.build().sendEventMsg(EventMsg.createDeviceUserRegMsg(dId));
		}
		
		//解析设备号 0:设备类型 1:设备名 2:设备号唯一标识
		String[] dIdSplit = dId.split("-");
		if(dIdSplit.length < 3){
			// 设备号错误
			throw new AuthException(MemCode.MEM_DEVICE_ANALYSIS_ERROR, "mem device analysis error! deviceNo = " + dId);
		}
        MemDevice memDevice = new MemDevice();
        memDevice.setMemId(userId);
        memDevice.setDeviceNo(dId);
        memDevice.setDeviceType(dIdSplit[0]);
        memDevice.setDeviceName(dIdSplit[1]);
        memDevice.setUseTime(new Date());
		int upNum = memDeviceMapper.updateByMemIdAndDeviceNo(memDevice);
		if (upNum == 0) {
			memDeviceMapper.insert(memDevice);
		}
		
		
		List<Map> olineDevice = new ArrayList<Map>();
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			//String key = RedisKey.GUEST_INFO + userId;
			Map<String, Object> map = new HashMap<String, Object>();
			/*String deviceNo = (String) redisHandler.hGet(key, "deviceNo");
			String deviceName = deviceNo.split("-")[1];
			String deviceType = deviceNo.split("-")[0];*/
			map.put("deviceNo", dId);
			map.put("deviceName", dIdSplit[0]);
			map.put("deviceType", dIdSplit[1]);
			//Long suspenDate = (Long) redisHandler.hGet(key, "suspenDate");
			GuestInfo guestInfo = memGuestInfoService.getGuestById(userId);
			MemInfo memInfo = new MemInfo();
			//memInfo.setSuspenDate(new Date(suspenDate));
			//map.put("suspenDate", suspenDate);//到期时间
			memInfo.setSuspenDate(guestInfo.getSuspenDate());
			map.put("suspenDate", guestInfo.getSuspenDate().getTime());
			map.put("isExpire", memInfo.isExpire());//是否继续使用
			olineDevice.add(map);
		}else {
			MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
			MemInfo memInfo = memService.queryMemById(userId);
			olineDevice = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
			for (Map map : olineDevice) {
				map.put("suspenDate", memInfo.getSuspenDate());//到期时间
				map.put("isExpire", memInfo.isExpire());//是否继续使用
			}
		}
		
		return olineDevice;
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> getOlineDeviceV2(String userId, String dId, String tokenType) {
    	
    	
    	Map<String, Object> cache = (Map<String, Object>) redisHandler.get("online:device:" + userId + dId + tokenType);
    	
    	if (cache != null && !cache.isEmpty()) {
    		return cache;
    	}
    	
    	
    	//触发实时活跃online用户
		//EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(userId));
		
		/*if (!StringUtils.isEmpty(dId)) {
			EventMsgProducter.build().sendEventMsg(EventMsg.createDeviceUserRegMsg(dId));
		}*/
    			
        MemDevice memDevice = new MemDevice();
        memDevice.setMemId(userId);
        memDevice.setDeviceNo(dId);
        memDevice.setUseTime(new Date());
        int upNum = memDeviceMapper.updateByMemIdAndDeviceNo(memDevice);
        if (upNum == 0) {
            //解析设备号 0:设备类型 1:设备名 2:设备号唯一标识
            String[] dIdSplit = dId.split("-");
            if(dIdSplit.length < 3){
                // 设备号错误
                throw new AuthException(MemCode.MEM_DEVICE_ANALYSIS_ERROR, "mem device analysis error! deviceNo = " + dId);
            }
            memDevice.setDeviceType(dIdSplit[0]);
            memDevice.setDeviceName(dIdSplit[1]);
            memDeviceMapper.insert(memDevice);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        if(AuthEnum.guest_us.name().equals(tokenType)) {
            //String key = RedisKey.GUEST_INFO + userId;

            /*String deviceNo = (String) redisHandler.hGet(key, "deviceNo");
            if (dId.equals(deviceNo)) {
                map.put("isExist", true);
            } else {
                map.put("isExist", false);
            }*/
            // 游客直接连，不判断
            map.put("isExist", true);
            //Long suspenDate = (Long) redisHandler.hGet(key, "suspenDate");
            GuestInfo guestInfo = memGuestInfoService.getGuestById(userId);
            MemInfo memInfo = new MemInfo();
            memInfo.setSuspenDate(guestInfo.getSuspenDate());
            map.put("suspenDate", guestInfo.getSuspenDate().getTime());//到期时间
            map.put("isExpire", memInfo.isExpire());//是否继续使用
        }else {
            MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
            if (memDeviceOline.getDeviceJson() == null
                    || memDeviceOline.getDeviceJson().equals("[]")
                    || memDeviceOline.getDeviceJson().indexOf(dId) > -1){
                map.put("isExist", true);
            } else {
            	//暂时更改，这里可能会还原代码
                //map.put("isExist", false);
            	map.put("isExist", true);
                List<Map> onLineDeviceList = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
                map.put("deviceName", onLineDeviceList.get(onLineDeviceList.size() - 1).get("deviceName"));
                map.put("deviceType", onLineDeviceList.get(onLineDeviceList.size() - 1).get("deviceType"));
                map.put("useTime", onLineDeviceList.get(onLineDeviceList.size() - 1).get("useTime"));
            }

            MemInfo memInfo = memService.queryMemById(userId);
            map.put("suspenDate", memInfo.getSuspenDate());//到期时间
            //map.put("isExpire", memInfo.isExpire());//是否继续使用
            //暂时更改，这里可能会还原代码
            map.put("isExpire", false);//是否继续使用
        }
        
        redisHandler.set("online:device:" + userId + dId + tokenType, map, 60);
        //log.warn("return on line device map " + map);
        return map;
    }
}