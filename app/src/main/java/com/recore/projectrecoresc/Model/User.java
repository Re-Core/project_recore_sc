package com.recore.projectrecoresc.Model;

public class User {

    private String username;
    private String userId;
    private String userPhone;
    private String userAvatar;
    private String userState;
    private String userPlane;

    private String userPoint, userContribution;

    private Object SubscriptionDate;
    private Object userTimeStamp;

    public User(String username, String userId, String userPhone, String userAvatar, String userState, String userPlane, String userPoint, String userContribution, Object subscriptionDate, Object userTimeStamp) {
        this.username = username;
        this.userId = userId;
        this.userPhone = userPhone;
        this.userAvatar = userAvatar;
        this.userState = userState;
        this.userPlane = userPlane;
        this.userPoint = userPoint;
        this.userContribution = userContribution;
        SubscriptionDate = subscriptionDate;
        this.userTimeStamp = userTimeStamp;
    }

    public User() {
    }

    public Object getSubscriptionDate() {
        return SubscriptionDate;
    }

    public void setSubscriptionDate(Object subscriptionDate) {
        SubscriptionDate = subscriptionDate;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
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

    public Object getUserTimeStamp() {
        return userTimeStamp;
    }

    public void setUserTimeStamp(Object userTimeStamp) {
        this.userTimeStamp = userTimeStamp;
    }

    public String getUserPlane() {
        return userPlane;
    }

    public void setUserPlane(String userPlane) {
        this.userPlane = userPlane;
    }

    public String getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(String userPoint) {
        this.userPoint = userPoint;
    }

    public String getUserContribution() {
        return userContribution;
    }

    public void setUserContribution(String userContribution) {
        this.userContribution = userContribution;
    }
}
