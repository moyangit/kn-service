package com.tsn.serv.mem.service.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.serv.mem.entity.env.EnvParams;
import com.tsn.serv.mem.entity.env.enm.EnvKeyEnum;
import com.tsn.serv.mem.mapper.env.EnvParamsMapper;

/**
 * <p>
 * 用户留存统计 Mapper 接口
 * </p>
 *
 * @author Tang longsen
 * @since 2022-06-15
 */
@Service
public class EnvParamsService extends ServiceImpl<EnvParamsMapper, EnvParams>{
	
	private static List<EnvParams> cacheEnvParams = new ArrayList<EnvParams>();
	
	public void clearParams() {
		cacheEnvParams.clear();
	}
	
	public String getValByKey(EnvKeyEnum keyEnum) {
		
		if (cacheEnvParams.isEmpty()) {
			cacheEnvParams = super.list();
		}
		
		for (EnvParams envparams : cacheEnvParams) {
			if (keyEnum.name().equals(envparams.getKeyName())) {
				return envparams.getValue();
			}
		}
		
		return "";
	}
	
	public List<Map<String, String>> getAppStoreAcc() {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
		if (cacheEnvParams.isEmpty()) {
			cacheEnvParams = super.list();
		}
		
		for (EnvParams envparams : cacheEnvParams) {
			if (EnvKeyEnum.app_store_acc.name().equals(envparams.getKeyName())) {
				String appStoreAcc = envparams.getValue();
				String[] appStoreAccArr = appStoreAcc.split(",");
				for (String acc : appStoreAccArr) {
					String[] accArr = acc.split("#");
					Map<String, String> accMap = new HashMap<String, String>();
					accMap.put("account", accArr[0]);
					accMap.put("pwd", accArr[1]);
					result.add(accMap);
				}
				
				return result;
			}
		}
		
		return result;
	}

}