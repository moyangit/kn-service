package com.tsn.serv.mem.service.channel;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tsn.common.utils.exception.BusinessException;
import com.tsn.common.utils.utils.cons.ResultCode;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.utils.web.utils.gener.SnowFlakeManager;
import com.tsn.serv.common.enm.id.GenIDEnum;
import com.tsn.serv.mem.entity.channel.Channel;
import com.tsn.serv.mem.entity.channel.ChannelStatisDay;
import com.tsn.serv.mem.mapper.channel.ChannelMapper;
import com.tsn.serv.mem.mapper.channel.ChannelStatisDayMapper;

@Service
public class ChannelService {
	
	private static Logger log = LoggerFactory.getLogger(ChannelService.class);
	
	@Autowired
	private ChannelMapper channelMapper;
	
	@Autowired
	private ChannelStatisDayMapper channelStatisDayMapper;
	
	public List<ChannelStatisDay> getChannelListByDay(String channelCode, String startDay, String endDay) {
		
		return this.channelStatisDayMapper.getChannelStatisListByDay(channelCode, startDay, endDay);
		
	}
	
	public List<Channel> getChannelAll() {
		return channelMapper.getChannelAll();
	}
	
	public Channel getChannelById(String channelCode) {
		return channelMapper.selectByPrimaryKey(channelCode);
	}
	
	public void getChannelPage(Page page) {
		List<Channel> channelList = channelMapper.selectByPage(page);
		page.setData(channelList);
	}
	
	public void addChannel(Channel channel) {
		channel.setCreateTime(new Date());
		channel.setUpdateTime(channel.getCreateTime());
		channelMapper.insert(channel);
	}
	
	public void updateChannel(Channel channel) {
		channel.setUpdateTime(channel.getCreateTime());
		channelMapper.updateByPrimaryKeySelective(channel);
	}
	
	public void removeChannel(String id) {
		channelMapper.deleteByPrimaryKey(id);
	}
	
	@Transactional
	public void updateChannelTypeCount(String channelCode, String typeName, String dayTime) {
		
		if (StringUtils.isEmpty(channelCode) || StringUtils.isEmpty(typeName) || StringUtils.isEmpty(dayTime)) {
			
			throw new BusinessException(ResultCode.PARAM_VALID_ERROR, "channelCode, typeName, dayTime can not be empty()");
			
		}
		
		
		//对typeName 这个字段进行加1
		int res = channelMapper.updateForType(channelCode, typeName);
		
		//如果更新失败 说明channelCode不存在，则不走下面
		if (res == 0 ) {
			log.warn("updateChannelTypeCount update fail, channelCode is no exists , channelCode = {}", channelCode);
			return;
		}
		
		
		 int statisRes = channelStatisDayMapper.updateStatisForType(channelCode, typeName, dayTime);
		 
		 //如果更新失败，这时添加记录
		 if (statisRes == 0) {
			 
			 ChannelStatisDay channelStatisDay = new ChannelStatisDay();
			 channelStatisDay.setDayTime(dayTime);
			 channelStatisDay.setChannelRecordId(SnowFlakeManager.build().create(GenIDEnum.CHANNEL_NO.name()).getIdByPrefix(GenIDEnum.CHANNEL_NO.getPreFix()));
			 channelStatisDay.setChannelCode(channelCode);
			 channelStatisDay.setCreateTime(new Date());
			 Channel channel = getChannelByCode(channelCode);
			 channelStatisDay.setChannelName(channel.getChannelName());
			 channelStatisDayMapper.insertIgnore(channelStatisDay);
			 
			 channelStatisDayMapper.updateStatisForType(channelCode, typeName, dayTime);
			 
		 }
		
	}
	
	
	private Channel getChannelByCode(String code) {
		Channel channel = channelMapper.selectByPrimaryKey(code);
		return channel;
	}

}
