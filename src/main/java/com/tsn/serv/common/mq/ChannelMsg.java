package com.tsn.serv.common.mq;

import com.tsn.common.utils.utils.tools.time.DateUtils;

public class ChannelMsg {
	
	private String msgId;
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	private String dayTime;

	private String channelCode;
	
	private String channelName;
	
	private MsgTypeEnum msgTypeEnum;
	
	public static enum MsgTypeEnum {
		channel_pay_momey_val,
		channel_guest_people_count,
		channel_pay_people_count, //付款人数
		channel_new_pay_people_count, //当日新付款人数
		channel_reg_people_count,
		channel_urge_task_people_count, //激励任务人数
		channel_sign_task_people_count, //签到任务人数
		channel_active_guest_people_count,
		channel_active_reg_people_count,
		channel_credits_convert_people_count,//积分兑换人数 
		channel_order_num, //支付订单个数
		channel_order_month_num,
		channel_order_quarter_num,
		channel_order_halfyear_num,
		channel_order_year_num,
		channel_order_forever_num,
		channel_sign_task_num, //签到任务次数
		channel_urge_task_num, //激励任务次数
		channel_credits_convert_num; //积分兑换次数
		
	}
	
	public ChannelMsg() {
		this.setDayTime(DateUtils.getCurrYMD("yyyyMMddHH"));
	}
	
	public ChannelMsg(String channelCode, MsgTypeEnum msgTypeEnum, String msgId) {
		this.channelCode = channelCode;
		this.msgTypeEnum = msgTypeEnum;
		this.msgId = msgId;
		this.setDayTime(DateUtils.getCurrYMD("yyyyMMddHH"));
	}
	

	private int value = 0;
	
	public static ChannelMsg createGuestPeopleCount(String channelCode, String guestUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_guest_people_count, guestUserId);
	}
	
	public static ChannelMsg createPayPeopleCount(String channelCode, String regUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_pay_people_count, regUserId);
	}
	
	public static ChannelMsg createRegPeopleCount(String channelCode, String regUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_reg_people_count, regUserId);
	}
	
	public static ChannelMsg createUrgePeopleCount(String channelCode, String regUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_urge_task_people_count, regUserId);
	}
	
	public static ChannelMsg createSignPeopleCount(String channelCode, String regUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_sign_task_people_count, regUserId);
	}
	
	public static ChannelMsg createActiveGuestPeopleCount(String channelCode, String guestUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_active_guest_people_count, guestUserId);
	}
	
	public static ChannelMsg createChannelOrderNum(String channelCode, String orderId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_order_num, orderId);
	}
	
	public static ChannelMsg createActiveRegPeopleCount(String channelCode, String regUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_active_reg_people_count, regUserId);
	}
	
	public static ChannelMsg createChannelSignTaskNum(String channelCode, String taskId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_sign_task_num, taskId);
	}
	
	public static ChannelMsg createChannelUrgeTaskNum(String channelCode, String taskId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_urge_task_num, taskId);
	}
	
	public static ChannelMsg createChannelCreditsConvertNum(String channelCode, String regUserId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_credits_convert_num, regUserId);
	}
	
	public static ChannelMsg createChannelCreditsConvertPeopleCount(String channelCode, String creditsConvertId) {
		return new ChannelMsg(channelCode, MsgTypeEnum.channel_credits_convert_people_count, creditsConvertId);
	}
	
	public static ChannelMsg createChannelOrderMoneyValue(String channelCode, String creditsConvertId, int money) {
		ChannelMsg msg = new ChannelMsg(channelCode, MsgTypeEnum.channel_pay_momey_val, creditsConvertId);
		msg.setValue(money);
		return msg;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public MsgTypeEnum getMsgTypeEnum() {
		return msgTypeEnum;
	}

	public void setMsgTypeEnum(MsgTypeEnum msgTypeEnum) {
		this.msgTypeEnum = msgTypeEnum;
	}

	@Override
	public String toString() {
		return "ChannelMsg [msgId=" + msgId + ", dayTime=" + dayTime
				+ ", channelCode=" + channelCode + ", channelName="
				+ channelName + ", msgTypeEnum=" + msgTypeEnum + ", value="
				+ value + "]";
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDayTime() {
		return dayTime;
	}

	public void setDayTime(String dayTime) {
		this.dayTime = dayTime;
	}

	
}
