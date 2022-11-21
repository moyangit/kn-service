package com.tsn.serv.mem.controller.mem;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.cons.api.ApiCons;
import com.tsn.serv.common.enm.comm.EnableStatus;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.common.entity.device.Device;
import com.tsn.serv.common.utils.DeviceUtils;
import com.tsn.serv.mem.entity.mem.MemAccessInfo;
import com.tsn.serv.mem.entity.mem.MemExtInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.mem.MemSource;
import com.tsn.serv.mem.entity.mem.NewUser;
import com.tsn.serv.mem.entity.proxy.ProxyGroup;
import com.tsn.serv.mem.service.account.MemAccountService;
import com.tsn.serv.mem.service.flow.FlowLimitService;
import com.tsn.serv.mem.service.geo.GeoService;
import com.tsn.serv.mem.service.mem.MemActiviService;
import com.tsn.serv.mem.service.mem.MemExtInfoService;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.mem.MemSourceService;
import com.tsn.serv.mem.service.node.NodeKeyService;

@RestController
@RequestMapping("mem")
public class MemController {
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private MemSourceService memSourceService;
	
	@Autowired
	private MemExtInfoService memExtInfoService;
	
	@Autowired
	private MemActiviService memActiviService;
	
	@Autowired
	private NodeKeyService nodeKeyService;
	
	@Autowired
	private MemAccountService memAccountService;
	
	@Autowired
	private FlowLimitService flowLimitService;
	
	@Autowired
	private GeoService geoService;
	
	/**
	 * 通过用户ID获取用户信息
	 * @return
	 */
	@GetMapping("/my")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> queryMemByUserId() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		MemInfo memInfo = memService.queryMemById(userId);
		//这个地方临时写，主要为了给黑豹 “我的” 显示名字
		/*if (memInfo != null && MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType()) ) {
			memInfo.setMemPhone("游客" + memInfo.getInviterCode());
			memInfo.setMemName(memInfo.getMemPhone());
		}*/
		
