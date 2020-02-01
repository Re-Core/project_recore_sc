package com.recore.projectrecoresc.Model;

public class UserPlan {
    private String userId;
    private String userPlan;
    private Object userTimeStamp;

    public UserPlan() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPlan() {
        return userPlan;
    }

    public void setUserPlan(String userPlan) {
        this.userPlan = userPlan;
    }

    public Object getUserTimeStamp() {
        return userTimeStamp;
    }

    public void setUserTimeStamp(Object userTimeStamp) {
        this.userTimeStamp = userTimeStamp;
    }
}
