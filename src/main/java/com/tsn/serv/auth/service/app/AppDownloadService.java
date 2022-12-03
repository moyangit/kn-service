package com.tsn.serv.auth.service.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tsn.common.utils.utils.CommUtils;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.common.utils.monit.DownServerMonitor;
import com.tsn.serv.auth.entity.app.AppDownload;
import com.tsn.serv.auth.mapper.app.AppDownloadMapper;

@Service
public class AppDownloadService {

    @Autowired
    private AppDownloadMapper appDownloadMapper;

    public void getAppDownloadByPage(Page page){
        List<AppDownload> downloadList = appDownloadMapper.getAppDownloadByPage(page);
        page.setData(downloadList);
    }

    public List<AppDownload> getAppDownloadList(){
        List<AppDownload> downloadList = appDownloadMapper.getAppDownloadList();
        return downloadList;
    }
    
    public List<AppDownload> getAppDownloadListUse(){
        List<AppDownload> downloadList = appDownloadMapper.getAppDownloadList();
        //获取所有第三方地址和临时的可以使用的下载路径
        List<AppDownload> thirdDownloadList = downloadList.stream().filter(a -> "0".equals(a.getStatus()) && ("1".equals(a.getDownType()) || "2".equals(a.getDownType()))).collect(Collectors.toList());
        return thirdDownloadList;
    }
    
    /**
     * 获取下载连接，根据type 和 name 分组取第一个
     * 下载面向网页下载，优先获取第三方和临时，如果没有就获取自建 
     * @return
     */
    public List<AppDownload> getAppDownloadListByStatus(){
    	
    	//先获取第三方地址 和 临时， 如果没有，则使用默认自建地址
        List<AppDownload> allDownloadList = appDownloadMapper.getAppDownloadList();
        
        //获取所有第三方地址和临时的可以使用的下载路径
        List<AppDownload> thirdDownloadList = allDownloadList.stream().filter(a -> "0".equals(a.getStatus()) && ("1".equals(a.getDownType()) || "2".equals(a.getDownType()))).collect(Collectors.toList());
        //按类型分组，同时随机取一个
        Map<String, AppDownload> resultList = thirdDownloadList.stream().collect(Collectors.groupingBy(down -> down.getType(),Collectors.collectingAndThen(Collectors.toList(), value -> value == null || value.isEmpty() ? null : value.get(new Random().nextInt(value.size())))));
        
        //如果为使用自建服务器，这里没有
        if (resultList.isEmpty()){
        	List<AppDownload> ownDownloadList = allDownloadList.stream().filter(a -> "0".equals(a.getDownType())).collect(Collectors.toList());
        	resultList = ownDownloadList.stream().collect(Collectors.groupingBy(down -> down.getType(),Collectors.collectingAndThen(Collectors.toList(), value -> value == null || value.isEmpty() ? null : value.get(new Random().nextInt(value.size())))));
        }
        
        List<AppDownload> downLoadList = new ArrayList<AppDownload>();
        
        resultList.forEach((key, val) -> {
        	downLoadList.add(val);
        });
        
        return downLoadList;
    }
    
    /**
     * 主要用于网盘链接推广，如果网盘链接出现问题，就获取临时的
     * 获取下载连接，根据type 和 name 分组取第一个
     * 只获取临时，不获取第三方，没有就获取自建
     * @return
     */
    public List<AppDownload> getAppDownloadForDownType2ListByStatus(){
    	
    	//先获取第三方地址 和 临时， 如果没有，则使用默认自建地址
        List<AppDownload> allDownloadList = appDownloadMapper.getAppDownloadList();
        
        //获取临时的
        List<AppDownload> thirdDownloadList = allDownloadList.stream().filter(a -> "0".equals(a.getStatus()) && "1".equals(a.getDownType())).collect(Collectors.toList());
        //按类型分组，同时随机取一个
        Map<String, AppDownload> resultList = thirdDownloadList.stream().collect(Collectors.groupingBy(down -> down.getType(),Collectors.collectingAndThen(Collectors.toList(), value -> value == null || value.isEmpty() ? null : value.get(new Random().nextInt(value.size())))));
        
        //如果为空使用自建服务器
        if (resultList.isEmpty()){
        	List<AppDownload> ownDownloadList = allDownloadList.stream().filter(a -> "0".equals(a.getDownType())).collect(Collectors.toList());
        	resultList = ownDownloadList.stream().collect(Collectors.groupingBy(down -> down.getType(),Collectors.collectingAndThen(Collectors.toList(), value -> value == null || value.isEmpty() ? null : value.get(new Random().nextInt(value.size())))));
        }
        
        List<AppDownload> downLoadList = new ArrayList<AppDownload>();
        
        resultList.forEach((key, val) -> {
        	downLoadList.add(val);
        });
        
        return downLoadList;
    }
    
