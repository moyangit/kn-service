package com.tsn.serv.common.cons.redis;

import com.tsn.serv.common.cons.sys.SysCons;

/**
 * 模块:业务类型：key-value
 * @author work
 *
 */
public class RedisKey {
	
	public static String USER_FLOW_LIMIT = SysCons.getPlatRedisSuffix() + "user:flow:limit:";
	
	public static String USER_ID_USERINFO = SysCons.getPlatRedisSuffix() + "user:id:userinfo:";
	public static String USER_DEVICE_NO_USERINFO = SysCons.getPlatRedisSuffix() + "user:device:userinfo:";
	
	/**
	 * 用户授权参数错误
	 */
	public static String USER_LOGIN_PHONE_SMSCODE = SysCons.getPlatRedisSuffix() + "user:login:phone-smscode:";
	
	public static String USER_REG_PHONE_SMSCODE = SysCons.getPlatRedisSuffix() + "user:reg:phone-smscode:1";

	public static String USER_FORGET_PHONE_SMSCODE = SysCons.getPlatRedisSuffix() + "user:forget:phone-smscode:";
	
	public static String CONN_SERV_PATH = SysCons.getPlatRedisSuffix() + "conn:serv:path:all";
	
	public static String MEM_TYPE_LIMIT_FLOW = SysCons.getPlatRedisSuffix() + "mem:flow:limit:config";
	
	//通过会员ID获取流量及最大限制流量情况
	public static String MEM_LIMIT_FLOW_ID = SysCons.getPlatRedisSuffix() + "mem:flow:limit:";
	
	public static String AUTH_USER_SECRET = SysCons.getPlatRedisSuffix() + "auth:user:secret:";
	
	public static String MEM_INVI_CODE = SysCons.getPlatRedisSuffix() + "incr:mem:invi:code";
	
	public static String GUEST_KEY = SysCons.getPlatRedisSuffix() + "guest:deviceNo:memId:";
	
	public static String GUEST_INFO = SysCons.getPlatRedisSuffix() + "guest:memInfo:";
	
	public static String GUEST_DAY = SysCons.getPlatRedisSuffix() + "guest:day:";
	
	public static String GUEST_DAY_PF = SysCons.getPlatRedisSuffix() + "guest:day:pf:";
	
	public static String GUEST_TOTAL = SysCons.getPlatRedisSuffix() + "guest:total";
	
	//临时存放用户Id和缓存的关系
	public static String USER_TOKEN = SysCons.getPlatRedisSuffix() + "user:token:";
	
	//临时存放用户Id和最后几次订单的关系
	public static String USER_LAST_ORDERs = SysCons.getPlatRedisSuffix() + "user:last:orders:";
	
	//临时存放防红链接
	public static String SHARE_LINK_CODE = SysCons.getPlatRedisSuffix() + "share:code:sharelink:";
	
	//订单统计数据
	public static String STATIS_ORDER_DATA_KEY = SysCons.getPlatRedisSuffix() + "statis:order:data";
	
	//用户统计数据
	public static String STATIS_USER_DATA_KEY = SysCons.getPlatRedisSuffix() + "statis:user:data";
	
	//5分钟在线用户数，用Set+score队列存储
	public static String USER_ONLINE_COUNT = SysCons.getPlatRedisSuffix() + "user:online:count";
	
	public static String USER_ONLINE_MAX_DAY_COUNT = SysCons.getPlatRedisSuffix() + "user:online:maxcount:day:";
	
	//5分钟在线游客数，用Set+score队列存储
	public static String GUEST_ONLINE_COUNT = SysCons.getPlatRedisSuffix() + "guest:online:count";
	//5分钟在线试用用户
	public static String TRYUSER_ONLINE_COUNT = SysCons.getPlatRedisSuffix() + "tryuser:online:count";
	//5分钟在线过期会员
	public static String EXPIREUSER_ONLINE_COUNT = SysCons.getPlatRedisSuffix() + "expireuser:online:count";
	//5分钟在线会员
	public static String NOEXPIREUSER_ONLINE_COUNT = SysCons.getPlatRedisSuffix() + "noexpireuser:online:count";
	
