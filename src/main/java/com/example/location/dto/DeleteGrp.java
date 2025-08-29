package com.example.location.dto;

public class DeleteGrp {
    private String userId;
    private String groupId;
    private String removedBy;


    
    public String getUserId() {
        return userId;
    }
    public DeleteGrp() {
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getRemovedBy() {
        return removedBy;
    }
    public void setRemovedBy(String removedBy) {
        this.removedBy = removedBy;
    }
    
    
}
