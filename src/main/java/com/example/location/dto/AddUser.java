package com.example.location.dto;

public class AddUser {
    String addedBy;
    String groupId;
    String userId;
    String role;


    public AddUser(String addedBy, String groupId, String userId, String role) {
        this.addedBy = addedBy;
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
    }
    public AddUser() {
    }
    public String getAddedBy() {
        return addedBy;
    }
    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    
}
