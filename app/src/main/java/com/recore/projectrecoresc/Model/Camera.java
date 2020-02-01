package com.recore.projectrecoresc.Model;

public class Camera {
    private double latitude;
    private double longitude;
    private Object timeStamp;

    private String username;
    private String userId;
    private String userPhone;
    private String userAvatar;

    private String cameraCity;
    private String cameraCountry;


    public Camera() {
    }

    public Camera(double latitude, double longitude, Object timeStamp, String username, String userId, String userPhone, String userAvatar, String cameraCity, String cameraCountry) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
        this.username = username;
        this.userId = userId;
        this.userPhone = userPhone;
        this.userAvatar = userAvatar;
        this.cameraCity = cameraCity;
        this.cameraCountry = cameraCountry;
    }

    public String getCameraCity() {
        return cameraCity;
    }

    public void setCameraCity(String cameraCity) {
        this.cameraCity = cameraCity;
    }

    public String getCameraCountry() {
        return cameraCountry;
    }

    public void setCameraCountry(String cameraCountry) {
        this.cameraCountry = cameraCountry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }



}
