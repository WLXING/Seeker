package com.example.bean;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Created by ${WLX} on 2019/6/23.
 */

public class WordList extends LitePalSupport implements Serializable{
    private int id;
    private String headword;
    private String notebookguid;
    private String phonetic;
    private String quickdefinition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeadword() {
        return headword;
    }

    public void setHeadword(String headword) {
        this.headword = headword;
    }

    public String getNotebookguid() {
        return notebookguid;
    }

    public void setNotebookguid(String notebookguid) {
        this.notebookguid = notebookguid;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getQuickdefinition() {
        return quickdefinition;
    }

    public void setQuickdefinition(String quickdefinition) {
        this.quickdefinition = quickdefinition;
    }
}
