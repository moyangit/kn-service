package com.tsn.serv.auth.controller.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsn.common.utils.web.entity.Response;
import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.common.web.entity.AuthEnum;
import com.tsn.common.web.web.auth.anno.AuthClient;
import com.tsn.serv.auth.entity.app.AppDownload;
import com.tsn.serv.auth.service.app.AppDownloadService;

/**
 * APP下载链接
 * @author admin
 *
 */
@RestController
@RequestMapping("appDownloa")
public class AppDownloadController {

    @Autowired
    private AppDownloadService appDownloadService;

    @GetMapping("page")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> getAppDownloadByPage(Page page){
        appDownloadService.getAppDownloadByPage(page);
        return Response.ok(page);
    }

    @GetMapping("getAppDownloadList")
    public Response<?> getAppDownloadList() {
        List<AppDownload> downloadList = appDownloadService.getAppDownloadListByStatus();
        return Response.ok(downloadList);
    }
    
    @GetMapping("all")
    public Response<?> getAppDownloadListAll() {
        List<AppDownload> downloadList = appDownloadService.getAppDownloadListUse();
        return Response.ok(downloadList);
    }

    @PostMapping("add")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> addAppDownload(@RequestBody AppDownload appDownload){
        appDownloadService.addAppDownload(appDownload);
        return Response.ok();
    }
    
    @PostMapping("adds")
    public Response<?> addAppDownloads(@RequestBody List<AppDownload> appDownloadList){
        appDownloadService.addAppDownload(appDownloadList);
        return Response.ok();
    }

    @PutMapping("update")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> updateAppDownload(@RequestBody AppDownload appDownload){
        appDownloadService.updateAppDownload(appDownload);
        return Response.ok();
    }
    
    @DeleteMapping("/{id}")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> delAppDownload(@PathVariable String id){
        appDownloadService.delAppDownload(id);
        return Response.ok();
    }
}
