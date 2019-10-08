package com.xdx.yyh.bit.finalwork.MiniDouyin.bean;

import android.app.Application;

public class LoginPerson extends Application {
    private Person loginPerson;

    public Person getLoginPerson() {
        return loginPerson;
    }

    public void setLoginPerson(Person loginPerson) {
        this.loginPerson = loginPerson;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Person person = new Person(1);
        person.setNum("1120172169");
        person.setName("杨训迪");
        person.setDate("2019-10-07 16:41:18");
        person.setBirth("1999-11-30");
        person.setPortrait("R.mipmap.yellowchic");
        setLoginPerson(person);
    }
}