    /**
     * 获取app在线更新，根据type 和 name 分组取第一个
     * 特殊处理，如果是pc，只需要放回上一层文件夹
     * 只获取临时，不获取第三方，没有就获取自建
     * @return
     */
    public String getAppDownloadForUpdateListByStatus(String appType){
    	
    	//先获取第三方地址 和 临时， 如果没有，则使用默认自建地址
        List<AppDownload> allDownloadList = appDownloadMapper.getAppDownloadList();
        
        //获取临时的
        List<AppDownload> thirdDownloadList = allDownloadList.stream().filter(a -> "0".equals(a.getStatus()) && "1".equals(a.getDownType())).collect(Collectors.toList());
        //按类型分组，同时随机取一个
        Map<String, AppDownload> resultList = thirdDownloadList.stream().collect(Collectors.groupingBy(down -> down.getType(),Collectors.collectingAndThen(Collectors.toList(), value -> value == null || value.isEmpty() ? null : value.get(new Random().nextInt(value.size())))));
        
        //如果为使用自建服务器
        if (resultList.isEmpty()){
        	List<AppDownload> ownDownloadList = allDownloadList.stream().filter(a -> "0".equals(a.getDownType())).collect(Collectors.toList());
        	resultList = ownDownloadList.stream().collect(Collectors.groupingBy(down -> down.getType(),Collectors.collectingAndThen(Collectors.toList(), value -> value == null || value.isEmpty() ? null : value.get(new Random().nextInt(value.size())))));
        }
        
        for (Map.Entry<String, AppDownload> entry : resultList.entrySet()) {
        	
        	AppDownload val = entry.getValue();
        	
        	//如果是pc获取下载链接的上一层
        	if ("1".equals(val.getType())) {
        		String pathTemp = val.getPath().substring(0, val.getPath().lastIndexOf("/"));
        		val.setPath(pathTemp);
        	}
        	
        	if ("ad".equals(appType) && "2".equals(val.getType())) {
        		return val.getPath();
        	} else if ("win32".equals(appType) && "1".equals(val.getType())) {
        		return val.getPath();
        	} else if ("darwin".equals(appType) && "4".equals(val.getType())) {
        		return val.getPath();
        	} else if ("ios".equals(appType) && "3".equals(val.getType())) {
        		return val.getPath();
        	}
        }
        
        return "";
    }

    public void addAppDownload(AppDownload appDownload) {
        appDownload.setId(CommUtils.getUuid());
        appDownload.setStatus("0");
        appDownload.setCreateDate(new Date());
        appDownload.setUpdateDate(appDownload.getCreateDate());
        appDownloadMapper.insertIngore(appDownload);
        
        DownServerMonitor.build().initQueue();
    }
    
    @Transactional
    public void addAppDownload(List<AppDownload> appDownloadList) {
    	
    	if (appDownloadList != null && !appDownloadList.isEmpty()) {
    		
    		for (AppDownload appDownload : appDownloadList) {
    			appDownload.setId(CommUtils.getUuid());
    			appDownload.setStatus("0");
                appDownload.setCreateDate(new Date());
                appDownload.setUpdateDate(new Date());
                int res = appDownloadMapper.updateByTypeAndName(appDownload);
                
                //如果更新失败，就添加
                if (res == 0) {
                	appDownloadMapper.insertIngore(appDownload);
                }
    		}
    		
    		DownServerMonitor.build().initQueue();
    		
    	}
    }
    
    

    public void updateAppDownload(AppDownload appDownload) {
        appDownload.setUpdateDate(new Date());
        appDownloadMapper.updateDynamic(appDownload);
        DownServerMonitor.build().initQueue();
    }
    
    /**
     * 只修改状态
     * @param appDownload
     */
    public void updateAppDownloadStatus(AppDownload appDownload) {
        appDownload.setUpdateDate(new Date());
        appDownloadMapper.updateDynamic(appDownload);
    }
    
    public void delAppDownload(String id) {
    	appDownloadMapper.deleteByPrimaryKey(id);
        DownServerMonitor.build().initQueue();
    }
}
