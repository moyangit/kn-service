package com.tsn.serv.mem.service.path;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.algo.WeightAlgo;
import com.tsn.common.utils.utils.tools.algo.WeightAlgo.WeightObj;
import com.tsn.common.utils.utils.tools.json.JsonUtils;
import com.tsn.common.utils.utils.tools.security.AESECBUtils;
import com.tsn.common.utils.utils.tools.security.Base64Utils;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.serv.common.code.mem.MemCode;
import com.tsn.serv.common.cons.sys.SysCons;
import com.tsn.serv.common.enm.comm.PlatTypeEnum;
import com.tsn.serv.common.enm.node.NodeKeyStatus;
import com.tsn.serv.common.enm.path.PathGrade;
import com.tsn.serv.common.entity.device.Device;
import com.tsn.serv.common.entity.v2ray.OutBound;
import com.tsn.serv.common.entity.v2ray.OutBound.Mux;
import com.tsn.serv.common.entity.v2ray.OutBound.Server;
import com.tsn.serv.common.entity.v2ray.OutBound.Settings;
import com.tsn.serv.common.entity.v2ray.OutBound.StreamSettings;
import com.tsn.serv.common.entity.v2ray.OutBound.UserAndServ;
import com.tsn.serv.common.entity.v2ray.OutBound.WsSettings;
import com.tsn.serv.common.entity.v2ray.Routing;
import com.tsn.serv.common.entity.v2ray.Routing.Rule;
import com.tsn.serv.common.entity.v2ray.Routing.Selector;
import com.tsn.serv.common.entity.v2ray.V2User;
import com.tsn.serv.common.mq.ChannelMsg;
import com.tsn.serv.common.mq.ChannelMsgProducter;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.common.utils.DeviceUtils;
import com.tsn.serv.common.utils.V2RayEncry;
import com.tsn.serv.common.utils.V2raySubLinkTools;
import com.tsn.serv.mem.entity.device.MemDevice;
import com.tsn.serv.mem.entity.device.MemDeviceOline;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.entity.mem.MemConfigPathInfo;
import com.tsn.serv.mem.entity.mem.MemExtInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.mem.MemInfoConfig;
import com.tsn.serv.mem.entity.node.NodeKey;
import com.tsn.serv.mem.entity.node.NodePath;
import com.tsn.serv.mem.entity.node.NodePathAutoGroup;
import com.tsn.serv.mem.mapper.device.MemDeviceMapper;
import com.tsn.serv.mem.mapper.device.MemDeviceOlineMapper;
import com.tsn.serv.mem.mapper.node.NodeKeyMapper;
import com.tsn.serv.mem.mapper.node.NodePathAutoGroupMapper;
import com.tsn.serv.mem.mapper.node.NodePathMapper;
import com.tsn.serv.mem.service.device.DeviceService;
import com.tsn.serv.mem.service.mem.MemExtInfoService;
import com.tsn.serv.mem.service.mem.MemGuestInfoService;
import com.tsn.serv.mem.service.mem.MemInfoConfigService;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.node.NodeKeyService;

@Service
public class PathService {
	
	@Autowired
	private NodePathMapper pathMapper;
	
	@Autowired
	private NodeKeyMapper nodeKeyMapper;
	
	@Autowired
	private MemService memService;

	@Autowired
    private MemDeviceMapper memDeviceMapper;

	@Autowired
    private MemDeviceOlineMapper memDeviceOlineMapper;
	
	@Autowired
	private RedisHandler redisHandler;

	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private MemGuestInfoService memGuestInfoService;
	
	@Autowired
	private NodePathAutoGroupMapper nodePathAutoGroupMapper;
	
	private static Logger log = LoggerFactory.getLogger(PathService.class);
	
	private static String ENCRYPTKEY = Env.getVal("mem.aes.key");
	
	private final String DEF_PATH_CODE = "ZIDONG";
	
	@Autowired
	private MemExtInfoService memExtInfoService;
	
	@Autowired
	private MemInfoConfigService memInfoConfigService;
	
	private String errSubInfo() {
		
		StringBuffer allSubAddr = new StringBuffer();
		
		String result = V2raySubLinkTools.getSrV2rayLink("/", "127.0.0.1", 8080, "充值域名:user.kuainiaojsq.xyz", "999999");
		allSubAddr.append(result).append("\n");
		
		String resultTime = V2raySubLinkTools.getSrV2rayLink("/", "127.0.0.1", 8080, "该链接已失效", "999999");
		allSubAddr.append(resultTime).append("\n");
		
		return Base64Utils.encodeToString(allSubAddr.toString().trim());
		
	}
	
	public String getSubLinkAll(String code, String type) {
		
		String userId = null;
		//判断code是userId 还是 subKey
		//如果是userId，查询is_sub_key 如果唯一表示0 表示可以userid用来订阅，如果为1 表示可以只能用subkey订阅
		if (code.length() > 12) {
			userId = code;
			
			MemInfoConfig memInfoConfig = memInfoConfigService.getMemConfigAndAddByMemId(userId);
			//如果为1表示只能用subkey
			if (memInfoConfig.getIsSubKey() == 1) {
				return errSubInfo();
			}
			
		}else {//如果是subKey，通过subkey查询memId
			
			userId = memInfoConfigService.getMemIdBySubKey(code);
			
			if (StringUtils.isEmpty(userId)) {
				return errSubInfo();
			}
			
		}
		
		List<NodePath> nodePathList = getPathAndTestInfos(userId, false);
		
		//随机取一个v2ray账号
		V2User v2User = getRandomV2rayUser();
		
		StringBuffer allSubAddr = new StringBuffer();
		
		MemInfo memInfo = memService.queryMemById(userId);
		
		int surDayNum = memInfo.getSurDayNum();
		
		Date susDate = memInfo.getSuspenDate();
		
		String title1 = "充值域名:user.kuainiaojsq.xyz";
		String title2 = surDayNum > 0 ? "剩余:" + surDayNum + "天" : "会员已过期";
		//小火箭v2ray配置
		if ("srvmess".equals(type)) {
			String result = V2raySubLinkTools.getSrV2rayLink("/", "127.0.0.1", 8080, title1, v2User.getId());
			allSubAddr.append(result).append("\n");
			
			String resultTime = V2raySubLinkTools.getSrV2rayLink("/", "127.0.0.1", 8080, title2, v2User.getId());
			allSubAddr.append(resultTime).append("\n");
		}
		
		for (NodePath pathInfo : nodePathList) {
			String serverArr = pathInfo.getServerArr();
			
			List<Server> servList = JsonUtils.jsonToList(serverArr, Server.class);
			
			//随机取一个服务
			Random random = new Random();
			int n = random.nextInt(servList.size());
			Server server = servList.get(n);
			
			
			Map<String,String> extMap = new HashMap<String, String>();
			extMap.put("dId", userId);
			//先搞死,快加速都是付费用户，不限速
			extMap.put("lLvl", "L0");
			
			if (!StringUtils.isEmpty(pathInfo.getNode())) {
				extMap.put("node", pathInfo.getNode());
			}
			
			extMap.put("plat", PlatTypeEnum.KN.name());
			
			//MemInfo memInfo = memService.queryMemById(userId);
			
//			if (memInfo == null) {
//				
//				throw new AuthException(MemCode.MEM_IS_NOT_EXISTS, "Member has expired, please again recharge!");
//				
//			}

			//Date susDate = memInfo.getSuspenDate();
			
//			if (susDate == null || memInfo.isExpire()) {
//				
//				throw new AuthException(MemCode.MEM_EXPIRE, "Member has expired, please again recharge!");
//				
//			}
			
			String v2Path = V2RayEncry.getEncryPathV2(pathInfo.getPath(), userId, susDate.getTime(), extMap);
			
			
			if ("generalvmess".equals(type)) {
				String result = V2raySubLinkTools.getGeneralV2rayLink(pathInfo.getPath() + "/" + v2Path, server.getIp(), server.getPort(), pathInfo.getPathName(), v2User.getId());
				allSubAddr.append(result).append("\n");
			}
			
			//小火箭v2ray配置
			if ("srvmess".equals(type)) {
				String result = V2raySubLinkTools.getSrV2rayLink(pathInfo.getPath() + "/" + v2Path, server.getIp(), server.getPort(), pathInfo.getPathName(), v2User.getId());
				allSubAddr.append(result).append("\n");
			}
			
		}
		
		return Base64Utils.encodeToString(allSubAddr.toString().trim());
	}
	
