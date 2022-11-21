package com.tsn.serv.mem.entity.credits;

import java.util.Date;

public class CreditsTask {
    private String id;

    private String taskName;

    private String taskType;

    private Byte taskAwardsType;

    private Integer taskCycleLimit;

    private Integer taskVal;
    
    private Integer taskUseNum;

    private Integer taskLimit;

    private Date createTime;

    private Date updateTime;
    
    private Byte taskStatus;
    
    private Integer taskOrder;
    
    /**
     * 可否使用 1.是 0.否
     */
    private Byte isUsable;
    
    /**
     * 描述
     */
    private String taskDescribe;

    /**
     * 前端按钮名
     */
    private String buttonName;
    /**
     * 设备类型
     */
    private String deviceType;
    
    private String unit;
    
    //是否在安卓上隐藏val数值
    private int isHideVal = 0;
    
    private String tag;
    
    public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType == null ? null : taskType.trim();
    }

    public Byte getTaskAwardsType() {
        return taskAwardsType;
    }

    public void setTaskAwardsType(Byte taskAwardsType) {
        this.taskAwardsType = taskAwardsType;
    }

    public Integer getTaskCycleLimit() {
        return taskCycleLimit;
    }

    public void setTaskCycleLimit(Integer taskCycleLimit) {
        this.taskCycleLimit = taskCycleLimit;
    }

    public Integer getTaskVal() {
        return taskVal;
    }

    public void setTaskVal(Integer taskVal) {
        this.taskVal = taskVal;
    }

    public Integer getTaskLimit() {
        return taskLimit;
    }

    public void setTaskLimit(Integer taskLimit) {
        this.taskLimit = taskLimit;
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

	public Byte getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Byte taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Byte getIsUsable() {
		return isUsable;
	}

	public void setIsUsable(Byte isUsable) {
		this.isUsable = isUsable;
	}

	public Integer getTaskOrder() {
		return taskOrder;
	}

	public void setTaskOrder(Integer taskOrder) {
		this.taskOrder = taskOrder;
	}

	public String getTaskDescribe() {
		return taskDescribe;
	}

	public void setTaskDescribe(String taskDescribe) {
		this.taskDescribe = taskDescribe;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getTaskUseNum() {
		return taskUseNum;
	}

	public void setTaskUseNum(Integer taskUseNum) {
		this.taskUseNum = taskUseNum;
	}

	public int getIsHideVal() {
		return isHideVal;
	}

	public void setIsHideVal(int isHideVal) {
		this.isHideVal = isHideVal;
	}
}