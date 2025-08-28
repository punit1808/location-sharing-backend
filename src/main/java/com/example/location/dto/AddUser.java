package com.example.location.dto;
import java.util.UUID;


public class AddUser {
    UUID addedBy;
    UUID groupId;
    UUID userId;
    String role;
    
    public AddUser(UUID addedBy, UUID groupId, UUID userId, String role) {
        this.addedBy = addedBy;
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
    }
    public UUID getAddedBy() {
        return addedBy;
    }
    public void setAddedBy(UUID addedBy) {
        this.addedBy = addedBy;
    }
    public UUID getGroupId() {
        return groupId;
    }
    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
