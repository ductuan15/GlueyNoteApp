package com.example.mynoteapp.data;

public class TagDetail {
    int num;
    String title;
    public TagDetail(String title, int num){
        this.num = num;
        this.title = title;
    }

    public int getNum() {
        return num;
    }

    public String getTitle() {
        return title;
    }
}
