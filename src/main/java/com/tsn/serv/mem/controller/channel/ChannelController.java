package com.tsn.serv.mem.controller.channel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.tsn.serv.mem.entity.channel.Channel;
import com.tsn.serv.mem.entity.channel.ChannelStatisDay;
import com.tsn.serv.mem.service.channel.ChannelService;
import com.tsn.serv.mem.service.channel.ChannelStatisService;

@RestController
@RequestMapping("channel")
public class ChannelController {

	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private ChannelStatisService channelStatisService;

	@GetMapping("/statis/list/{channelCode}/{startDay}/{endDay}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getListByPage(@PathVariable String channelCode, @PathVariable String startDay, @PathVariable String endDay){
		List<ChannelStatisDay> data = channelService.getChannelListByDay(channelCode, startDay, endDay);
		return Response.ok(data);
	}
	
	@GetMapping("{channelCode}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getChannelId(@PathVariable String channelCode){
		Channel channel = channelService.getChannelById(channelCode);
		return Response.ok(channel);
	}
	
	@GetMapping("all")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getListAll(){
		List<Channel> cList = channelService.getChannelAll();
		return Response.ok(cList);
	}

	@GetMapping("page")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> getListByPage(Page page){
		channelService.getChannelPage(page);
		return Response.ok(page);
	}
	
	@GetMapping("statis/page")
	@AuthClient(client = AuthEnum.bea_mn)
	public Response<?> getStatisListByPage(Page page){
		channelStatisService.getChannelListByPageByTime(page);
		return Response.ok(page);
	}
	
	@PostMapping()
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> addChannel(@RequestBody Channel channel){
		channelService.addChannel(channel);
		return Response.ok();
	}
	
	@PutMapping()
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> updateChannel(@RequestBody Channel channel){
		channelService.updateChannel(channel);
		return Response.ok();
	}
	
	@DeleteMapping("{id}")
	@AuthClient(client = AuthEnum.bea_us)
	public Response<?> delChannel(@PathVariable String id){
		channelService.removeChannel(id);
		return Response.ok();
	}

}
