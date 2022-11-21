package com.tsn.serv.auth.service.app;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.serv.auth.entity.app.AppVersion;
import com.tsn.serv.auth.mapper.app.AppVersionMapper;

@Service
public class AppVersionService {
	
	@Autowired
	private AppVersionMapper appVersionMapper;
	
	@Autowired
	private AppDownloadService appDownloadService;
	
	public Map<String, String> getVersion(@PathVariable String appType, @PathVariable String version){
		
		Map<String, String> result = new HashMap<String, String>();
		
		AppVersion appVersion = appVersionMapper.selectLastByAppType(appType);
		
		if (appVersion == null) {
			result.put("isUpate", "0");
			result.put("appVersion", version);
			return result;
		}
		
		//最新的
		String vers = appVersion.getAppVersion().replace(".", "");
		//传过来的
		version = version.replace(".", "");
		
		if (vers.compareTo(version) > 0) {
			result.put("isUpate", "1");
			result.put("appVersion", appVersion.getAppVersion());
			
			String downUrl = appDownloadService.getAppDownloadForUpdateListByStatus(appType);
			result.put("downUrl", downUrl);
			
			//result.put("downUrl", appVersion.getDownurl());
			result.put("versionDesc", appVersion.getVersionDesc());
			return result;
		}
		
		result.put("isUpate", "0");
		result.put("appVersion", appVersion.getAppVersion());
		
		return result;
	}
	
	public Map<String, String> getVersionForNo(@PathVariable String appType, @PathVariable String version){
		
		Map<String, String> result = new HashMap<String, String>();
		
		//临时代码
		if ("222".equals(version)) {
			result.put("isUpate", "0");
			result.put("appVersion", version);
			result.put("force", "1");
			return result;
		}

		AppVersion appVersion = appVersionMapper.selectLastByAppType(appType);
		
		if (appVersion == null) {
			result.put("isUpate", "0");
			result.put("appVersion", version);
			result.put("force", "1");
			return result;
		}

		if (Integer.parseInt(appVersion.getVersionNo()) > Integer.parseInt(version)) {
			result.put("isUpate", "1");
			result.put("appVersion", appVersion.getAppVersion());
			//result.put("downUrl", appVersion.getDownurl());
			String downUrl = appDownloadService.getAppDownloadForUpdateListByStatus(appType);
			result.put("downUrl", downUrl);
			
			result.put("versionDesc", appVersion.getVersionDesc());
			result.put("addrUrl", Env.getVal("mem.credits.task.url"));
			result.put("force", StringUtils.isEmpty(appVersion.getForceUpdate()) ? "1" : appVersion.getForceUpdate());
			return result;
		}
		
		result.put("isUpate", "0");
		result.put("appVersion", appVersion.getAppVersion());
		result.put("force", "0");
		
		return result;
		
	}
	
	public Map<String, Object> getVersionIos(@PathVariable String appType, @PathVariable String version){

		Map<String, Object> result = new HashMap<String, Object>();
		
		AppVersion appVersion = appVersionMapper.selectLastByAppType(appType);
		
		if (appVersion == null) {
			result.put("mark", false);
			return result;
		}
		
		//app商店版本
		String vers = appVersion.getAppVersion().replace(".", "");
		
		//传过来的
		version = version.replace(".", "");

		if (Integer.parseInt(vers) >= Integer.parseInt(version)) {
			result.put("mark", false);
			return result;
		}
		
		result.put("mark", true);
		
		return result;
		
	}

	public void getAppVersionByPage(Page page) {
		List<AppVersion> appVersionList = appVersionMapper.getAppVersionByPage(page);
		page.setData(appVersionList);
	}

	@Transactional
	public void addAppVersion(AppVersion appVersion) {
		// 修改当前可用版本号数据状态
		appVersionMapper.updateIsNew(appVersion);
		// 新增版本信息
		appVersion.setIsNew((byte)1);
		appVersion.setCreateTime(new Date());
		appVersionMapper.insertSelective(appVersion);
	}

	public void updateAppVersion(AppVersion appVersion) {
		appVersionMapper.updateByPrimaryKeySelective(appVersion);
	}
}
