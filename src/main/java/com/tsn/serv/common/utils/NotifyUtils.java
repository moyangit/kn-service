package com.tsn.serv.common.utils;

import com.tsn.common.utils.enm.notify.NotifyBusTypeEm;
import com.tsn.common.utils.enm.notify.NotifyTypeEm;
import com.tsn.common.utils.utils.notify.Notify;
import com.tsn.common.utils.utils.notify.sms.bao.SmsBao;

public class NotifyUtils {
	
	public static String createLoginSms(SmsBao smsBao) {
		Notify notify = new Notify();
		notify.setPlatType(0);
		notify.setNotifyBusType(NotifyBusTypeEm.login.name());
		notify.setNotifyType(NotifyTypeEm.SMS);
		notify.setNotifyUrl(smsBao.getUrl());
		notify.setNotifyToNo(smsBao.getPhone());
		notify.setNotifyContent(smsBao.toJson());
		return notify.toJsonStr();
	}
	
	public static String createRegSms(SmsBao smsBao) {
		Notify notify = new Notify();
		notify.setPlatType(0);
		notify.setNotifyBusType(NotifyBusTypeEm.reg.name());
		notify.setNotifyType(NotifyTypeEm.SMS);
		notify.setNotifyUrl(smsBao.getUrl());
		notify.setNotifyToNo(smsBao.getPhone());
		notify.setNotifyContent(smsBao.toJson());
		return notify.toJsonStr();
	}

}
