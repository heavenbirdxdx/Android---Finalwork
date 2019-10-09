package com.xdx.yyh.bit.finalwork.MiniDouyin.bean;

public class MsgClass {
    private String time;
    private String message;
    private String title;
    private int img;

    public MsgClass() {
    }

    public MsgClass(String title, String message, String time, int img) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.img = img;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
