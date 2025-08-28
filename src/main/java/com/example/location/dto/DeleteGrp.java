package com.example.location.dto;
import java.util.UUID;

public class DeleteGrp {
    private UUID userId;
    private UUID groupId;
    private UUID removedBy;
    public UUID getUserId() {
        return userId;
    }
    public UUID getRemovedBy() {
        return removedBy;
    }
    public DeleteGrp(UUID userId, UUID groupId, UUID removedBy) {
        this.userId = userId;
        this.groupId = groupId;
        this.removedBy = removedBy;
    }
    public void setRemovedBy(UUID removedBy) {
        this.removedBy = removedBy;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public DeleteGrp(UUID userId, UUID groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }
    public UUID getGroupId() {
        return groupId;
    }
    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
    
}
