package com.tsn.serv.pub.common.down;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.utils.tools.json.JsonUtils;

public class DownManager {
	
	private static Logger log = LoggerFactory.getLogger(DownManager.class);
	
	private Map<String, DownUrl> kjsMap = new HashMap<String, DownUrl>();
	
	private Map<String, DownUrl> hbMap = new HashMap<String, DownUrl>();
	
	private Map<String, DownUrl> ggMap = new HashMap<String, DownUrl>();
	
	private static DownManager downManager = new DownManager();
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private ExecutorService kjsExecutor = Executors.newSingleThreadExecutor();
	
	private ExecutorService ggExecutor = Executors.newSingleThreadExecutor();
	
	private DelayQueue<QueueObj> hbQueue = new DelayQueue<>();
	
	private DelayQueue<QueueObj> kjsQueue = new DelayQueue<>();
	
	private DelayQueue<QueueObj> ggQueue = new DelayQueue<>();
	/*
	 * 安卓 https://kjsdd.lanzoui.com/iv8OWr2ekve
windows电脑 https://kjsdd.lanzoui.com/iIkoJr3d54h
苹果电脑 https://kjsdd.lanzoui.com/i8yuVr3d4ob
	 */
	

	private DownManager() {
		
		init();
		
		executor.execute(new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					
					
					try {
						QueueObj queueObj = hbQueue.take();
						
						DownUrl downUrl = hbMap.get(queueObj.name);
						downUrl.refresh();
						queueObj.setStartTime(downUrl.getNextTime().getTime());
						hbQueue.put(queueObj);
						
						log.info("refresh hb url success, {}, {} ", queueObj.name, downUrl);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
				
			}
		}));
		
		kjsExecutor.execute(new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					
					
					try {
						QueueObj queueObj = kjsQueue.take();
						
						DownUrl downUrl = kjsMap.get(queueObj.name);
						downUrl.refresh();
						queueObj.setStartTime(downUrl.getNextTime().getTime());
						kjsQueue.put(queueObj);
						
						log.info("refresh kjs url success, {}, {} ", queueObj.name, downUrl);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
				
			}
		}));
		
		ggExecutor.execute(new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(true) {
					
					
					try {
						QueueObj queueObj = ggQueue.take();
						
						DownUrl downUrl = ggMap.get(queueObj.name);
						downUrl.refresh();
						queueObj.setStartTime(downUrl.getNextTime().getTime());
						ggQueue.put(queueObj);
						
						log.info("refresh kjs url success, {}, {} ", queueObj.name, downUrl);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
				
			}
		}));
	}
	
	/**
	 * https://s456s456.com/down/hb/sgwjg —-未加固
		https://s456s456.com/down/hb/sgjg —加固
	 */
	private void init() {
		//https://kjsdd.lanzoue.com
		ggMap.put("ad",new DownUrl("https://kjsdd.lanzoue.com/id3Sv0cnb3wh", ""));
		ggMap.put("pc",new DownUrl("https://kjsdd.lanzoue.com/i476i0cnbfda", ""));
		ggMap.put("mac",new DownUrl("https://kjsdd.lanzoue.com/ieW4G0cnbvdg", ""));
		
		
		hbMap.put("sgwjg",new DownUrl("https://kjsdd.lanzoui.com/sgweijiagu", ""));
		hbMap.put("sgjg",new DownUrl("https://kjsdd.lanzoui.com/sgjiagu", ""));
		
		
		
		hbMap.put("adyyxb",new DownUrl("https://kjsdd.lanzoue.com/yyxbad", ""));
		hbMap.put("adyyxbsign",new DownUrl("https://kjsdd.lanzoue.com/yyxbadsign", ""));
		
		hbMap.put("ad_unsign",new DownUrl("https://kjsdd.lanzoue.com/heibaoanzhuomiananzhuangunsign", ""));
		
/*		hbMap.put("ad",new DownUrl("https://kjsdd.lanzoui.com/heibaoanzhuo", ""));
		hbMap.put("pc",new DownUrl("https://kjsdd.lanzoui.com/iIkoJr3d54h", ""));
		hbMap.put("mac",new DownUrl("https://kjsdd.lanzoui.com/i8yuVr3d4ob", ""));
		
		kjsMap.put("ad",new DownUrl("https://kjsdd.lanzoui.com/kjsad", ""));
		kjsMap.put("pc",new DownUrl("https://kjsdd.lanzoui.com/kjspc", ""));
		kjsMap.put("mac",new DownUrl("https://kjsdd.lanzoui.com/kjsmac", ""));*/
		
		hbMap.put("ad",new DownUrl("https://kjsdd.lanzoue.com/hb511ad", ""));
		hbMap.put("pc",new DownUrl("https://kjsdd.lanzoue.com/hb511pc", ""));
		hbMap.put("mac",new DownUrl("https://kjsdd.lanzoue.com/hb511mac", ""));
		
		kjsMap.put("ad",new DownUrl("https://kjsdd.lanzoue.com/kjs511ad", ""));
		kjsMap.put("pc",new DownUrl("https://kjsdd.lanzoue.com/kjs511pc", ""));
		kjsMap.put("mac",new DownUrl("https://kjsdd.lanzoue.com/kjs511mac", ""));
		
		kjsMap.put("ad_unsign",new DownUrl("https://kjsdd.lanzoue.com/kjsadweijiamituiguang", ""));
		kjsMap.put("ad",new DownUrl("https://kjsdd.lanzoue.com/kjsad", ""));
		kjsMap.put("pc",new DownUrl("https://kjsdd.lanzoue.com/kjspc", ""));
		kjsMap.put("mac",new DownUrl("https://kjsdd.lanzoue.com/kjsmac", ""));
		
	}
	
	public void initData() {
		for (Map.Entry<String, DownUrl> entry : hbMap.entrySet()) {
			DownUrl downUrl = entry.getValue();
			downUrl.refresh();
			hbQueue.add(new QueueObj(entry.getKey(), downUrl.getNextTime().getTime()));
		}
		
		for (Map.Entry<String, DownUrl> entry : kjsMap.entrySet()) {
			DownUrl downUrl = entry.getValue();
			downUrl.refresh();
			kjsQueue.add(new QueueObj(entry.getKey(), downUrl.getNextTime().getTime()));
		}
		
		for (Map.Entry<String, DownUrl> entry : ggMap.entrySet()) {
			DownUrl downUrl = entry.getValue();
			downUrl.refresh();
			ggQueue.add(new QueueObj(entry.getKey(), downUrl.getNextTime().getTime()));
		}
	}
	
	public static DownManager build() {
		return downManager;
	}
	
	public void refreshDownUrl () {
		
		
		
	}
	
	public class QueueObj implements Delayed{
	    /* 触发时间*/
	    private long startTime;
	    private String name;
	 
	    public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public QueueObj(String name, long time) {
	        this.name = name;
	        this.startTime = time;
	    }
	 
	    @Override
	    public long getDelay(TimeUnit unit) {
	        return startTime - System.currentTimeMillis();
	    }
	 
	    @Override
	    public int compareTo(Delayed o) {
	    	QueueObj item = (QueueObj) o;
	        long diff = this.startTime - item.startTime;
	        if (diff <= 0) {// 改成>=会造成问题
	            return -1;
	        }else {
	            return 1;
	        }
	    }
	 
	    @Override
	    public String toString() {
	        return "Item{" +
	                "time=" + startTime +
	                ", name='" + name + '\'' +
	                '}';
	    }
	}
	
	
	public static class DownUrl {
		
		private String sourceUrl;
		
		private String downUrl;
		
		private Date lastTime;
		
		private Date nextTime;
		
		public DownUrl(String sourceUrl, String downUrl) {
			this.setSourceUrl(sourceUrl);
			this.setDownUrl(downUrl);
			this.lastTime = new Date();
			this.nextTime = DateUtils.addMinutes(this.lastTime, new Random().nextInt(3));
			this.nextTime = DateUtils.addSeconds(this.nextTime, new Random().nextInt(60));
		}
		
		public void refresh() {
			String downUrl = getYunPanUrl(sourceUrl);
			
			if (!StringUtils.isEmpty(downUrl)) {
				this.downUrl = downUrl;
			}
			
			this.lastTime = new Date();
			this.nextTime = DateUtils.addMinutes(this.lastTime, 2 + new Random().nextInt(3));
			this.nextTime = DateUtils.addSeconds(this.nextTime, new Random().nextInt(60));
		}
		
		public String getYunPanUrl(String reqUrl) {
			
			try {
				URL url = new URL(reqUrl);
				String host = url.getHost();
				//String reqUrl = "https://kjsdd.lanzoui.com/kjsad";
				
				Connection.Response response = Jsoup.connect(reqUrl).execute();
				//System.out.println(response.cookies());
				//System.out.println(response.body());
				
				Document document = Jsoup.parse(response.body());
				
				String iframeContent = "";
				String iframeAffer = "";
				
				Elements elements = document.select("iframe[class='ifr2']");
				
				if (elements == null || elements.isEmpty()) {
					elements = document.select("iframe[class='n_downlink']");
				}
				
				for (Element element : elements) {
					iframeAffer = "https://" + host + "/" + element.baseUri() + element.attr("src");
				    Connection.Response responseFn = Jsoup.connect(iframeAffer).cookies(response.cookies()).execute();
				    
				    System.out.println(responseFn.cookies());
				    iframeContent  = responseFn.body();
				    //System.out.println(iframeContent);
				}
				
				Document iframeDoc = Jsoup.parse(iframeContent);
				Element script = iframeDoc.select("script").get(1); // Get the script part

				String content = script.html();
				
				String[] contentArr = content.split("\n");
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("action", "downprocess");
				params.put("ves", "1");
				params.put("websign", "");
				
				for (String html : contentArr) {
					
					if (html.contains("//")) {
						continue;
					}
					
					if (html.contains("var ajaxdata")) {
						params.put("signs", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
					}else if (html.contains("var vsign")) {
						params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
					}else if (html.contains("var ispostdowns")) {
						params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
					}else if (html.contains("var pdownload")) {
						params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
					}else if (html.contains("var msigns")) {
						params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
					}else if (html.contains("var postdown")) {
						params.put("sign", html.split("=")[1].replaceAll("'", "").replaceAll(";", "").trim());
					}else if (html.contains("data :")) {
						String[] htmlArr = html.split(":");
						String websignKey = htmlArr[htmlArr.length - 1].replace("'", "").replace("}", "").replace(",", "").trim();
						//'VDJUag4_aBzYIAQM8ATECPlo1BTACbVRlUGZaaFI9BD1QdlJxWzsAZVU1AmBWMQUzVzUDMVc6AjRQYw_c_c','ves'
						String signStr = htmlArr[4];
						//如果sign直接写在ajax data中
						if (!StringUtils.isEmpty(signStr) && signStr.indexOf("','v") > -1) {
							params.put("sign", signStr.replaceAll("','ves'", "").replaceAll("'", "").trim());
						}
						params.put("websignkey", websignKey);
						break;
					}
				}
				
				List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
				paramsList.add(new BasicNameValuePair("action", params.get("action")));
				paramsList.add(new BasicNameValuePair("signs", params.get("signs")));
				paramsList.add(new BasicNameValuePair("sign", params.get("sign")));
				paramsList.add(new BasicNameValuePair("ves", params.get("ves")));
				paramsList.add(new BasicNameValuePair("websign", params.get("websign")));
				paramsList.add(new BasicNameValuePair("websignkey", params.get("websignkey")));
				
				//UM_distinctid=17b447b3a1916d-0036a02eb37d8d-4343363-144000-17b447b3a1a5ee; CNZZDATA5289133=cnzz_eid%3D1236576885-1628937230-%26ntime%3D1628937230; CNZZDATA1253610888=181102480-1628936242-%7C1628936242; CNZZDATA422640=cnzz_eid%3D1603193747-1628937477-%26ntime%3D1628937477
				//response.cookies()
				/*response.cookies().put("UM_distinctid", "17b447b3a1916d-0036a02eb37d8d-4343363-144000-17b447b3a1a5ee");
				response.cookies().put("CNZZDATA5289133", "cnzz_eid%3D1236576885-1628937230-%26ntime%3D1628937230");
				response.cookies().put("CNZZDATA1253610888", "181102480-1628936242-%7C1628936242");
				response.cookies().put("CNZZDATA422640", "cnzz_eid%3D1603193747-1628937477-%26ntime%3D1628937477");*/
				
				Map<String, String> headerMap = new HashMap<String, String>();
				headerMap.put("origin", "https://" + host);
				headerMap.put("referer", iframeAffer);
				
				
				Connection.Response responseFn = Jsoup.connect("https://" + host + "/ajaxm.php").method(Method.POST)
						.data("action", params.get("action"))
						.data("signs", params.get("signs"))
						.data("sign", params.get("sign") == null ? "" : params.get("sign"))
						.data("ves", params.get("ves"))
						.data("websign", params.get("websign"))
						.data("websignkey", params.get("websignkey"))
						.headers(headerMap)
						.cookies(response.cookies()).followRedirects(true)
				        .execute();
				
				
				/*HttpPostReq req = new HttpPostReq("https://kjsdd.lanzoui.com/ajaxm.php", null, paramsList);
				String result = req.excuteReturnStr();*/
				//System.out.println(responseFn.body());
				//date.dom+"/file/"+ date.url
				//String paraStr = MapUtil.mapJoin(params, false, true);
				//String paraStr = "action=downprocess&signs=%3Fctdf&sign=AWcGOFprVWRUXVBvAjJSbgFvUGJSPFZhAzxRbl0zBDYBJwckCGgOa1IyAWUHa1JqAWgEMlA2UGNQYg_c_c&ves=1&websign=&websignkey=8PaD";
				Map<String, Object> resultMap = JsonUtils.jsonToPojo(responseFn.body(), Map.class);
				
				String downUrl = resultMap.get("dom") + "/file/" + resultMap.get("url");
				return downUrl;
			} catch (Exception e) {
				log.error("get yunpan url error, e ={}", e);
			} 
			
			return null;
		}
		
		public Date getLastTime() {
			return lastTime;
		}

		public void setLastTime(Date lastTime) {
			this.lastTime = lastTime;
		}

		public Date getNextTime() {
			return nextTime;
		}

		public void setNextTime(Date nextTime) {
			this.nextTime = nextTime;
		}

		public String getDownUrl() {
			return downUrl;
		}

		public void setDownUrl(String downUrl) {
			this.downUrl = downUrl;
		}

		public String getSourceUrl() {
			return sourceUrl;
		}

		public void setSourceUrl(String sourceUrl) {
			this.sourceUrl = sourceUrl;
		}

		@Override
		public String toString() {
			return "DownUrl [sourceUrl=" + sourceUrl + ", downUrl=" + downUrl
					+ ", lastTime=" + lastTime + ", nextTime=" + nextTime + "]";
		}
		
	}


	public Map<String, DownUrl> getKjsMap() {
		return kjsMap;
	}

	public void setKjsMap(Map<String, DownUrl> kjsMap) {
		this.kjsMap = kjsMap;
	}

	public Map<String, DownUrl> getHbMap() {
		return hbMap;
	}

	public void setHbMap(Map<String, DownUrl> hbMap) {
		this.hbMap = hbMap;
	}

	public Map<String, DownUrl> getGgMap() {
		return ggMap;
	}

	public void setGgMap(Map<String, DownUrl> ggMap) {
		this.ggMap = ggMap;
	}
	
	
}
