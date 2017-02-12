package com.example.entity;

/**
 * Created by Deep on 16/3/12.
 */
public class ErrorResponse extends Object {
    private int type;
    private int code;

    public ErrorResponse(int code, int type) {
        this.code = code;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
