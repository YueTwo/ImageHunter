package com.example.sag.newClass;

import android.graphics.Bitmap;

public class Item {
    private String text;
    private String time;
    private Bitmap image;
    public Item(String text,String time,Bitmap image){
        this.image=image;
        this.text=text;
        this.time=time;
    }
    public String getText(){
        return text;
    }
    public String getTime(){
        return time;
    }
    public Bitmap getImage(){
        return image;
    }
}