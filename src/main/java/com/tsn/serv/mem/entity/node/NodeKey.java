package com.tsn.serv.mem.entity.node;

import java.util.Date;

public class NodeKey {
    private String seriNo;
    
    private String inboundTag;

    private String keyBatchNo;

    private Byte status = 0;

    private Integer keyAge;

    private Date createDate;
    
    private Date updateDate;

	public String getSeriNo() {
		return seriNo;
	}

	public void setSeriNo(String seriNo) {
		this.seriNo = seriNo;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	private Byte isDel = 0;

    private String keyArry;

    @Override
	public String toString() {
		return "NodeKey [seriNo=" + seriNo + ", inboundTag=" + inboundTag
				+ ", keyBatchNo=" + keyBatchNo + ", status=" + status
				+ ", keyAge=" + keyAge + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", isDel=" + isDel
				+ ", keyArry=" + keyArry + "]";
	}

	public String getKeyBatchNo() {
        return keyBatchNo;
    }

    public void setKeyBatchNo(String keyBatchNo) {
        this.keyBatchNo = keyBatchNo == null ? null : keyBatchNo.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getKeyAge() {
        return keyAge;
    }

    public void setKeyAge(Integer keyAge) {
        this.keyAge = keyAge;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Byte getIsDel() {
        return isDel;
    }

    public void setIsDel(Byte isDel) {
        this.isDel = isDel;
    }

    public String getKeyArry() {
        return keyArry;
    }

    public void setKeyArry(String keyArry) {
        this.keyArry = keyArry == null ? null : keyArry.trim();
    }

	public String getInboundTag() {
		return inboundTag;
	}

	public void setInboundTag(String inboundTag) {
		this.inboundTag = inboundTag;
	}
}