		return Response.ok(memInfo);
	}
	
	@PutMapping("/my/invcode/{invcode}")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> updateInvitCode(@PathVariable String invcode) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		memService.updateMemInviCode(userId, invcode);
		return Response.ok();
	}
	
	@PutMapping("/my")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> updateMyInfo(@RequestBody MemInfo memInfo) {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}

		MemInfo upMemInfo = new MemInfo();
		upMemInfo.setMemId(userId);
		upMemInfo.setParentInvCode(memInfo.getParentInvCode());
		upMemInfo.setMemNickName(memInfo.getMemNickName());
		upMemInfo.setMemSex(memInfo.getMemSex());
		memService.updateSelMemInfo(upMemInfo);
		return Response.ok();
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/phone/{phone}")
	public Response<?> queryMemByPhone(@PathVariable String phone) {
		MemInfo memInfo = memService.queryMemByPhone(phone);
/*		OutBound outBound = nodeKeyService.getRandomNodekeyByAge0();
		memInfo.setOutBound(outBound);*/
		return Response.ok(memInfo);
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/access/{userId}")
	public Response<?> access(@PathVariable String userId) {
		MemAccessInfo memInfo = memService.accessByUserId(userId);
		return Response.ok(memInfo);
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/flow/{userId}")
	public Response<?> getFlowInfo(@PathVariable String userId) {
		Map<String, Object> flowLimit = flowLimitService.getFlowLimitTo(userId);
        return Response.ok(flowLimit);
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/device/{deviceNo}")
	public Response<?> getGuest(@PathVariable String deviceNo){
		//MemInfo memInfo = memExtInfoService.getMemInfoByCacheDeviceId(deviceNo);
		Device device = DeviceUtils.getDeviceInfo(deviceNo);
		MemInfo memInfo = memExtInfoService.getMemInfoByDeviceId(device.getDeviceNo());
		return Response.ok(memInfo);
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/{memId}")
	public Response<?> getMemInfo(@PathVariable String memId){
		MemInfo memInfo = memService.queryMemById(memId);
		//这个地方临时写，主要为了给黑豹 “我的” 显示名字
		if (MemTypeEum.guest_mem.getCode().equals(memInfo.getMemType()) ) {
			memInfo.setMemPhone("游客" + memInfo.getInviterCode());
			memInfo.setMemName(memInfo.getMemPhone());
		}
		return Response.ok(memInfo);
	}
	
	@GetMapping(ApiCons.PRIVATE_PATH + "/email/{email}")
	public Response<?> queryMemByEmail(@PathVariable String email) {
		MemInfo memInfo = memService.queryMemByEmail(email);
		return Response.ok(memInfo);
	}
	
	@GetMapping("/destory")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> destoryUser() {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		MemInfo memInfo = new MemInfo();
		memInfo.setMemId(userId);
		memInfo.setStatus(String.valueOf(EnableStatus.disable.getCode()));
		
		memService.updateMemInfoSelect(memInfo);
		
		return Response.ok();
	}
	
	/**
	 * 通过手机号判断是否存在
	 * @param phone
	 * @return
	 */
	@GetMapping("/exist/phone/{phone}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> isExistByPhone(@PathVariable String phone) {
		MemInfo memInfo = memService.queryMemByPhone(phone);
		return Response.ok(memInfo == null ? false : true);
	}
	
	/**
	 * 修改最后一次登录时间
	 * @param memId
	 * @return
	 */
	@PutMapping(ApiCons.PRIVATE_PATH + "/logintime/{memId}")
	public Response<?> updateLoginTime(@PathVariable String memId) {
		MemInfo info = new MemInfo();
		info.setMemId(memId);
		memService.updateTime(info);
		return Response.ok();
	}
	
	/**
	 * 添加会员用户
	 * @param mem
	 * @param br
	 * @return
	 */
	@PostMapping
	public Response<?> addMem(@Valid @RequestBody MemInfo mem, BindingResult br) {
		
		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());
		}
		
		memService.addMem(mem);
		
		return Response.ok();
	}
	
	@PutMapping()
	public Response<?> updateMem(@Valid @RequestBody MemInfo mem, BindingResult br) {

		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());
		}

		memService.updateMemInfo(mem);

		return Response.ok();
	}

	@PutMapping("phone")
	public Response<?> updateMemByPhone(@Valid @RequestBody MemInfo mem, BindingResult br) {

		if (br.hasErrors()) {
			throw new RequestParamValidException(br.getAllErrors(), br.toString());
		}

		memService.updateMemByPhone(mem);

		return Response.ok();
	}
	
	@GetMapping("/share/url")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getShareUrl() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		/*String shareUrl = Env.getVal("mem.share.reg.url");
		MemInfo memInfo = memService.queryMemById(userId);
		
		String sign = ShareUtils.getSign(userId, memInfo.getInviterCode(), shareUrl);
		
		return Response.ok(new StringBuffer(shareUrl).append("?").append(sign).toString());*/
		
		String shareUrl = memService.shareOwnLink(userId);
		
		/*String shareUrl = Env.getVal("mem.share.reg.url");
		MemInfo memInfo = memService.queryMemById(userId);
		
		String sign = ShareUtils.getSign(userId, memInfo.getInviterCode(), shareUrl);
		
		return Response.ok(new StringBuffer(shareUrl).append("?").append(sign).toString());*/
		
		return Response.ok(shareUrl);
	}
	
	@GetMapping("/cashout")
	public Response<?> getCashout() {
		
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}
		
		MemInfo memInfo = memService.queryMemById(userId);
		
		String cashout = memInfo.getMemCashDetail();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> cashoutObj = JsonUtils.jsonToPojo(cashout, Map.class);
		
		return Response.ok(cashoutObj);
	}

	/*
	* 用户信息管理列表请求
	* */
	@GetMapping("/userDataTableList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> userDataTableList(Page page) {
		memService.userDataTableList(page);
		return Response.ok(page);
	}
	
	@GetMapping("/list")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getUserList(Page page) {
		memService.getUserList(page);
		return Response.ok(page);
	}

	/**
	 * 用户信息管理-用户新增
	 * @param memInfo
	 * @return
	 */
	@PostMapping("/addMemInfo")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> addMemInfo(@RequestBody MemInfo memInfo) {
		MemInfo isExist = memService.queryMemByPhone(memInfo.getMemPhone());
		if (isExist != null){
			return Response.retn(AuthCode.AUTH_USER_EXISTS_ERROR, "该手机号已存在!");
		}else {
			int i = memService.addMem(memInfo);
			return Response.ok(i);
		}
	}

	/**
	 * 用户信息管理-用户编辑
	 * @param memInfo
	 * @return
	 */
	@PutMapping("/updateMemInfo")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateMemInfo(@RequestBody MemInfo memInfo) {
        int i = memService.updateMemInfoTo(memInfo);
        return Response.ok(i);
	}

	/**
	 * 修改会员上级
	 * @param memInfo
	 * @return
	 */
	@PutMapping("/updateInviter")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateInviter(@RequestBody MemInfo memInfo) {
		memService.updateInviter(memInfo);
		return Response.ok();
	}

	/**
	 * 修改允许会员提现时间
	 * @param memInfo
	 * @return
	 */
	@PutMapping("/updateCashoutTime")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateCashoutTime(@RequestBody MemInfo memInfo) {
		memService.updateMemInfo(memInfo);
		return Response.ok();
	}

	/**
	 * 修改会员允许在线设备数
	 * @param memInfo
	 * @return
	 */
	@PutMapping("/deviceNum")
	public Response<?> deviceNum(@RequestBody MemInfo memInfo) {
		memService.updateDeviceNum(memInfo);
		return Response.ok();
	}

