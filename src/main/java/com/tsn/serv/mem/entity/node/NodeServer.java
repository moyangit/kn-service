package com.tsn.serv.mem.entity.node;

public class NodeServer {
    private String uuid;

    private String serverId;

    private String portArr;

    private String isUsed;
    
    private String type;

    @Override
	public String toString() {
		return "NodeServer [uuid=" + uuid + ", serverId=" + serverId
				+ ", v2Port=" + portArr + ", isUsed=" + isUsed + ", type="
				+ type + ", serverIp=" + serverIp + "]";
	}

	private String serverIp;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId == null ? null : serverId.trim();
    }

	public String getPortArr() {
		return portArr;
	}

	public void setPortArr(String portArr) {
		this.portArr = portArr;
	}

	public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed == null ? null : isUsed.trim();
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp == null ? null : serverIp.trim();
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}