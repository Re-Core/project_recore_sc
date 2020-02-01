package com.recore.projectrecoresc.Model;

public class PostVideoItem {

    private String videoURL;
    private String userImg;
    private Object timeStamp;
    private String contentType;


    public PostVideoItem() {
    }

    public PostVideoItem(String videoURL, String userImg, Object timeStamp, String contentType) {
        this.videoURL = videoURL;
        this.userImg = userImg;
        this.timeStamp = timeStamp;
        this.contentType = contentType;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
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
