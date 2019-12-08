package com.example.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Created by ${WLX} on 2019.
 */
//背诵、测试进度表
public class Process extends LitePalSupport {
    private int id;
    private String guid;//每个库对应的索引
    private int currentReciteId;//背单词的进度
    private int total;//每个库单词的总数,学习记录那里做统计用
    private int currentTestId;//测试的进度


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getCurrentReciteId() {
        return currentReciteId;
    }

    public void setCurrentReciteId(int currentReciteId) {
        this.currentReciteId = currentReciteId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentTestId() {
        return currentTestId;
    }

    public void setCurrentTestId(int currentTestId) {
        this.currentTestId = currentTestId;
    }
}