	public List<NodePath> getPathAndTestInfos(String memId, boolean isShowZidong) {
		
		List<NodePath> pathList = pathMapper.getPathInfoAllUse();
		
		//添加自动线路放在第一个，作为默认，自动线路是虚拟的
		String isShowZiDong = Env.getVal("path.zidong.show");
		if ("1".equals(isShowZiDong) && isShowZidong) {
			pathList.add(0, buildDefPathInfo());
		}
		
		if (StringUtils.isEmpty(memId)) {
			return pathList;
		}
		
		MemInfoConfig memInfoConfig = memInfoConfigService.getMemConfigById(memId);
		
		if (memInfoConfig == null) {
			return pathList;
		}
		
		List<MemConfigPathInfo> testPathList = JsonUtils.jsonToList(memInfoConfig.getPathArr(), MemConfigPathInfo.class);
		
		if (testPathList == null || testPathList.isEmpty()) {
			return pathList;
		}
		
		List<String> codeList = new ArrayList<String>();
		
		for (MemConfigPathInfo memConfigPathInfo : testPathList) {
			codeList.add(memConfigPathInfo.getCode());
		}
		
		List<NodePath> pathTestList = pathMapper.getPathTestInfo(codeList);
		
		if (pathTestList == null || pathTestList.isEmpty()) {
			return pathList;
		}
		
		pathList.addAll(pathTestList);
		
		//redisHandler.set(RedisKey.PATH_MEM_LIST + memId, pathList, 1 * 1 * 30);
		
		return pathList;
		
	}
	
	public String getSubLinkByCode(String userId, String type, String code) {
		
		NodePath pathInfo = pathMapper.getPathInfoAllUseByCode(code);
		
		String serverArr = pathInfo.getServerArr();
		
		List<Server> servList = JsonUtils.jsonToList(serverArr, Server.class);
		
		Map<String,String> extMap = new HashMap<String, String>();
		extMap.put("dId", userId);
		//先搞死,快加速都是付费用户，不限速
		extMap.put("lLvl", "L0");
		
		if (!StringUtils.isEmpty(pathInfo.getNode())) {
			extMap.put("node", pathInfo.getNode());
		}
		
		extMap.put("plat", PlatTypeEnum.KN.name());
		
		MemInfo memInfo = memService.queryMemById(userId);
		
//		if (memInfo == null) {
//			
//			throw new AuthException(MemCode.MEM_IS_NOT_EXISTS, "Member has expired, please again recharge!");
//			
//		}

		Date susDate = memInfo.getSuspenDate();
		
//		if (susDate == null || memInfo.isExpire()) {
//			
//			throw new AuthException(MemCode.MEM_EXPIRE, "Member has expired, please again recharge!");
//			
//		}
		
		String v2Path = V2RayEncry.getEncryPathV2(pathInfo.getPath(), userId, susDate.getTime(), extMap);
		
		//随机取一个服务
		Random random = new Random();
		int n = random.nextInt(servList.size());
		Server server = servList.get(n);
		
		//随机取一个v2ray账号
		V2User v2User = getRandomV2rayUser();
		
		//常用配置
		if ("generalvmess".equals(type)) {
			String result = V2raySubLinkTools.getGeneralV2rayLink(pathInfo.getPath() + "/" + v2Path, server.getIp(), server.getPort(), pathInfo.getPathName(), v2User.getId());
			return result;
		}
		
		//小火箭v2ray配置
		if ("srvmess".equals(type)) {
			String result = V2raySubLinkTools.getSrV2rayLink(pathInfo.getPath() + "/" + v2Path, server.getIp(), server.getPort(), pathInfo.getPathName(), v2User.getId());
			return result;
		}
		
		return "";
	}
	
	private NodePath buildDefPathInfo() {
	
		NodePath pathInfo = new NodePath();
		String name = Env.getVal("path.zidong.name");
		String iconUrl = Env.getVal("path.zidong.iconUrl");
		pathInfo.setPathId(DEF_PATH_CODE);
		pathInfo.setPathCode(DEF_PATH_CODE);
		pathInfo.setRunStatus("great");
		pathInfo.setPathName(StringUtils.isEmpty(name)? "自动" : name);
		pathInfo.setIconUrl(StringUtils.isEmpty(iconUrl)? "http://" : iconUrl);
		
		return pathInfo;
	}
	
	private V2User getRandomV2rayUser() {
		
		NodeKey nodeKey = nodeKeyMapper.getNodeKeyByAge0();
		
		if (NodeKeyStatus.create.getCode() == nodeKey.getStatus()) {
			nodeKey = nodeKeyMapper.getNodeKeyByAge1();
		}
		
		String keyArr = nodeKey.getKeyArry();
		List<V2User> v2List = JsonUtils.jsonToList(keyArr, V2User.class);
		
		int n = new Random().nextInt(v2List.size());
		V2User v2User = v2List.get(n);
		
		return v2User;
	}
	
	
	public List<NodePath> getPathInfos() {
		
		return pathMapper.getPathInfoAllUse();
		
	}
	
	public List<NodePath> getPathInfosUseByGrade(String pathGrade) {
		
		List<NodePath> pathInfoList = pathMapper.getPathInfoAllUseByGrade(pathGrade);
		
		if (pathInfoList.isEmpty()) {
			return new ArrayList<NodePath>();
		}
		
		//添加自动线路放在第一个，作为默认，自动线路是虚拟的
		String isShowZiDong = Env.getVal("path.zidong.show");
		if ("1".equals(isShowZiDong)) {
			pathInfoList.add(0, buildDefPathInfo());
		}
				
		return pathInfoList;
		
	}
	