	//5分钟在线用户数，用Set+score队列存储
	public static String DEVICE_ONLINE_COUNT = SysCons.getPlatRedisSuffix() + "device:online:count:";
	
	public static String DEVICE_COUNT_DAY = SysCons.getPlatRedisSuffix() + "device:count:day:";
	
	public static String DEVICE_ONLINE_MAX_DAY_COUNT = SysCons.getPlatRedisSuffix() + "device:online:maxcount:day:";
	
	//用户每日的活跃数包括试用注册用户，充值用户，游客,这是总数，用pf, 
	@Deprecated
	public static String USER_ACTIVE_COUNT1 = SysCons.getPlatRedisSuffix() + "user:active:count:";
	
	//用户每日的活跃数包括注册用户，用pf
	public static String GUEST_ACTIVE_COUNT = SysCons.getPlatRedisSuffix() + "guest:active:count:";
	
	//试用用户每日的活跃数，用pf
	public static String TRYUSER_ACTIVE_COUNT = SysCons.getPlatRedisSuffix() + "tryuser:active:count:";
	
	//过期会员用户每日的活跃数，用pf
	public static String EXPIREUSER_ACTIVE_COUNT = SysCons.getPlatRedisSuffix() + "expireuser:active:count:";
	
	//未过期会员用户每日的活跃数，用pf
	public static String NOEXPIREUSER_ACTIVE_COUNT = SysCons.getPlatRedisSuffix() + "noexpireuser:active:count:";
	
	//线路点击人数
	public static String PATH_ACTIVE_PEOPLE_COUNT = SysCons.getPlatRedisSuffix() + "path:active:people:count:";
	
	//线路点击次数
	public static String PATH_ACTIVE_CLICK_COUNT = SysCons.getPlatRedisSuffix() + "path:active:click:count:";
	
	//-----存放留存用户----------
	
	//设置每日的留存人数,3天内，5天内，7天内等等连续每天使用的用户数
	public static String KEEP_STORE_PEOPLE_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:people:count:";
	
	//游客
	public static String KEEP_GUEST_STORE_PEOPLE_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:guestpeople:count:";
	
	//自行注册用户
	public static String KEEP_ZXUSER_STORE_PEOPLE_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:zxpeople:count:";
	
	//邀请用户
	public static String KEEP_YQUSER_STORE_PEOPLE_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:yqpeople:count:";
	
	//设置每日做任务的留存人数,3天内，5天内，7天内等等连续每天使用的用户数
	public static String KEEP_STORE_PEOPLE_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:people:count:";
	
	//游客
	public static String KEEP_GUEST_STORE_PEOPLE_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:guestpeople:count:";
	
	//自行注册用户
	public static String KEEP_ZXUSER_STORE_PEOPLE_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:zxpeople:count:";
	
	//邀请用户
	public static String KEEP_YQUSER_STORE_PEOPLE_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:yqpeople:count:";
	
	
	//所有新注册用户
	public static String KEEP_ALL_STORE_PEOPLE_REG_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:people:reg:count:";
	
	//自行注册用户
	public static String KEEP_GUEST_STORE_PEOPLE_REG_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:gueustpeople:reg:count:";
	
	//自行注册用户
	public static String KEEP_ZXUSER_STORE_PEOPLE_REG_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:zxpeople:reg:count:";
	
	//邀请用户
	public static String KEEP_YQUSER_STORE_PEOPLE_REG_COUNT_DAY = SysCons.getPlatRedisSuffix() + "keep:day:yqpeople:reg:count:";
	
	//设置每日做任务的留存人数,3天内，5天内，7天内等等连续每天使用的用户数
	//public static String KEEP_STORE_PEOPLE_REG_COUNT_MONTH1 = SysCons.getPlatRedisSuffix() + "keep:month:people:reg:count:";
	
