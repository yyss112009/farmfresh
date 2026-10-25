package com.backend.dto;

import com.backend.entity.Farmer;

public class FarmerResponse {
    public Long id;
    public String fullName;
    public String email;
    public String mobile;
    public String farmName;
    public String farmAddress;
    public String state;
    public String city;
    public String pincode;
    public String about;
    public String status;

    public static FarmerResponse from(Farmer f) {
        FarmerResponse r = new FarmerResponse();
        r.id = f.getId();
        r.fullName = f.getFullName();
        r.email = f.getEmail();
        r.mobile = f.getMobile();
        r.farmName = f.getFarmName();
        r.farmAddress = f.getFarmAddress();
        r.state = f.getState();
        r.city = f.getCity();
        r.pincode = f.getPincode();
        r.about = f.getAbout();
        r.status = f.getStatus();
        return r;
    }
}
