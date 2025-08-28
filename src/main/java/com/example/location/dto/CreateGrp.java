package com.example.location.dto;

import java.util.UUID;
public class CreateGrp {
    private String name;
    private UUID creatorId;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public UUID getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

}