/*	*//**
	 * 获取一级代理会员
	 * @param page
	 * @return
	 *//*
	@GetMapping("/oneProxyMemList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> oneProxyMemList(Page page) {
		memService.getOneProxyMemList(page);
		return Response.ok(page);
	}

	*//**
	 * 获取二级代理
	 * @param page
	 * @return
	 *//*
	@GetMapping("/twoProxyMemList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> twoProxyMemList(Page page) {
		memService.getTwoProxyMemList(page);
		return Response.ok(page);
	}*/

	/*
	* 树形代理
	* */
	@GetMapping("/getProxyTreeList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getProxyTreeList(Page page) {
		//memService.getProxyTreeList(page);
		memService.queryProxyPage(page);
		return Response.ok(page);
	}
	
	@GetMapping("/proxy/invit/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getProxyInvitTreeList(Page page) {
		//memService.getProxyTreeList(page);
		memService.queryProxyAndInvitList(page);
		return Response.ok(page);
	}

	/**
	 * 根据代理会员ID获取下级会员(Tree)
	 * @param memInfo
	 * @return
	 */
	@PostMapping("/userGrade")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> userGrade(@RequestBody MemInfo memInfo) {
		List<Map<String, Object>> userGrade = memService.userGrade(memInfo);
		return Response.ok(userGrade);
	}

	/**
	 * 获取返利方案
	 * @param page
	 * @return
	 */
	@GetMapping("/proxyGroupList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> proxyGroupList(Page page) {
		memService.proxyGroupList(page);
		return Response.ok(page);
	}

	/**
	 * 返利方案修改
	 * @param proxyGroup
	 * @return
	 */
	@PutMapping("/updateProxyGroup")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateProxyGroup(@RequestBody ProxyGroup proxyGroup) {
		int i = memService.updateProxyGroup(proxyGroup);
		return Response.ok(i);
	}

	/**
	 * 新老账户绑定
	 * @param newUserMap
	 * @return
	 */
	@PostMapping("/bindingAccount")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> bindingAccount(@RequestBody Map<String, String> newUserMap) {
		return memService.bindingAccount(newUserMap);
	}

	/**
	 * 获取新老用户绑定信息
	 * @param page
	 * @return
	 */
	@GetMapping("/newUserList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> newUserList(Page page) {
		memService.newUserList(page);
		return Response.ok(page);
	}

	@PutMapping("/updateNewUser")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> updateNewUser(@RequestBody NewUser newUser) {
		int i = memService.updateNewUser(newUser);
		return Response.ok(i);
	}

	@DeleteMapping("/deleteNewUser")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> deleteNewUser(@RequestBody NewUser newUser) {
		int i = memService.deleteNewUser(newUser);
		return Response.ok(i);
	}

	/**
	 * 根据手机号查询代理信息
	 * @param phone
	 * @return
	 */
	@GetMapping("proxy/phone/{phone}")
	public Response<?> queryProxyByPhone(@PathVariable String phone) {
		MemInfo memInfo = memService.queryProxyByPhone(phone);
		return Response.ok(memInfo);
	}

	@GetMapping("/todayMemInfoList")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> todayMemInfoList(Page page) {
		List<Map> todayMemInfoList = memService.todayMemInfoList(page);
		page.setData(todayMemInfoList);
		return Response.ok(page);
	}

	@GetMapping("/memInfoQuery")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> memInfoQuery(String memPhone) {
        Map<String, Object> respMap = memService.memInfoQuery(memPhone);
        return Response.ok(respMap);
	}
	
	@GetMapping("/move/{guestInviCode}/{memId}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> move(@PathVariable String guestInviCode, @PathVariable String memId) {
        memService.move(guestInviCode, memId);
        return Response.ok();
	}
	
	@GetMapping("/invicode/{guestInviCode}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getMemInfoByInviCode(@PathVariable String guestInviCode) {
        return Response.ok(memService.queryMemInfoByInviCode(guestInviCode));
	}

	/**
	 * 提供给客户端查询允许用户提现时间
	 * @return
	 */
	@GetMapping("/lastCashoutDate")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> lastCashoutDate() {
		JwtInfo jwtInfo = JwtLocal.getJwt();
		String userId = jwtInfo.getSubject();
		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "jwtinfo no exist user info");
		}

		MemInfo memInfo = memService.queryMemById(userId);
		return Response.ok(memInfo.getLastCashoutDate());
	}

	/**
	 * 根据会员ID查询邀请人列表信息
	 * @param page
	 * @return
	 */
	@GetMapping("/inviteList")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> inviteList(Page page) {
		JwtInfo info = JwtLocal.getJwt();

		String userId = info.getSubject();

		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}

		page.setParamObj("memId", userId);
		memService.inviteList(page);
		return Response.ok(page);
	}

	/**
	 * 最近使用的用户
	 * @param page
	 * @return
	 */
	@GetMapping("/recentUseMem")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> recentUseMem(Page page) {
		memService.recentUseMem(page);
		return Response.ok(page);
	}
	
	@GetMapping("/statis/reg/peoples/{monthDate}")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getUserRegStatisGroupByDay(@PathVariable String monthDate) {
		return Response.ok(memService.getUserRegStatisGroupByDay(monthDate));
	}
	
	@PostMapping("/source")
	@AuthClient(client = {AuthEnum.bea_us, AuthEnum.guest_us})
	public Response<?> addSource(@RequestBody MemSource memSource) {
		JwtInfo info = JwtLocal.getJwt();

		String userId = info.getSubject();

		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}

		memSource.setId(userId);
		boolean res = memSourceService.updateById(memSource);
		
		if (!res) {
			memSourceService.save(memSource);
		}
		
		return Response.ok();
	}
	
	@GetMapping("/source")
	@AuthClient(client = {AuthEnum.bea_mn})
	public Response<?> getSource(String phone) {

		MemInfo memInfo = memService.queryMemByPhone(phone);

		if (memInfo == null) {
			throw new AuthException(AuthCode.AUTH_PARAM_ERROR, "use not exist");
		}

		MemSource source = memSourceService.getById(memInfo.getMemId());
		
		return Response.ok(source);
	}
	
	@GetMapping("/device")
	@AuthClient(client = {AuthEnum.bea_mn})
	public Response<?> getDevice(String memId) {

		MemExtInfo memExtInfo = memExtInfoService.getById(memId);
		
		return Response.ok(memExtInfo);
	}
	
	@GetMapping("/activi")
	@AuthClient(client = {AuthEnum.bea_mn})
	public Response<?> getActivi(String memId) {

		Map<String, Object> memExtInfo = memActiviService.queryMemActiviData(memId);
		
		return Response.ok(memExtInfo);
	}
	
	@PutMapping("/pwd")
	@AuthClient(client = {AuthEnum.bea_us})
	public Response<?> updatePwd(@RequestBody Map<String, String> params) {
		
		JwtInfo info = JwtLocal.getJwt();

		String userId = info.getSubject();

		if (StringUtils.isEmpty(userId)) {
			throw new AuthException(AuthCode.AUTH_TOKEN_VALID_ERROR, "user valid error, please again login");
		}
		
		MemInfo memInfo = memService.queryAllMemById(userId);
		
		String md5OldPwd = params.get("oldPwd");
		
		if (!memInfo.getMemPwd().equals(MD5Utils.digest(md5OldPwd))) {
			throw new AuthException(AuthCode.AUTH_O_PWD_ERROR, "old password input error");
		}
		
		String newPwd = params.get("newPwd");
		if (StringUtils.isEmpty(newPwd)) {
			throw new AuthException(AuthCode.AUTH_PARAM_ERROR, "new password input emtpy()");
		}
		
		MemInfo updateMemInfo = new MemInfo();
		updateMemInfo.setMemId(userId);
		updateMemInfo.setMemPwd(newPwd);
		memService.updateMemInfo(updateMemInfo);
		return Response.ok();
	}
}
