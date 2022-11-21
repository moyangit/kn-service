package com.tsn.serv.auth.common.utils;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tsn.serv.auth.common.utils.monit.DownServerMonitor;
import com.tsn.serv.pub.common.down.DownManager;

@Component
public class InitTask implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
    	DownServerMonitor.build();
    	DownManager.build().initData();
    }
}