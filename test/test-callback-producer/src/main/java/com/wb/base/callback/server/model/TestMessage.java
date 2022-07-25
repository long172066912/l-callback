package com.wb.base.callback.server.model;

/**
* @Title: TestMessage
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/6/2 11:27 AM
* @version V1.0
*/
public class TestMessage {
    /**
     * 必须有无参构造，否则泛型会反序列化失败
     */
    public TestMessage(){}

    public TestMessage(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    private Boolean status;
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}