package com.example.sag.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sag.R;

import java.io.IOException;
import java.sql.Connection;

import util.DialogUtil;
import util.LoginRegisterUtil;
import util.myPair;
import util.myPairThree;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    protected Context context = this;
    CheckBox privacy;
    boolean isChecked = false, loginOk = false;
    EditText mailEdt;
    EditText pwdEdt;
    TextView privacy_text;
    Button login, register, back;
    Connection connection;
    String jwt;
    SharedPreferences sharedPreferences;
    int user_id;
    String user_login_name;
    String user_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);//加载布局
        init();//初始化
    }

    public void init() {
        mailEdt = findViewById(R.id.mail_edittext);
        pwdEdt = findViewById(R.id.psw_edittext);
        privacy = findViewById(R.id.privacy);
        privacy_text = findViewById(R.id.privacy_text);
        login = findViewById(R.id.btn_goLogin);
        register = findViewById(R.id.btn_goRegister);
        back = findViewById(R.id.returntoMain);


        login.setOnClickListener(this);
        register.setOnClickListener(this);
        privacy_text.setOnClickListener(this);
        privacy.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goLogin://登录
                String user = mailEdt.getText().toString(); //获取用户名
                String pwd = pwdEdt.getText().toString(); //获取密码

                if (user.equals("") || pwd.equals("")) {
                    Toast.makeText(this, "邮箱或密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!isChecked) {//默认是false
                    Toast.makeText(this, "请勾选用户隐私协议", Toast.LENGTH_SHORT).show();
                } else {
                    LoginRegisterUtil loginRegisterUtil = new LoginRegisterUtil();
                    //后端相关：进行登录检索工作，用loginOK接收返回参数
                    Thread login_by_userid = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            jwt = loginRegisterUtil.login(user, pwd);
                            loginOk = !(jwt == null) && !jwt.equals("login Failed");


                        }
                    });
                    login_by_userid.start();
                    try {
                        login_by_userid.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (!loginOk) {
                        Toast.makeText(context, "登录失败！", Toast.LENGTH_LONG).show();
                        editor.putBoolean("loginOk", false);
                        editor.putInt("user_id", 0);
                        editor.apply();
                    } else {

                        try {
                            Thread login_by_jwt = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        myPairThree user_id_name_nickname = loginRegisterUtil.loginByJWT(jwt);
                                        user_id = (int) user_id_name_nickname.getFirst();
                                        user_login_name = (String) user_id_name_nickname.getSecond();
                                        user_nickname = (String) user_id_name_nickname.getThird();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                            login_by_jwt.start();
                            login_by_jwt.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        editor.putInt("user_id", user_id);
                        editor.putString("user_login_name", user_login_name);
                        editor.putString("user_nickname", user_nickname);
                        editor.putBoolean("loginOk", true);
                        editor.putString("user", user);
                        editor.apply();
                        Intent intent = new Intent(this, MainActivity.class);
                        Toast.makeText(context, "登录成功！", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }

                }
                break;
            case R.id.btn_goRegister://前往注册页面
                Intent rg = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(rg);
                break;
            case R.id.privacy_text://显示隐私条例
                DialogUtil.showNormalDialog(context, true, "《用户隐私协议》", getResources().getString(R.string.privacy_detail), "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                break;
            case R.id.privacy:
                isChecked = !isChecked;
                break;
            case R.id.returntoMain:
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}