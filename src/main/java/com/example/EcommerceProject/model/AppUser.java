package com.example.EcommerceProject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private long number;
    private String email;
    private String address;
    private String city;
    private String state;
    private int pincode;
    private String password;
    @Transient
    private String confirmPassword;

    private String imageName;
    private String role;
    private boolean enabled;
    private boolean accountNonLocked;
    private Integer failedAttempt;
    private Date lockTime;
    private String resetToken;


    // ✅ Constructors
    public AppUser( String confirmPassword,String resetToken, Date lockTime,Integer failedAttempt,boolean accountNonLocked,int id, String name, long number, String email, String address, String city, String state,
                   int pincode, String password, String imageName, String role, boolean enabled) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.password = password;
        this.imageName = imageName;
        this.role = role;
        this.enabled = enabled;
        this.accountNonLocked=accountNonLocked;
        this.failedAttempt=failedAttempt;
        this.lockTime=lockTime;
        this.resetToken=resetToken;
        this.confirmPassword=confirmPassword;
    }

    public AppUser() {

    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Integer getFailedAttempt() {
        return failedAttempt;
    }

    public void setFailedAttempt(Integer failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    // ✅ Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ✅ Proper getter for Thymeleaf
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
