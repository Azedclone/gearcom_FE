package com.gearcom.api.model;

public class RegistrationBody {

    private String username;
    private String password;
    private String name;
    private String phone;
    private String address;

    public RegistrationBody(String username, String password, String name, String phone, String address) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
