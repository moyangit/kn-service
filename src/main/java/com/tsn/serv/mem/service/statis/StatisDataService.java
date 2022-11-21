package com.tsn.serv.mem.service.statis;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.exception.RequestParamValidException;
import com.tsn.common.utils.utils.CommUtils;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.serv.common.cons.redis.RedisKey;
import com.tsn.serv.common.cons.redis.RedisMqKey;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.common.enm.mem.MemTypeEum;
import com.tsn.serv.common.mq.EventMsg;
import com.tsn.serv.common.mq.EventMsg.EventTypeEnum;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.entity.node.NodePath;
import com.tsn.serv.mem.entity.statis.StatisRegUserKeepDay;
import com.tsn.serv.mem.entity.statis.StatisRegUserKeepMonth;
import com.tsn.serv.mem.entity.statis.StatisUserKeepDay;
import com.tsn.serv.mem.entity.statis.StatisUserKeepMonth;
import com.tsn.serv.mem.mapper.mem.MemInfoMapper;
import com.tsn.serv.mem.mapper.order.ChargeOrderMapper;
import com.tsn.serv.mem.service.mem.MemService;
import com.tsn.serv.mem.service.path.PathService;

/**
 * 统计订单，用户
 * @author Administrator
 *
 */
@Service
public class StatisDataService {
	
	@Autowired
	private RedisHandler redisHandler;
	
	@Autowired
	private ChargeOrderMapper chargeOrderMapper;
	
	@Autowired
	private MemInfoMapper memInfoMapper;
	
	@Autowired
	private MemService memService;
	
	@Autowired
	private PathService pathService;
	
	//存放用户和时间的对应关系，过期时间设置30分钟
	private Cache<String, Long> eventUserIdCache;
	
	@Autowired
	private IStatisUserKeepDayService statisUserKeepDayService;
	
	@Autowired
	private IStatisUserKeepMonthService statisUserKeepMonthService;
	
	@Autowired
	private IStatisRegUserKeepDayService statisRegUserKeepDayService;
	
	@Autowired
	private IStatisRegUserKeepMonthService statisRegUserKeepMonthService;
	
	private static Logger logger = LoggerFactory.getLogger(StatisDataService.class);
	
