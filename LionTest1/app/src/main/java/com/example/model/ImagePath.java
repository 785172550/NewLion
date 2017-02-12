package com.example.model;

import com.orm.SugarRecord;

/**
 * Created by Administrator on 2016/3/19.
 */
public class ImagePath extends SugarRecord{

	String path;

	public ImagePath() {
	}

	public ImagePath(String path) {

		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
