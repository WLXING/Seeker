package com.example.bean;

/**
 * Created by ${WLX} on 2019/8/30.
 */

public class FanYi {
    private String from;
    private String to;
    private String src;//要翻译的文本
    private String dst;//翻译后的文本

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }
}
