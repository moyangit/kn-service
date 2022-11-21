package com.tsn.serv.mem.service.mem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.serv.common.code.mem.MemCode;
import com.tsn.serv.mem.entity.mem.MemConfigPathInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.mem.MemInfoConfig;
import com.tsn.serv.mem.mapper.mem.MemInfoConfigMapper;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.service.path.PathService;

@Service
public class MemInfoConfigService {
	
	@Autowired
	private MemInfoConfigMapper memInfoConfigMapper;
	
	@Autowired
	private MemInfoMapper memMapper;
	
	@Autowired
	private PathService pathService;
	
	@Autowired
	public RedisHandler redisHandler;
	
	public MemInfoConfig getMemConfigById(String id) {
		return memInfoConfigMapper.selectByPrimaryKey(id);
	}
	
	public MemInfoConfig getMemConfigAndAddByMemId(String id){
		
		MemInfoConfig memInfoConfig = memInfoConfigMapper.selectByPrimaryKey(id);
		
		if (memInfoConfig == null || StringUtils.isEmpty(memInfoConfig.getSubKey())) {
			MemInfoConfig memInfoConfigTmp = new MemInfoConfig();
			memInfoConfigTmp.setMemId(id);
			memInfoConfigTmp.setSubKey(Long.toHexString(new Date().getTime()));
			memInfoConfigTmp.setIsSubKey(0);
			
			int res = memInfoConfigMapper.updateByPrimaryKeySelective(memInfoConfigTmp);
			
			if (res == 0) {
				memInfoConfigMapper.insert(memInfoConfigTmp);
			}
			
			return memInfoConfigTmp;
		}
		
		return memInfoConfig;
		
	}
	
	public String getMemIdBySubKey(String subKey) {
		
		MemInfoConfig memInfoConfig = memInfoConfigMapper.selectBySubKey(subKey);
		
		if (memInfoConfig != null) {
			return memInfoConfig.getMemId();
		}
		
		return null;
		
	}
	
	public void resetSubKey(String memId) {
		
		MemInfoConfig memInfoConfigTmp = new MemInfoConfig();
		memInfoConfigTmp.setMemId(memId);
		memInfoConfigTmp.setSubKey(Long.toHexString(new Date().getTime()));
		memInfoConfigMapper.updateByPrimaryKeySelective(memInfoConfigTmp);
		
	}
	
	public MemInfoConfig getMemConfigByPhone(String phone) {
		MemInfo memInfo = memMapper.selectMemByPhone(phone);
		
		if (memInfo == null) {
			throw new BusinessException(MemCode.MEM_IS_NOT_EXISTS, "mem is not exists");
		}
		
		return memInfoConfigMapper.selectByPrimaryKey(memInfo.getMemId());
	}
	
	public void updateMemConfigForPathTestInfo(String pathArr, String phone) {
		
		MemInfo memInfo = memMapper.selectMemByPhone(phone);
		
		if (memInfo == null) {
			throw new BusinessException(MemCode.MEM_IS_NOT_EXISTS, "mem is not exists");
		}
		
		MemInfoConfig memInfoConfig = new MemInfoConfig();
		memInfoConfig.setMemId(memInfo.getMemId());
		String[] pathStrArr = pathArr.split(",");
		memInfoConfig.setPathArr(JsonUtils.objectToJson(pathStrArr));
		
		int res = this.memInfoConfigMapper.updateByPrimaryKeySelective(memInfoConfig);
		
		if (res == 0) {
			this.memInfoConfigMapper.insert(memInfoConfig);
		}
		
		//redisHandler.del(RedisKey.PATH_ALL_LIST);
	}
	
	/**
	 * 更新测试线路的指定节点
	 * @param phone
	 * @param code
	 * @param node
	 */
	public void updateMemConfigForPathTestNode(String phone, String code, String node) {
		
		MemInfo memInfo = memMapper.selectMemByPhone(phone);
		
		if (memInfo == null) {
			throw new BusinessException(MemCode.MEM_IS_NOT_EXISTS, "mem is not exists");
		}
		
		MemInfoConfig memInfoConfig = this.memInfoConfigMapper.selectByPrimaryKey(memInfo.getMemId());
		
		if (memInfoConfig == null || StringUtils.isEmpty(memInfoConfig.getPathArr())) {
			return;
		}
		
		List<MemConfigPathInfo> memConfigPathInfoList = JsonUtils.jsonToList(memInfoConfig.getPathArr(), MemConfigPathInfo.class);
		
		if (memConfigPathInfoList == null || memConfigPathInfoList.isEmpty()) {
			return;
		}
		
		for (MemConfigPathInfo configPathInfo : memConfigPathInfoList) {
			
			if (code.equals(configPathInfo.getCode())) {
				configPathInfo.setNode(node);
			}
			
		}
		
		MemInfoConfig memInfoConfigTemp = new MemInfoConfig();
		memInfoConfigTemp.setMemId(memInfo.getMemId());
		memInfoConfigTemp.setPathArr(JsonUtils.objectToJson(memConfigPathInfoList));
		
		this.memInfoConfigMapper.updateByPrimaryKeySelective(memInfoConfigTemp);
		
		//redisHandler.del(RedisKey.PATH_ALL_LIST);
		
	}
	
