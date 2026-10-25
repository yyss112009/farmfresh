package com.backend.dto;

import jakarta.validation.constraints.*;

public class RegisterFarmerRequest {
    @NotBlank private String fullName;
    @NotBlank @Email private String email;
    @NotBlank private String mobile;
    private String farmName;
    private String farmAddress;
    private String state;
    @NotBlank @Size(min = 6) private String password;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }
    public String getFarmAddress() { return farmAddress; }
    public void setFarmAddress(String farmAddress) { this.farmAddress = farmAddress; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
