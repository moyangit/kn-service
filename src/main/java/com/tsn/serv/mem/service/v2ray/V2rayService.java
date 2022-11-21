package com.tsn.serv.mem.service.v2ray;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tsn.serv.common.entity.v2ray.V2User;
import com.tsn.serv.mem.entity.node.NodeServer;
import com.tsn.serv.mem.mapper.node.NodeServerMapper;
import com.v2ray.core.V2RayApiClient;
import com.v2ray.core.app.proxyman.command.AddUserOperation;
import com.v2ray.core.app.proxyman.command.AlterInboundRequest;
import com.v2ray.core.app.proxyman.command.RemoveUserOperation;
import com.v2ray.core.common.protocol.SecurityConfig;
import com.v2ray.core.common.protocol.SecurityType;
import com.v2ray.core.common.protocol.User;
import com.v2ray.core.common.serial.TypedMessage;
import com.v2ray.core.proxy.vmess.Account;

@Service
public class V2rayService {
	
	@Autowired
	private NodeServerMapper nodeServerMapper;
	
	public List<NodeServer> selectUseServer() {
		return nodeServerMapper.selectUseServer("node");
	}

    public void rmProxyAccount(String host, Integer port, V2User v2User, String inBoundTag) {
        V2RayApiClient client = V2RayApiClient.getInstance(host, port);
        TypedMessage rmOp = TypedMessage.newBuilder().setType(RemoveUserOperation.getDescriptor().getFullName()).setValue(RemoveUserOperation.newBuilder().setEmail(v2User.getEmail()).build().toByteString()).build();
        AlterInboundRequest req = AlterInboundRequest.newBuilder().setTag(inBoundTag).setOperation(rmOp).build();
        client.getHandlerServiceBlockingStub().alterInbound(req);
    }

    public void addProxyAccount(String host, Integer port, V2User v2User, String inBoundTag) {
        V2RayApiClient client = V2RayApiClient.getInstance(host, port);
        Account account = Account.newBuilder().setAlterId(v2User.getAlterId()).setId(v2User.getId()).setSecuritySettings(SecurityConfig.newBuilder().setType(SecurityType.AUTO).build()).build();

        TypedMessage AccountTypedMsg = TypedMessage.newBuilder().setType(Account.getDescriptor().getFullName()).setValue(account.toByteString()).build();
        User user = User.newBuilder().setEmail(v2User.getEmail()).setLevel(v2User.getLevel()).setAccount(AccountTypedMsg).build();
        AddUserOperation addUserOperation = AddUserOperation.newBuilder().setUser(user).build();

        TypedMessage typedMessage = TypedMessage.newBuilder().setType(AddUserOperation.getDescriptor().getFullName()).setValue(addUserOperation.toByteString()).build();

        client.getHandlerServiceBlockingStub().alterInbound(AlterInboundRequest.newBuilder().setTag(inBoundTag).setOperation(typedMessage).build());
    }
}