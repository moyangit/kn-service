package com.tsn.serv.common.utils;


public class BlackDomainsFilter {
	
	private static String[] oweBlackDomains = new String[]{"knapi.xyz","fast123.xyz","kuainiaojsq.xyz"};
	
	private static String[] userWebBlackDomains = new String[]{"knapi.xyz","fast123.xyz","knsj.xyz"};
	
	public static boolean isOweValid(String domainName){
		
		if ("localhost".equals(domainName) || "127.0.0.1".equals(domainName)) {
			return true;
		}
		
		for (String domain : oweBlackDomains) {
			
			if (domainName.contains(domain)) {
				return false;
			}
			
		}
		
		return true;
		
	}
	
	public static boolean isUserWebValid(String domainName){
		
		if ("localhost".equals(domainName) || "127.0.0.1".equals(domainName)) {
			return true;
		}
		
		for (String domain : userWebBlackDomains) {
			
			if (domainName.contains(domain)) {
				return false;
			}
			
		}
		
		return true;
		
	}

}
