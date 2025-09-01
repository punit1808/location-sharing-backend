package com.example.location.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "MEMBERS")
public class GroupMemberEntity {

    @EmbeddedId
    private GroupMemberId id = new GroupMemberId();

    public GroupMemberEntity() {
    }

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(nullable = false)
    private Instant joinedAt = Instant.now();

    @Column(nullable = false)
    private String role = "MEMBER"; // default role

    public UserEntity getUser() {
        return user;
    }

    public GroupMemberId getId() {
        return id;
    }

    public void setId(GroupMemberId id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    
}