	public StatisDataService() {
		
		eventUserIdCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).removalListener((RemovalListener<Object, Long>) rn -> 
			{
				logger.warn("eventUserIdCache clean up key {}, value {}, reason {}", rn.getKey(), rn.getValue(), rn.getCause());
			}
		).build();
		
	}
	
	/**
	 * 上一天
	 * @param yyyyMMdd
	 * @param preyyyyMMdd
	 */
	@Transactional
	public void calcAddAndUpdateStaticUserKeepDay(String yyyyMMdd) {
		
		initKeepDay(yyyyMMdd);
		
		calcKeepDay(yyyyMMdd);
		
		initRegKeepDay(yyyyMMdd);
		
		calcKeepRegDay(yyyyMMdd);
		
	}
	
	
	@Transactional
	public void calcAddAndUpdateStaticUserKeepMonth(String yyyyMM) {
		
		initKeepMonth(yyyyMM);
		
		calcKeepMonth(yyyyMM);
		
		initRegKeepMonth(yyyyMM);
		
		calcKeepRegMonth(yyyyMM);
		
	}
	
	private void calcKeepDay(String yyyyMMdd) {
		//获取前30条数据  30 * 4
		QueryWrapper<StatisUserKeepDay> wrapper = new QueryWrapper<>();
	    wrapper.orderByDesc("date_time");
	    //查询最高10条数据
	    wrapper.last("limit 0,120");
	    List<StatisUserKeepDay> statisUserKeepDayList = statisUserKeepDayService.list(wrapper);
	    
	    for (StatisUserKeepDay statisUser : statisUserKeepDayList) {
	    	String type = statisUser.getType();
	    	Date dateTmp = getDateByYmd(statisUser.getDateTime());
	    	int diff = DateUtils.diffDays(statisUser.getDateTime(), yyyyMMdd);
	    	if (diff == 0) {
	    		continue;
	    	}
	    	
	    	//第一个取当日
	    	if (diff == 1) {
	    		Date dateTemp = DateUtils.getCurrDate2Num(dateTmp, diff - 1);
	    		Set<Object> objs = redisHandler.sGet(getKeepKeyByType(type) + DateUtils.getCurrYMD(dateTemp, "yyyyMMdd"));
	    		if (objs == null || objs.isEmpty()) {
	    			continue;
	    		}
	    		statisUser.setDay1(objs.size());
	    		
	    		statisUserKeepDayService.updateById(statisUser);
	    		
	    		continue;
	    	}
	    	
	    	List<String> dateTimeTempListTmp = DateUtils.getDayListAfterRange(dateTmp, DateUtils.getCurrDate2Num(dateTmp, diff - 1), "yyyyMMdd");
			
			List<String> dateTimeTempList = new ArrayList<String>();
			dateTimeTempListTmp.stream().forEach(item -> {
				dateTimeTempList.add(getKeepKeyByType(type) + item);
			});
			
			Set<Object> objs = redisHandler.sinterNoStore(null, dateTimeTempList);
			if (objs == null || objs.isEmpty()) {
    			continue;
    		}
	    	
	    	if(diff == 2) {
	    		statisUser.setDay2(objs.size());
	    	}else if (diff == 3) {
	    		statisUser.setDay3(objs.size());
	    	}else if (diff == 4) {
	    		statisUser.setDay4(objs.size());
	    	}else if (diff == 5) {
	    		statisUser.setDay5(objs.size());
	    	}else if (diff == 6) {
	    		statisUser.setDay6(objs.size());
	    	}else if (diff == 7) {
	    		statisUser.setDay7(objs.size());
	    	}else if (diff == 8) {
	    		statisUser.setDay8(objs.size());
	    	}else if (diff == 9) {
	    		statisUser.setDay9(objs.size());
	    	}else if (diff == 10) {
	    		statisUser.setDay10(objs.size());
	    	}else if (diff == 11) {
	    		statisUser.setDay11(objs.size());
	    	}else if (diff == 12) {
	    		statisUser.setDay12(objs.size());
	    	}else if (diff == 13) {
	    		statisUser.setDay13(objs.size());
	    	}else if (diff == 14) {
	    		statisUser.setDay14(objs.size());
	    	}else if (diff == 15) {
	    		statisUser.setDay15(objs.size());
	    	}else if (diff == 16) {
	    		statisUser.setDay16(objs.size());
	    	}else if (diff == 17) {
	    		statisUser.setDay17(objs.size());
	    	}else if (diff == 18) {
	    		statisUser.setDay18(objs.size());
	    	}else if (diff == 19) {
	    		statisUser.setDay19(objs.size());
	    	}else if (diff == 20) {
	    		statisUser.setDay20(objs.size());
	    	}else if (diff == 21) {
	    		statisUser.setDay21(objs.size());
	    	}else if (diff == 22) {
	    		statisUser.setDay22(objs.size());
	    	}else if (diff == 23) {
	    		statisUser.setDay23(objs.size());
	    	}else if (diff == 24) {
	    		statisUser.setDay24(objs.size());
	    	}else if (diff == 25) {
	    		statisUser.setDay25(objs.size());
	    	}else if (diff == 26) {
	    		statisUser.setDay26(objs.size());
	    	}else if (diff == 27) {
	    		statisUser.setDay27(objs.size());
	    	}else if (diff == 28) {
	    		statisUser.setDay28(objs.size());
	    	}else if (diff == 29) {
	    		statisUser.setDay29(objs.size());
	    	}else if (diff == 30) {
	    		statisUser.setDay30(objs.size());
	    	}
	    	
	    	statisUserKeepDayService.updateById(statisUser);
	    }
	}
	
	private void calcKeepRegDay(String yyyyMMdd) {
		//获取前30条数据  30 * 4
		QueryWrapper<StatisRegUserKeepDay> wrapper = new QueryWrapper<>();
	    wrapper.orderByDesc("date_time");
	    //查询最高10条数据
	    wrapper.last("limit 0,120");
	    List<StatisRegUserKeepDay> statisUserKeepDayList = statisRegUserKeepDayService.list(wrapper);
	    
	    for (StatisRegUserKeepDay statisUser : statisUserKeepDayList) {
	    	String type = statisUser.getType();
	    	Date dateTmp = getDateByYmd(statisUser.getDateTime());
	    	int diff = DateUtils.diffDays(statisUser.getDateTime(), yyyyMMdd);
	    	if (diff == 0) {
	    		continue;
	    	}
	    	
	    	String currRegKeepKey = getKeepRegKeyByType(type) + statisUser.getDateTime();
	    	
	    	//第一个取当日
	    	if (diff == 1) {
	    		//这里取第一个注册的日活数据
		    	Set<Object> objs = redisHandler.sGet(getKeepRegKeyByType(type) + statisUser.getDateTime());
			    if (objs == null || objs.isEmpty()) {
					continue;
				}
	    		statisUser.setDay1(objs.size());
	    		
	    		statisRegUserKeepDayService.updateById(statisUser);
	    		
	    		continue;
	    	}
	    	
	    	//这里取
	    	List<String> dateTimeTempListTmp = DateUtils.getDayListAfterRange(dateTmp, DateUtils.getCurrDate2Num(dateTmp, diff - 1), "yyyyMMdd");
			List<String> dateTimeTempList = new ArrayList<String>();
			for (String item : dateTimeTempListTmp) {
				//如果是当前时间,去掉，其他的取日活留存key
				if (item.equals(statisUser.getDateTime())) {
					continue;
				}
				
				dateTimeTempList.add(getKeepKeyByType(type) + item);
			}
			
			Set<Object> objs = redisHandler.sinterNoStore(currRegKeepKey, dateTimeTempList);
			if (objs == null || objs.isEmpty()) {
    			continue;
    		}
	    	
	    	if(diff == 2) {
	    		statisUser.setDay2(objs.size());
	    	}else if (diff == 3) {
	    		statisUser.setDay3(objs.size());
	    	}else if (diff == 4) {
	    		statisUser.setDay4(objs.size());
	    	}else if (diff == 5) {
	    		statisUser.setDay5(objs.size());
	    	}else if (diff == 6) {
	    		statisUser.setDay6(objs.size());
	    	}else if (diff == 7) {
	    		statisUser.setDay7(objs.size());
	    	}else if (diff == 8) {
	    		statisUser.setDay8(objs.size());
	    	}else if (diff == 9) {
	    		statisUser.setDay9(objs.size());
	    	}else if (diff == 10) {
	    		statisUser.setDay10(objs.size());
	    	}else if (diff == 11) {
	    		statisUser.setDay11(objs.size());
	    	}else if (diff == 12) {
	    		statisUser.setDay12(objs.size());
	    	}else if (diff == 13) {
	    		statisUser.setDay13(objs.size());
	    	}else if (diff == 14) {
	    		statisUser.setDay14(objs.size());
	    	}else if (diff == 15) {
	    		statisUser.setDay15(objs.size());
	    	}else if (diff == 16) {
	    		statisUser.setDay16(objs.size());
	    	}else if (diff == 17) {
	    		statisUser.setDay17(objs.size());
	    	}else if (diff == 18) {
	    		statisUser.setDay18(objs.size());
	    	}else if (diff == 19) {
	    		statisUser.setDay19(objs.size());
	    	}else if (diff == 20) {
	    		statisUser.setDay20(objs.size());
	    	}else if (diff == 21) {
	    		statisUser.setDay21(objs.size());
	    	}else if (diff == 22) {
	    		statisUser.setDay22(objs.size());
	    	}else if (diff == 23) {
	    		statisUser.setDay23(objs.size());
	    	}else if (diff == 24) {
	    		statisUser.setDay24(objs.size());
	    	}else if (diff == 25) {
	    		statisUser.setDay25(objs.size());
	    	}else if (diff == 26) {
	    		statisUser.setDay26(objs.size());
	    	}else if (diff == 27) {
	    		statisUser.setDay27(objs.size());
	    	}else if (diff == 28) {
	    		statisUser.setDay28(objs.size());
	    	}else if (diff == 29) {
	    		statisUser.setDay29(objs.size());
	    	}else if (diff == 30) {
	    		statisUser.setDay30(objs.size());
	    	}
	    	
	    	statisRegUserKeepDayService.updateById(statisUser);
	    }
	}
	
	private void calcKeepMonth(String yyyyMM) {
		//获取前30条数据  12 * 4
		QueryWrapper<StatisUserKeepMonth> wrapper = new QueryWrapper<>();
	    wrapper.orderByDesc("date_time");
	    //查询最高10条数据
	    wrapper.last("limit 0,48");
	    List<StatisUserKeepMonth> statisUserKeepMonthList = statisUserKeepMonthService.list(wrapper);
	    
	    for (StatisUserKeepMonth statisMonth : statisUserKeepMonthList) {
	    	String type = statisMonth.getType();
	    	Date dateTmp = getDateByYmd(statisMonth.getDateTime() + "01");
	    	int diff = DateUtils.diffMonths(statisMonth.getDateTime(), yyyyMM);
	    	if (diff == 0) {
	    		continue;
	    	}
	    	
	    	//第一个取当日
	    	if (diff == 1) {
	    		
	    		Set<Object> objs = redisHandler.sGet(getKeepMonthKeyByType(type) + statisMonth.getDateTime());
	    		if (objs == null || objs.isEmpty()) {
	    			continue;
	    		}
	    		statisMonth.setOne(objs.size());
	    		
	    		statisUserKeepMonthService.updateById(statisMonth);
	    		
	    		continue;
	    	}
	    	
	    	List<String> monthList = DateUtils.getMonthListAfterRange(dateTmp, diff - 1, "yyyyMM");
			List<String> dateTimeTempList = new ArrayList<String>();
			monthList.stream().forEach(item -> {
				dateTimeTempList.add(getKeepMonthKeyByType(type) + item);
			});
			
			Set<Object> objs = redisHandler.sinterNoStore(null, dateTimeTempList);
			if (objs == null || objs.isEmpty()) {
    			continue;
    		}
			
			
			if (diff == 2) {
				statisMonth.setTwo(objs.size());
			}else if (diff == 3) {
				statisMonth.setThree(objs.size());
			}else if (diff == 4) {
				statisMonth.setFour(objs.size());
			}else if (diff == 5) {
				statisMonth.setFive(objs.size());
			}else if (diff == 6) {
				statisMonth.setSix(objs.size());
			}else if (diff == 7) {
				statisMonth.setSeven(objs.size());
			}else if (diff == 8) {
				statisMonth.setEight(objs.size());
			}else if (diff == 9) {
				statisMonth.setNine(objs.size());
			}else if (diff == 10) {
				statisMonth.setTen(objs.size());
			}else if (diff == 11) {
				statisMonth.setEleven(objs.size());
			}else if (diff == 12) {
				statisMonth.setTwelve(objs.size());
			}
	    	
	    	statisUserKeepMonthService.updateById(statisMonth);
	    }
	}
	
	private void calcKeepRegMonth(String yyyyMM) {
		//获取前30条数据  12 * 4
		QueryWrapper<StatisRegUserKeepMonth> wrapper = new QueryWrapper<>();
	    wrapper.orderByDesc("date_time");
	    //查询最高10条数据
	    wrapper.last("limit 0,48");
	    List<StatisRegUserKeepMonth> statisUserKeepMonthList = statisRegUserKeepMonthService.list(wrapper);
	    
	    for (StatisRegUserKeepMonth statisMonth : statisUserKeepMonthList) {
	    	String type = statisMonth.getType();
	    	Date dateTmp = getDateByYmd(statisMonth.getDateTime() + "01");
	    	int diff = DateUtils.diffMonths(statisMonth.getDateTime(), yyyyMM);
	    	if (diff == 0) {
	    		continue;
	    	}
	    	
	    	String currRegKeepKey = getKeepRegKeyByType(type) + statisMonth.getDateTime();
	    	
	    	//第一个取当日
	    	if (diff == 1) {
	    		
	    		Set<Object> objs = redisHandler.sGet(getKeepRegMonthKeyByType(type) + statisMonth.getDateTime());
	    		if (objs == null || objs.isEmpty()) {
	    			continue;
	    		}
	    		statisMonth.setOne(objs.size());
	    		
	    		statisRegUserKeepMonthService.updateById(statisMonth);
	    		
	    		continue;
	    	}
	    	
	    	List<String> monthList = DateUtils.getMonthListAfterRange(dateTmp, diff - 1, "yyyyMM");
			List<String> dateTimeTempList = new ArrayList<String>();
			for (String item : monthList) {
				//如果是当前时间,去掉，其他的取日活留存key
				if (item.equals(statisMonth.getDateTime())) {
					continue;
				}
				
				dateTimeTempList.add(getKeepMonthKeyByType(type) + item);
			}
			
			Set<Object> objs = redisHandler.sinterNoStore(currRegKeepKey, dateTimeTempList);
			if (objs == null || objs.isEmpty()) {
    			continue;
    		}
			
			if (diff == 2) {
				statisMonth.setTwo(objs.size());
			}else if (diff == 3) {
				statisMonth.setThree(objs.size());
			}else if (diff == 4) {
				statisMonth.setFour(objs.size());
			}else if (diff == 5) {
				statisMonth.setFive(objs.size());
			}else if (diff == 6) {
				statisMonth.setSix(objs.size());
			}else if (diff == 7) {
				statisMonth.setSeven(objs.size());
			}else if (diff == 8) {
				statisMonth.setEight(objs.size());
			}else if (diff == 9) {
				statisMonth.setNine(objs.size());
			}else if (diff == 10) {
				statisMonth.setTen(objs.size());
			}else if (diff == 11) {
				statisMonth.setEleven(objs.size());
			}else if (diff == 12) {
				statisMonth.setTwelve(objs.size());
			}
	    	
			statisRegUserKeepMonthService.updateById(statisMonth);
	    }
	}
	
	private void initKeepDay(String yyyyMMdd) {
		
		//判断改日对应的数据是否存在
		String[] types = new String[]{"0","1","2","3"};
		for (String type : types) {
			QueryWrapper<StatisUserKeepDay> queryOneWrapper = new QueryWrapper<StatisUserKeepDay>();
			queryOneWrapper.eq("date_time", yyyyMMdd);
			queryOneWrapper.eq("type", type);
			StatisUserKeepDay statisUserKeepDay = statisUserKeepDayService.getOne(queryOneWrapper);
			
			//如果不存在，添加
			if (statisUserKeepDay == null) {
				StatisUserKeepDay statisUserKeepDayTemp = new StatisUserKeepDay();
				statisUserKeepDayTemp.setType(type);
				statisUserKeepDayTemp.setDateTime(yyyyMMdd);
				statisUserKeepDayService.save(statisUserKeepDayTemp);
			}
		}
		
	}
	
	private void initKeepMonth(String yyyyMM) {
		String[] types = new String[]{"0","1","2","3"};
		for (String type : types) {
			QueryWrapper<StatisUserKeepMonth> queryOneWrapper = new QueryWrapper<StatisUserKeepMonth>();
			queryOneWrapper.eq("date_time", yyyyMM);
			queryOneWrapper.eq("type", type);
			StatisUserKeepMonth statisUserKeepDay = statisUserKeepMonthService.getOne(queryOneWrapper);
			
			//如果不存在，添加
			if (statisUserKeepDay == null) {
				StatisUserKeepMonth statisUserKeepDayTemp = new StatisUserKeepMonth();
				statisUserKeepDayTemp.setType(type);
				statisUserKeepDayTemp.setDateTime(yyyyMM);
				statisUserKeepMonthService.save(statisUserKeepDayTemp);
			}
		}
	}
	
	private void initRegKeepDay(String yyyyMMdd) {
		
		//判断改日对应的数据是否存在
		String[] types = new String[]{"0","1","2","3"};
		for (String type : types) {
			QueryWrapper<StatisRegUserKeepDay> queryOneWrapper = new QueryWrapper<StatisRegUserKeepDay>();
			queryOneWrapper.eq("date_time", yyyyMMdd);
			queryOneWrapper.eq("type", type);
			StatisRegUserKeepDay statisUserKeepDay = statisRegUserKeepDayService.getOne(queryOneWrapper);
			
			//如果不存在，添加
			if (statisUserKeepDay == null) {
				StatisRegUserKeepDay statisUserKeepDayTemp = new StatisRegUserKeepDay();
				statisUserKeepDayTemp.setType(type);
				statisUserKeepDayTemp.setDateTime(yyyyMMdd);
				statisRegUserKeepDayService.save(statisUserKeepDayTemp);
			}
		}
		
	}
	
	private void initRegKeepMonth(String yyyyMM) {
		String[] types = new String[]{"0","1","2","3"};
		for (String type : types) {
			QueryWrapper<StatisRegUserKeepMonth> queryOneWrapper = new QueryWrapper<StatisRegUserKeepMonth>();
			queryOneWrapper.eq("date_time", yyyyMM);
			queryOneWrapper.eq("type", type);
			StatisRegUserKeepMonth statisUserKeepDay = statisRegUserKeepMonthService.getOne(queryOneWrapper);
			
			//如果不存在，添加
			if (statisUserKeepDay == null) {
				StatisRegUserKeepMonth statisUserKeepDayTemp = new StatisRegUserKeepMonth();
				statisUserKeepDayTemp.setType(type);
				statisUserKeepDayTemp.setDateTime(yyyyMM);
				statisRegUserKeepMonthService.save(statisUserKeepDayTemp);
			}
		}
	}
	
	private Date getDateByYmd(String yyyyMMdd) {
		//获取当前时间
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(yyyyMMdd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
		
	}
	
	private String getKeepKeyByType(String type) {
		String redisKey = RedisKey.KEEP_STORE_PEOPLE_COUNT_DAY;
		if (type.equals("1")) {
			redisKey = RedisKey.KEEP_ZXUSER_STORE_PEOPLE_COUNT_DAY;
		}else if (type.equals("2")){
			redisKey = RedisKey.KEEP_YQUSER_STORE_PEOPLE_COUNT_DAY;
		}else if (type.equals("3")){
			redisKey = RedisKey.KEEP_GUEST_STORE_PEOPLE_COUNT_DAY;
		}
		
		return redisKey;
	}
	
	private String getKeepRegKeyByType(String type) {
		String redisKey = RedisKey.KEEP_ALL_STORE_PEOPLE_REG_COUNT_DAY;
		if (type.equals("1")) {
			redisKey = RedisKey.KEEP_ZXUSER_STORE_PEOPLE_REG_COUNT_DAY;
		}else if (type.equals("2")){
			redisKey = RedisKey.KEEP_YQUSER_STORE_PEOPLE_REG_COUNT_DAY;
		}else if (type.equals("3")){
			redisKey = RedisKey.KEEP_GUEST_STORE_PEOPLE_REG_COUNT_DAY;
		}
		
		return redisKey;
	}
	
	private String getKeepMonthKeyByType(String type) {
		String redisKey = RedisKey.KEEP_STORE_PEOPLE_COUNT_MONTH;
		if (type.equals("1")) {
			redisKey = RedisKey.KEEP_ZXUSER_STORE_PEOPLE_COUNT_MONTH;
		}else if (type.equals("2")){
			redisKey = RedisKey.KEEP_YQUSER_STORE_PEOPLE_COUNT_MONTH;
		}else if (type.equals("3")){
			redisKey = RedisKey.KEEP_GUEST_STORE_PEOPLE_COUNT_MONTH;
		}
		
		return redisKey;
	}
	
	private String getKeepRegMonthKeyByType(String type) {
		String redisKey = RedisKey.KEEP_ALL_STORE_PEOPLE_REG_COUNT_MONTH;
		if (type.equals("1")) {
			redisKey = RedisKey.KEEP_ZXUSER_STORE_PEOPLE_REG_COUNT_MONTH;
		}else if (type.equals("2")){
			redisKey = RedisKey.KEEP_YQUSER_STORE_PEOPLE_REG_COUNT_MONTH;
		}else if (type.equals("3")){
			redisKey = RedisKey.KEEP_GUEST_STORE_PEOPLE_REG_COUNT_MONTH;
		}
		
		return redisKey;
	}
	
	
	/*public void calcAddAndUpdateStaticUserKeepDay(String yyyyMMdd) {
		
		//计算日
		StatisUserKeepDay statisUserKeepDay = calcKeepDay2db(yyyyMMdd);
		
		UpdateWrapper<StatisUserKeepDay> updateWrapper = new UpdateWrapper<StatisUserKeepDay>();
		updateWrapper.eq("date_time", yyyyMMdd);
		updateWrapper.eq("type", "0");
		boolean res = statisUserKeepDayService.update(statisUserKeepDay, updateWrapper);
		if (!res) {
			statisUserKeepDayService.save(statisUserKeepDay);
		}
	
		
	}
	
	public void calcAddAndUpdateStaticUserKeepMonth(String yyyyMM) {
		//计算月
		StatisUserKeepMonth statisUserKeepMonth = this.calcKeep2Month(yyyyMM);
		
		UpdateWrapper<StatisUserKeepMonth> updateMonthWrapper = new UpdateWrapper<StatisUserKeepMonth>();
		updateMonthWrapper.eq("date_time", yyyyMM);
		updateMonthWrapper.eq("type", "0");
		boolean res = statisUserKeepMonthService.update(statisUserKeepMonth, updateMonthWrapper);
		
		if (!res) {
			statisUserKeepMonthService.save(statisUserKeepMonth);
		}
		
	}*/
	
	/**
	 * 通过月进行计算交集
	 * @param yyyyMM
	 * @return
	 */
	private StatisUserKeepMonth calcKeep2Month(String yyyyMM) {
		
		StatisUserKeepMonth statisUserKeepMonth = new StatisUserKeepMonth();
		statisUserKeepMonth.setDateTime(yyyyMM);
		statisUserKeepMonth.setType("0");
		
		//获取当前时间
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(yyyyMM + "01");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//前1个月，前2个月。。。。到前12个月
		for (int num = 1; num <= 12; num++) {
			
			int mm = num;
			
			List<String> monthList = DateUtils.getMonthListBeforeRange(date, num, "yyyyMM");
			
			List<String> dateTimeTempList = new ArrayList<String>();
			monthList.stream().forEach(item -> {
				dateTimeTempList.add(RedisKey.KEEP_STORE_PEOPLE_COUNT_MONTH + item);
			});
			
			if (mm == 1) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setOne(count.size());
			}else if (mm == 2) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setTwo(count.size());
			}else if (mm == 3) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setThree(count.size());
			}else if (mm == 4) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setFour(count.size());
			}else if (mm == 5) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setFive(count.size());
			}else if (mm == 6) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setSix(count.size());
			}else if (mm == 7) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setSeven(count.size());
			}else if (mm == 8) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setEight(count.size());
			}else if (mm == 9) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setNine(count.size());
			}else if (mm == 10) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setTen(count.size());
			}else if (mm == 11) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setEleven(count.size());
			}else if (mm == 12) {
				//null 写死的没啥用
				Set<Object> count = redisHandler.sinterNoStore(null, dateTimeTempList);
				statisUserKeepMonth.setTwelve(count.size());
			}
			
		}
		
		return statisUserKeepMonth;
		
	}
	
	/**
	 * List<Map<String, Object>> 返回记录为 年， 当月，5月
	 * @param yyyy
	 * @return
	 */
	public List<StatisUserKeepDay> getStatisUserKeepDayList(String yyyyMM, String type) {
		String yyyyMMdd = DateUtils.getCurrYMD("yyyyMMdd");
		initKeepDay(yyyyMMdd);
		
		QueryWrapper<StatisUserKeepDay> queryWrapper = new QueryWrapper<StatisUserKeepDay>();
		queryWrapper.likeRight("date_time", yyyyMM);
		queryWrapper.eq("type", type);
		List<StatisUserKeepDay> statisUserKeepDayList = statisUserKeepDayService.list(queryWrapper);
		
		return statisUserKeepDayList;
	}
	
	/**
	 * List<Map<String, Object>> 返回记录为 年， 当月，5月
	 * @param yyyy
	 * @return
	 */
	public List<StatisUserKeepMonth> getStatisUserKeepMonthList(String yyyy, String type) {
		
		String yyyyMM = DateUtils.getCurrYMD("yyyyMM");
		initKeepMonth(yyyyMM);
		
		QueryWrapper<StatisUserKeepMonth> queryWrapper = new QueryWrapper<StatisUserKeepMonth>();
		queryWrapper.likeRight("date_time", yyyy);
		queryWrapper.eq("type", type);
		List<StatisUserKeepMonth> statisUserKeepDayList = statisUserKeepMonthService.list(queryWrapper);
		
		return statisUserKeepDayList;
	}
	
	
	public List<StatisRegUserKeepDay> getStatisRegUserKeepDayList(String yyyyMM, String type) {
		String yyyyMMdd = DateUtils.getCurrYMD("yyyyMMdd");
		initRegKeepDay(yyyyMMdd);
		
		QueryWrapper<StatisRegUserKeepDay> queryWrapper = new QueryWrapper<StatisRegUserKeepDay>();
		queryWrapper.likeRight("date_time", yyyyMM);
		queryWrapper.eq("type", type);
		List<StatisRegUserKeepDay> statisUserKeepDayList = statisRegUserKeepDayService.list(queryWrapper);
		
		return statisUserKeepDayList;
	}
	
	/**
	 * List<Map<String, Object>> 返回记录为 年， 当月，5月
	 * @param yyyy
	 * @return
	 */
	public List<StatisRegUserKeepMonth> getStatisRegUserKeepMonthList(String yyyy, String type) {
		String yyyyMM = DateUtils.getCurrYMD("yyyyMM");
		initRegKeepMonth(yyyyMM);
		
		QueryWrapper<StatisRegUserKeepMonth> queryWrapper = new QueryWrapper<StatisRegUserKeepMonth>();
		queryWrapper.likeRight("date_time", yyyy);
		queryWrapper.eq("type", type);
		List<StatisRegUserKeepMonth> statisUserKeepDayList = statisRegUserKeepMonthService.list(queryWrapper);
		
		return statisUserKeepDayList;
	}
	
	
	/**
	 * 综合统计
	 */
	public void statis() {
		calcOrderValueStatis();
		calcUserStatis();
	}
	
	/**
	 * 统计7日内 path的点击次数 / 人数
	 */
	public Map<String, Object> statisPathPeopleMonth(String pathGrade) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<String> times = DateUtils.getDayListRange(new Date(), DateUtils.getCurrDate2Num(-7), "yyyyMMdd");
		
		//反序
		Collections.reverse(times);
		
		result.put("header", times);
		
		List<NodePath> nodePathList = pathService.getPathInfosUseByGrade(pathGrade);
		
		List<Map<String, String>> statisDateList = new ArrayList<Map<String, String>>();
		
		for (NodePath path : nodePathList) {
			
			Map<String, String> statisMap = new HashMap<String, String>();
			statisMap.put("pathCode", path.getPathCode());
			statisMap.put("pathName", path.getPathName());
			
			for (String timeTmp : times) {
				long peopleCount = redisHandler.pfcount(RedisKey.PATH_ACTIVE_PEOPLE_COUNT + timeTmp + path.getPathCode() + "_" + pathGrade);
				long clickCount = redisHandler.pfcount(RedisKey.PATH_ACTIVE_CLICK_COUNT + timeTmp + path.getPathCode() + "_" + pathGrade);
				statisMap.put("count_people_" + timeTmp, clickCount + "/" + peopleCount);
			}
			
			statisDateList.add(statisMap);
		}
		
		result.put("dataList", statisDateList);
		return result;
		
	}
	
	/**
	 * 付款人数，激励任务人数，签到任务人数，积分兑换人数
	 */
	public Map<String, Object> statisTaskPeopleMonth(String time) {
		
		int maxDate = DateUtils.getMaxDayNumByMonth(time);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		//if ("channel_pay_people_count，channel_urge_task_people_count,channel_sign_task_people_count,channel_credits_convert_people_count,channel_order_num,channel_sign_task_num,channel_urge_task_num,channel_credits_convert_num".contains(msgTypeEnum.name())){
		String[] peopleKeys = new String[]{RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_pay_people_count", RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_urge_task_people_count", RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_sign_task_people_count", RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_credits_convert_people_count"};
		
		for (String peopleKey : peopleKeys) {
			
			String onlineKeyName = peopleKey.substring(peopleKey.lastIndexOf(":") + 1);
			
			List<Map<String, Object>> resultTimeList = new ArrayList<>();
			for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				String day = time + (dateNum < 10 ? "0" + dateNum : dateNum);
				long count = redisHandler.pfcount(peopleKey + ':' + day);
				dataMap.put("day", day);
				dataMap.put("count", (int)count);
				resultTimeList.add(dataMap);
			}
			
			result.put(onlineKeyName, resultTimeList);
		}
		
		return result;
		
	}
	
	/**
	 * 充值订单数，签到任务数，激励任务次数，兑换次数
	 * @param time
	 * @return
	 */
	public Map<String, Object> statisTaskNumMonth(String time) {
		
		int maxDate = DateUtils.getMaxDayNumByMonth(time);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		//if ("channel_pay_people_count，channel_order_num,channel_sign_task_num,channel_urge_task_num,channel_credits_convert_num".contains(msgTypeEnum.name())){
		String[] taskKeys = new String[]{RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_order_num",RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_sign_task_num",RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_urge_task_num",RedisMqKey.CHANNEL_QUEUE_TYPE_HLL + "channel_credits_convert_num"};
		
		for (String taskKey : taskKeys) {
			
			String onlineKeyName = taskKey.substring(taskKey.lastIndexOf(":") + 1);
			
			List<Map<String, Object>> resultTimeList = new ArrayList<>();
			for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				String day = time + (dateNum < 10 ? "0" + dateNum : dateNum);
				long count = redisHandler.pfcount(taskKey + ":" + day);
				dataMap.put("day", day);
				dataMap.put("count", (int)count);
				resultTimeList.add(dataMap);
			}
			
			result.put(onlineKeyName, resultTimeList);
		}
		
		return result;
		
	}
	
	/**
	 * 设备统计
	 * @return
	 */
	public Map<String, Object> deviceStatis() {
		
		return null;
	}
	
	public Map<String, Object> getOnlineUserAndDeviceCount() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		//统计实时在线人数
		//删除前5分钟的用户数据
		Date currDate = new Date();
		
		String[] onlineKeys = new String[]{RedisKey.USER_ONLINE_COUNT + "_total", RedisKey.GUEST_ONLINE_COUNT + "_guest", RedisKey.TRYUSER_ONLINE_COUNT + "_tryuser", RedisKey.EXPIREUSER_ONLINE_COUNT  + "_expireuser", RedisKey.NOEXPIREUSER_ONLINE_COUNT + "_noexpireuser"};
		
		Map<String, Object> userOnlineMap = new HashMap<String, Object>();
		for (String onlineKey : onlineKeys) {
			
			String[] onlineKeyArr = onlineKey.split("_");
			Date beforeTime = DateUtils.getCurrDateBeforeMinTime(currDate, -5);
			Set<Object> olineUsers = redisHandler.zRangeByScore(onlineKeyArr[0], beforeTime.getTime(), currDate.getTime());
			
		    int onlineUserCount = 0;
			if (olineUsers != null && !olineUsers.isEmpty()) {
				onlineUserCount = olineUsers.size();
			}
			
			userOnlineMap.put(onlineKeyArr[1], onlineUserCount);
		}
		
		result.put("user", userOnlineMap);
		
		Date deviceBeforeTime = DateUtils.getCurrDateBeforeMinTime(currDate, -5);
		//10 安卓，11苹果，12pc ，13 mac
		String[] deviceTypes = new String[]{"10_ad","11_ios","12_pc","13_mac"};
		for (String deviceType : deviceTypes) {
			String[] type = deviceType.split("_");
			Set<Object> olineUsersTemp = redisHandler.zRangeByScore(RedisKey.DEVICE_ONLINE_COUNT + type[0], deviceBeforeTime.getTime(), currDate.getTime());
			result.put(type[1], olineUsersTemp != null && !olineUsersTemp.isEmpty() ? olineUsersTemp.size() : 0);
		}
		
		return result;
	}
	
	public Map<String, List<Map<String, Object>>> getActiveUserAndDeviceCount(String type, String time) {
		
		Map<String, List<Map<String, Object>>> resultMap = new HashMap<>();
		
		/*if ("day".equals(type)) {
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			long count = redisHandler.pfcount(RedisKey.USER_ACTIVE_COUNT + time);
			dataMap.put("day", time);
			dataMap.put("count", (int)count);
			//result.add(dataMap);
		} else */
		if ("month".equals(type)) {
			
			//通过年月获取日
			if (time.length() != 6) {
				throw new RequestParamValidException("time format error");
			}
			int maxDate = DateUtils.getMaxDayNumByMonth(time);
			
			//RedisKey.USER_ACTIVE_COUNT + "_total",  去掉总数
			String[] onlineKeys = new String[]{RedisKey.GUEST_ACTIVE_COUNT + "_guest", RedisKey.TRYUSER_ACTIVE_COUNT + "_tryuser", RedisKey.EXPIREUSER_ACTIVE_COUNT  + "_expireuser", RedisKey.NOEXPIREUSER_ACTIVE_COUNT + "_noexpireuser", RedisKey.BOX_PEOPLE_COUNT_DAY + "_boxusers", RedisKey.BOX_CLICK_COUNT_DAY + "_boxclicks",RedisKey.DEVICE_COUNT_DAY + "_devices", RedisKey.USER_ONLINE_MAX_DAY_COUNT + "_maxOnlineUsers", RedisKey.DEVICE_ONLINE_MAX_DAY_COUNT + "_maxOnlineDevices"};
			
			for (String onlineKey : onlineKeys) {
				
				String[] onlineKeyArr = onlineKey.split("_");
				
				List<Map<String, Object>> result = new ArrayList<>();
				for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					String day = time + (dateNum < 10 ? "0" + dateNum : dateNum);
					
					long count = 0;
					if (onlineKeyArr[0].contains(RedisKey.USER_ONLINE_MAX_DAY_COUNT) || onlineKeyArr[0].contains(RedisKey.DEVICE_ONLINE_MAX_DAY_COUNT)) {
						Object obj = redisHandler.get(onlineKeyArr[0] + day);
						count = obj == null ? 0 : Long.valueOf(String.valueOf(obj));
					}else {
						count = redisHandler.pfcount(onlineKeyArr[0] + day);
					}
					
					dataMap.put("day", day);
					dataMap.put("count", (int)count);
					result.add(dataMap);
				}
				
				resultMap.put(onlineKeyArr[1], result);
			}
			
		}
		
		//同时获取有效用户
		Map<String, List<Map<String, Object>>> effitMap = getEffectUserCountDay(type, time);
		
		resultMap.putAll(effitMap);
		
		return resultMap;
	}
	
	/**
	 * 获取有效用户
	 * @param type
	 * @param time
	 * @return
	 */
	public Map<String, List<Map<String, Object>>> getEffectUserCountDay(String type, String time) {
		
		Map<String, List<Map<String, Object>>> resultMap = new HashMap<>();
		
		/*if ("day".equals(type)) {
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			long count = redisHandler.pfcount(RedisKey.USER_ACTIVE_COUNT + time);
			dataMap.put("day", time);
			dataMap.put("count", (int)count);
			//result.add(dataMap);
		} else */
		if ("month".equals(type)) {
			
			//通过年月获取日
			if (time.length() != 6) {
				throw new RequestParamValidException("time format error");
			}
			int maxDate = DateUtils.getMaxDayNumByMonth(time);
			
			//RedisKey.ALL_EFFECTIVE_COUNT + "_eftotal", 去掉总数
			String[] onlineKeys = new String[]{RedisKey.GUEST_EFFECTIVE_COUNT + "_efguest", RedisKey.TRYUSER_EFFECTIVE_COUNT + "_eftryuser", RedisKey.EXPIREUSER_EFFECTIVE_COUNT  + "_efexpireuser", RedisKey.NOEXPIREUSER_EFFECTIVE_COUNT + "_efnoexpireuser"};
			
			//获取每个key对应的每一天的总数
			for (String onlineKey : onlineKeys) {
				
				String[] onlineKeyArr = onlineKey.split("_");
				
				List<Map<String, Object>> result = new ArrayList<>();
				int totalCount = 0;
				for (int dateNum = 1; dateNum <= maxDate; dateNum++) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					String day = time + (dateNum < 10 ? "0" + dateNum : dateNum);
					long count = redisHandler.pfcount(onlineKeyArr[0] + day);
					dataMap.put("day", day);
					dataMap.put("count", (int)count);
					totalCount = totalCount + (int)count;
					result.add(dataMap);
				}
				
				resultMap.put(onlineKeyArr[1], result);
			}
			
		}
		return resultMap;
	}
	
	public Map<String, Object> getAllOrderData() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orderVal", this.getOrderValueStatisTotal());
		result.put("orderNum", this.getOrderNumStatisTotal());
