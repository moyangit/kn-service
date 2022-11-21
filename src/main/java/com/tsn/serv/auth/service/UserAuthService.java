package com.tsn.serv.auth.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

import sun.misc.BASE64Encoder;

import com.google.common.collect.Maps;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.core.jms.MqHandler;
import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.CommUtils;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.utils.tools.regx.RegxValidUtils;
import com.tsn.common.utils.utils.tools.security.MD5Utils;
import com.tsn.common.utils.web.exception.AuthException;
import com.tsn.common.utils.web.utils.env.Env;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.common.web.context.JwtLocal;
import com.tsn.common.web.core.JwtTokenFactory;
import com.tsn.common.web.entity.AccessToken;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.entity.JwtInfo;
import com.tsn.serv.auth.entity.Result;
import com.tsn.serv.auth.entity.User;
import com.tsn.serv.auth.service.email.EmailService;
import com.tsn.serv.auth.service.email.EmailService.MailParam;
import com.tsn.serv.common.code.auth.AuthCode;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsgProducter;
import com.tsn.serv.common.utils.msg.RCSCloudAPI;
import com.tsn.serv.mem.entity.mem.GuestInfo;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.mem.Settings;
import com.tsn.serv.mem.service.geo.GeoService;

@Service
public class UserAuthService {
	
/*	@Autowired
	private MemAuthFeignService memService;*/
	
	@Autowired
	private MemAuthFeignService memService;
	
	@Autowired
	private EmailService emailService;
	
	private Logger logger = LoggerFactory.getLogger(UserAuthService.class);
	
	@Autowired
	private RedisHandler redisHandler;

	@Autowired
	private MqHandler mqHandler;
	
	@Autowired
	private JwtTokenFactory jwtFactory;
	
	private Random random = new Random();
	private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";// 随机产生的字符串

	private int width = 80;// 图片宽
	private int height = 26;// 图片高
	private int lineSize = 40;// 干扰线数量
	private int stringNum = 4;// 随机产生字符数量

	/**
	 *	账号
	 */
	@Value("${ACCOUNT_SID}")
	private String ancountSid;

	/**
	 * APIKEY
	 */
	@Value("${ACCOUNT_APIKEY}")
	private String ancountApiKey;

	/**
	 * utf8编码
	 */
	@Value("${CHARSET_UTF8}")
	private String charsetUtf8;

	/**
	 * HttpUrl
	 */
	@Value("${HttpUrl}")
	private String httpUrl;  //或使用ip:  http://121.41.114.153:8030/rcsapi/rest

	/**
	*
	*/
	@Value("${TPlID}")
	private String tplId;
	
	@Autowired
	private GeoService geoService;
	
	public void addGuest(Map<String, Object> guestMap) {
		memService.addGuest(guestMap);
	}
	
	public GuestInfo getGuestInfo(String deviceNo) {
		return memService.getGuestByDeviceId(deviceNo);
	}
	
	/**
	 * v2 主要用于手机号和邮箱同时使用
	 * @param cert
	 * @return
	 */
	public AccessToken valideUserByPswdV2(User cert) {
		
		if (StringUtils.isEmpty(cert.getUserName()) || StringUtils.isEmpty(cert.getPassword())) {
			throw new AuthException(AuthCode.AUTH_PARAM_ERROR, "user login name and password is not empty");
		}
		
		MemInfo user = null;
		
		if (RegxValidUtils.checkEmail(cert.getUserName())) {
			user = selectUserByEmail(cert.getUserName());
		}
		
		if (RegxValidUtils.checkPhone(cert.getUserName())) {
			user = selectUserByPhone(cert.getUserName());
		}

		if (user == null) {
			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "用户不存在");
		}
		
		
		String md5Password = DigestUtils.md5Hex(cert.getPassword().getBytes());
		if (!md5Password.equals(user.getMemPwd())){
			throw new AuthException(AuthCode.AUTH_PWD_ERROR, "用户名密码错误");
		}
		