	//所有新注册用户
	public static String KEEP_ALL_STORE_PEOPLE_REG_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:people:reg:count:";
	
	//游客
	public static String KEEP_GUEST_STORE_PEOPLE_REG_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:guestpeople:reg:count:";
	
	//自行注册用户
	public static String KEEP_ZXUSER_STORE_PEOPLE_REG_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:zxpeople:reg:count:";
	
	//邀请用户
	public static String KEEP_YQUSER_STORE_PEOPLE_REG_COUNT_MONTH = SysCons.getPlatRedisSuffix() + "keep:month:yqpeople:reg:count:";
	
	// ------- 新用户 日活-------
	//有效用户记录用户在线时长, 默认10分钟
	public static String USER_EFFECTIVE_ONIINE_TIME = SysCons.getPlatRedisSuffix() + "user:effect:online:time:";
	
	//每日注册用户包括所有类型 有效用户
	@Deprecated
	public static String USER_EFFECTIVE_REG_COUNT1 = SysCons.getPlatRedisSuffix() + "user:effect:reg:count:";
	
	//每日游客 有效用户
	public static String GUEST_EFFECTIVE_REG_COUNT = SysCons.getPlatRedisSuffix() + "guestuser:effect:reg:count:";
		
	//每日自行用户 有效用户
	public static String ZXUSER_EFFECTIVE_REG_COUNT = SysCons.getPlatRedisSuffix() + "zxuser:effect:reg:count:";
	
	//每日邀请用户 有效用户
	public static String YQUSER_EFFECTIVE_REG_COUNT = SysCons.getPlatRedisSuffix() + "yquser:effect:reg:count:";
	
	// -------- 所有用户日活 --------
	//所有的有效用户数
	public static String ALL_EFFECTIVE_COUNT1 = SysCons.getPlatRedisSuffix() + "all:effect:count:";
	
	//试用用户每日的有效用户数，用pf
	public static String GUEST_EFFECTIVE_COUNT = SysCons.getPlatRedisSuffix() + "guest:effect:count:";
	
	//试用用户每日的有效用户数，用pf
	public static String TRYUSER_EFFECTIVE_COUNT = SysCons.getPlatRedisSuffix() + "tryuser:effect:count:";
	
	//过期会员用户每日有效用户数，用pf
	public static String EXPIREUSER_EFFECTIVE_COUNT = SysCons.getPlatRedisSuffix() + "expireuser:effect:count:";
	
	//未过期会员用户每日有效用户数，用pf
	public static String NOEXPIREUSER_EFFECTIVE_COUNT = SysCons.getPlatRedisSuffix() + "noexpireuser:effect:count:";
	
	//新增用户及日活缓存, 按月
	public static String REG_EFFECT_MONTH = SysCons.getPlatRedisSuffix() + "reg:effect:month:";
	
	
	//**每日百宝箱点击次数**//
	public static String BOX_CLICK_COUNT_DAY = SysCons.getPlatRedisSuffix() + "box:click:count:day:";
	
	//**每日百宝箱点击人数**//
	public static String BOX_PEOPLE_COUNT_DAY = SysCons.getPlatRedisSuffix() + "box:people:count:day:";
	
	
	
	//**vm回调记录回收金额，用来存放cId**//
	public static String VM_QUEUE_CID_CHANNEL = SysCons.getPlatRedisSuffix() + "channel:vm:queue:cid:";
	
	/** 广告记录 展示量 **/
	public static String AD_DAY_SHOW = SysCons.getPlatRedisSuffix() + "ad:day:show:";
	
	/** 广告记录 点击量 **/
	public static String AD_DAY_CLICK = SysCons.getPlatRedisSuffix() + "ad:day:click:";
	
	/** 广告记录 点击人数 **/
	public static String AD_DAY_USER = SysCons.getPlatRedisSuffix() + "ad:day:user:";
	
}
