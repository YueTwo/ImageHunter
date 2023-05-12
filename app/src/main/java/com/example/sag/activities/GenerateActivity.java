package com.example.sag.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.sag.R;
import com.example.sag.adapter.generateItemAdapter;
import com.example.sag.newClass.generateItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.DBListener;
import util.DBUtils;
import util.DialogUtil;
import util.FTPUtilsByApache;
import util.ToastUtil;
import util.TransUtil;
import util.myPair;

public class GenerateActivity extends AppCompatActivity implements View.OnClickListener {
    private final List<generateItem> List = new ArrayList<>();
    private Button generate;
    private View take_photo;
    private ImageView upload;
    private CheckBox video, picture;
    private Context context = this;
    private boolean video_checked = false, picture_checked = false;
    private ProgressDialog progressDialog;
    public int count = 1;

    //子布局的title(置信度)，text(生成的描述)
    private String title, text;
    private generateItemAdapter adapter;
    private RecyclerView recyclerView;
    int text_id;
    private Uri photoUri, videoUri;
    //private String photoPath;
    public static final int TAKE_PHOTO = 1;
    public static final int OPEN_FILES = 2;
    private File outputImage;
    private Bitmap bm;
    int file_id;
    int request_id;
    ResultSet resultSet;
    String Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        recyclerView = (RecyclerView) findViewById(R.id.generate_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //初始化List
        initGenerateItems();
        //创建对应list的适配器
        adapter = new generateItemAdapter(List);
        //为可循环视图绑定适配器
        recyclerView.setAdapter(adapter);
        //默认加载子布局的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //其他控件的绑定和注册
        initOthers();
    }

    private void initGenerateItems() {
        //title
        title = "点击上方加号上传图片或视频，我们能为您生成内容描述哦！";
        //time
        text = getCurrentTime();
        generateItem first_item = new generateItem(title, text, bm);
        //初始化List里面只有提示信息
        List.add(first_item);
    }

