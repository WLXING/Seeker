package com.example.bean;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Created by ${WLX} on 2019/5/19.
 */

public class User extends LitePalSupport implements Serializable {
    private int id;
    private String username;
    private String password;
    private String personality;//个性签名
    private String headimguri;//头像URI
    private String petname;//昵称

    public int getId() {
        return id;
    }

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeadimguri() {
        return headimguri;
    }

    public void setHeadimguri(String headimguri) {
        this.headimguri = headimguri;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
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


}
