package com.xdx.yyh.bit.finalwork.MiniDouyin.bean;

public class Person {
    public final long id;
    public String num;
    public String name;
    public String birth;
    private String date;
    private String portrait;

    public Person(long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

}
