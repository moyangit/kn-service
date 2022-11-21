package com.tsn.serv.auth.entity.sys;

import java.util.Date;

public class SysModule {
	/**
	 * 模块id，模块可以是菜单，可以是虚拟的
	 */
	private Integer moduleId;

	/**
	 * 上级菜单ID
	 */
	private Integer superId;

	private String moduleCode;

	private String moduleName;

	/**
	 * 点击链接，事件地址
	 */
	private String clickHref;

	/**
	 * 0 表示目录，1表示其他
	 */
	private Integer isMenu;

	/**
	 * 是否可用 0可以，1不可用
	 */
	private Integer isUsed;

	private Integer sort;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public Integer getSuperId() {
		return superId;
	}

	public void setSuperId(Integer superId) {
		this.superId = superId;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getClickHref() {
		return clickHref;
	}

	public void setClickHref(String clickHref) {
		this.clickHref = clickHref;
	}

	public Integer getIsMenu() {
		return isMenu;
	}

	public void setIsMenu(Integer isMenu) {
		this.isMenu = isMenu;
	}

	public Integer getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}