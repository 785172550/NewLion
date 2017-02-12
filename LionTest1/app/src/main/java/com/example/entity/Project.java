package com.example.entity;

import java.io.Serializable;

/**
 * Created by Deep on 16/3/16.
 */
public class Project implements Serializable {
    private int id;
    private String title;
    private String time;
    private int launcher_id;
    private int favorite;
    private String cover_image;
    private String details_page;
    private String create_time;
    private String name;

    public Project(int id, String title, String time, int launcher_id,
                   int favorite, String details_page, String cover_image,
                   String create_time, String name) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.launcher_id = launcher_id;
        this.favorite = favorite;
        this.details_page = details_page;
        this.cover_image = cover_image;
        this.create_time = create_time;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLauncher_id() {
        return launcher_id;
    }

    public void setLauncher_id(int launcher_id) {
        this.launcher_id = launcher_id;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getDetails_page() {
        return details_page;
    }

    public void setDetails_page(String details_page) {
        this.details_page = details_page;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
