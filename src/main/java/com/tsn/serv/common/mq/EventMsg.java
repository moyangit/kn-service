package com.tsn.serv.common.mq;

import java.util.Date;

import com.tsn.common.utils.utils.CommUtils;

public class EventMsg {
	
	/**
	 * 根据msgTypeEnum类型决定
	 */
	private String msgId;
	
	private Date createTime;

	private EventTypeEnum msgTypeEnum;
	
	public static enum EventTypeEnum {
		guest_reg_msg,
		online_user_count_msg,
		active_user_count_msg,
		online_device_count_msg,
		active_path_count_msg,
		click_box_people_msg,
	}
	
	public EventMsg() {
		this.createTime = new Date();
	}
	
	public EventMsg(EventTypeEnum msgTypeEnum, String msgId) {
		this.msgTypeEnum = msgTypeEnum;
		this.createTime = new Date();
		this.msgId = msgId;
	}
	
	public EventMsg(EventTypeEnum msgTypeEnum, String msgId, Date createTime) {
		this.msgTypeEnum = msgTypeEnum;
		this.createTime = createTime;
		this.msgId = msgId;
	}
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public EventTypeEnum getMsgTypeEnum() {
		return msgTypeEnum;
	}

	public void setMsgTypeEnum(EventTypeEnum msgTypeEnum) {
		this.msgTypeEnum = msgTypeEnum;
	}

	public static EventMsg createGuestRegMsg(String guestUserId, Date regDate) {
		return new EventMsg(EventTypeEnum.guest_reg_msg, guestUserId, regDate);
	}
	
	public static EventMsg createOnlineUserRegMsg(String userId) {
		return new EventMsg(EventTypeEnum.online_user_count_msg, userId);
	}
	
	public static EventMsg createActiveUserRegMsg(String userId) {
		return new EventMsg(EventTypeEnum.active_user_count_msg, userId);
	}
	
	public static EventMsg createDeviceUserRegMsg(String userId) {
		return new EventMsg(EventTypeEnum.online_device_count_msg, userId);
	}
	
	public static EventMsg createActivePathCountRegMsg(String memId, String code) {
		return new EventMsg(EventTypeEnum.active_path_count_msg, memId + "," + code + "," + CommUtils.getUuid());
	}
	
	public static EventMsg createClickBoxPeopleMsg(String memId) {
		return new EventMsg(EventTypeEnum.click_box_people_msg, memId);
	}
	
	@Override
	public String toString() {
		return "EventMsg [msgId=" + msgId + ", createTime=" + createTime
				+ ", msgTypeEnum=" + msgTypeEnum + "]";
	}
	
}
