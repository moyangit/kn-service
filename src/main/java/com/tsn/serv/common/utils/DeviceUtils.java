package com.tsn.serv.common.utils;

import org.springframework.util.StringUtils;

import com.tsn.serv.common.entity.device.Device;

public class DeviceUtils {
	
	public static Device getDeviceInfo(String deviceStr) {
		
		if (StringUtils.isEmpty(deviceStr) || deviceStr.indexOf("-") < 0) {
			return new Device();
		}
		
		String[] deviceArr = deviceStr.split("-");
		
		if (deviceArr.length == 3) {
			Device device = new Device();
			device.setDeviceType(getDeviceTypeByHead(deviceArr[0]));
			device.setDeviceName(deviceArr[2]);
			device.setDeviceNo(deviceArr[1]);
			return device;
		}
		
		return new Device();
		
		
	}
	
	private static String getDeviceTypeByHead(String head) {
		if ("W".equals(head)) {
			return "12";
		}else if ("A".equals(head)) {
			return "10";
		}else if ("M".equals(head)) {
			return "13";
		}else if ("I".equals(head)) {
			return "11";
		}
		return "";
	}

}
