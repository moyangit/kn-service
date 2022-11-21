package com.tsn.serv.auth.entity.app;

import java.util.Date;

public class AppDownload {
    private String id;

    /**
     * 名称
     */
    private String name;
    
    /**
     * 唯一标识
     */
    private String code;

    /**
     * 地址
     */
    private String path;

    /**
     * 类型：PC、Android、IOS、MacOS
     */
    private String type;
    
    private String status;
    
    /**
     * 0 固定，1 动态
     */
    private String downType;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    @Override
	public String toString() {
		return "AppDownload [id=" + id + ", name=" + name + ", code=" + code
				+ ", path=" + path + ", type=" + type + ", status=" + status
				+ ", downType=" + downType + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + "]";
	}

	public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

	public String getDownType() {
		return downType;
	}

	public void setDownType(String downType) {
		this.downType = downType;
	}
}
