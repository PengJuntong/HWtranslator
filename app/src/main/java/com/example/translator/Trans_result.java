package com.example.translator;

public class Trans_result {

    private String src;
    private String dst;
    public void setSrc(String src) {
        this.src = src;
    }
    public String getSrc() {
        return src;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }
    public String getDst() {
        if (dst!=null) return dst;
        else return "error";
    }

}