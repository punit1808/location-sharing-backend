package com.example.location.dto;
import java.util.UUID;

public class GrpNames {
    private UUID id;
    private String name;

    public GrpNames(){
        
    }
    public UUID getId() {
        return id;
    }
    public GrpNames(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
