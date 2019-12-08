package com.example.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by ${WLX} on 2019/7/27.
 */

public class AllReciteWord extends LitePalSupport {
    private int id;
    private int wordid;//单词的ID
    private String wordguid;//单词所在的库
    private int year;//已背单词的年月日，用于查找昨天，今天已背单词
    private int month;
    private int day;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWordid() {
        return wordid;
    }

    public void setWordid(int wordid) {
        this.wordid = wordid;
    }

    public String getWordguid() {
        return wordguid;
    }

    public void setWordguid(String wordguid) {
        this.wordguid = wordguid;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
