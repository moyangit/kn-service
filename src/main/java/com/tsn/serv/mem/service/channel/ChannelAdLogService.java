package com.tsn.serv.mem.service.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tsn.serv.mem.entity.channel.ChannelAdLog;
import com.tsn.serv.mem.entity.mem.MemInfo;
import com.tsn.serv.mem.mapper.channel.ChannelAdLogMapper;
import com.tsn.serv.mem.service.mem.MemService;

/**
 * <p>
 * 用户留存统计 服务实现类
 * </p>
 *
 * @author Tang longsen
 * @since 2022-06-15
 */
@Service
public class ChannelAdLogService extends ServiceImpl<ChannelAdLogMapper, ChannelAdLog>{
	
	@Autowired
	private MemService memService;
	
	public Long recordVmCallbackLog(String memId, String callbackUrl, String channelCode) {
		ChannelAdLog channelAdLog = new ChannelAdLog();
		channelAdLog.setCallbackUrl(callbackUrl);
		channelAdLog.setChannelCode(channelCode);
		channelAdLog.setCallbackType("vm_money");
		channelAdLog.setMemId(memId);
		
		MemInfo memInfo = memService.queryMemById(memId);
		if (memInfo != null) {
			channelAdLog.setMemType(memInfo.getMemType());
		}
		channelAdLog.setStatus(0);
		super.save(channelAdLog);
		return channelAdLog.getId();
	}
	
	public Long recordAdCallbackLog(String memId, String extId, String channelCode) {
		ChannelAdLog channelAdLog = new ChannelAdLog();
		channelAdLog.setCallbackUrl(extId);
		channelAdLog.setChannelCode(channelCode);
		channelAdLog.setCallbackType("ad_convert");
		channelAdLog.setMemId(memId);
		
		MemInfo memInfo = memService.queryMemById(memId);
		if (memInfo != null) {
			channelAdLog.setMemType(memInfo.getMemType());
		}
		channelAdLog.setStatus(0);
		super.save(channelAdLog);
		return channelAdLog.getId();
	}
	
	public boolean updateCallbackLogSuccess(Long id, String callbackResult) {
		ChannelAdLog channelAdLog = new ChannelAdLog();
		channelAdLog.setId(id);
		channelAdLog.setCallbackResult(callbackResult);
		channelAdLog.setStatus(1);
		return super.updateById(channelAdLog);
	}
	
	public boolean updateCallbackLogFail(Long id, String callbackResult) {
		ChannelAdLog channelAdLog = new ChannelAdLog();
		channelAdLog.setId(id);
		channelAdLog.setCallbackResult(callbackResult);
		channelAdLog.setStatus(2);
		return super.updateById(channelAdLog);
	}
	
}
