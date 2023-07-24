package com.example.sag.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sag.R;

import util.LoginRegisterUtil;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    protected Context context = this;
    EditText mailEdt, pwdEdt, rpwdEdt;
    Button register, back;
    boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

    }

    public void init() {
        mailEdt = findViewById(R.id.mail_edittext_r);
        pwdEdt = findViewById(R.id.psw_edittext_r);
        rpwdEdt = findViewById(R.id.rpsw_edittext_r);
        register = findViewById(R.id.register_r);
        back = findViewById(R.id.returntoLogin);

        back.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_r:
                String mail = mailEdt.getText().toString(), //获取用户名
                        pwd = pwdEdt.getText().toString(),  //获取密码
                        rpwd = rpwdEdt.getText().toString();  //获取确认密码
                if (mail.equals("") || pwd.equals("") || rpwd.equals("")) {
                    Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!pwd.equals(rpwd)) {
                    Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    //后端添加用户数据并返回详情
                    //后端添加用户数据并返回详情
                    Thread t1 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            //后端添加用户数据并返回详情
                            LoginRegisterUtil registerUtil = new LoginRegisterUtil();
                            success = registerUtil.register(mail, pwd);
                        }
                    });
                    t1.start();
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    //success接受后端传来的消息
                    if (!success) {
                        Toast.makeText(this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "注册成功，快去登录吧！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.returntoLogin:
                Intent back = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(back);
                break;
        }
    }
}