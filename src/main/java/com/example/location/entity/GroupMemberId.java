package com.example.location.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;

@Embeddable
public class GroupMemberId implements Serializable {
    private UUID userId;
    private UUID groupId;

    public GroupMemberId() {}
    public GroupMemberId(UUID userId, UUID groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupMemberId)) return false;
        GroupMemberId that = (GroupMemberId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }
}
