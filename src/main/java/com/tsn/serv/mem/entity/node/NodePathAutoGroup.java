package com.tsn.serv.mem.entity.node;

import java.util.Date;
import java.util.List;

public class NodePathAutoGroup {
    private String autoGroupId;
    
    private String pathGrade;

    private String autoGroupName;

    private Integer autoGroupWeight;

    private Byte status;

    private Date createTime;

    private Date updateTime;
    
    private List<NodePath> nodePathList;

    public String getAutoGroupId() {
        return autoGroupId;
    }

    public void setAutoGroupId(String autoGroupId) {
        this.autoGroupId = autoGroupId == null ? null : autoGroupId.trim();
    }

    public String getAutoGroupName() {
        return autoGroupName;
    }

    public void setAutoGroupName(String autoGroupName) {
        this.autoGroupName = autoGroupName == null ? null : autoGroupName.trim();
    }

    public Integer getAutoGroupWeight() {
        return autoGroupWeight;
    }

    public void setAutoGroupWeight(Integer autoGroupWeight) {
        this.autoGroupWeight = autoGroupWeight;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public List<NodePath> getNodePathList() {
		return nodePathList;
	}

	public void setNodePathList(List<NodePath> nodePathList) {
		this.nodePathList = nodePathList;
	}

	public String getPathGrade() {
		return pathGrade;
	}

	public void setPathGrade(String pathGrade) {
		this.pathGrade = pathGrade;
	}
}