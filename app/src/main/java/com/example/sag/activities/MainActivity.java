package com.example.sag.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sag.R;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import util.DBListener;
import util.DBUtils;
import util.DialogUtil;
import util.FTPUtilsByApache;
import util.TransUtil;
import util.myPair;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView upload, search, generate;
    private ImageView user;
    private Context context = this;
    private static final int REQUEST_CODE_SELECT_FILES = 1;
    private  ProgressDialog progressDialog;
    private Handler handler = new Handler(Looper.getMainLooper());
    List<Uri> uris = new ArrayList<>();
    String user_login_name;
    SharedPreferences sharedPreferences;
    String file_name;
    int user_id;
    File _file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        user = findViewById(R.id.user);
        search = findViewById(R.id.search);
        generate = findViewById(R.id.generate);
        upload = findViewById(R.id.upload);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        search.setOnClickListener(this);
        generate.setOnClickListener(this);
        upload.setOnClickListener(this);
        user.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user:
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                Boolean login = sharedPreferences.getBoolean("loginOk", false);
                if (!login) {
                    Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent1);
                } else {
                    Intent intent1 = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.search:
                Intent intent2 = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent2);
                break;
            case R.id.generate:
                Intent intent3 = new Intent(MainActivity.this, GenerateActivity.class);
                startActivity(intent3);
                break;
            case R.id.upload:
                selectMedia();
                break;
            default:
                break;
        }
    }

    private void selectMedia() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*","audio/*"});
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILES);
    }

    //显示进度条
    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("映像上传中，请稍候……");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //隐藏进度条
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_FILES && resultCode == RESULT_OK) {
            if (data != null) {
                uris.clear();
                //用户有选择则取出用户选择的多文件的对象
                ClipData clipData = data.getClipData();
                //用num记录uri列表的长度num+1
                //Log.e("ZY","num:"+num);
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        //一次获取uri并将获取的uri插入uris
                        Uri uri = clipData.getItemAt(i).getUri();
                        uris.add(uri);
                    }
                } else if (data.getData() != null) {
                    uris.add(data.getData());
                } else {
                    return;
                }
                //判断用户是否登录
                SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean login = sp.getBoolean("loginOk", false);
                if (login == false) {
                    AlertDialog alertDialog = new DialogUtil().showNormalDialog(context, true, "小贴士", "为了保护您的隐私，请先登录再上传文件哦！", "去登录>", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    //处理uris列表
                    Thread upload_t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showProgress();
                                }
                            });
                            //音频转文字放入后台任务中


                            //视频和图片上传ftp+上传数据库
                            user_login_name = sharedPreferences.getString("user_login_name", "用户命名错误");
                            user_id = sharedPreferences.getInt("user_id", 0);
                            FTPUtilsByApache ftpUtilsByApache = new FTPUtilsByApache();
                            ftpUtilsByApache.getFTPClient();
                            DBUtils dbUtils = new DBUtils();
                            List<myPair<String, Integer>> path_tag_list = new ArrayList<>();
                            for (Uri tmp_uri : uris) {
                                myPair<String, Integer> path_tag = new myPair<String, Integer>();
                                String tmp_path = TransUtil.uri2File(context, tmp_uri);
                                path_tag.setFirst(tmp_path);
                                //int tag = isImage(tmp_uri) ? 1 : 2;
                                int tag=whatType(tmp_uri);
                                path_tag.setSecond(tag);
                                path_tag_list.add(path_tag);
                            }
                            try {
                                ftpUtilsByApache.uploadFile(user_login_name, path_tag_list);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            for (Uri tmp_uri : uris) {
                                String tmp_path = TransUtil.uri2File(context, tmp_uri);
                                _file = new File(tmp_path);
                                file_name = _file.getName();
                                //int tag = isImage(tmp_uri) ? 1 : 2;
                                int tag=whatType(tmp_uri);
                                try {
                                    dbUtils.upload_db(user_login_name, user_id, file_name, tmp_path, tag);
                                } catch (ClassNotFoundException | SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgress();
                                }
                            });
                        }
                    });
                    upload_t.start();
                }
            }
        }

    }

/*    //用isImage判断这条uri是否为图片，true为图片，false为视频
    private boolean isImage(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(uri);
        if (mimeType.startsWith("video/")) {
            Log.e("ZY", String.valueOf(uri) + ":video");
            return false;
        }
        Log.e("ZY", String.valueOf(uri) + ":image");
        return true;
    }*/

    private int whatType(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(uri);
        if (mimeType.startsWith("video/")) {
            Log.e("mainActivity", String.valueOf(uri) + ":video");
            return 0;
        }else if (mimeType.startsWith("image/")) {
            Log.e("mainActivity", String.valueOf(uri) + ":image");
            return 1;
        }else {
            Log.e("mainActivity", String.valueOf(uri) + ":audio");
            return 2;
        }
    }

    //用uriToString将uri转化为path
    private String uriToString(Uri uri) {
        TransUtil util = new TransUtil();
        String uriPath = util.uri2File(this, uri);
        return uriPath;
    }
}
