package com.example.entity;

import java.io.Serializable;

/**
 * Created by Deep on 16/3/18.
 */
public class Company implements Serializable {
    private int id;
    private int user_id;
    private String company_name;
    private String address_longitude;
    private String address_latitude;
    private String business_scope;
    private int industry;
    private String show_photo;
    private String introduction;
    private String contact;
    private String create_time;
    private String update_time;
    private String name;  // 公司所在用户用户名

    public Company(int id, int user_id, String company_name, String address_longitude,
                   String address_latitude, String business_scope, int industry,
                   String show_photo, String introduction, String contact, String create_time,
                   String update_time, String name) {
        this.id = id;
        this.user_id = user_id;
        this.company_name = company_name;
        this.address_longitude = address_longitude;
        this.address_latitude = address_latitude;
        this.business_scope = business_scope;
        this.industry = industry;
        this.show_photo = show_photo;
        this.introduction = introduction;
        this.contact = contact;
        this.create_time = create_time;
        this.update_time = update_time;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAddress_longitude() {
        return address_longitude;
    }

    public void setAddress_longitude(String address_longitude) {
        this.address_longitude = address_longitude;
    }

    public String getAddress_latitude() {
        return address_latitude;
    }

    public void setAddress_latitude(String address_latitude) {
        this.address_latitude = address_latitude;
    }

    public String getBusiness_scope() {
        return business_scope;
    }

    public void setBusiness_scope(String business_scope) {
        this.business_scope = business_scope;
    }

    public int getIndustry() {
        return industry;
    }

    public void setIndustry(int industry) {
        this.industry = industry;
    }

    public String getShow_photo() {
        return show_photo;
    }

    public void setShow_photo(String show_photo) {
        this.show_photo = show_photo;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
