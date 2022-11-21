package com.tsn.serv.mem.controller.node;

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
import com.tsn.serv.mem.entity.node.NodePath;
import com.tsn.serv.mem.entity.node.NodePathAutoGroup;
import com.tsn.serv.mem.service.node.NodeKeyService;
import com.tsn.serv.mem.service.node.NodePathService;

@RestController
@RequestMapping("node")
public class NodeController {

    @Autowired
    private NodePathService nodePathService;

    @Autowired
    private NodeKeyService nodeKeyService;

    @GetMapping("/nodePathList")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> nodePathList(Page page) {
        nodePathService.nodePathList(page);
        return Response.ok(page);
    }

    @PostMapping("/addNodePath")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> addNodePath(@RequestBody NodePath nodePath) {
        nodePathService.addNodePath(nodePath);
        return Response.ok();
    }

    @PutMapping("/updateNodePath")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> updateNodePath(@RequestBody NodePath nodePath) {
        nodePathService.updateNodePath(nodePath);
        return Response.ok();
    }

    @DeleteMapping("/deleteNodePath")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> deleteNodePath(@RequestBody NodePath nodePath) {
        nodePathService.deleteNodePath(nodePath);
        return Response.ok();
    }

    @GetMapping("/nodeKeyList")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> nodeKeyList(Page page) {
        nodeKeyService.nodeKeyList(page);
        return Response.ok(page);
    }
    
    @GetMapping("/nodePathGroupList/{pathType}")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> nodePathList(@PathVariable String pathType) {
    	List<NodePathAutoGroup> nodePathAutoGroupList = nodePathService.getNodePathAutoGroupList(pathType);
        return Response.ok(nodePathAutoGroupList);
    }
    
    @PostMapping("/addNodeGroup")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> addNodePath(@RequestBody NodePathAutoGroup nodeGroup) {
        nodePathService.addNodeGroup(nodeGroup);
        return Response.ok();
    }
    
    @PutMapping("/udpateNodeGroup")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> udpateNodeGroup(@RequestBody NodePathAutoGroup nodeGroup) {
        nodePathService.updateNodeGroup(nodeGroup);
        return Response.ok();
    }
    
    @PutMapping("/batch/{pathType}")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> batchNodePath(@PathVariable String pathType, @RequestBody NodePath nodePath) {
        nodePathService.batchNodePath(nodePath, pathType);
        return Response.ok();
    }
    
    @DeleteMapping("/delNodeGroup/{groupId}")
    @AuthClient(client = AuthEnum.bea_mn)
    public Response<?> delNodeGroup(@PathVariable String groupId) {
        nodePathService.deleteNodeGroup(groupId);
        return Response.ok();
    }

}
