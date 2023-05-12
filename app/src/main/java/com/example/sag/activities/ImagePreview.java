package com.example.sag.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sag.R;

import java.io.File;

public class ImagePreview extends AppCompatActivity {

    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("FILE_PATH");
        image=findViewById(R.id.imagePreview);
        initImage(filePath);
    }

    private void initImage(String filePath){
        File file=new File(filePath);
        if (file.exists()) {
            // 检查读写权限
            if (file.canRead() && file.canWrite()) {
                Bitmap bp = BitmapFactory.decodeFile(filePath);
                image.setImageBitmap(bp);
            }
        }else{
            Toast.makeText(this,"本地不存在该图片，无法查看哦！",Toast.LENGTH_LONG).show();
        }
    }
}