	public List<NodePath> getPathInfosByUserId(String userId) {
		
		/*if (StringUtils.isEmpty(userId)) {
			return pathMapper.getPathInfoAllUseByGrade(PathGrade.lev_1.getGrade());
		}
		
		// 获取用户线路级别
		String pathGrade = memService.getMemPathGrade(userId);
		
		List<NodePath> pathInfoList = pathMapper.getPathInfoAllUseByGrade(pathGrade);*/
		
		List<NodePath> pathInfoList = pathMapper.getPathInfoAllUseByGrade(PathGrade.lev_0.getGrade());
		
		if (pathInfoList.isEmpty()) {
			return new ArrayList<NodePath>();
		}
		
		//添加自动线路放在第一个，作为默认，自动线路是虚拟的
		String isShowZiDong = Env.getVal("path.zidong.show");
		if ("1".equals(isShowZiDong)) {
			pathInfoList.add(0, buildDefPathInfo());
		}
				
		return pathInfoList;
		
	}
	
	
	public NodePath getPathInfosByCode(String code) {
		
		return pathMapper.getPathInfoAllUseByCode(code);
		
	}
	
	/**
	 * 通过权重获取code
	 * @return
	 */
	/*private String getPathCodeByRandom(String gradeCode) {
		List<NodePath> pathList = pathMapper.getPathInfoAllUseByWeight(gradeCode);
		
		List<WeightObj> weightObjList = new ArrayList<WeightObj>();
		for (NodePath pathInfo : pathList) {
			if (pathInfo.getWeight() == 0) {
				continue;
			}
			
			weightObjList.add(new WeightObj(pathInfo.getPathCode(), pathInfo.getWeight()));
		}
		
		
		WeightAlgo weightAlgo = new WeightAlgo(weightObjList);
		
		String code = weightAlgo.getWeight();
		
		if (StringUtils.isEmpty(code)) {
			log.warn("get random path code is null, default use first code");
			code = pathList.get(0).getPathCode();
			
		}
		log.info("user use random path, weight paths = {}, random path code value = {}", weightObjList, code);
		return code;
	}*/
	
	private NodePath getDefaultZiDongPath(String gradeCode) {
		NodePath pathInfo = pathMapper.getPathInfoAllUseByCodeV1("HK", gradeCode);
		return pathInfo;
	}
	
	private NodePath getPathCodeByRandomGroup(String gradeCode) {
		
		List<NodePathAutoGroup> pathAutoGroupList = nodePathAutoGroupMapper.selectAllByType(gradeCode);
		
		if (pathAutoGroupList == null || pathAutoGroupList.isEmpty()) {
			//这里先默认香港吧
			return getDefaultZiDongPath(gradeCode);
		}
		
		List<WeightObj> groupWeightGroup = new ArrayList<WeightObj>();
		
		for (NodePathAutoGroup autoGroup : pathAutoGroupList) {
			
			//组的权重如果0，自动选择就会忽略
			if (autoGroup.getAutoGroupWeight() == 0) {
				continue;
			}
			
			groupWeightGroup.add(new WeightObj(autoGroup.getAutoGroupId(), autoGroup.getAutoGroupWeight()));
		}
		
		WeightAlgo groupWeightAlgo = new WeightAlgo(groupWeightGroup);
		String autoGroupId = groupWeightAlgo.getWeight();
		
		log.debug("user use random path group, weight autoGroupId = {}", autoGroupId);
		
		if (StringUtils.isEmpty(autoGroupId)) {
			//这里先默认香港吧
			return getDefaultZiDongPath(gradeCode);
		}
		
		
		//通过groupId查询线路列表信息
		List<NodePath> nodePathList = pathMapper.getPathInfoByAutoGroup(gradeCode, autoGroupId);
		
		List<WeightObj> pathWeightGroup = new ArrayList<WeightObj>();
		
		for (NodePath pathInfo : nodePathList) {
			
			//组的权重如果0，自动选择就会忽略
			if (pathInfo.getWeight() == 0) {
				continue;
			}
			
			pathWeightGroup.add(new WeightObj(pathInfo.getPathCode(), pathInfo.getWeight()));
		}
		
		WeightAlgo pathWeightAlgo = new WeightAlgo(pathWeightGroup);
		String pathCode = pathWeightAlgo.getWeight();
		
		//这里如果pathcode为空，在判断里面是否有权重为0的，有的话作为备用
		if (StringUtils.isEmpty(pathCode)) {
			
			if (nodePathList != null && nodePathList.size() > 0) {
				//这个地方要改进做出随机的
				NodePath pathInfo = nodePathList.get(new Random().nextInt(nodePathList.size()));
				log.info("user use random path, weight paths = {}, random path code value = {}", pathInfo.getWeight(), pathInfo.getPathCode());
				return pathInfo;
			}
			
			//这里先默认香港吧
			return getDefaultZiDongPath(gradeCode);
		}
		
		
		//如果随机到了直接用pathcode对应的path信息进行返回
		NodePath pathInfo = nodePathList.stream()
				.filter(item -> item.getPathCode().equals(pathCode))
				.findFirst()
				.get();
		log.info("user use random path, weight paths = {}, random path code value = {}", pathWeightGroup, pathCode);
		return pathInfo;
	}
	
	/**
	 * 先获取0age，没有写成功的获取1age
	 * @param isEncrypt 
	 * @return
	 */
	@Deprecated
	public Map<String, Object> getPathByCodeV2(String code, String userId, boolean isSplitFlow, String deviceType, String dId, boolean isEncrypt) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		MemInfo memInfo = memService.queryMemById(userId);
		
		if (memInfo == null) {
			
			throw new AuthException(MemCode.MEM_IS_NOT_EXISTS, "Member has expired, please again recharge!");
			
		}

		// 更新用户实时设备信息
		deviceOlineUpdate(dId, memInfo.getMemId(), memInfo.getDeviceNum());
		
		Date susDate = memInfo.getSuspenDate();
		
		if (susDate == null || memInfo.isExpire()) {
			
			throw new AuthException(MemCode.MEM_EXPIRE, "Member has expired, please again recharge!");
			
		}
		
		NodeKey nodeKey = nodeKeyMapper.getNodeKeyByAge0();
		
		if (NodeKeyStatus.create.getCode() == nodeKey.getStatus()) {
			nodeKey = nodeKeyMapper.getNodeKeyByAge1();
		}
		
		String keyArr = nodeKey.getKeyArry();
		List<V2User> v2List = JsonUtils.jsonToList(keyArr, V2User.class);
		
		NodePath pathInfo = null;
		if (DEF_PATH_CODE.equals(code)) {
			// 获取用户线路级别
			String pathGrade = memService.getMemPathGrade(userId);
			pathInfo = this.getPathCodeByRandomGroup(pathGrade);
		}else {
			pathInfo = pathMapper.getPathInfoAllUseByCode(code);
		}
		
		if (pathInfo == null) {
			return null;
		}
		
		int n = new Random().nextInt(v2List.size());
		V2User v2User = v2List.get(n);
		
		String serverArr = pathInfo.getServerArr();
		
		List<Server> servList = JsonUtils.jsonToList(serverArr, Server.class);
		
		List<UserAndServ> userAndServs = new ArrayList<UserAndServ>();
		
