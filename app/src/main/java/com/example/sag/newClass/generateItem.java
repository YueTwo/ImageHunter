package com.example.sag.newClass;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class generateItem {
    private String title;
    private String text;
    private Bitmap image;
    public generateItem(String title, String text, Bitmap image){
        this.title=title;
        this.text=text;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Bitmap getImage() { return image;}
}