package com.example.model;

import com.orm.SugarRecord;

/**
 * Created by Deep on 16/3/12.
 */
public class User extends SugarRecord {
    int userId;
    String username;
    String password;
    String header;
    String name;
    int sex;
    String address;
    private String contact;
    String service_team;
    String create_time;
    String update_time;

    public User() {

    }

    public User(int userId, String username, String password,
                String header, String name, int sex, String address, String contact,
                String service_team, String create_time, String update_time) {
        this.userId = userId;
        this.username = username;
        this.header = header;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.address = address;
        this.contact = contact;
        this.service_team = service_team;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getService_team() {
        return service_team;
    }

    public void setService_team(String service_team) {
        this.service_team = service_team;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
