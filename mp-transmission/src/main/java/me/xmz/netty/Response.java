package me.xmz.netty;

import java.io.Serializable;

public class Response implements Serializable{

    private String message;

    private int code;

    public Response(String message, int code) {
        this.message = message;
        this.code = code;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
