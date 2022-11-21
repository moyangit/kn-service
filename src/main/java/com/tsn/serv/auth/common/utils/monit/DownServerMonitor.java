package com.tsn.serv.auth.common.utils.monit;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsn.common.utils.web.utils.bean.SpringContext;
import com.tsn.serv.auth.entity.app.AppDownload;
import com.tsn.serv.auth.service.app.AppDownloadService;

public class DownServerMonitor {
	
	private static Logger log = LoggerFactory.getLogger(DownServerMonitor.class);
	
	private static DownServerMonitor downServerMonitor = new DownServerMonitor();
	
	private DelayQueue<QueueObj> hbQueue = new DelayQueue<>();
	
	private AppDownloadService appDownloadService;
	
	private byte[] lock = new byte[0];
	
	private DownServerMonitor() {
		this.appDownloadService = SpringContext.getBean(AppDownloadService.class);
		this.initQueue();
		this.init();
	}
	
	private void init() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					
					try {
						
						//如果队列为空 则阻塞
						QueueObj qObj = hbQueue.take();
						
						//取出来，监测心跳，然后在放回去
						String path = qObj.getPath();
						
						//从path中获取host和端口，判断是否可用
						URI uri = new URI(path);
						String host = uri.getHost();
						int port = uri.getPort();
						port = port < 0 ? (path.contains("https") ? 443 : 80) : port;
						
						AppDownload appDownLoad = new AppDownload();
						if (isHostConnectable(host, port)) {
							appDownLoad.setStatus("0");
						}else {
							//添加失败次数
							qObj.addCount();
							appDownLoad.setStatus("1");
						}
						appDownLoad.setId(qObj.getId());
						log.info("DownServerMonitor id:{},path:{},type:{},count:{}, status:{}", new Object[]{qObj.getId(), qObj.getPath(), qObj.getType(), qObj.getCount(), appDownLoad.getStatus()});
						
						//如果是失败情况，之前的 要失败三次才会更改状态
						if ("1".equals(appDownLoad.getStatus()) && "0".equals(qObj.getStatus()) && qObj.getCount() > 3) {
							appDownloadService.updateAppDownloadStatus(appDownLoad);
							qObj.setStatus(appDownLoad.getStatus());
							//重置次数
							qObj.resetCount();
						} else if ("0".equals(appDownLoad.getStatus()) && "1".equals(qObj.getStatus())) {//如果判断成功，并且之前的状态是失败时才更新
							appDownloadService.updateAppDownloadStatus(appDownLoad);
							qObj.setStatus(appDownLoad.getStatus());
						}
						
						//延迟5秒后执行
						qObj.setStartTime(DateUtils.addSeconds(new Date(), 20).getTime());
						DownServerMonitor.build().putQueue(qObj);
					} catch (Exception e) {
						log.error("{}",e);
					}
					
				}				
			}
		}).start();
		
	}
	
	//判断是否能连接
	private boolean isHostConnectable(String host, int port) {
		Socket socket = null;
        try {
        	//this.socket.connect(new InetSocketAddress(host, port), 5 * 1000);
        	log.warn("host:{} and port:{} connecting ...... ", host, port);
        	socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 5 * 1000);
        } catch (Exception e) {
        	//log.error("host:{} and port:{} connect e = {} ", host, port, e);
            return false;
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return true;
    }
	
	public static DownServerMonitor build() {
		return downServerMonitor;
	}
	
	public void initQueue() {
		
		log.info("init down monit queue data...");
		List<AppDownload> appDownLoadList = appDownloadService.getAppDownloadList();
		
		synchronized (lock) {
			this.hbQueue.clear();
			Date nextDate = new Date();
			for (AppDownload appDownload : appDownLoadList) {
				//初始化时候 打乱初始 执行顺序
				nextDate = DateUtils.addSeconds(new Date(), new Random().nextInt(10));
				hbQueue.add(new QueueObj(appDownload.getId(), appDownload.getName(), appDownload.getType(), appDownload.getPath(), appDownload.getStatus(), nextDate.getTime()));
			}
		}
		
		log.info("init down monit queue data... success");
	}
	
	public void putQueue(QueueObj qObj) {
		synchronized (lock) {
			if (hbQueue.contains(qObj)) {
				return;
			}
			this.hbQueue.put(qObj);
		}
	}
	
	public void clearQueue() {
		
		synchronized (lock) {
			this.hbQueue.clear();
		}
	}
	
	public class QueueObj implements Delayed{
	    /* 触发时间*/
	    private long startTime;
	    
	    private String id;
	    private String name;
	    private String path;
	    private String type;
	    private String status;
	    
	    private int count;
	    
	    public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		public int addCount() {
			this.count++;
			return this.count;
		}
		
		public int resetCount() {
			this.count = 0;
			return this.count;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

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

		public QueueObj(String id, String name, String type, String path, String status, long time) {
	        this.name = name;
	        this.startTime = time;
	        this.path = path;
	        this.id = id;
	        this.type = type;
	        this.setStatus(status);
	    }
	 
	    @Override
	    public long getDelay(TimeUnit unit) {
	        return startTime - System.currentTimeMillis();
	    }
	    
	    @Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
	    	QueueObj qObj = (QueueObj) obj;
	    	
	    	if (this.id.equals(qObj.getId())) {
	    		return true;
	    	}
	    	
	    	return false;
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

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}
	

}