		for (Server server : servList) {
			
			List<V2User> v2UserList = new ArrayList<V2User>();
			v2UserList.add(v2User);

			UserAndServ userAndSe = new UserAndServ(v2UserList, server.getIp(), server.getPort());
			
			userAndServs.add(userAndSe);
		}
		
		//setttings
		Settings settings = new Settings(userAndServs);
		
		Map<String,String> extMap = new HashMap<String, String>();
		extMap.put("dId", dId);
		extMap.put("plat", SysCons.getPlatType());
		
		String v2Path = V2RayEncry.getEncryPathV2(pathInfo.getPath(), userId, susDate.getTime(), extMap);
		
		log.debug("v2Path = {}", v2Path);
		//streamSetting
		StreamSettings streamSettings = new StreamSettings(pathInfo.getNetwork(), pathInfo.getSecurity(), new WsSettings(pathInfo.getPath() + "/" + v2Path));
		
		List<?> outBounds = toOutbounds(pathInfo.getProtocol(), settings, streamSettings, ("ios".equals(deviceType) ? true : false));
		Map<String, Object> v2Config = new HashMap<String, Object>();
		v2Config.put("outbounds", outBounds);
		v2Config.put("routing", JsonUtils.jsonToPojo(JsonUtils.objectToJson(toRouting(isSplitFlow, deviceType), true), Map.class));
		String dnsJson = "{\"servers\": [{\"address\": \"114.114.114.114\",\"port\": 53,\"domains\": [\"geosite:cn\",\"domain:heibaojsq.xyz\",\"domain:api.heibaohouduan.xyz\",\"domain:web.heibaohouduan.xyz\"]},{\"address\": \"8.8.8.8\",\"port\": 53,\"domains\": [\"geosite:geolocation-!cn\",\"geosite:speedtest\"]},\"1.1.1.1\",\"localhost\"]}";
		v2Config.put("dns", JsonUtils.jsonToPojo(dnsJson, Map.class));
		result.put("pathInfo", pathInfo);
		result.put("v2Config", v2Config);

		// IOS配置
		String iosConfig = Env.getVal("ios.param.config");
		Map<String, Object> paramConfig = JSON.parseObject(iosConfig, Map.class);
		result.put("paramConfig", paramConfig);
		result.put("serverTime", new Date().getTime());

		// 设备轮询时间
		String pollingTime = Env.getVal("mem.device.polling.time");
		result.put("pollingTime", pollingTime);

		JsonUtils.objectToJson(result);
		
		try {
			if(isEncrypt) {
				Map<String, Object> result1 = new HashMap<String, Object>();
				String ciphertext = AESECBUtils.aesEncrypt(JSONObject.toJSONString(result) ,ENCRYPTKEY);
				result1.put("ciphertext", ciphertext);
				return result1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Encrypt ERROR , e = {}", e);
			throw new BusinessException(ResultCode.UNKNOW_ERROR, e.toString()) ;
		}
		
		return result;
	}
	
	/**
	 * 先获取0age，没有写成功的获取1age
	 * @return
	 */
	@Deprecated
	public Map<String, Object> getPathByCode(String code, String userId, boolean isSplitFlow, String deviceType) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		MemInfo memInfo = memService.queryMemById(userId);
		
		if (memInfo == null) {
			
			throw new AuthException(MemCode.MEM_IS_NOT_EXISTS, "Member has expired, please again recharge!");
			
		}
		
		Date susDate = memInfo.getSuspenDate();
		
		if (susDate == null || memInfo.isExpire()) {
			
			throw new AuthException(MemCode.MEM_EXPIRE, "Member has expired, please again recharge!");
			
		}
		
		NodeKey nodeKey = nodeKeyMapper.getNodeKeyByAge0();
		
		if (NodeKeyStatus.create.getCode() == nodeKey.getStatus()) {
			nodeKey = nodeKeyMapper.getNodeKeyByAge1();
		}
		
		String keyArr = nodeKey.getKeyArry();
		List<V2User> v2List = JsonUtils.jsonToList(keyArr, V2User.class);
		
		NodePath pathInfo = null;
		// 获取用户线路级别
		String pathGrade = memService.getMemPathGrade(userId);
		if (DEF_PATH_CODE.equals(code)) {
			pathInfo = this.getPathCodeByRandomGroup(pathGrade);
		}else {
			pathInfo = pathMapper.getPathInfoAllUseByCodeV1(code, pathGrade);
		}

		if (pathInfo == null) {
			return null;
		}

		int n = new Random().nextInt(v2List.size());
		V2User v2User = v2List.get(n);
		
		String serverArr = pathInfo.getServerArr();
		
		List<Server> servList = JsonUtils.jsonToList(serverArr, Server.class);
		
		List<UserAndServ> userAndServs = new ArrayList<UserAndServ>();
		
		for (Server server : servList) {
			
			List<V2User> v2UserList = new ArrayList<V2User>();
			v2UserList.add(v2User);

			UserAndServ userAndSe = new UserAndServ(v2UserList, server.getIp(), server.getPort());
			
			userAndServs.add(userAndSe);
		}
		
		//setttings
		Settings settings = new Settings(userAndServs);
		
		String v2Path = V2RayEncry.getEncryPath(pathInfo.getPath(), userId, susDate.getTime());
		
		log.debug("v2Path = {}", v2Path);
		//streamSetting
		StreamSettings streamSettings = new StreamSettings(pathInfo.getNetwork(), pathInfo.getSecurity(), new WsSettings(pathInfo.getPath() + "/" + v2Path));
		
		List<?> outBounds = toOutbounds(pathInfo.getProtocol(), settings, streamSettings, ("ios".equals(deviceType) ? true : false));
		Map<String, Object> v2Config = new HashMap<String, Object>();
		v2Config.put("outbounds", outBounds);
		v2Config.put("routing", JsonUtils.jsonToPojo(JsonUtils.objectToJson(toRouting(isSplitFlow, deviceType), true), Map.class));
		String dnsJson = "{\"servers\": [{\"address\": \"114.114.114.114\",\"port\": 53,\"domains\": [\"geosite:cn\",\"domain:heibaojsq.xyz\",\"domain:api.heibaohouduan.xyz\",\"domain:web.heibaohouduan.xyz\"]},{\"address\": \"8.8.8.8\",\"port\": 53,\"domains\": [\"geosite:geolocation-!cn\",\"geosite:speedtest\"]},\"1.1.1.1\",\"localhost\"]}";
		v2Config.put("dns", JsonUtils.jsonToPojo(dnsJson, Map.class));
		result.put("pathInfo", pathInfo);
		result.put("v2Config", v2Config);

		// IOS配置
		String iosConfig = Env.getVal("ios.param.config");
		Map<String, Object> paramConfig = JSON.parseObject(iosConfig, Map.class);
		result.put("paramConfig", paramConfig);
		result.put("serverTime", new Date().getTime());