	/**
	 * 删除测试节点
	 * @param phone
	 * @param code
	 */
	public void deleteTestPath(String phone, String code) {
		
		MemInfo memInfo = memMapper.selectMemByPhone(phone);
		
		if (memInfo == null) {
			throw new BusinessException(MemCode.MEM_IS_NOT_EXISTS, "mem is not exists");
		}
		
		MemInfoConfig memInfoConfig = this.memInfoConfigMapper.selectByPrimaryKey(memInfo.getMemId());
		
		if (memInfoConfig == null || StringUtils.isEmpty(memInfoConfig.getPathArr())) {
			return;
		}
		
		List<MemConfigPathInfo> memConfigPathInfoList = JsonUtils.jsonToList(memInfoConfig.getPathArr(), MemConfigPathInfo.class);
		
		if (memConfigPathInfoList == null || memConfigPathInfoList.isEmpty()) {
			return;
		}
		
		
		for (int num = memConfigPathInfoList.size() - 1; num >=0 ; num--) {
			if (code.equals(memConfigPathInfoList.get(num).getCode())) {
				memConfigPathInfoList.remove(num);
			}
		}
		
		MemInfoConfig memInfoConfigTemp = new MemInfoConfig();
		memInfoConfigTemp.setMemId(memInfo.getMemId());
		memInfoConfigTemp.setPathArr(JsonUtils.objectToJson(memConfigPathInfoList));
		
		this.memInfoConfigMapper.updateByPrimaryKeySelective(memInfoConfigTemp);
		
		//redisHandler.del(RedisKey.PATH_ALL_LIST);
		
	}
	
	/**
	 * 添加
	 * @param phone
	 * @param code
	 * @param node
	 */
	public void addTestPath(String phone, String code, String node) {
		
		List<MemConfigPathInfo> memConfigPathInfoList = new ArrayList<MemConfigPathInfo>();
		
		MemInfo memInfo = memMapper.selectMemByPhone(phone);
		
		if (memInfo == null) {
			throw new BusinessException(MemCode.MEM_IS_NOT_EXISTS, "mem is not exists");
		}
		
		MemInfoConfig memInfoConfig = this.memInfoConfigMapper.selectByPrimaryKey(memInfo.getMemId());
		
		if (memInfoConfig == null || StringUtils.isEmpty(memInfoConfig.getPathArr())) {
			memConfigPathInfoList.add(new MemConfigPathInfo(code, node));
			updateAndAddTestPath(memInfo.getMemId(), memConfigPathInfoList);
			return;
		}
		
		memConfigPathInfoList = JsonUtils.jsonToList(memInfoConfig.getPathArr(), MemConfigPathInfo.class);
		
		if (memConfigPathInfoList == null || memConfigPathInfoList.isEmpty()) {
			memConfigPathInfoList.add(new MemConfigPathInfo(code, node));
			updateAndAddTestPath(memInfo.getMemId(), memConfigPathInfoList);
			return;
		}
		
		//遍历如果存在，则更新，如果不存在 则更新
		boolean isExists = false;
		for (MemConfigPathInfo configPathInfo : memConfigPathInfoList) {
			
			if (code.equals(configPathInfo.getCode())) {
				configPathInfo.setNode(node);
				isExists = true;
			}
			
		}
		
		//如果不存在添加
		if (!isExists) {
			memConfigPathInfoList.add(new MemConfigPathInfo(code, node));
		}
		
		updateAndAddTestPath(memInfo.getMemId(), memConfigPathInfoList);
		
		/*MemInfoConfig memInfoConfigTemp = new MemInfoConfig();
		memInfoConfigTemp.setMemId(memInfo.getMemId());
		memInfoConfigTemp.setPathArr(JsonUtils.objectToJson(memConfigPathInfoList));
		
		this.memInfoConfigMapper.updateByPrimaryKeySelective(memInfoConfigTemp);*/
		
		//redisHandler.del(RedisKey.PATH_ALL_LIST);
		
	}
	
	private void updateAndAddTestPath(String memId, List<MemConfigPathInfo> memConfigPathInfoList) {
		MemInfoConfig memInfoConfig = new MemInfoConfig();
		memInfoConfig.setMemId(memId);
		memInfoConfig.setPathArr(JsonUtils.objectToJson(memConfigPathInfoList));
		
		int res = this.memInfoConfigMapper.updateByPrimaryKeySelective(memInfoConfig);
		
		if (res == 0) {
			this.memInfoConfigMapper.insert(memInfoConfig);
		}
		
		//redisHandler.del(RedisKey.PATH_ALL_LIST);
	}
	
	public List<MemConfigPathInfo> getMemConfigTestPathListById(String memId) {
		MemInfoConfig memInfoConfig = memInfoConfigMapper.selectByPrimaryKey(memId);
		
		List<MemConfigPathInfo> testPathList = JsonUtils.jsonToList(memInfoConfig.getPathArr(), MemConfigPathInfo.class);
		
		return testPathList;
		
	}
	
	public MemConfigPathInfo getMemConfigTestPathInfo(String memId, String code) {
		
		MemInfoConfig memInfoConfig = memInfoConfigMapper.selectByPrimaryKey(memId);
		
		if (memInfoConfig == null) {
			return null;
		}
		
		List<MemConfigPathInfo> testPathList = JsonUtils.jsonToList(memInfoConfig.getPathArr(), MemConfigPathInfo.class);
		
		if (testPathList == null || testPathList.isEmpty()) {
			return null;
		}
		
		for (MemConfigPathInfo memConfigPathInfo : testPathList) {
			
			if (code.equals(memConfigPathInfo.getCode())) {
				return memConfigPathInfo;
			}
		}
		
		return null;
	}

}
