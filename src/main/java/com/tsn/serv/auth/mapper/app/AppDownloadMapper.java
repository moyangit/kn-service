package com.tsn.serv.auth.mapper.app;

import java.util.List;

import com.tsn.common.utils.web.core.IBaseMapper;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.app.AppDownload;

public interface AppDownloadMapper extends IBaseMapper<AppDownload> {

    int delete(Integer id);

    int insert(AppDownload appDownload);

    int insertDynamic(AppDownload appDownload);
    
    int insertIngore(AppDownload appDownload);

    int updateDynamic(AppDownload appDownload);

    int update(AppDownload appDownload);
    
    int updateByTypeAndName(AppDownload appDownload);

    AppDownload selectById(Integer id);

    List<AppDownload> getAppDownloadByPage(Page page);

    List<AppDownload> getAppDownloadList();
    
    List<AppDownload> getAppDownloadListStatus(int status);

}