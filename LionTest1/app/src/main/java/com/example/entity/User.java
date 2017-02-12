package com.example.entity;

/**
 * Created by Deep on 16/3/12.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String header;
    private String name;
    private int sex;
    private String address;
    private String contact;
    private String service_team;
    private String create_time;
    private String update_time;

    public User(int id, String username, String password,
                String header, String name, int sex, String address, String contact,
                String service_team, String create_time, String update_time) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.header = header;
        this.name = name;
        this.sex = sex;
        this.address = address;
        this.contact = contact;
        this.service_team = service_team;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
