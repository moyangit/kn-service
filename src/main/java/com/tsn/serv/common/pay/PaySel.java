package com.tsn.serv.common.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.AtomicLongMap;
import com.tsn.common.utils.utils.tools.algo.WeightAlgo;
import com.tsn.common.utils.utils.tools.algo.WeightAlgo.WeightObj;

public class PaySel {
	
	private static Map<String, Integer> payWeightMap = null;
	
	//用来判断工位订单是否执行完毕
	private static AtomicLongMap<String> failRecord = AtomicLongMap.create();
	
	private static ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);
	
	private static int failNum = 5;
	
	private static boolean isHeart = false;
	
	static {
		
		payWeightMap = new HashMap<String, Integer>();
		/*payWeightMap.put("hb", 50);
		payWeightMap.put("pay4", 50);*/
		payWeightMap.put("pay4", 10);
		payWeightMap.put("kjs_yuansfer", 90);
		
		//每隔30分钟
		if (isHeart) {
			schedule.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					try {
						
						Map<String, Long> failProCode = failRecord.asMap();
						
						for (Map.Entry<String, Long> entry : failProCode.entrySet()) {
							
							//如果大于5次，清理一下，让它重新使用，尽量保证kjs大于pay3,
							if (entry.getValue() != null && entry.getValue() > failNum) {
								failRecord.remove(entry.getKey());
							}
							
						}
						
					} catch (Exception e) {
					}
					
				}
			}, 30, 30, TimeUnit.MINUTES);
		}
		
		
	}
	
	public static String getPayProd() {
		
		Map<String, Integer> payWeightMapTemp = new HashMap<String, Integer>();
		payWeightMapTemp.putAll(payWeightMap);
		
		Map<String, Long> failProCode = failRecord.asMap();
		
		for (Map.Entry<String, Long> entry : failProCode.entrySet()) {
			
			//如果连续大于10次，任务已经挂了,就不取
			if (entry.getValue() != null && entry.getValue() > failNum) {
				payWeightMapTemp.remove(entry.getKey());
			}
			
		}
		
		//如果为空，走初始权重
		if (payWeightMapTemp.isEmpty()) {
			return getWeightCode(payWeightMap);
		}
		
		return getWeightCode(payWeightMapTemp); 
		
	}
	
	private static String getWeightCode(Map<String, Integer> payWeightMap) {
		
		List<WeightObj> weightObjList = new ArrayList<WeightObj>(); 
		for (Map.Entry<String, Integer> entry : payWeightMap.entrySet()) {
			weightObjList.add(new WeightObj(entry.getKey(), entry.getValue()));
		} 
		
		WeightAlgo payWeight = new WeightAlgo(weightObjList);
		String proCode = payWeight.getWeight();
		
		return proCode;
		
	}
	
	
	
	public static Map<String, Integer> getPayWeightMap() {
		return payWeightMap;
	}

	public static void setPayWeightMap(Map<String, Integer> payWeightMap) {
		PaySel.payWeightMap = payWeightMap;
	}

	public static void recordProFail(String prodCode) {
		failRecord.incrementAndGet(prodCode);
	}
	
	public static void resetProFail(String prodCode) {
		failRecord.remove(prodCode);
	}
	
/*    public static void main(String[] args) {
		
		for (int i =0; i<6;i++) {
			recordProFail("pay3");
		}
		
		
		System.out.println(getPayProd());
		
		
	}*/
	
	
}
