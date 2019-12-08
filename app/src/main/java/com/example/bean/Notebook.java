package com.example.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by ${WLX} on 2019/5/12.
 */

public class Notebook extends LitePalSupport {
    private int id;
    private String  guide;
    private String displayname;

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }


}
