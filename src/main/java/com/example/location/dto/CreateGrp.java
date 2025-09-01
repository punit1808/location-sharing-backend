package com.example.location.dto;

public class CreateGrp {
    private String email;
    private String grpName;

    
    public CreateGrp(String email, String grpName) {
        this.email = email;
        this.grpName = grpName;
    }
    public CreateGrp() {
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return grpName;
    }
    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

}
