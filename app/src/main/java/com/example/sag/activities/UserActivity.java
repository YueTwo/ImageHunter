package com.example.sag.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sag.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{
    private View daily;
    private TextView day,name;
    private RelativeLayout item1,item2,item3,item4;
    private Context context=this;
    private SharedPreferences sp;
    private String todayDate;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
        //设置用户名
        sp=getSharedPreferences("MyPrefs",getApplicationContext().MODE_PRIVATE);
        editor=sp.edit();
        String user_name=sp.getString("user","123456@njtech.com");
        name.setText(user_name);
        todayDate=getCurrentTime();
    }

    private void init(){
        daily=findViewById(R.id.signup);
        day=findViewById(R.id.clock_day);
        name=findViewById(R.id.name);
        item1=findViewById(R.id.item1);
        item2=findViewById(R.id.item2);
        item3=findViewById(R.id.item3);
        item4=findViewById(R.id.item4);

        daily.setOnClickListener(this);
        day.setOnClickListener(this);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signup:
                if(sp.getString("sign_in_time",null)!=todayDate){
                    editor.putString("sign_in_time",todayDate);
                    int day_num=sp.getInt("clock_day",0)+1;
                    Log.e("ZY", "clock_num: "+String.valueOf(day_num));
                    String strNum=String.valueOf(day_num);
                    day.setText(strNum);
                    Log.e("ZY","设置成功！");
                    editor.putInt("clock_day",day_num);
                    editor.apply();
                }else {
                    Toast.makeText(this, "您今天已经签到过了哦！", Toast.LENGTH_LONG).show();
                }
                break;
/*            case R.id.item1:
                //Toast.makeText(this,"你点击了云空间！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.item2:
                //Toast.makeText(this,"你点击了隐私设置！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.item3:
                //Toast.makeText(this,"你点击了关于我们！",Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.item4:
                editor.putBoolean("loginOk",false);
                editor.apply();
                Toast.makeText(this,"用户已退出！",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(UserActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
    private String getCurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");//   HH:mm:ss
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}