package com.tsn.serv.auth.mapper.app;

import java.util.List;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.app.AppVersion;

public interface AppVersionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppVersion record);

    int insertSelective(AppVersion record);

    AppVersion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppVersion record);

    int updateByPrimaryKey(AppVersion record);
    
    AppVersion selectLastByAppType(String appType);

    List<AppVersion> getAppVersionByPage(Page page);

    void updateIsNew(AppVersion appVersion);
}