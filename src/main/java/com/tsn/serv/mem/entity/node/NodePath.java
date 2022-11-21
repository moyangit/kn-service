package com.tsn.serv.mem.entity.node;

public class NodePath {

    private String pathId;

    private String pathCode;

    private String pathName;

    private String path;

    private String inboundTag;

    private String protocol;

    private String network;

    private String security;
    
    private Integer direct;
    
    private String serverIds;

    /**
     * 一组服务器信息[{\
     */
    private String serverArr;

    private String nodeArr;

    private String createUserId;

    /**
     * 运行状态 bad，good，great
     */
    private String runStatus;
    
    private String countryCode;

    /**
     * http://192.168.0.203/gq/falg_america.png
     */
    private String iconUrl;

    private Integer isUse;

    public Integer getDirect() {
		return direct;
	}

	public void setDirect(Integer direct) {
		this.direct = direct;
	}

	public String getServerIds() {
		return serverIds;
	}

	public void setServerIds(String serverIds) {
		this.serverIds = serverIds;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	private String pathGrade;
    
    private Integer weight;
    
    private String node;
    
    private Integer isAuto;
    
    private String autoGroupId;
    
    private Integer sort;

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	
    public String getPathId() {
		return pathId;
	}

	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	public String getPathCode() {
		return pathCode;
	}

	public void setPathCode(String pathCode) {
		this.pathCode = pathCode;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getInboundTag() {
		return inboundTag;
	}

	public void setInboundTag(String inboundTag) {
		this.inboundTag = inboundTag;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getServerArr() {
		return serverArr;
	}

	public void setServerArr(String serverArr) {
		this.serverArr = serverArr;
	}

	public String getNodeArr() {
		return nodeArr;
	}

	public void setNodeArr(String nodeArr) {
		this.nodeArr = nodeArr;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Integer getIsUse() {
		return isUse;
	}

	public void setIsUse(Integer isUse) {
		this.isUse = isUse;
	}

	public String getPathGrade() {
		return pathGrade;
	}

	public void setPathGrade(String pathGrade) {
		this.pathGrade = pathGrade;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Integer getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(Integer isAuto) {
		this.isAuto = isAuto;
	}

	public String getAutoGroupId() {
		return autoGroupId;
	}

	public void setAutoGroupId(String autoGroupId) {
		this.autoGroupId = autoGroupId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "NodePath [pathId=" + pathId + ", pathCode=" + pathCode
				+ ", pathName=" + pathName + ", path=" + path + ", inboundTag="
				+ inboundTag + ", protocol=" + protocol + ", network="
				+ network + ", security=" + security + ", serverArr="
				+ serverArr + ", nodeArr=" + nodeArr + ", createUserId="
				+ createUserId + ", runStatus=" + runStatus + ", iconUrl="
				+ iconUrl + ", isUse=" + isUse + ", pathGrade=" + pathGrade
				+ ", weight=" + weight + ", node=" + node + ", isAuto="
				+ isAuto + ", autoGroupId=" + autoGroupId + ", sort=" + sort
				+ "]";
	}
}
