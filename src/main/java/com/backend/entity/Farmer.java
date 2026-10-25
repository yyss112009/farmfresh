package com.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "farmers")
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String mobile;

    private String farmName;

    @Column(length = 500)
    private String farmAddress;

    private String state;
    private String city;
    private String pincode;

    @Column(length = 1000)
    private String about;

    @Column(nullable = false)
    private String password; // BCrypt hashed

    // ok (Approved) / pending / blocked -- matches admin dashboard filter values
    @Column(nullable = false)
    private String status = "ok";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