//		result.put("mem", this.getUserStatisTotal());
//		result.put("guest", this.guestUserStatis());
		return result;
	}
	
	public Map<String, Object> getAllUserData() {
		Map<String, Object> result = new HashMap<String, Object>();
//		result.put("orderVal", this.getOrderValueStatisTotal());
//		result.put("orderNum", this.getOrderNumStatisTotal());
		result.put("mem", this.getUserStatisBeforeAndToday());
		result.put("guest", this.guestUserStatis());
		return result;
	}
	
	/**
	 * 游客统计
	 * @return
	 */
	public Map<String, Object> guestUserStatis() {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		//获取游客总数
		long guestTotalCount = redisHandler.pfcount(RedisKey.GUEST_TOTAL);
		
		String todayTime = DateUtils.getCurrYMD("yyyyMMdd");
		//获取今日游客总数
		long todayGuestCount = redisHandler.pfcount(RedisKey.GUEST_DAY_PF + todayTime);
		
		result.put("total", guestTotalCount);
		result.put("today", todayGuestCount);
		return result;
	}
	
	
	/**
	 * 统计除今日之前的所有的金额
	 */
	public Map<String, Object> calcOrderValueStatis() {
		
		//获取今天
		String todayTime = DateUtils.getCurrYMD("yyyy-MM-dd");
		if (isUpdate(todayTime)) {
			Map<String, Object> orderStatisData = chargeOrderMapper.getStatisDataTodayBefore(todayTime);
			if (orderStatisData == null || orderStatisData.isEmpty()) {
				return null;
			}
			orderStatisData.put("todayTime", todayTime);
			redisHandler.hSetMap(RedisKey.STATIS_ORDER_DATA_KEY, orderStatisData);
			redisHandler.expire(RedisKey.STATIS_ORDER_DATA_KEY, 24 * 60 * 60);
			return orderStatisData;
		}
		
		return redisHandler.hGetMapObj(RedisKey.STATIS_ORDER_DATA_KEY);
	}
	
	public Map<String, Object> calcUserStatis() {
		
		//获取今天
		String todayTime = DateUtils.getCurrYMD("yyyy-MM-dd");
		if (isUpdate(todayTime)) {
			Map<String, Object> userStatisData = memInfoMapper.getUserStatisDataTodayBefore(todayTime);
			if (userStatisData == null || userStatisData.isEmpty()) {
				return null;
			}
			userStatisData.put("todayTime", todayTime);
			
			redisHandler.hSetMap(RedisKey.STATIS_USER_DATA_KEY, userStatisData);
			
			redisHandler.expire(RedisKey.STATIS_USER_DATA_KEY, 24 * 60 * 60);
			
			return userStatisData;
		}
		
		return redisHandler.hGetMapObj(RedisKey.STATIS_USER_DATA_KEY);
	}
	
	public Map<String, Object> getUserStatisTotal() {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> orderStatis = memInfoMapper.getUserStatisDataTodayBefore(null);
		
		result.put("total", orderStatis);
		
		Map<String, Object> todayData = memInfoMapper.getUserStatisDataToday(DateUtils.getCurrYMD("yyyy-MM-dd"));
		
		result.put("today", todayData);
		
		return result;
	}
	
	/**
	 * 订单金额统计
	 * @return
	 */
	public Map<String, Object> getOrderValueStatisTotal() {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> orderStatis = chargeOrderMapper.getStatisDataTodayBefore(null);
		
		result.put("total", orderStatis);
		
		Map<String, Object> todayData = chargeOrderMapper.getStatisDataToday(DateUtils.getCurrYMD("yyyy-MM-dd"));
		
		result.put("today", todayData);
		
		return result;
	}
	
	/**
	 * 订单数统计
	 * @return
	 */
	public Map<String, Object> getOrderNumStatisTotal() {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> orderStatis = chargeOrderMapper.getOrderStatisNumDataTodayBefore(null);
		
		result.put("total", orderStatis);
		
		Map<String, Object> todayData = chargeOrderMapper.getOrderStatisNumDataToday(DateUtils.getCurrYMD("yyyy-MM-dd"));
		
		result.put("today", todayData);
		
		return result;
	}
	
	
	public Map<String, Object> getUserStatisBeforeAndToday() {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> userStatis = redisHandler.hGetMapObj(RedisKey.STATIS_USER_DATA_KEY);
		
		if (userStatis == null || userStatis.isEmpty()) {
			userStatis = calcUserStatis();
		}
		
		Map<String, Object> todayData = memInfoMapper.getUserStatisDataToday(DateUtils.getCurrYMD("yyyy-MM-dd"));
		
		result.put("today", todayData);
		result.put("total", calcTotal(userStatis,todayData));
		
		return result;
	}
	
	public Map<String, Object> getOrderStatisBeforeAndToday() {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> orderStatis = redisHandler.hGetMapObj(RedisKey.STATIS_ORDER_DATA_KEY);
		
		if (orderStatis == null || orderStatis.isEmpty()) {
			orderStatis = calcOrderValueStatis();
		}
		
		Map<String, Object> todayData = chargeOrderMapper.getStatisDataToday(DateUtils.getCurrYMD("yyyy-MM-dd"));
		
		result.put("today", todayData);
		
		result.put("total", calcTotal(orderStatis,todayData));
		
		return result;
	}
	
	private Map<String, Object> calcTotal(Map<String, Object> totalMap, Map<String, Object> todayMap) {
		todayMap.forEach((k,val) -> {
			totalMap.put(k, new BigDecimal(String.valueOf(val)).add(new BigDecimal(String.valueOf(totalMap.get(k)))));
		});
		
		return totalMap;
	}
	
	private boolean isUpdate(String todayTime) {
		
		if (!redisHandler.hasKey(RedisKey.STATIS_ORDER_DATA_KEY)) {
			return true;
		}
		
		String cacheTodayTime = (String) redisHandler.hGet(RedisKey.STATIS_ORDER_DATA_KEY, "todayTime");
		
		//如果当前时间比缓存的时间大 则更新
		if (todayTime.compareTo(cacheTodayTime) > 0) {
			return true;
		}
		
		return false;
	}

	public void collectEvent(EventMsg eventMsg) {
		
		EventTypeEnum msgTypeEnum = eventMsg.getMsgTypeEnum();
		
		switch (msgTypeEnum) {
		//如果是游客注册事件，对其进行游客计数统计
		case guest_reg_msg:
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String todayTime = sdf.format(eventMsg.getCreateTime());
			redisHandler.pfAdd(RedisKey.GUEST_DAY_PF + todayTime, eventMsg.getMsgId());
			redisHandler.pfAdd(RedisKey.GUEST_TOTAL, eventMsg.getMsgId());
			
			break;
			
		//在线用户数量统计（5分钟用户在线统计），同时统计每日的用户活跃数
		case online_user_count_msg:
			
			doOnlineAndAtivceUserCount(eventMsg);
			
			break;
			
		//活跃线路的点击次数
		case active_path_count_msg:
			
			this.statisActivePathUserAndClickCount(eventMsg);
			
			break;
			
		case active_user_count_msg:
			//暂时没用
			//statisAtivceUserCount(eventMsg);
			break;
		case online_device_count_msg:
			
			
			//设置本地缓存进行过滤
			Long userCreateTime = eventUserIdCache.getIfPresent("online_device_count_msg:" + eventMsg.getMsgId());
			
			boolean isInsertOnlineQueue = true;
			
			//如果时间不为空，说明已经存过一次，这时要判断是否要加入实时在线队列，和 日活跃 队列
			if (userCreateTime != null) {
				
				//如果当前时间和缓存的时间小于5分钟 就不插入实时在线队列
				if ((new Date().getTime() - userCreateTime.longValue()) < 5 * 60 * 1000) {
					isInsertOnlineQueue = false;
				}
				
			}else {
				eventUserIdCache.put("online_device_count_msg:" + eventMsg.getMsgId(), eventMsg.getCreateTime().getTime());
			}
			
			
			if (isInsertOnlineQueue) {
				String deviceId = eventMsg.getMsgId();
				
				String[] dIdSplit = deviceId.split("-");
				if(dIdSplit.length < 3){
					break;
				}

				//获取设备类型
				String deviceType = dIdSplit[0];
				
				//统计实时在线人数
				boolean res = redisHandler.zSet(RedisKey.DEVICE_ONLINE_COUNT + deviceType, eventMsg.getMsgId(), eventMsg.getCreateTime().getTime());
				
				//如果res为true表示添加成功，这是走下面，如果为false，重复数据，不走下面
				if (!res) {
					logger.debug("online_device_count_msg zSet onlineKey = {}, is chongfu, ,msgId = {}, res = {}", RedisKey.DEVICE_ONLINE_COUNT + deviceType, eventMsg.getMsgId(), res);
					return;
				}
				
				//删除前5分钟的设备数据
				Date deviceBeforeTime = DateUtils.getCurrDateBeforeMinTime(new Date(), -5);
				redisHandler.zRemoveRangeByScore(RedisKey.DEVICE_ONLINE_COUNT + deviceType, 0, deviceBeforeTime.getTime());
			}
			
			String currTimeTmp = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
			redisHandler.pfAdd(RedisKey.DEVICE_COUNT_DAY + currTimeTmp, eventMsg.getMsgId());
			
			
			// 计算最大在线数据量
			String currTimeStrTmp = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
			String[] deviceTypes = new String[]{"10_ad","11_ios","12_pc","13_mac"};
			long currCount = 0l;
			for (String deviceType : deviceTypes) {
				String[] type = deviceType.split("_");
				long deviceTypeCurrCount = redisHandler.zSize(RedisKey.DEVICE_ONLINE_COUNT + type[0]);
				currCount = currCount + deviceTypeCurrCount;
			}
			boolean isExist = redisHandler.hasKey(RedisKey.DEVICE_ONLINE_MAX_DAY_COUNT + currTimeStrTmp);
			if (isExist) {
				
				Object obj = redisHandler.get(RedisKey.DEVICE_ONLINE_MAX_DAY_COUNT + currTimeStrTmp);
				
				long maxCount = obj == null ? 0 : Long.valueOf(String.valueOf(obj));
				
				if (currCount > maxCount) {
					redisHandler.set(RedisKey.DEVICE_ONLINE_MAX_DAY_COUNT + currTimeStrTmp, currCount);
				}
				
			}else {
				redisHandler.set(RedisKey.DEVICE_ONLINE_MAX_DAY_COUNT + currTimeStrTmp, currCount);
			}
			
			break;
			
		case click_box_people_msg:
			
			String msgId = eventMsg.getMsgId();
			//统计每日线路点击人数的活跃人数
			String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
			redisHandler.pfAdd(RedisKey.BOX_PEOPLE_COUNT_DAY + currTimeStr, msgId);
			redisHandler.pfAdd(RedisKey.BOX_CLICK_COUNT_DAY + currTimeStr, CommUtils.getUuid());
			break;

		default:
			break;
		}
		
	}
	
	private void statisActivePathUserAndClickCount(EventMsg eventMsg) {
		
		//msgid  ==  userId + "," + pathCode + "," + uuid
		String msgId =  eventMsg.getMsgId();
		String[] msgArr = msgId.split(",");
		
		String memId = msgArr[0];
		//path : code
		String pathCode = msgArr[1];
		
		String pathGrade = memService.getMemPathGrade(memId);
		
		//统计每日线路点击人数的活跃人数
		String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
		redisHandler.pfAdd(RedisKey.PATH_ACTIVE_PEOPLE_COUNT + currTimeStr + pathCode + "_" + pathGrade, memId);
		//设置2个月有效期
//		if (pfRes > 0) {
//			redisHandler.expire(RedisKey.PATH_ACTIVE_PEOPLE_COUNT + currTimeStr + path, 1 * 2 * 30 * 24 * 60 * 60);
//		}
		
		redisHandler.pfAdd(RedisKey.PATH_ACTIVE_CLICK_COUNT + currTimeStr + pathCode + "_" + pathGrade, msgId);
		//设置2个月有效期
//		if (pfRes2 > 0) {
//			redisHandler.expire(RedisKey.PATH_ACTIVE_CLICK_COUNT + currTimeStr + path, 1 * 2 * 30 * 24 * 60 * 60);
//		}
		
	}
	
	public void doOnlineAndAtivceUserCount(EventMsg eventMsg) {
		
		//统计实时在线和活跃速
		statisOnlineAndActive(eventMsg);
		
		boolean isEffect = isEffectUser(eventMsg);
		
		//统计有效用户
		statisEffectiveUser(eventMsg, isEffect);
		
		//统计有效注册用户
		statisEffectRegUser(eventMsg, isEffect);
		
		//统计留存总用户
		statisKeepUser(eventMsg, isEffect);
		
		//统计留存注册用户
		statisKeepRegUser(eventMsg, isEffect);
		
	}
	
	private void statisOnlineAndActive(EventMsg eventMsg) {
		
		String userId = eventMsg.getMsgId();
		
		MemInfo memInfo = memService.queryMemCacheById(userId);
		
		//统计实时在线人数 和 总日 活跃人数
		redisSetOnlineAndActive(RedisKey.USER_ONLINE_COUNT, null, eventMsg);
		//redisSetOnlineAndActive(RedisKey.USER_ONLINE_COUNT, RedisKey.USER_ACTIVE_COUNT, eventMsg);
	    
		if (memInfo == null) {
			logger.warn("collectEvent, memInfo query is empty, userId = {}, eventMsg= {} ", userId, eventMsg);
			return;
		}
		
		String userType = userId.substring(0, 1);
		//判断是否游客
		if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType))  {
			//统计游客在线和日活跃
			redisSetOnlineAndActive(RedisKey.GUEST_ONLINE_COUNT, RedisKey.GUEST_ACTIVE_COUNT, eventMsg);
			return;
		}
		
		//新游客
		if (memService.isGuestByType(userId)) {
			//统计游客在线和日活跃
			redisSetOnlineAndActive(RedisKey.GUEST_ONLINE_COUNT, RedisKey.GUEST_ACTIVE_COUNT, eventMsg);
			return;
		}
		
		//判断是否为试用用户
		if (MemTypeEum.trial_mem.getCode().equals(memInfo.getMemType())) {
			//统计游客在线和日活跃
			redisSetOnlineAndActive(RedisKey.TRYUSER_ONLINE_COUNT, RedisKey.TRYUSER_ACTIVE_COUNT, eventMsg);
		}else {
			//如果是过期
			if (memInfo.isExpire()) {
				redisSetOnlineAndActive(RedisKey.EXPIREUSER_ONLINE_COUNT, RedisKey.EXPIREUSER_ACTIVE_COUNT, eventMsg);
			}else {//如果是正常会员
				redisSetOnlineAndActive(RedisKey.NOEXPIREUSER_ONLINE_COUNT, RedisKey.NOEXPIREUSER_ACTIVE_COUNT, eventMsg);
			}
		}
		
	}
	
	private void redisSetOnlineAndActive(String onlineKey, String activeKey, EventMsg eventMsg) {
		
		//设置本地缓存进行过滤
/*		Long userCreateTime = eventUserIdCache.getIfPresent(activeKey + eventMsg.getMsgId());
		
		boolean isInsertOnlineQueue = true;
		boolean isInsertActiveQueue = true;
		
		//如果时间不为空，说明已经存过一次，这时要判断是否要加入实时在线队列，和 日活跃 队列
		if (userCreateTime != null) {
			
			//如果当前时间和缓存的时间小于5分钟 就不插入实时在线队列
			if ((new Date().getTime() - userCreateTime.longValue()) < 5 * 60 * 1000) {
				isInsertOnlineQueue = false;
			}
			
			//如果当前时间和缓存时间所在日期相同，就说明已经插入过，就不需要在插
			if (DateUtils.getCurrYMD(new Date(), "yyyyMMdd").equals(DateUtils.getCurrYMD(new Date(userCreateTime), "yyyyMMdd"))) {
				isInsertActiveQueue = false;
			}
		}else {
			eventUserIdCache.put(activeKey + eventMsg.getMsgId(), eventMsg.getCreateTime().getTime());
		}*/
		
/*		if (isInsertOnlineQueue) {
			//统计实时在线人数
			boolean res = redisHandler.zSet(onlineKey, eventMsg.getMsgId(), eventMsg.getCreateTime().getTime());
			
			//如果res为true表示添加成功，这是走下面，如果为false，重复数据，不走下面
			if (!res) {
				logger.debug("settingOnlineAndActive zSet onlineKey = {}, is chongfu, msgId = {}, res = {}", onlineKey, eventMsg.getMsgId(), res);
				return;
			}
			
			//删除前5分钟的用户数据  (先展示屏蔽影响速度)
			Date beforeTime = DateUtils.getCurrDateBeforeMinTime(new Date(), -5);
			redisHandler.zRemoveRangeByScore(onlineKey, 0, beforeTime.getTime());
		}
		
		if (isInsertActiveQueue) {
			//统计每日的活跃人数
			String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
			long pfRes = redisHandler.pfAdd(activeKey + currTimeStr, eventMsg.getMsgId());
			//设置2年有效期
			if (pfRes > 0) {
				redisHandler.expire(activeKey + currTimeStr, 2 * 12 * 30 * 24 * 60 * 60);
			}
			
		}*/
		
		//统计实时在线人数
		boolean res = redisHandler.zSet(onlineKey, eventMsg.getMsgId(), eventMsg.getCreateTime().getTime());
		
		//如果res为true表示添加成功，这是走下面，如果为false，重复数据，不走下面
		if (!res) {
			logger.debug("settingOnlineAndActive zSet onlineKey = {}, is chongfu, msgId = {}, res = {}", onlineKey, eventMsg.getMsgId(), res);
			return;
		}
		
		//删除前5分钟的用户数据  (先展示屏蔽影响速度)
		Date beforeTime = DateUtils.getCurrDateBeforeMinTime(new Date(), -5);
		redisHandler.zRemoveRangeByScore(onlineKey, 0, beforeTime.getTime());
		
		if (!StringUtils.isEmpty(activeKey)) {
			//统计每日的活跃人数
			String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
			long pfRes = redisHandler.pfAdd(activeKey + currTimeStr, eventMsg.getMsgId());
			//设置2年有效期
			if (pfRes > 0) {
				redisHandler.expire(activeKey + currTimeStr, 2 * 12 * 30 * 24 * 60 * 60);
			}
		}
		
		if (RedisKey.USER_ONLINE_COUNT.equals(onlineKey)) {
			String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
			long currCount = redisHandler.zSize(RedisKey.USER_ONLINE_COUNT);
			boolean isExist = redisHandler.hasKey(RedisKey.USER_ONLINE_MAX_DAY_COUNT + currTimeStr);
			if (isExist) {
				
				Object obj = redisHandler.get(RedisKey.USER_ONLINE_MAX_DAY_COUNT + currTimeStr);
				
				long maxCount = obj == null ? 0 : Long.valueOf(String.valueOf(obj));
				
				if (currCount > maxCount) {
					redisHandler.set(RedisKey.USER_ONLINE_MAX_DAY_COUNT + currTimeStr, currCount);
				}
				
			}else {
				redisHandler.set(RedisKey.USER_ONLINE_MAX_DAY_COUNT + currTimeStr, currCount);
			}
			
			
					
		}
		
		
	}
	
	private boolean isEffectUser(EventMsg eventMsg) {
		String userId = eventMsg.getMsgId();
		
		String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
		
		String redisEffectKey = RedisKey.USER_EFFECTIVE_ONIINE_TIME + currTimeStr + userId;
		
		if (redisHandler.hasKey(redisEffectKey)) {
			
			long startTime = (long) redisHandler.get(redisEffectKey);
			long currTime = new Date().getTime();
			
			//如果当前时间和开始时间相差10分钟，说明用户使用了10分钟，这是记录
			if ((currTime - startTime) > 10 * 60 * 1000) {
				return true;
			}
			
			return false;
			
		}
		
		redisHandler.set(redisEffectKey, new Date().getTime(), 24 * 60 * 60);
		return false;
	}
	
	/**
	 * 有效用户 要使用超过10分钟
	 * @param eventMsg
	 */
	private void statisEffectiveUser(EventMsg eventMsg, boolean isEffect) {
		
		if (!isEffect) {
			return;
		}
		
		String userId = eventMsg.getMsgId();
		
		MemInfo memInfo = memService.queryMemCacheById(userId);
		
		if (memInfo == null) {
			logger.warn("collectEvent, memInfo query is empty, userId = {}, eventMsg= {} ", userId, eventMsg);
			return;
		}
		
		String userType = userId.substring(0, 1);
		//判断是否游客
		if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType))  {
			//统计游客在线和日活跃
			setRedisEffectiveUser(RedisKey.GUEST_EFFECTIVE_COUNT, eventMsg);
			return;
		}
		
		//新游客
		if (memService.isGuestByType(userId)) {
			setRedisEffectiveUser(RedisKey.GUEST_EFFECTIVE_COUNT, eventMsg);
			return;
		}
		
		//试用用户有效用户统计
		//setRedisEffectiveUser(RedisKey.ALL_EFFECTIVE_COUNT, eventMsg);
		
		//判断是否为试用用户
		if (MemTypeEum.trial_mem.getCode().equals(memInfo.getMemType())) {
			//试用用户有效用户统计
			setRedisEffectiveUser(RedisKey.TRYUSER_EFFECTIVE_COUNT, eventMsg);
		}else {
			//如果是过期
			if (memInfo.isExpire()) {
				//试用用户有效用户统计
				setRedisEffectiveUser(RedisKey.EXPIREUSER_EFFECTIVE_COUNT, eventMsg);
			}else {//如果是正常会员
				//试用用户有效用户统计
				setRedisEffectiveUser(RedisKey.NOEXPIREUSER_EFFECTIVE_COUNT, eventMsg);
			}
		}
		
		
	}
	
	private void setRedisEffectiveUser(String effectKey, EventMsg eventMsg) {
		String userId = eventMsg.getMsgId();
		String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
		//记录今日有效用户数
		redisHandler.pfAdd(effectKey + currTimeStr, userId);
		
	}
	
	/**
	 * 统计有效注册用户
	 * 先判断是不是10分钟有效
	 * @param eventMsg
	 */
	private void statisEffectRegUser(EventMsg eventMsg, boolean isEffect) {
		
		//先判断是不是10分钟有效
		if (!isEffect) {
			return;
		}
		
		String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
		String userId = eventMsg.getMsgId();
		
		Date regDate = memService.getRegTimeByMemId(userId);
		
		//如果用户注册时间是今日表示今日注册, 添加新增用户 有效数量
		if (regDate != null && DateUtils.isToday(regDate)) {
			
			boolean isGuest = memService.isGuest(userId);
			if (isGuest) {
				//这里不管是过期，还是没过期，试用用户 还是 充值用户，都会走这里，后面可以优化
				redisHandler.pfAdd(RedisKey.GUEST_EFFECTIVE_REG_COUNT + currTimeStr, userId);
			}else {
				//这里不管是过期，还是没过期，试用用户 还是 充值用户，都会走这里，后面可以优化
				//redisHandler.pfAdd(RedisKey.USER_EFFECTIVE_REG_COUNT + currTimeStr, userId);
				
				//判断是否邀请还是自动注册
				MemInfo mem = memService.queryMemCacheById(userId);
				String inviUserId = mem.getInviterUserId();
				//自行注册
				if (StringUtils.isEmpty(inviUserId)) {
					redisHandler.pfAdd(RedisKey.ZXUSER_EFFECTIVE_REG_COUNT + currTimeStr, userId);
				}else {//有值表示邀请用户
					redisHandler.pfAdd(RedisKey.YQUSER_EFFECTIVE_REG_COUNT + currTimeStr, userId);
				}
			}
			
		}
		
	}
	
	/**
	 * 统计留存用户，存储用户的ID
	 * @param keepUserDayKey
	 * @param keepUserMonthKey
	 * @param eventMsg
	 */
	private void statisKeepUser(EventMsg eventMsg, boolean isEffect) {
		
		//先判断是不是10分钟有效
		if (!isEffect) {
			return;
		}
		
		String userId = eventMsg.getMsgId();
		MemInfo mem = memService.queryMemCacheById(userId);
		
		boolean isGuest = memService.isGuest(userId);
		if (isGuest) {
			setRedisKeepUser(RedisKey.KEEP_GUEST_STORE_PEOPLE_COUNT_DAY, RedisKey.KEEP_GUEST_STORE_PEOPLE_COUNT_MONTH, eventMsg);
		}else{//里面不包含游客
			setRedisKeepUser(RedisKey.KEEP_STORE_PEOPLE_COUNT_DAY, RedisKey.KEEP_STORE_PEOPLE_COUNT_MONTH, eventMsg);
			
			//判断是否邀请还是自动注册
			String inviUserId = mem.getInviterUserId();
			//自行注册
			if (StringUtils.isEmpty(inviUserId)) {
				setRedisKeepUser(RedisKey.KEEP_ZXUSER_STORE_PEOPLE_COUNT_DAY, RedisKey.KEEP_ZXUSER_STORE_PEOPLE_COUNT_MONTH, eventMsg);
			}else {//有值表示邀请用户
				setRedisKeepUser(RedisKey.KEEP_YQUSER_STORE_PEOPLE_COUNT_DAY, RedisKey.KEEP_YQUSER_STORE_PEOPLE_COUNT_MONTH, eventMsg);
			}
		}
		
	}
	
	private void statisKeepRegUser(EventMsg eventMsg, boolean isEffect) {
		
		if (!isEffect) {
			return;
		}
		
		String userId = eventMsg.getMsgId();
		
		//如果用户注册时间是今日表示今日注册, 添加新增用户 有效数量
		Date regDate = memService.getRegTimeByMemId(userId);
		if (regDate != null && DateUtils.isToday(regDate)) {
			
			boolean isGuest = memService.isGuest(userId);
			if (isGuest) {
				setRedisKeepUser(RedisKey.KEEP_GUEST_STORE_PEOPLE_REG_COUNT_DAY, RedisKey.KEEP_GUEST_STORE_PEOPLE_REG_COUNT_MONTH, eventMsg);
			}else {
				//这里不管是过期，还是没过期，试用用户 还是 充值用户，都会走这里，后面可以优化
				setRedisKeepUser(RedisKey.KEEP_ALL_STORE_PEOPLE_REG_COUNT_DAY, RedisKey.KEEP_ALL_STORE_PEOPLE_REG_COUNT_MONTH, eventMsg);
			
				//判断是否邀请还是自动注册
				MemInfo mem = memService.queryMemCacheById(userId);
				String inviUserId = mem.getInviterUserId();
				//自行注册
				if (StringUtils.isEmpty(inviUserId)) {
					setRedisKeepUser(RedisKey.KEEP_ZXUSER_STORE_PEOPLE_REG_COUNT_DAY, RedisKey.KEEP_ZXUSER_STORE_PEOPLE_REG_COUNT_MONTH, eventMsg);
				}else {//有值表示邀请用户
					setRedisKeepUser(RedisKey.KEEP_YQUSER_STORE_PEOPLE_REG_COUNT_DAY, RedisKey.KEEP_YQUSER_STORE_PEOPLE_REG_COUNT_MONTH, eventMsg);
				}
			}
			
		}
	}
	
	/**
	 * 留存用户
	 * @param eventMsg
	 */
	private void setRedisKeepUser(String keepDayKey, String keepMonthKey, EventMsg eventMsg) {
		
		String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
		
		//用于计算留存用户人数，存一天的数据，
		long size = redisHandler.sAdd(keepDayKey + currTimeStr, eventMsg.getMsgId());
		
		//2个月有效期
		if (size > 0) {
			redisHandler.expire(keepDayKey + currTimeStr, 1 * 2 * 30 * 24 * 60 * 60);
		}
		
		String yyyyMM = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMM");
		//用于计算留存用户人数，存一天的数据，
		long monthSize = redisHandler.sAdd(keepMonthKey + yyyyMM, eventMsg.getMsgId());
		
		//1年有效期
		if (monthSize > 0) {
			redisHandler.expire(keepMonthKey + yyyyMM, 365 * 24 * 60 * 60);
		}
		
	}
	
	/*public void statisOnlineUserCount(EventMsg eventMsg) {
		//统计实时在线人数 和 总日 活跃人数
		settingOnline(RedisKey.USER_ONLINE_COUNT, eventMsg);
		
		String userId = eventMsg.getMsgId();
		String userType = userId.substring(0, 1);
		//判断是否游客
		if (GenIDEnum.GUEST_MEMID.getPreFix().equals(userType))  {
			//统计游客在线和日活跃
			settingOnline(RedisKey.GUEST_ONLINE_COUNT, eventMsg);
		}else {
			MemInfo memInfo = memService.queryMemCacheById(userId);
			
			if (memInfo == null) {
				logger.debug("collectEvent, memInfo query is empty, userId = {}, eventMsg= {} ", userId, eventMsg);
				return;
			}
			
			if (memService.isGuestByType(userId)) {
				//统计游客在线和日活跃
				settingOnline(RedisKey.GUEST_ONLINE_COUNT, eventMsg);
				return;
			}
			
			//判断是否为试用用户
			if (MemTypeEum.trial_mem.getCode().equals(memInfo.getMemType())) {
				//统计游客在线和日活跃
				settingOnline(RedisKey.TRYUSER_ONLINE_COUNT, eventMsg);
			}else {
				//如果是过期
				if (memInfo.isExpire()) {
					settingOnline(RedisKey.EXPIREUSER_ONLINE_COUNT, eventMsg);
				}else {//如果是正常会员
					settingOnline(RedisKey.NOEXPIREUSER_ONLINE_COUNT, eventMsg);
				}
			}
		}
	}*/
	
	/**
	 * 计算在线
	 * @param onlineKey
	 * @param eventMsg
	 */
/*	private void settingOnline(String onlineKey, EventMsg eventMsg) {
		//统计实时在线人数
		redisHandler.zSet(onlineKey, eventMsg.getMsgId(), eventMsg.getCreateTime().getTime());
		
		//删除前5分钟的用户数据
		Date beforeTime = DateUtils.getCurrDateBeforeMinTime(new Date(), -5);
		redisHandler.zRemoveRangeByScore(onlineKey, 0, beforeTime.getTime());
	}*/
	
	/**
	 * 计算活跃
	 * @param activeKey
	 * @param eventMsg
	 */
	private long settingActive(String activeKey, EventMsg eventMsg) {
		//统计每日的活跃人数
		String currTimeStr = DateUtils.getCurrYMD(eventMsg.getCreateTime(), "yyyyMMdd");
		long result = redisHandler.pfAdd(activeKey + currTimeStr, eventMsg.getMsgId());
		//设置2年有效期
		redisHandler.expire(activeKey + currTimeStr, 2 * 12 * 30 * 24 * 60 * 60);
		
		return result;
	}
	
}
