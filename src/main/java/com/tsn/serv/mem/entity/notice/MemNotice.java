package com.tsn.serv.mem.entity.notice;

public class MemNotice {
    private Integer id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告标题关键字
     */
    private String keyword;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 0：需跳转详情 1：无需跳转详情
     */
    private String isDetails;

    /**
     * 0：需跳转网页 1：无需跳转网页
     */
    private String isJump;

    /**
     * 跳转按钮名称
     */
    private String buttonName;

    /**
     * 跳转网址
     */
    private String url;

    /**
     * 0：通用公告 1：安卓公告 2：ios公告
     */
    private String type;

    /**
     * 0：可用 1：不可用
     */
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsDetails() {
        return isDetails;
    }

    public void setIsDetails(String isDetails) {
        this.isDetails = isDetails;
    }

    public String getIsJump() {
        return isJump;
    }

    public void setIsJump(String isJump) {
        this.isJump = isJump;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
