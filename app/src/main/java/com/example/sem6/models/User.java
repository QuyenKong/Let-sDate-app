package com.example.sem6.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String fullName;
    private String password;
    private String email;
    private String userRole;
    private String address;
    private Date birthDate;
    private String gender;
    private String phone;
    private String bio;
    private String avatar;
    private double rank;
    private double pricePerHour;
    private double balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public User(Long id, String fullName, String password, String email, String userRole, String address, Date birthDate, String gender, String phone, String bio, String avatar, double rank, double pricePerHour, double balance) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.email = email;
        this.userRole = userRole;
        this.address = address;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
        this.bio = bio;
        this.avatar = avatar;
        this.rank = rank;
        this.pricePerHour = pricePerHour;
        this.balance = balance;
    }
}
