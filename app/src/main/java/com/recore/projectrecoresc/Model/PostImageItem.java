package com.recore.projectrecoresc.Model;

public class PostImageItem {

    private String imgURL;
    private String userImg;
    private Object timeStamp;
    private String contentType;

    public PostImageItem() {
    }

    public PostImageItem(String imgURL, String userImg, Object timeStamp, String contentType) {
        this.imgURL = imgURL;
        this.userImg = userImg;
        this.timeStamp = timeStamp;
        this.contentType = contentType;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
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