		JsonUtils.objectToJson(result);
		return result;
	}
	
	private List<?> toOutbounds(String protocol, Settings settings, StreamSettings streamSettings, boolean isMux) {
		OutBound proxy = new OutBound(NodeKeyService.memProxyTag, protocol, settings, streamSettings);
		proxy.setMux(isMux ? new Mux(true, 1000) : null);
		List<OutBound> outbounds = new ArrayList<OutBound>();
		outbounds.add(proxy);
		
		Settings freedomSettings = new Settings("UseIP");
		OutBound freedom = new OutBound("Direct", "freedom", freedomSettings, null);
		outbounds.add(freedom);
		
		OutBound dns = new OutBound("Dns-Out", "dns", null, null);
		outbounds.add(dns);
		
		List<?> outBoundList = JsonUtils.jsonToList(JsonUtils.objectToJson(outbounds, true), Object.class) ;
		return outBoundList;
	}
	
	private Routing toRouting(boolean isSplitFlow, String deviceType) {
		
		List<Rule> rules = new ArrayList<Rule>();
		
		Rule dns2TunRule = new Rule("field", "Dns-Out");
		dns2TunRule.setInboundTag("tun2socks");
		dns2TunRule.setNetwork("udp");
		dns2TunRule.setPort(53);
		rules.add(dns2TunRule);
		
		/*Rule cnRule = new Rule("field", NodeKeyService.memProxyTag);
		cnRule.setDomain("googleapis.cn,google.cn");
		rules.add(cnRule);*/
		
		Rule dnsExtRule = new Rule("field", NodeKeyService.memProxyTag);
		dnsExtRule.setIp("8.8.8.8,1.1.1.1");
		rules.add(dnsExtRule);
		
		if (isSplitFlow) {
			Rule cnDomainRule = new Rule("field", "Direct");
			cnDomainRule.setDomain("geosite:cn,domain:heibaojsq.xyz,domain:api.heibaohouduan.xyz,domain:web.heibaohouduan.xyz");
			rules.add(cnDomainRule);
			
			Rule cnIpRule = new Rule("field", "Direct");
			cnIpRule.setIp("geoip:cn,geoip:private,223.5.5.5,114.114.114.114");
			rules.add(cnIpRule);
		} else {
			Rule cnDomainRule = new Rule("field", "Direct");
			cnDomainRule.setDomain("domain:heibaojsq.xyz,domain:api.heibaohouduan.xyz,domain:web.heibaohouduan.xyz");
			rules.add(cnDomainRule);
		}
		
		Rule proxyRule = new Rule("field", NodeKeyService.memProxyTag);
		proxyRule.setDomain("geolocation-!cn");
		rules.add(proxyRule);
		
		//如果是安卓不要network的这个配置,安卓v2 4.27加上这个配置后，不生效，反而导致智能分流不生效
		/*
		 * if ("ad".equals(deviceType)) {
		 * 
		 * }else { Rule vmessRule = new Rule("field", NodeKeyService.memProxyTag);
		 * vmessRule.setNetwork("tcp,udp"); rules.add(vmessRule); }
		 */
		
		Rule vmessRule = new Rule("field", NodeKeyService.memProxyTag);
		vmessRule.setNetwork("tcp,udp");
		rules.add(vmessRule);
		
		
		Routing routing = new Routing(rules, "IPIfNonMatch");
		
		return routing;
	}

	public void deviceOlineUpdate(String dId, String userId, String deviceNum){
		try {
			if (StringUtils.isEmpty(dId)) {
				// 当设备号为null时，打印日志，清空用户在线设备信息
				log.warn("mem device no is not exists!");
				// deviceService.resetDeviceOline(userId);
				throw new AuthException(MemCode.MEM_DEVICE_IS_NOT_EXISTS, "mem device no is not exists!");
			}

			//解析设备号 0:设备类型 1:设备名 2:设备号唯一标识
			String[] dIdSplit = dId.split("-");
			if(dIdSplit.length < 3){
				// 设备号错误
				log.warn("mem device analysis error! deviceNo = " + dId);
				// deviceService.resetDeviceOline(userId);
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

			// 查询当前用户实时在线表
			MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
			if (memDeviceOline == null) {
				List<Map> OlineMapList = new ArrayList<>();
				Map<String, Object> OlineMap = new HashMap<>();
				OlineMap.put("deviceNo", dId);
				OlineMap.put("deviceType", dIdSplit[0]);
				OlineMap.put("deviceName", dIdSplit[1]);
				OlineMap.put("useTime", new Date());
				OlineMapList.add(OlineMap);

				MemDeviceOline insertMemDeviceOline = new MemDeviceOline();
				insertMemDeviceOline.setMemId(userId);
				insertMemDeviceOline.setDeviceJson(JsonUtils.objectToJson(OlineMapList));
				insertMemDeviceOline.setUpdateTime(new Date());
				memDeviceOlineMapper.insertDynamic(insertMemDeviceOline);
			} else {
				// 若用户实时设备在线表数据不为null，则修改
				List<Map> deviceList = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
				// 允许在线的设备数量
				Integer envSize = new Integer(1);
				if (deviceNum != null) {
					envSize = Integer.parseInt(deviceNum);
				}

				// 当在线设备数小于允许在线设备数时，新增当前设备
				if (deviceList.size() < envSize){
					for(int i = deviceList.size() - 1; i >= 0; i--){
						if (deviceList.get(i).get("deviceNo").equals(dId)) {
							deviceList.remove(i);
						}
					}
					Map<String, Object> OlineMap = new HashMap<>();
					OlineMap.put("deviceNo", dId);
					OlineMap.put("deviceType", dIdSplit[0]);
					OlineMap.put("deviceName", dIdSplit[1]);
					OlineMap.put("useTime", new Date());
					deviceList.add(OlineMap);
				} else if (deviceList.size() == envSize) {
					boolean is = true;
					// 当在线设备数等于允许在线设备数时，默认删除最老的一条设备信息添加，若已存在，则删除之后再添加
					for(int i = deviceList.size() - 1; i >= 0; i--){
						if (deviceList.get(i).get("deviceNo").equals(dId)) {
							deviceList.remove(i);
							Map<String, Object> OlineMap = new HashMap<>();
							OlineMap.put("deviceNo", dId);
							OlineMap.put("deviceType", dIdSplit[0]);
							OlineMap.put("deviceName", dIdSplit[1]);
							OlineMap.put("useTime", new Date());
							deviceList.add(OlineMap);
							is = false;
						}
					}

					if (is) {
						// 删除最老的设备信息
						deviceList.remove(0);
						Map<String, Object> OlineMap = new HashMap<>();
						OlineMap.put("deviceNo", dId);
						OlineMap.put("deviceType", dIdSplit[0]);
						OlineMap.put("deviceName", dIdSplit[1]);
						OlineMap.put("useTime", new Date());
						deviceList.add(OlineMap);
					}
				} else if (deviceList.size() > envSize) {
					// 当前设备在线数大于允许在线设备数时，先添加新的设备信息，然后大于几个设备就删除相对于老的设备
					for(int i = deviceList.size() - 1; i >= 0; i--){
						if (deviceList.get(i).get("deviceNo").equals(dId)) {
							deviceList.remove(i);
						}
					}
					Map<String, Object> OlineMap = new HashMap<>();
					OlineMap.put("deviceNo", dId);
					OlineMap.put("deviceType", dIdSplit[0]);
					OlineMap.put("deviceName", dIdSplit[1]);
					OlineMap.put("useTime", new Date());
					deviceList.add(OlineMap);

					int size = deviceList.size() - envSize;
					for ( int i = 0; i < size; i++){
						deviceList.remove(i);
					}
				}

				memDeviceOline.setMemId(userId);
				memDeviceOline.setDeviceJson(JsonUtils.objectToJson(deviceList));
				memDeviceOline.setUpdateTime(new Date());
				memDeviceOlineMapper.updateDynamic(memDeviceOline);
			}

		} catch (Exception e) {
			log.error("Exception， e = {}", e);
		}
	}

	public void deviceOlineUpdateV1(String dId, String userId, String deviceNum){
		try {
			if (StringUtils.isEmpty(dId)) {
				// 当设备号为null时，打印日志，清空用户在线设备信息
				log.warn("mem device no is not exists!");
				deviceService.resetDeviceOline(userId);
				// throw new AuthException(MemCode.MEM_DEVICE_IS_NOT_EXISTS, "mem device no is not exists!");
			} else {
				//解析设备号 0:设备类型 1:设备名 2:设备号唯一标识
				String[] dIdSplit = dId.split("-");
				if(dIdSplit.length < 3){
					// 设备号错误
					log.warn("mem device analysis error! deviceNo = " + dId);
					deviceService.resetDeviceOline(userId);
					// throw new AuthException(MemCode.MEM_DEVICE_ANALYSIS_ERROR, "mem device analysis error! deviceNo = " + dId);
				} else {
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

					// 查询当前用户实时在线表
					MemDeviceOline memDeviceOline = memDeviceOlineMapper.selectByMemId(userId);
					if (memDeviceOline == null) {
						List<Map> OlineMapList = new ArrayList<>();
						Map<String, Object> OlineMap = new HashMap<>();
						OlineMap.put("deviceNo", dId);
						OlineMap.put("deviceType", dIdSplit[0]);
						OlineMap.put("deviceName", dIdSplit[1]);
						OlineMap.put("useTime", new Date());
						OlineMapList.add(OlineMap);

						MemDeviceOline insertMemDeviceOline = new MemDeviceOline();
						insertMemDeviceOline.setMemId(userId);
						insertMemDeviceOline.setDeviceJson(JsonUtils.objectToJson(OlineMapList));
						insertMemDeviceOline.setUpdateTime(new Date());
						memDeviceOlineMapper.insertDynamic(insertMemDeviceOline);
					} else {
						// 若用户实时设备在线表数据不为null，则修改
						List<Map> deviceList = JsonUtils.jsonToList(memDeviceOline.getDeviceJson(), Map.class);
						// 允许在线的设备数量
						Integer envSize = new Integer(1);
						if (deviceNum != null) {
							envSize = Integer.parseInt(deviceNum);
						}

						// 当在线设备数小于允许在线设备数时，新增当前设备
						if (deviceList.size() < envSize){
							for(int i = deviceList.size() - 1; i >= 0; i--){
								if (deviceList.get(i).get("deviceNo").equals(dId)) {
									deviceList.remove(i);
								}
							}
							Map<String, Object> OlineMap = new HashMap<>();
							OlineMap.put("deviceNo", dId);
							OlineMap.put("deviceType", dIdSplit[0]);
							OlineMap.put("deviceName", dIdSplit[1]);
							OlineMap.put("useTime", new Date());
							deviceList.add(OlineMap);
						} else if (deviceList.size() == envSize) {
							boolean is = true;
							// 当在线设备数等于允许在线设备数时，默认删除最老的一条设备信息添加，若已存在，则删除之后再添加
							for(int i = deviceList.size() - 1; i >= 0; i--){
								if (deviceList.get(i).get("deviceNo").equals(dId)) {
									deviceList.remove(i);
									Map<String, Object> OlineMap = new HashMap<>();
									OlineMap.put("deviceNo", dId);
									OlineMap.put("deviceType", dIdSplit[0]);
									OlineMap.put("deviceName", dIdSplit[1]);
									OlineMap.put("useTime", new Date());
									deviceList.add(OlineMap);
									is = false;
								}
							}

							if (is) {
								// 删除最老的设备信息
								deviceList.remove(0);
								Map<String, Object> OlineMap = new HashMap<>();
								OlineMap.put("deviceNo", dId);
								OlineMap.put("deviceType", dIdSplit[0]);
								OlineMap.put("deviceName", dIdSplit[1]);
								OlineMap.put("useTime", new Date());
								deviceList.add(OlineMap);
							}
						} else if (deviceList.size() > envSize) {
							// 当前设备在线数大于允许在线设备数时，先添加新的设备信息，然后大于几个设备就删除相对于老的设备
							for(int i = deviceList.size() - 1; i >= 0; i--){
								if (deviceList.get(i).get("deviceNo").equals(dId)) {
									deviceList.remove(i);
								}
							}
							Map<String, Object> OlineMap = new HashMap<>();
							OlineMap.put("deviceNo", dId);
							OlineMap.put("deviceType", dIdSplit[0]);
							OlineMap.put("deviceName", dIdSplit[1]);
							OlineMap.put("useTime", new Date());
							deviceList.add(OlineMap);

							int size = deviceList.size() - envSize;
							List<Map> addList = new ArrayList<>();
							for(int i = deviceList.size() - 1; i >= size; i--){
								addList.add(deviceList.get(i));
							}

							deviceList = addList;
						}

						memDeviceOline.setMemId(userId);
						memDeviceOline.setDeviceJson(JsonUtils.objectToJson(deviceList));
						memDeviceOline.setUpdateTime(new Date());
						memDeviceOlineMapper.updateDynamic(memDeviceOline);
					}
				}
			}

		} catch (Exception e) {
			log.error("Exception， e = {}", e);
		}
	}


	public Map<String, Object> getPathByCodeV3(String code, String userId, boolean isSplitFlow, String deviceType, String dId, boolean isEncrypt, String tokenType) {
		
		//更新扩展信息表
		try {
			
			MemInfo memInfo = memService.queryMemById(userId);
			
			if (memInfo != null) {
				MemExtInfo memExtInfoTemp = new MemExtInfo();
				memExtInfoTemp.setId(userId);
				Device device = DeviceUtils.getDeviceInfo(dId);
				memExtInfoTemp.setDeviceNo(device.getDeviceNo());
				memExtInfoTemp.setDeviceType(device.getDeviceType());
				memExtInfoTemp.setDeviceName(device.getDeviceName());
				boolean res = memExtInfoService.updateById(memExtInfoTemp);
				if(!res) {
					memExtInfoService.save(memExtInfoTemp);
				}
			}
			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Map<String, Object> result = new HashMap<String, Object>();
		Date susDate = null;
		
		//触发线路实时统计
		EventMsgProducter.build().sendEventMsg(EventMsg.createActivePathCountRegMsg(userId, code));
		
		//触发实时活跃online用户
		//EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(userId));
		
		EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(userId));
		
		//触发实时活跃online设备
		if (!StringUtils.isEmpty(dId)) {
			EventMsgProducter.build().sendEventMsg(EventMsg.createDeviceUserRegMsg(dId));
		}
		
		if(AuthEnum.guest_us.name().equals(tokenType)) {
			//游客连接
			//String key = RedisKey.GUEST_INFO + userId;
			GuestInfo guestInfo = memGuestInfoService.getGuestById(userId);
			//Long suspenDate = (Long) redisHandler.hGet(key, "suspenDate");
			Long suspenDate = guestInfo.getSuspenDate().getTime();
			
			MemInfo memInfo = new MemInfo();
			memInfo.setSuspenDate(new Date(suspenDate));
			
			// 更新用户实时设备信息
			//deviceOlineUpdateV1(dId, userId, "1");
			
			susDate = memInfo.getSuspenDate();
			
			if (susDate == null || memInfo.isExpire()) {
				
				throw new AuthException(MemCode.MEM_EXPIRE, "Member has expired, please again recharge!");
				
			}
			
			//String channelCode = (String) redisHandler.hGet(key, "channelCode");
			String channelCode = guestInfo.getChannelCode();
			ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createActiveGuestPeopleCount(channelCode, userId));
			
		}else {
			MemInfo memInfo = memService.queryMemById(userId);
			
			if (memInfo == null) {
				
				throw new AuthException(MemCode.MEM_IS_NOT_EXISTS, "Member has expired, please again recharge!");
				
			}

			// 更新用户实时设备信息
			//deviceOlineUpdateV1(dId, memInfo.getMemId(), memInfo.getDeviceNum());
			
			susDate = memInfo.getSuspenDate();
			
			if (susDate == null || memInfo.isExpire()) {
				
				throw new AuthException(MemCode.MEM_EXPIRE, "Member has expired, please again recharge!");
				
			}
			
			ChannelMsgProducter.build().sendChannelMsg(ChannelMsg.createActiveRegPeopleCount(memInfo.getChannelCode(), userId));
		}
		
		NodeKey nodeKey = nodeKeyMapper.getNodeKeyByAge0();
		
		if (NodeKeyStatus.create.getCode() == nodeKey.getStatus()) {
			nodeKey = nodeKeyMapper.getNodeKeyByAge1();
		}
		
		String keyArr = nodeKey.getKeyArry();
		List<V2User> v2List = JsonUtils.jsonToList(keyArr, V2User.class);

		// 获取用户线路级别
		String pathGrade = memService.getMemPathGrade(userId);
		
		NodePath pathInfo = null;
		if (DEF_PATH_CODE.equals(code)) {
			// 随机获取code
			pathInfo = this.getPathCodeByRandomGroup(pathGrade);
		}else {
			pathInfo = pathMapper.getPathInfoAllUseByCodeV1(code, pathGrade);
		}

		if (pathInfo == null) {
			return null;
		}
		
		Object v2Config = toConfig(code, pathInfo, v2List, userId, susDate, deviceType, isSplitFlow, dId, isEncrypt);
		
		
		result.put("pathInfo", pathInfo);
		result.put("v2Config", v2Config);

		// IOS配置
		String iosConfig = Env.getVal("ios.param.config");
		Map<String, Object> paramConfig = JSON.parseObject(iosConfig, Map.class);
		result.put("paramConfig", paramConfig);
		result.put("serverTime", new Date().getTime());

		// 设备轮询时间
		String pollingTime = Env.getVal("mem.device.polling.time");
		result.put("pollingTime", pollingTime);

		
		try {
			if(isEncrypt) {
				String ciphertext = AESECBUtils.aesEncrypt(JSONObject.toJSONString(v2Config) ,ENCRYPTKEY);
				result.put("v2Config", ciphertext);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Encrypt ERROR , e = {}", e);
			throw new BusinessException(ResultCode.UNKNOW_ERROR, e.toString()) ;
		}
		
		
		
		
		
		return result;
	}
	
	private Object toConfig(String code, NodePath pathInfo, List<V2User> v2List, String userId, Date susDate, String deviceType, boolean isSplitFlow, String dId, boolean isEncrypt) {
		
		int n = new Random().nextInt(v2List.size());
		V2User v2User = v2List.get(n);
		
		String serverArr = pathInfo.getServerArr();
		
		List<Server> servList = JsonUtils.jsonToList(serverArr, Server.class);
		
		Map<String,String> extMap = new HashMap<String, String>();
		extMap.put("dId", dId);
		//先搞死,快加速都是付费用户，不限速
		//获取用户限速级别
		String lLvl = memService.getLimitLvlByMemId(userId);
		extMap.put("lLvl", lLvl);
		extMap.put("plat", SysCons.getPlatType());
		
		if (!StringUtils.isEmpty(pathInfo.getNode())) {
			extMap.put("node", pathInfo.getNode());
		}
		
		String v2Path = V2RayEncry.getEncryPathV2(pathInfo.getPath(), userId, susDate.getTime(), extMap);
		
		//String v2Path = V2RayEncry.getEncryPathV2(pathInfo.getPath(), userId, susDate.getTime(), extMap);
		
		//如果是ios返回ini字符串格式，用base64编码
		if (deviceType != null && "IOS".equals(deviceType.toUpperCase())) {
			
			//v2ray这个变量是用base64，这里要去掉后面的等号，为了解决ios的bug，对ini格式产生破坏
			v2Path = v2Path.replaceAll("=", "");
			
			try {
				Ini ini = new Ini();
				
				ini.add("General", "loglevel", "trace");
				ini.add("General", "dns-server", "114.114.114.114,8.8.8.8");
				ini.add("General", "tun-fd", "REPLACE-ME-WITH-THE-FD");
				
				ini.add("Proxy", "Reject", "reject");
				ini.add("Proxy", "Direct", "direct");
				
				int num = 0;
				String proxyGroup = "";
				
				//String v2Path = V2RayEncry.getEncryPath(pathInfo.getPath(), userId, susDate.getTime());
				
				for (Server server : servList) {
					
					String proxyName = "vmess" + num;
					//ini.add("Proxy", "vmess1", "vmess, ec2-18-167-102-93.ap-east-1.compute.amazonaws.com, 10175, username=5a26cc45-3e1a-4d33-8d1a-e19a5e6058b8, ws=true, tls=false, ws-path=/general/hk");
					String proxyProtocol = String.format("%s, %s, %d, username=%s, ws=%b, tls=false, ws-path=%s",pathInfo.getProtocol(), server.getIp(), server.getPort(), v2User.getId(), true, pathInfo.getPath() + "/" + v2Path);
					
					ini.add("Proxy", proxyName, proxyProtocol);
					
					proxyGroup = proxyGroup + proxyName + ",";
					
					num++;
				}
				//ini.add("Proxy", "vmess1", "vmess, ec2-18-167-102-93.ap-east-1.compute.amazonaws.com, 10175, username=5a26cc45-3e1a-4d33-8d1a-e19a5e6058b8, ws=true, tls=false, ws-path=/general/hk");
				
				String proxyGroupStr = String.format("fallback, %s, interval=600, timeout=5", proxyGroup.substring(0, proxyGroup.length() - 1));
				
				ini.add("Proxy Group", "Fallback", proxyGroupStr);
				
				ini.add("Rule", "DOMAIN-SUFFIX, heibaohouduan.com, Direct", "##");
				ini.add("Rule", "DOMAIN-SUFFIX, heibaojiasuqi.com, Direct", "##");
				
				ini.add("Rule", "IP-CIDR, 8.8.8.8/32, Fallback", "##");
				ini.add("Rule", "DOMAIN, www.google.com, Fallback", "##");
				ini.add("Rule", "DOMAIN-SUFFIX, google.com, Fallback", "##");
				ini.add("Rule", "DOMAIN-KEYWORD, google, Fallback", "##");
				
				ini.add("Rule", "EXTERNAL, site:cn, Direct", "##");
				ini.add("Rule", "EXTERNAL, mmdb:cn, Direct", "##");
				ini.add("Rule", "FINAL, Fallback", "##");
				
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ini.store(outputStream);
				
				@SuppressWarnings("deprecation")
				String outputConfig = StringEscapeUtils.unescapeJava(new String(outputStream.toByteArray()));
				outputConfig = outputConfig.replaceAll(" = ##", "");
				
				String config = Base64Utils.encodeToString(outputConfig);
				
				outputStream.close();
			
				/*if(isEncrypt) {
					config = AESECBUtils.aesEncrypt(config ,ENCRYPTKEY);
				}*/
				
				return config;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("pathservice, get v3 Exception ERROR , e = {}", e);
				throw new BusinessException(ResultCode.UNKNOW_ERROR, e.toString()) ;
			}
			
		}
		
		//streamSetting
		StreamSettings streamSettings = new StreamSettings(pathInfo.getNetwork(), pathInfo.getSecurity(), new WsSettings(pathInfo.getPath() + "/" + v2Path));
		
		List<OutBound> outbounds = new ArrayList<OutBound>();
		
		int nameIndex = 0;
		List<String> selector = new ArrayList<String>();
		for (Server server : servList) {
			
			List<UserAndServ> userAndServs = new ArrayList<UserAndServ>();
			
			List<V2User> v2UserList = new ArrayList<V2User>();
			v2UserList.add(v2User);

			UserAndServ userAndSe = new UserAndServ(v2UserList, server.getIp(), server.getPort());
			
			userAndServs.add(userAndSe);
			
			Settings settings = new Settings(userAndServs);
			
			String tagName = "proxy-" + nameIndex;
			
			//这里临时加上，如果断开包含443默认走ssl
			if (String.valueOf(server.getPort()).contains("443")) {
				streamSettings.setSecurity("tls");
			}
			
			addProxyTag(outbounds, tagName, "vmess", settings, streamSettings, false);
			
			selector.add(tagName);
			
			nameIndex++;
		}
		
		addOtherTag(outbounds);
		
		//List<?> outBounds = toOutbounds(pathInfo.getProtocol(), settings, streamSettings, ("ios".equals(deviceType) ? true : false));
		Map<String, Object> v2Config = new HashMap<String, Object>();
		v2Config.put("outbounds", JsonUtils.jsonToList(JsonUtils.objectToJson(outbounds, true), Object.class));
		v2Config.put("routing", JsonUtils.jsonToPojo(JsonUtils.objectToJson(toRouting(isSplitFlow, deviceType, code, selector), true), Map.class));
		String dnsJson = "{\"servers\": [{\"address\": \"114.114.114.114\",\"port\": 53,\"domains\": [\"geosite:cn\",\"domain:heibaojsq.xyz\",\"domain:api.heibaohouduan.xyz\",\"domain:web.heibaohouduan.xyz\",\"domain:web.heibaohouduan.com\",\"domain:api.heibaohouduan.com\",\"domain:web.heibaojiasuqi.com\",\"domain:api.heibaojiasuqi.com\"]},{\"address\": \"8.8.8.8\",\"port\": 53,\"domains\": [\"geosite:geolocation-!cn\",\"geosite:speedtest\"]},\"1.1.1.1\",\"localhost\"]}";
		v2Config.put("dns", JsonUtils.jsonToPojo(dnsJson, Map.class));
		
		return v2Config;
	}

	private void addProxyTag(List<OutBound> outbounds, String tagName, String protocol, Settings settings, StreamSettings streamSettings, boolean isMux) {
		OutBound proxy = new OutBound(tagName, protocol, settings, streamSettings);
		proxy.setMux(isMux ? new Mux(true, 1000) : null);
		outbounds.add(proxy);
	}
	
	private void addOtherTag(List<OutBound> outbounds) {
		Settings freedomSettings = new Settings("UseIP");
		OutBound freedom = new OutBound("Direct", "freedom", freedomSettings, null);
		outbounds.add(freedom);
		
		OutBound dns = new OutBound("Dns-Out", "dns", null, null);
		outbounds.add(dns);
	}
	
	private Routing toRouting(boolean isSplitFlow, String deviceType, String blanceName, List<String> selector) {
		
		List<Rule> rules = new ArrayList<Rule>();
		
		Rule dns2TunRule = new Rule("field", "Dns-Out");
		dns2TunRule.setInboundTag("tun2socks");
		dns2TunRule.setNetwork("udp");
		dns2TunRule.setPort(53);
		rules.add(dns2TunRule);
		
		/*Rule cnRule = new Rule("field", NodeKeyService.memProxyTag);
		cnRule.setDomain("googleapis.cn,google.cn");
		rules.add(cnRule);*/
		
		Rule dnsExtRule = new Rule("field", null);
		dnsExtRule.setBalancerTag(blanceName);
		dnsExtRule.setIp("8.8.8.8,1.1.1.1");
		rules.add(dnsExtRule);
		
		if (isSplitFlow) {
			Rule cnDomainRule = new Rule("field", "Direct");
			cnDomainRule.setDomain("geosite:cn,domain:heibaojsq.xyz,domain:api.heibaohouduan.xyz,domain:web.heibaohouduan.xyz,domain:heibaohouduan.com,domain:heibaojiasuqi.com");
			rules.add(cnDomainRule);
			
			Rule cnIpRule = new Rule("field", "Direct");
			cnIpRule.setIp("geoip:cn,geoip:private,223.5.5.5,114.114.114.114");
			rules.add(cnIpRule);
		} else {
			Rule cnDomainRule = new Rule("field", "Direct");
			cnDomainRule.setDomain("domain:heibaojsq.xyz,domain:api.heibaohouduan.xyz,domain:web.heibaohouduan.xyz,domain:heibaohouduan.com,domain:heibaojiasuqi.com");
			rules.add(cnDomainRule);
		}
		
		Rule vmessRule = new Rule("field", null);
		vmessRule.setNetwork("tcp,udp");
		vmessRule.setBalancerTag(blanceName);
		rules.add(vmessRule);
		
		List<Selector> balancers = new ArrayList<Selector>();
		balancers.add(new Selector(blanceName, selector));
		
		Routing routing = new Routing(rules, "IPIfNonMatch", balancers);
		
		return routing;
	}
}
