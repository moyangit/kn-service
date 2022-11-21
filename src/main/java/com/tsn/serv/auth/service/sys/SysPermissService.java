package com.tsn.serv.auth.service.sys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.sys.SysPermiss;
import com.tsn.serv.auth.mapper.sys.SysPermissMapper;

@Service
public class SysPermissService {

	@Autowired
	private SysPermissMapper sysPermissMapper;

    public void sysPermissList(Page page) {
        List<SysPermiss> sysPermissList = sysPermissMapper.sysPermissList(page);
        page.setData(sysPermissList);
    }

    public void addSysPermiss(SysPermiss sysPermiss) {
        sysPermissMapper.insert(sysPermiss);
    }

    public void updateSysPermiss(SysPermiss sysPermiss) {
        sysPermissMapper.updateDynamic(sysPermiss);
    }

    public void deleteSysPermiss(List<SysPermiss> sysPermissList) {
        List<Integer> idList = new ArrayList<>();
        sysPermissList.stream()
                .forEach(item -> {
                    idList.add(item.getPermissId());
                });
        int i = sysPermissMapper.deleteSysPermissById(idList);
    }
}
