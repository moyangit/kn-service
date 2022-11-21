/*package com.tsn.serv.pub.controller.down;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.tsn.common.core.cache.RedisHandler;
import com.tsn.common.utils.utils.tools.time.DateUtils;
import com.tsn.serv.common.cons.redis.RedisKey;

@RestController
@RequestMapping("down")
public class DownRedirectController {

	@Autowired
	private RedisHandler redisHandler;

	*//**
	 * S2是clickId，S1是广告回传exId
	 * @param model
	 * @param appType
	 * @param type
	 * @param cc
	 * @param s2
	 * @param s1
	 * @param request
	 * @return
	 *//*
	@GetMapping("{appType}/{type}")
	public ModelAndView redirectDownLine(Model model, @PathVariable String appType, @PathVariable String type, String cc, String s2, String s1,HttpServletRequest request){
		ModelAndView mv = new ModelAndView("redirect:https://s456s456.com/down/"+ appType + "/" + type);
		
		try {
			
			String yyyyMMdd = DateUtils.getCurrYMD("yyyyMMdd");
			
			//去重
			//redisHandler.zSet(RedisKey.VM_QUEUE_CID_CHANNEL + cc, s2 + "#" + s1, new Date().getTime());
			
			redisHandler.zSet(RedisKey.VM_QUEUE_CID_CHANNEL + cc + ":" + yyyyMMdd, s2 + "#" + s1, new Date().getTime());
			
			redisHandler.expire(RedisKey.VM_QUEUE_CID_CHANNEL + cc + ":" + yyyyMMdd, 365 * 24 * 60 * 60);
			
			//删除前5分钟的用户数据  (先展示屏蔽影响速度)
			//Date beforeTime = DateUtils.getCurrDateBeforeMinTime(new Date(), -5);
			//redisHandler.zRemoveRangeByScore(onlineKey, 0, beforeTime.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mv;
	}
}
*/