package com.tsn.serv.auth.service.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.common.utils.web.entity.page.Page;
import com.tsn.serv.auth.entity.sys.SysModule;
import com.tsn.serv.auth.entity.sys.SysPermiss;
import com.tsn.serv.auth.mapper.sys.SysModuleMapper;
import com.tsn.serv.auth.mapper.sys.SysPermissMapper;

@Service
public class SysModuleService {

	@Autowired
	private SysModuleMapper sysModuleMapper;

	@Autowired
    private SysPermissMapper sysPermissMapper;

    public void sysSysModuleList(Page page) {
        List<SysModule> sysModuleList = sysModuleMapper.sysSysModuleList(page);
        page.setData(sysModuleList);
    }

    public void addSysModule(SysModule sysModule) {
        sysModuleMapper.insert(sysModule);
    }

    public void updateSysModule(SysModule sysModule) {
        sysModuleMapper.updateDynamic(sysModule);
    }

    public void deleteSysModule(List<SysModule> sysModuleList) {
        List<Integer> idList = new ArrayList<>();
        sysModuleList.stream()
                .forEach(item -> {
                    idList.add(item.getModuleId());
                });
        int i = sysModuleMapper.deleteSysModuleById(idList);
    }

    /**
     * 获取一级菜单
     * @param page
     */
    public void getOneModuleList(Page page) {
        page.setData(moduleGrade());
    }

    /**
     * 根据菜单ID获取所有下级菜单
     * @param
     * @return
     */
    public List<Map<String, Object>> moduleGrade(){
        List<SysModule> moduleList = sysModuleMapper.getOneModuleList();
        List<Map<String, Object>> list = new ArrayList<>();

        moduleList.stream()
                .forEach(module -> {
                    Map<String, Object> map = new HashMap<>();
                    List<Map<String, Object>> listChildren = new ArrayList<>();
                    Integer id = module.getModuleId();
                    // 不存储菜单的nodeKey
                    // map.put("nodeKey",module.getModuleCode());
                    map.put("data",module);
                    map.put("type","module");//类型 module：菜单 permiss：权限
                    map.put("label",module.getModuleName());
                    listChildren = moduleGradeTo(id);
                    if (listChildren.size()>0) {
                        map.put("children", listChildren);
                    }else{
                        //当菜单没有下级添加权限
                        // 根据菜单Code获取对应权限
                        List<SysPermiss> permissList = sysPermissMapper.getPermissByModuleCode(module.getModuleCode());

                        // 权限Children
                        List<Map<String, Object>> permissChildren = new ArrayList<Map<String,Object>>();
                        if (permissList.size() > 0){
                            permissList.stream().forEach(permiss -> {
                                Map<String, Object> permissMap = new HashMap<String, Object>();
                                // 只存储按钮权限的nodeKey
                                permissMap.put("nodeKey",permiss.getPermissCode());
                                permissMap.put("data",permiss);
                                map.put("type","permiss");
                                permissMap.put("label",permiss.getPermissName());
                                permissChildren.add(permissMap);
                            });
                        }

                        map.put("children", permissChildren);
                    }
                    list.add(map);
                });

        return list;
    }

    public List<Map<String, Object>> moduleGradeTo(Integer parentId){
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        List firstIdList = new ArrayList();
        firstIdList.add(parentId);
        // 根据ID获取下级会员信息
        List<SysModule> moduleList = sysModuleMapper.getSubordinateModule(firstIdList);
        moduleList.stream().forEach(module -> {
            Map<String, Object> map = new HashMap<String, Object>();
            List<Map<String, Object>> listChildren = new ArrayList<Map<String,Object>>();
            Integer id = module.getModuleId();
            // 不存储菜单的nodeKey
            // map.put("nodeKey",module.getModuleCode());
            map.put("data",module);
            map.put("type","module");
            map.put("label",module.getModuleName());
            listChildren = moduleGradeTo(id);
            if (listChildren.size()>0) {
                map.put("children", listChildren);
            }else{
                //当菜单没有下级添加权限
                // 根据菜单Code获取对应权限
                List<SysPermiss> permissList = sysPermissMapper.getPermissByModuleCode(module.getModuleCode());

                // 权限Children
                List<Map<String, Object>> permissChildren = new ArrayList<Map<String,Object>>();
                if (permissList.size() > 0){
                    permissList.stream().forEach(permiss -> {
                        Map<String, Object> permissMap = new HashMap<String, Object>();
                        // 只存储
                        permissMap.put("nodeKey",permiss.getPermissCode());
                        permissMap.put("data",permiss);
                        map.put("type","permiss");
                        permissMap.put("label",permiss.getPermissName());
                        permissChildren.add(permissMap);
                    });
                }

                map.put("children", permissChildren);
            }
            list.add(map);
        });

        return list;
    }
}