    public void initOthers() {
        generate = findViewById(R.id.generate_button);
        upload = findViewById(R.id.upload_0);
        video = findViewById(R.id.video_check);
        picture = findViewById(R.id.picture_check);
        take_photo = findViewById(R.id.take_photo);

        generate.setOnClickListener(this);
        upload.setOnClickListener(this);
        video.setOnClickListener(this);
        picture.setOnClickListener(this);
        take_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_0:
                openFileManager();
                break;
            case R.id.video_check:
                if (video_checked) {
                    video_checked = false;
                    video.setChecked(false);
                } else {
                    video_checked = true;
                    if (video_checked && picture_checked) {
                        AlertDialog alertDialog = new DialogUtil().showNormalDialog(context, true, "注意", "检索只支持单选哦！", "我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                video_checked = false;
                                video.setChecked(false);
                            }
                        });
                    }
                }
                break;
            case R.id.picture_check:
                if (picture_checked) {
                    picture_checked = false;
                    picture.setChecked(false);
                } else {
                    picture_checked = true;
                    if (video_checked && picture_checked) {
                        AlertDialog alertDialog = new DialogUtil().showNormalDialog(context, true, "注意", "检索只支持单选哦！", "我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                picture_checked = false;
                                picture.setChecked(false);
                            }
                        });
                    }
                }
                break;
            case R.id.generate_button:
                if (video_checked && !picture_checked) {
                    //视频检索
                    try {
                        generate(videoUri);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (!video_checked && picture_checked) {
                    //图片检索
                    try {
                        generate(photoUri);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (!video_checked && !picture_checked) {
                    ToastUtil.show(this, "请手动选择文件类型，只能选择一个哦！");
                } else {
                    ToastUtil.show(this, "请手动取消多选的文件类型，只能选择一个哦！");
                }
                break;
            case R.id.take_photo:
                openCamera();
                break;
            default:
                break;
        }
    }


    //显示进度条
    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在生成中，请稍候……");
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

    private void generate(Uri targetUri) throws InterruptedException {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        //若为未登录状态，则跳出提示框，用户点击后跳转到登录界面

        //如果要测试登录状态，将defValue的值改为false
        if (!sharedPreferences.getBoolean("loginOk", false)) {
            AlertDialog alertDialog = new DialogUtil().showNormalDialog(context, true, "小贴士", "为保护您的隐私数据，请先登录再上传哦！", "去登陆>", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(GenerateActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            //若为登录状态，通过Uri上传文件并返回描述
            Text = "太阳很大，操场上有七个人在打篮球。";
            //Text接受从后端返回的描述
            //这里进行后端的上传文件并返回相关数据
//-------------------------------------------------------------------------------------------------------------------------------------
            //Text接受从后端返回的描述
            //这里进行后端的上传文件并返回相关数据


            //(1)确定tag user_id
            int user_id = sharedPreferences.getInt("user_id", 0);
            String user_login_name = sharedPreferences.getString("user_login_name", "");
            int tag = picture_checked ? 1 : 2;
            String path = TransUtil.uri2File(context, targetUri);
            File _file = new File(path);
            String file_name = _file.getName();
            List<myPair<String, Integer>> path_tag_list = new ArrayList<>();
            path_tag_list.add(new myPair(path, tag));

            Thread upload_t = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress();
                        }
                    });
                    FTPUtilsByApache ftpUtilsByApache = new FTPUtilsByApache();
                    ftpUtilsByApache.getFTPClient();
                    DBUtils dbUtils = new DBUtils();
                    int file_id;
                    //先判断是否上传过此视频
                    try {
                        file_id = dbUtils.search_file_by_name(user_id, file_name, tag);
                    } catch (ClassNotFoundException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                    if (file_id == 0) {//如果没有上传过，就上传到ftp，同时同步到数据库
                        //1.上传文件到ftp
                        try {
                            ftpUtilsByApache.uploadFile(user_login_name, path_tag_list);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //2.上传到数据库
                        try {
                            file_id = dbUtils.upload_db(user_login_name, user_id, file_name, path, tag);
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                        //3.上传请求到数据库
                        try {
                            request_id = dbUtils.generate_request_db(user_id, file_id, tag);
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d("lyt数据库", "成功上传请求");
                        //4.开启监听
                        DBListener dbListener = new DBListener();
                        try {
                            dbListener.text_generation_listener(file_id,tag);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d("lyt数据库监听", "已经结束监听");
                        //5.查询结果
                        try {
                            Text = dbUtils.search_generate_text(tag, file_id);
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else {//如果已经上传过了，直接检索结果
                        try {
                            Text = dbUtils.search_generate_text(tag, file_id);
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            String Time = getCurrentTime();
                            count++;
                            generateItem item = new generateItem(Text, Time, bm);
                            adapter.addItem(count, item);
                            recyclerView.smoothScrollToPosition(count - 1);
                        }
                    });
                }
            });
            upload_t.start();
            //可能用到的参数及类型有：
            //photoUri(targetUri)，videoUri(targetUri),video_checked(boolean),picture_checked(boolean)

            //Time我打算用来显示用户名，用户名可以存储在preferReference直接调用，也可以由后端返回

            //----------------------------------------------------------------------------------------------------------
            //可能用到的参数及类型有：
            //photoUri(targetUri)，videoUri(targetUri),video_checked(boolean),picture_checked(boolean)

            //Time我打算用来显示用户名，用户名可以存储在preferReference直接调用，也可以由后端返回

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_PHOTO) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera();
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setType("video/*;image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, OPEN_FILES);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
        } else {
            //创建file对象，用于存储拍照获得的图片
            long currentTimeMillis = System.currentTimeMillis();
            String flag = Long.toString(currentTimeMillis);
            //这里我用outputImage将创建的文件真实路径保存下来了
            outputImage = new File(getExternalCacheDir(),
                    flag + ".png");

            //文件存在则删除在保存
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("Path", outputImage.toString());
            //API>24将file转换为封装后的Uri，否则直接获取文件Uri
            if (Build.VERSION.SDK_INT >= 24) {
                photoUri = FileProvider.getUriForFile(GenerateActivity.this,
                        "com.example.sag.file_provider", outputImage);
            } else {
                photoUri = Uri.fromFile(outputImage);
            }

            //打开照相机:图片将保存到APP的外部存储空间的缓存目录
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                        //封面设置
                        upload.setImageBitmap(bm);
                        //记录生成对象为图片
                        picture_checked = true;
                        video_checked = false;
                        picture.setChecked(true);
                        video.setChecked(false);
                        //Log.e("ZYZY","Uri "+photoUri.toString());
                        //photoPath=outputImage.toString();
                        //Log.e("ZYZY","Path "+outputImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case OPEN_FILES:
                try {
                    Uri uri = data.getData();
/*
                    String str="content://com.android.providers.media.documents/document/video%3A39";
                    Uri uri=Uri.parse(str);
*/
                    //Log.e("ZY上传的文件","Uri "+uri.toString());
                    //upload.setImageURI(uri);
                    //String tmpUri=uri.toString();
                    //uri=Uri.parse(tmpUri);
                    bm = getImageUriIfVideo(uri);
                    String uriPath;
                    TransUtil util = new TransUtil();
                    uriPath = util.uri2File(context, uri);
                    //打印文件位置信息
                    Log.e("ZY", uri.toString());
                    Log.e("ZY", "path:" + uriPath);
                    upload.setImageBitmap(bm);
                    Log.d("ZY", "封面设置成功！");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ZY", "封面设置异常！");
                }
                break;
            default:
                break;
        }
    }

    private Bitmap getImageUriIfVideo(Uri uri) throws IOException {
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(uri);
        //如果是视频通过MediaMetadataRetriever获取视频第一帧的bitmap,同时将文件uri保存到videoUri
        if (mimeType.startsWith("video/")) {
            videoUri = uri;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, uri);
            Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            retriever.release();
            if (bitmap != null) {
                Log.d("ZY", "成功获取封面！");
            }
            video.setChecked(true);
            picture.setChecked(false);
            video_checked = true;
            picture_checked = false;
            return bitmap;
        } else {
            //如果是图片，将文件uri保存到photoUri，同时通过uri获取输入流，再将输入流解析为bitmap
            photoUri = uri;
            InputStream input = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();
            picture.setChecked(true);
            video.setChecked(false);
            picture_checked = true;
            video_checked = false;
            return bitmap;
        }
    }


/*    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }*/


    private String getCurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");//   HH:mm:ss
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}