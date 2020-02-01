package com.recore.projectrecoresc.Model;

public class PostTextItem {

    private String postText;
    private String imgUser;
    private String contentType;

    private Object timeStamp;
    public PostTextItem() {
    }

    public PostTextItem(String postText, String imgUser, Object timeStamp, String contentType) {
        this.postText = postText;
        this.imgUser = imgUser;
        this.timeStamp = timeStamp;
        this.contentType = contentType;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getImgUser() {
        return imgUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
