package com.tsn.serv.auth.controller.app;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.auth.entity.app.AppDownload;
import com.tsn.serv.auth.entity.app.AppVersion;
import com.tsn.serv.auth.service.MemAuthFeignService;
import com.tsn.serv.auth.service.app.AppDownloadService;
import com.tsn.serv.auth.service.app.AppVersionService;

@RestController
@RequestMapping("app/mem/")
public class AppController {
	
	@Autowired
	private AppVersionService appVersionService;

	@Autowired
	private AppDownloadService appDownloadService;

	@Autowired
	private MemAuthFeignService memService;
	
	@GetMapping("{appType}/vers/{version}")
	public Response<?> getVersion(@PathVariable String appType, @PathVariable String version){
		Map<String, String> result = appVersionService.getVersion(appType, version);
		return Response.ok(result);
	}
	
	@GetMapping("{appType}/vers/no/{version}")
	public Response<?> getVersionForNo(@PathVariable String appType, @PathVariable String version){
		Map<String, String> result = appVersionService.getVersionForNo(appType, version);
		return Response.ok(result);
	}
	
	@GetMapping("ios/version/{version}")
	public Response<?> getGetVersionForNo(@PathVariable String version){
		Map<String, Object> result = appVersionService.getVersionIos("ios", version);
		return Response.ok(result);
	}

	/**
	 * 获取IOS下载二维码图片地址
	 * @return
	 */
	@GetMapping("getIosDownload")
//	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getIosDownload() {
		List<AppDownload> appDownloadList = appDownloadService.getAppDownloadListByStatus();
		return Response.ok(appDownloadList);
	}

	@GetMapping("page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getAppVersionByPage(Page page){
		appVersionService.getAppVersionByPage(page);
		return Response.ok(page);
	}

	@PostMapping("add")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addAppVersion(@RequestBody AppVersion appVersion){
		appVersionService.addAppVersion(appVersion);
		return Response.ok();
	}

	@PutMapping()
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateAppVersion(@RequestBody AppVersion appVersion){
		appVersionService.updateAppVersion(appVersion);
		return Response.ok();
	}

	/*
	 * 新老账户绑定
	 * */
	@PostMapping("/bindingAccount")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> bindingAccount(@RequestBody Map<String, String> newUserMap) {
		return memService.bindingAccount(newUserMap);
	}

	@GetMapping("/type")
	public Response<?> getMemNoticeByType(String type) {
		return memService.getMemNoticeByType(type);
	}
}
