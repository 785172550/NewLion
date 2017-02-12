package com.example.entity;

/**
 * Created by Deep on 16/3/12.
 */
public class SuccessResponse <T> {
    private int code;
    private T data;

    public SuccessResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }
}