		try {
			geoService.use2saveMemSource(user.getMemId(), cert.getIp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rtnToken(user);
	}

	public AccessToken valideUserByPswd(User cert) {
		
		if (StringUtils.isEmpty(cert.getUserPhone()) || StringUtils.isEmpty(cert.getPassword())) {
			throw new AuthException(AuthCode.AUTH_PARAM_ERROR, "user login name and password is not empty");
		}

		MemInfo user = selectUserByPhone(cert.getUserPhone());
		
		if (user == null) {
			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "用户不存在");
		}
		
		String md5Password = DigestUtils.md5Hex(cert.getPassword().getBytes());
		if (!cert.getUserPhone().equals(user.getMemPhone()) || !md5Password.equals(user.getMemPwd())){
			throw new AuthException(AuthCode.AUTH_PWD_ERROR, "用户名密码错误");
		}
		
		user.setMemPhone(user.getMemPhone().substring(0, 3)+"*****"+user.getMemPhone().substring(8,11));

		
		try {
			geoService.use2saveMemSource(user.getMemId(), cert.getIp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rtnToken(user);
	}
	
	public AccessToken validLoginUserBySmsCode(String phone, String inputCode) {
		
		MemInfo user = selectUserByPhone(phone);
		
		if (user == null) {
			
			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user no exists");
			
		}
		
		String validCode = (String) redisHandler.get(RedisKey.USER_LOGIN_PHONE_SMSCODE + phone);
		
		if (StringUtils.isEmpty(validCode)) {
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code can not be null");
		}
		if(!inputCode.equals(validCode)){
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code");
		}

		return rtnToken(user);
		
	}
	
	
	public AccessToken regMemV2(User user) {
		
		Map<String, String> regMemInfo = new HashMap<String, String>();
		regMemInfo.put("memName", user.getUserName());
		regMemInfo.put("memNickName", user.getNickName());
		regMemInfo.put("memSex", user.getSex());
		regMemInfo.put("memAvatar", user.getUserAvatar());
		regMemInfo.put("memType", user.getUserType());
		regMemInfo.put("memPwd", user.getPassword());
		regMemInfo.put("parentInvCode", user.getParenInviCode());
		regMemInfo.put("terminalType", user.getTerminalType());
		regMemInfo.put("channelCode", user.getChannelCode());
		regMemInfo.put("deviceNo", user.getDeviceNo());

		String validCode = (String) redisHandler.get(RedisKey.USER_REG_PHONE_SMSCODE + user.getUserName());
		
		if (StringUtils.isEmpty(validCode)) {
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code, code can not be null");
		}
		if(!user.getSmsCode().equals(validCode)){
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code");
		}
		if(StringUtils.isEmpty(user.getPassword())){
			throw new AuthException(AuthCode.AUTH_PWD_ERROR, "password is not null");
		}
		
		boolean flag = false;
		
		if (RegxValidUtils.checkEmail(user.getUserName())) {
			MemInfo memInfo = memService.selectUserByEmail(user.getUserName());
			
			if (memInfo != null) {
				throw new AuthException(AuthCode.AUTH_USER_EXISTS_ERROR, "this phone always exists!");
			}
			
			regMemInfo.put("memEmail", user.getUserName());
			flag = true;
		}
		
		if (RegxValidUtils.checkPhone(user.getUserName())) {
			MemInfo memInfo = memService.selectUserByPhone(user.getUserName());
			
			if (memInfo != null) {
				throw new AuthException(AuthCode.AUTH_USER_EXISTS_ERROR, "this phone always exists!");
			}
			
			regMemInfo.put("memPhone", user.getUserName());
			flag = true;
		}
		
		if (!flag) {
			throw new AuthException(AuthCode.AUTH_PARAM_ERROR, "username is no valid");
		}
		
		
		memService.addMem(regMemInfo);
		
		MemInfo newMemInfo = null;
		
		if (RegxValidUtils.checkEmail(user.getUserName())) {
			newMemInfo = memService.selectUserByEmail(user.getUserName());
		}
		
		if (RegxValidUtils.checkPhone(user.getUserName())) {
			newMemInfo = memService.selectUserByPhone(user.getUserName());
		}
		
		try {
			geoService.use2saveMemSource(newMemInfo.getMemId(), user.getIp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtnToken(newMemInfo);
		
	}
	
	
	public AccessToken regMem(User user) {
		
		Map<String, String> regMemInfo = new HashMap<String, String>();
		regMemInfo.put("memName", user.getUserName());
		regMemInfo.put("memNickName", user.getNickName());
		regMemInfo.put("memSex", user.getSex());
		regMemInfo.put("memPhone", user.getUserPhone());
		regMemInfo.put("memAvatar", user.getUserAvatar());
		regMemInfo.put("memType", user.getUserType());
		regMemInfo.put("memPwd", user.getPassword());
		regMemInfo.put("parentInvCode", user.getParenInviCode());
		regMemInfo.put("terminalType", user.getTerminalType());
		regMemInfo.put("channelCode", user.getChannelCode());
		regMemInfo.put("deviceNo", user.getDeviceNo());

		String validCode = (String) redisHandler.get(RedisKey.USER_REG_PHONE_SMSCODE + user.getUserPhone());
		
		if (StringUtils.isEmpty(validCode)) {
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code, code can not be null");
		}
		if(!user.getSmsCode().equals(validCode)){
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code");
		}
		if(StringUtils.isEmpty(user.getPassword())){
			throw new AuthException(AuthCode.AUTH_PWD_ERROR, "password is not null");
		}
		
		MemInfo memInfo = memService.selectUserByPhone(user.getUserPhone());
		
		if (memInfo != null) {
			throw new AuthException(AuthCode.AUTH_USER_EXISTS_ERROR, "this phone always exists!");
		}
		
		memService.addMem(regMemInfo);
		
		MemInfo newMemInfo = selectUserByPhone(user.getUserPhone());
		
		try {
			geoService.use2saveMemSource(newMemInfo.getMemId(), user.getIp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rtnToken(newMemInfo);
		
	}
	
	public AccessToken regGuestMem(User user) {
		
		//测试代码，要删掉
		//redisHandler.del(RedisKey.USER_DEVICE_NO_USERINFO + user.getDeviceNo());
		
		MemInfo guestInfo = memService.getMemByDeviceId(user.getDeviceNo());
		
		if (guestInfo != null) {
			
			if (!MemTypeEum.guest_mem.getCode().equals(guestInfo.getMemType())) {
				throw new AuthException(AuthCode.AUTH_DEVICE_NOT_LOGIN_ERROR, "deviceNo not login");
			}
			
			return rtnToken(guestInfo);
		}
		
		String guestId = SnowFlakeManager.build().create(GenIDEnum.MEM_ID.name()).getIdByPrefix(GenIDEnum.MEM_ID.getPreFix());
		
		//String memName = "游客" + guestId.substring(guestId.length() - 5);
		
		Map<String, String> regMemInfo = new HashMap<String, String>();
		//regMemInfo.put("memName", memName);
		regMemInfo.put("memSex", user.getSex());
		regMemInfo.put("memPhone", user.getUserPhone());
		regMemInfo.put("memAvatar", user.getUserAvatar());
		regMemInfo.put("memType", user.getUserType());
		regMemInfo.put("memPwd", user.getPassword());
		regMemInfo.put("parentInvCode", user.getParenInviCode());
		regMemInfo.put("terminalType", user.getTerminalType());
		regMemInfo.put("channelCode", user.getChannelCode());
		regMemInfo.put("deviceNo", user.getDeviceNo());
		regMemInfo.put("memId", guestId);
		memService.addMem(regMemInfo);

		MemInfo newMemInfo = new MemInfo();
		newMemInfo.setMemId(guestId);
		newMemInfo.setMemName("游客");
		newMemInfo.setMemNickName(newMemInfo.getMemName());
		newMemInfo.setMemPhone(newMemInfo.getMemName());
		//暂时这样写
		newMemInfo.setMemType("00");
		
		try {
			geoService.use2saveMemSource(guestId, user.getIp());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rtnToken(newMemInfo);
		
	}
	
	public Result validRegUserBySmsCode(String phone, String inputCode) {
		
		MemInfo user = selectUserByPhone(phone);
		
		if (user == null) {
			
			throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user no exists");
			
		}
		
		String validCode = (String) redisHandler.get(RedisKey.USER_LOGIN_PHONE_SMSCODE + phone);
		
		if (StringUtils.isEmpty(validCode)) {
			throw new AuthException(AuthCode.AUTH_USER_CODE_NOT_EMPTY_ERROR, "code can not be null");
		}
		if(!inputCode.equals(validCode)){
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code");
		}
		
		JwtInfo info = new JwtInfo();
		info.setSubject(user.getMemId());
		info.setSubName(user.getMemNickName());
		//写入 权限和角色    暂时为空
		info.setPermiss("all");
		info.setRoles("all");
		
/*		if ("client".equals(cert.getOrigin())) {  
			//一个星期
			info.setExpireTime(24 * 60 * 7);
		}*/
		
		JwtLocal.setJwt(info);
		//拼装accessToken
		AccessToken token = jwtFactory.createAccessJwtToken(AuthEnum.bea_us);
		return new Result(token, info);
		
	}
	
	/**
	 * 生成随机图片
	 */
	public Map<String,Object> getRandcode(HttpServletRequest request) {

		Map<String,Object> resultMap = Maps.newHashMap();
		// BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
		g.setColor(getRandColor(110, 133));
		// 绘制干扰线
		for (int i = 0; i <= lineSize; i++) {
			drowLine(g);
		}
		// 绘制随机字符
		String randomString = "";
		for (int i = 1; i <= stringNum; i++) {
			randomString = drowString(g, randomString, i);
		}
		String tokenType = request.getParameter("tokenType");
		String domain = request.getParameter("domain");
		logger.debug("##########################login randomStr={},loginType={},domain{}",randomString,StringUtils.isEmpty(tokenType)?"is null":tokenType,domain);
		g.dispose();
//		String base64Img = null;
		/**
		 *  登陆方式
		 *  1. 原始的往cookie写登陆信息
		 *  2. 根据tokenType往页面写token
		 */
		ByteArrayOutputStream tmp = new ByteArrayOutputStream();
		try {
			String token = CommUtils.getUuid();
			redisHandler.set((token+randomString).toUpperCase(), randomString);

			ImageIO.write(image, "png", tmp);
			// 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
			byte[] captchaChallengeAsJpeg = tmp.toByteArray();
			// 防止图片不显示 删除转换后的换行符和空格
			BASE64Encoder encoder = new BASE64Encoder();
			String png_base64 = encoder.encodeBuffer(captchaChallengeAsJpeg).trim();//转换成base64串
			png_base64 = png_base64.replaceAll("\n", "").
					replaceAll("\r", "");//删除 \r\n
			resultMap.put("imgBase64",png_base64);
			resultMap.put("code",token);
		} catch (Exception e) {
			logger.error("##########################login imageCode error!exception{}",e);
		}
		return resultMap;
	}
	
	
	/**
	 * 短信发送
	 * @return
	 */
	public String sendSMSCode(@PathVariable String phone, String type){
		logger.info("checkTSMSCode SMS code Start");
		
		String redisKey = null;
		if ("login".equals(type)) {
			redisKey = RedisKey.USER_LOGIN_PHONE_SMSCODE;
			
			MemInfo user = selectUserByPhone(phone);
			if(user == null){
				throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user is not exists");
			}
		} else if ("reg".equals(type)){
			MemInfo user = selectUserByPhone(phone);
			if(user != null){
				throw new AuthException(AuthCode.AUTH_USER_EXISTS_ERROR, "user is already exists");
			}
			redisKey = RedisKey.USER_REG_PHONE_SMSCODE;
			//tempId = "SMS_140625141";
		} else if ("forget".equals(type)){
            redisKey = RedisKey.USER_FORGET_PHONE_SMSCODE;
            
            MemInfo user = selectUserByPhone(phone);
			if(user == null){
				throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user is not exists");
			}
        }
		
		int code = (int) ((Math.random()*9+1)*100000);
		//int flag = 123456;
		//塞入队列
		//"时波网络","SMS_140625141","{\"code\":\"{}\"}"
		String content = "{\"code\":\"" + code + "\"}";
		
		//先60内如果发送了一次，就不能再发
		if (redisHandler.hasKey(redisKey + phone)) {
			
			long expireTime = redisHandler.getExpire(redisKey + phone);
			
			//如果剩余时间大于2分钟，说明发完短信还没有超过一分钟，这种情况就不让再发送
			if (expireTime > 60 * 2) {
				throw new BusinessException(AuthCode.AUTH_USER_CODE_SENDD_ERROR, "phone sms send count 1(num), in 60s");
			}
			
		}
		
		//塞入队列发短信
		//String contentStr = Notify.createAliSms(phone, tempId, content, "xx公司").setBusData(NotifyBusTypeEm.login, null, null, null, NotifyLvlEm.HIGH).toJsonStr();
		
		logger.info(content);
		
		// mqHandler.send(MqCons.QUEUE_NOTIFY_NAME, NotifyUtils.createLoginSms(new SmsBao(phone, "", String.valueOf(code), "5")));
		
		redisHandler.set(redisKey + phone, String.valueOf(code), 60 * 3);
		
		String envType = Env.getVal("spring.profiles.active");
		if ("dev".equals(envType)) {
			return String.valueOf(code);
		}else {
			// 调用短信发送接口
			new RCSCloudAPI().sendTplSms(phone, "@1@=" + String.valueOf(code), "", ancountSid, ancountApiKey, charsetUtf8, httpUrl, tplId);
		}
		
		return "";
		
	}
	
	
	public String sendSMSEmail(String email, String type){
		logger.info("checkTSMSCode SMS code Start");
		
		String redisKey = null;
		if ("login".equals(type)) {
			redisKey = RedisKey.USER_LOGIN_PHONE_SMSCODE;
			
			MemInfo user = selectUserByEmail(email);
			if(user == null){
				throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user is not exists");
			}
		} else if ("reg".equals(type)){
			MemInfo user = selectUserByEmail(email);
			if(user != null){
				throw new AuthException(AuthCode.AUTH_USER_EXISTS_ERROR, "user is already exists");
			}
			redisKey = RedisKey.USER_REG_PHONE_SMSCODE;
			//tempId = "SMS_140625141";
		} else if ("forget".equals(type)){
            redisKey = RedisKey.USER_FORGET_PHONE_SMSCODE;
            
            MemInfo user = selectUserByEmail(email);
			if(user == null){
				throw new AuthException(AuthCode.AUTH_USER_NOT_ISEXISTS_ERROR, "user is not exists");
			}
        }
		
		int code = (int) ((Math.random()*9+1)*100000);
		//int flag = 123456;
		//塞入队列
		String content = "{\"code\":\"" + code + "\"}";
		
		//先60内如果发送了一次，就不能再发
		//String emailMd5 = MD5Utils.digest(email);
		String emailMd5 = email;
		if (redisHandler.hasKey(redisKey + emailMd5)) {
			
			long expireTime = redisHandler.getExpire(redisKey + emailMd5);
			
			//如果剩余时间大于2分钟，说明发完短信还没有超过一分钟，这种情况就不让再发送
			if (expireTime > 60 * 2) {
				throw new BusinessException(AuthCode.AUTH_USER_CODE_SENDD_ERROR, "phone sms send count 1(num), in 60s");
			}
			
		}
		
		//塞入队列发短信
		//String contentStr = Notify.createAliSms(phone, tempId, content, "xx公司").setBusData(NotifyBusTypeEm.login, null, null, null, NotifyLvlEm.HIGH).toJsonStr();
		
		logger.info(content);
		
		// mqHandler.send(MqCons.QUEUE_NOTIFY_NAME, NotifyUtils.createLoginSms(new SmsBao(phone, "", String.valueOf(code), "5")));
		
		redisHandler.set(redisKey + emailMd5, String.valueOf(code), 60 * 3);
		
		String envType = Env.getVal("spring.profiles.active");
		
		if ("dev".equals(envType)) {
			emailService.sendEmail(new MailParam(email, "【快鸟】发送验证码", "您的验证码为：" + code + ",在5分钟之内有效【快鸟】"));
			//emailService.sendMailGun(new MailParam(email, "", "" + code));
			//emailService.sendOwnMail(new MailParam(email, "欢迎使用快加速", "【快加速】您的验证码为：" + code + ",在5分钟之内有效"));
			return String.valueOf(code);
		}else {
			emailService.sendEmail(new MailParam(email, "【快鸟】发送验证码", "您的验证码为：" + code + ",在5分钟之内有效【快鸟】"));
			//emailService.sendOwnMail(new MailParam(email, "欢迎使用快加速", "【快加速】您的验证码为：" + code + ",在5分钟之内有效"));
			/*// 调用短信发送接口
			new RCSCloudAPI().sendTplSms(phone, "@1@=" + String.valueOf(code), "", ancountSid, ancountApiKey, charsetUtf8, httpUrl, tplId);*/
			return "";
		}
		
	}
	
	
	/**
	 * 通过邮箱查询paas用户信息
	 * @param phone
	 * @return
	 */
	public MemInfo selectUserByEmail(String email) {
		try {
			MemInfo memInfo = memService.selectUserByEmail(email);
			return memInfo;
		} catch (Exception e) {
			logger.error("{}", e);
			return null;
		}
	}
	
	
	/*
	 * 绘制字符串
	 */
	private String drowString(Graphics g, String randomString, int i) {
		g.setFont(getFont());
		g.setColor(new Color(random.nextInt(101), random.nextInt(111), random.nextInt(121)));
		String rand = String.valueOf(getRandomString(random.nextInt(randString.length())));
		randomString += rand;
		g.translate(random.nextInt(3), random.nextInt(3));
		g.drawString(rand, 13 * i, 16);
		return randomString;
	}

	/*
	 * 绘制干扰线
	 */
	private void drowLine(Graphics g) {
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		int xl = random.nextInt(13);
		int yl = random.nextInt(15);
		g.drawLine(x, y, x + xl, y + yl);
	}

	/*
	 * 获取随机的字符
	 */
	public String getRandomString(int num) {
		return String.valueOf(randString.charAt(num));
	}
	
	/*
	 * 获得字体
	 */
	private Font getFont() {
		return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
	}

	/*
	 * 获得颜色
	 */
	private Color getRandColor(int fc, int bc) {
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc - 16);
		int g = fc + random.nextInt(bc - fc - 14);
		int b = fc + random.nextInt(bc - fc - 18);
		return new Color(r, g, b);
	}
	
	public Map<String, String> refresh(String reToken, AuthEnum authEnum){
		
		JwtInfo info = jwtFactory.parseJWT(reToken, authEnum);
		
		if (info == null) {
			throw new AuthException(ResultCode.USER_NO_LOGIN_ERROR, "Token 过期或非法");
		}
		
		JwtLocal.setJwt(info);
		//拼装accessToken
		AccessToken token = jwtFactory.createAccessJwtToken(AuthEnum.bea_us);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("accessToken", token.getAccessToken());
		resultMap.put("refreshToken", token.getRefreshToken());
		return resultMap;
		
	}
	
	/**
	 * 通过用户名查询用户信息
	 * @param userName
	 * @return
	 */
	public User queryUserByName(String userName){
		//return userMapper.selectUserByName(userName);
		/*Response<?> res = memService.selectUserByUserName(userName);
		if(!"000000".equals(res.getCode())){
			throw new AuthException(res.getCode(), res.getMsg());
		}
		Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(res.getData()), Map.class);
		User user = JSONObject.parseObject(JSONObject.toJSONString(map.get("user")), User.class);
		return user;*/
		return null;
	}

	/**
	 * 通过手机号查询paas用户信息
	 * @param phone
	 * @return
	 */
	public MemInfo selectUserByPhone(String phone) {
		MemInfo memInfo = memService.selectUserByPhone(phone);
		return memInfo;
	}

	/**
	 * 忘记密码(修改密码)
	 * @param cert
	 * @return
	 */
	public void forget(User cert) {

		String validCode = (String) redisHandler.get(RedisKey.USER_FORGET_PHONE_SMSCODE + cert.getUserPhone());

		if (StringUtils.isEmpty(validCode)) {
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code can not be null");
		}
		if(!cert.getSmsCode().equals(validCode)){
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code");
		}

		Map<String, String> regMemInfo = new HashMap<String, String>();
		MemInfo memInfo = selectUserByPhone(cert.getUserPhone());
		regMemInfo.put("memId", memInfo.getMemId());
		regMemInfo.put("memPwd", cert.getPassword());

		memService.updateMem(regMemInfo);
	}
	
	public void forgetV2(User cert) {

		String validCode = (String) redisHandler.get(RedisKey.USER_FORGET_PHONE_SMSCODE + cert.getUserName());

		if (StringUtils.isEmpty(validCode)) {
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code can not be null");
		}
		if(!cert.getSmsCode().equals(validCode)){
			throw new AuthException(AuthCode.AUTH_USER_CODE_ERROR, "code input no equal to send code");
		}

		Map<String, String> regMemInfo = new HashMap<String, String>();
		
		MemInfo memInfo = null;
		if (RegxValidUtils.checkEmail(cert.getUserName())) {
			memInfo = memService.selectUserByEmail(cert.getUserName());
		}
		
		if (RegxValidUtils.checkPhone(cert.getUserName())) {
			memInfo = memService.selectUserByPhone(cert.getUserName());
		}
		
		if (memInfo == null) {
			throw new AuthException(AuthCode.AUTH_USER_EXISTS_ERROR, "this phone always exists!");
		}
		regMemInfo.put("memId", memInfo.getMemId());
		regMemInfo.put("memPwd", cert.getPassword());

		memService.updateMem(regMemInfo);
	}

	public AccessToken rtnToken(MemInfo user){
		JwtInfo info = new JwtInfo();
		info.setSubject(user.getMemId());
		info.setSubName(MemTypeEum.guest_mem.getCode().equals(user.getMemType()) ? "游客" : user.getMemName());
		//写入 权限和角色    暂时为空
		info.setPermiss("all");
		info.setRoles("all");
		info.setExpireTime(43200);//一个月

		JwtLocal.setJwt(info);
		//拼装accessToken
		AccessToken token = jwtFactory.createAccessJwtToken(AuthEnum.bea_us);

		Settings settings = new Settings();
		String isPayOpen = Env.getVal("isPayOpen");
		settings.setIsPayOpen(isPayOpen);
		user.setSettings(settings);

		token.setData(user);
		
		//触发实时活跃online用户
		EventMsgProducter.build().sendEventMsg(EventMsg.createOnlineUserRegMsg(user.getMemId()));

		try {
			//不影响登录
			memService.updateLogintime(user.getMemId());
		} catch (Exception e) {
			logger.error("updateLogintime, e= {}",e);
		}

		return token;
	}
	
}
