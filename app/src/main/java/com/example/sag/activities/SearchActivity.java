package com.example.sag.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sag.R;
import com.example.sag.adapter.itemAdapter;
import com.example.sag.custom.search_edittext;
import com.example.sag.newClass.Item;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import util.DBListener;
import util.DBUtils;
import util.DialogUtil;
import util.ToastUtil;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Item> itemList = new ArrayList<>();
    private boolean video_checked = false, picture_checked = false, audio_checked=false,local_checked = false, online_checked = false;
    private CheckBox video, picture, local, online, audio;
    private Button start, end, search;
    private ImageView help_1, help_2, help_3;
    private itemAdapter adapter;
    private RecyclerView recyclerView;
    private boolean flag1, flag2;
    private String startTime, endTime;
    private String inputText;
    private search_edittext searchEdittext;
    private int count = 1;
    private Uri videoUri[] = new Uri[]{};
    private Uri photoUri[] = new Uri[]{};
    private Context context = this;
    private  ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;

    //内部类接收检索信息
    private static class pair {
        String address;
        float conf;

        public pair(String address, float conf) {
            this.address = address;
            this.conf = conf;
        }

        public String getAddress() {
            return address;
        }

        public float getConf() {
            return conf;
        }

        Comparator<pair> comparator = new Comparator<pair>() {
            @Override
            public int compare(pair p1, pair p2) {
                return Float.compare(p2.conf, p1.conf);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //滚动界面绑定
        recyclerView = (RecyclerView) findViewById(R.id.search_recycle);
        //线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //线性布局绑定
        recyclerView.setLayoutManager(layoutManager);
        //
        layoutManager.scrollToPosition(0);
        //初始化itemList
        initItems();
        //获得itemList对应的适配器
        adapter = new itemAdapter(itemList);
        //给滚动布局设置适配器
        recyclerView.setAdapter(adapter);
        //设置默认子布局变化动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //绑定其他控件
        initOthers();
    }
    public static void hideKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
    }

    private void initItems() {
        //title
        String str_title = "上方输入对文件内容的文字描述，勾选文件类型与检索模式后点击搜索，我们可以为您找到对应的文件哦！";
        //time
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str_time = formatter.format(curDate);
        Item item = new Item(str_title, str_time, null);
        itemList.add(item);
    }

    private void initOthers() {
        searchEdittext = findViewById(R.id.search_edittext);
        video = findViewById(R.id.video);
        picture = findViewById(R.id.picture);
        audio=findViewById(R.id.audio);
        local = findViewById(R.id.local);
        online = findViewById(R.id.online);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        search = findViewById(R.id.search);
        help_1 = findViewById(R.id.help_1);
        help_2 = findViewById(R.id.help_2);
        help_3 = findViewById(R.id.help_3);

        video.setOnClickListener(this);
        picture.setOnClickListener(this);
        audio.setOnClickListener(this);
        local.setOnClickListener(this);
        online.setOnClickListener(this);
        start.setOnClickListener(this);
        end.setOnClickListener(this);
        search.setOnClickListener(this);
        help_1.setOnClickListener(this);
        help_2.setOnClickListener(this);
        help_3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video:
                hideKeyboard(this);
                video.setChecked(true);
                picture.setChecked(false);
                audio.setChecked(false);
                video_checked = true;
                picture_checked = false;
                audio_checked=false;
                break;
            case R.id.picture:
                hideKeyboard(this);
                picture.setChecked(true);
                video.setChecked(false);
                audio.setChecked(false);
                picture_checked = true;
                video_checked = false;
                audio_checked=false;
                break;
            case R.id.audio:
                hideKeyboard(this);
                audio.setChecked(true);
                picture.setChecked(false);
                video.setChecked(false);
                picture_checked = false;
                video_checked = false;
                audio_checked=true;
                break;
            case R.id.local:
                hideKeyboard(this);
                local.setChecked(true);
                local_checked = true;
                online_checked = false;
                online.setChecked(false);
                break;
            case R.id.online:
                hideKeyboard(this);
                online.setChecked(true);
                online_checked = true;
                local_checked = false;
                local.setChecked(false);
                break;
            case R.id.start:
                flag1 = true;
                flag2 = false;
                show();
                break;
            case R.id.end:
                flag1 = false;
                flag2 = true;
                show();
                break;
            case R.id.search:
                hideKeyboard(this);
                sharedPreferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
                boolean login=sharedPreferences.getBoolean("loginOk",false);
                if(login==false) {
                    AlertDialog alertDialog = new DialogUtil().showNormalDialog(context, true, "小贴士", "您还没有登录，我们无权访问你的云空间哦！", "去登录>", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    Log.e("ZY", "点击了搜索按钮");
                    if (!(video_checked||picture_checked||audio_checked)) {
                        Toast.makeText(context, "请先选择文件类型！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (!(local_checked||online_checked)) {
                        Toast.makeText(context, "请先选择检索模式", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    try {
                        search();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            case R.id.help_1:
                AlertDialog alertDialog1 = new DialogUtil().showNormalDialog(context, true, "小贴士", "您可以在此处选择您要检索的文件类型，我们可以为您更快更准地找到您想要的文件哦！", "去选择>", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                break;
            case R.id.help_2:
                AlertDialog alertDialog2 = new DialogUtil().showNormalDialog(context, true, "小贴士", "无网时您可以选择本地计算模式，但选择云计算模式检索会更快更准哦！" +
                        "、", "去选择>", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                break;
            case R.id.help_3:
                AlertDialog alertDialog3 = new DialogUtil().showNormalDialog(context, true, "小贴士", "若您选择本地检索模式并手动提供本地文件所属的大致时间范围，我们可以为您更快更准地找到您想要的文件哦!", "我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                break;
            default:
                break;
        }
    }

    //显示进度条
    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在检索中，请稍候……");
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

/*    private void search_audio() {
        List<Item> data = new ArrayList<>();
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.play, null);
        Bitmap tb = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tb);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        if(tb==null){
            Log.e("ZYYY","没有获取到音频封面");
        }
        Item item=new Item("86.584112%","/data/user/0/com.example.sag/files/AUD_20230411183495.mp3",tb);
        data.add(item);
        item=new Item("50.481189%","/data/user/0/com.example.sag/files/AUD_20230411182451.mp3",tb);
        data.add(item);
        item=new Item("30.470029%","/data/user/0/com.example.sag/files/AUD_2023041185816.mp3",tb);
        data.add(item);
        item=new Item("25.682546%","/data/user/0/com.example.sag/files/AUD_20230411181347.mp3",tb);
        data.add(item);
        item=new Item("12.152655%","/data/user/0/com.example.sag/files/AUD_20230411181113.mp3",tb);
        data.add(item);
        item=new Item("11.580022%","/data/user/0/com.example.sag/files/AUD_20230411181246.mp3",tb);
        data.add(item);
        // 在新线程上等待3秒钟
        //showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //在UI线程上显示进度条
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress();
                    }
                });
                //延时3s
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 在UI线程上隐藏进度条
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        adapter.updateItem(data);
                    }
                });
            }
        }).start();
    }*/

    private void search() throws InterruptedException {
        String Text = String.valueOf(searchEdittext.getText());
        if(Text.equals("")){
            Log.e("ZY","inputText为空");
            Toast.makeText(this,"您还没有输入文字描述哦！",Toast.LENGTH_LONG).show();
            return;
        }/*else{
            //防止相同描述文字重复点击搜索按钮，两次加载相同结果
            if(Text.equals(inputText)){
                Toast.makeText(this,"请换一个描述哦！",Toast.LENGTH_LONG).show();
                return;
            }
        }*/
        inputText = Text;
        //后端操作部分，inputText是用户输入的描述性文本信息
        //Log.e("ZY",inputText);
        //创建一个自定义类型pair的数组results
        //---------------------------------------------
        List<pair> results = new ArrayList<pair>();
        //1.向数据库发送检索请求
        Thread search_t = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress();
                    }
                });

                DBUtils dbUtils = new DBUtils();
                int user_id = sharedPreferences.getInt("user_id", 0);
                int tag = picture_checked ? 1 : 2;
                int text_id = 0;
                try {
                    text_id = dbUtils.search_text_db(user_id, inputText, tag);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                //2.监听模型端检索情况
                DBListener dbListener = new DBListener();
                try {
                    dbListener.retrieve_end_listener(text_id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //3.接受检索结果
                ResultSet resultSet = null;
                try {
                    resultSet = dbUtils.search_result(tag, text_id);
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }

                while (true) {
                    try {
                        if (!resultSet.next()) break;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    pair tmp_pair = null;
                    try {
                        tmp_pair = new pair(resultSet.getString("path"), resultSet.getFloat("confidence"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    results.add(tmp_pair);
                }
                try {
                    dbUtils.connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                Log.e("ZY","已将检索文本提交给数据库！");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Log.e("ZY","已隐藏进度条！");
                        // 定义一个比较器，对接收到的数组进行排序
                        Comparator<pair> comparator = Comparator.comparing(pair::getConf).reversed();
                        results.sort(comparator);
                        //对排序后的结果进行处理
                        List<Item> data = new ArrayList<>();
                        int num=0;
                        for (pair p : results) {
                            //Log.e("ZY","处理数据中……");
                            if(++num>5){
                                break;
                            }
                            StringBuilder sb = new StringBuilder("置信度：");
                            sb.append(p.conf * 100);
                            sb.append("%");
                            String confidence = sb.toString();

                            Log.e("ZY", confidence);

                            try {
                                Log.e("ZY", "进入Uri处理逻辑");
                                Bitmap targetBitmap;

                                if (whatPathType(p.address)==1) {
                                    //获取bitmap对象
                                    targetBitmap = BitmapFactory.decodeFile(p.address);
                                } else if(whatPathType(p.address)==0) {
                                    //获取bitmap对象
                                    targetBitmap = getVideoThumbnail(p.address);
                                    //Log.d("ZY","成功获取视频封面！");
                                }else{
                                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.play, null);
                                    targetBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                    Canvas canvas = new Canvas( targetBitmap);
                                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                    drawable.draw(canvas);
                                    if(targetBitmap==null){
                                        Log.e("SearchActivity","没有获取到音频封面");
                                    }
                                }
                                Item item = new Item(confidence, p.address, targetBitmap);
                                data.add(item);
                                //Log.e("ZY","URI: " + str + ", Conf: " + confidence);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("ZY", "封面设置异常！");
                            }
                        }
                        adapter.updateItem(data);
                    }
                });
            }
        });
        search_t.start();
        //search_t.join();

/*        // 定义一个比较器，对接收到的数组进行排序
        Comparator<pair> comparator = Comparator.comparing(pair::getConf).reversed();
        results.sort(comparator);
        //对排序后的结果进行处理
        List<Item> data = new ArrayList<>();
        for (pair p : results) {
            //Log.e("ZY","处理数据中……");
            count++;
            StringBuilder sb = new StringBuilder("置信度：");
            sb.append(p.conf * 100);
            sb.append("%");
            String confidence = sb.toString();

            Log.e("ZY", confidence);

            try {
                Log.e("ZY", "进入Uri处理逻辑");
                Bitmap targetBitmap;
                //判断返回的文件是图片还是视频路径，分开处理获取封面
                if (pathIsImage(p.address)) {
                    //获取bitmap对象
                    targetBitmap = BitmapFactory.decodeFile(p.address);
                } else {
                    //获取bitmap对象
                    targetBitmap = getVideoThumbnail(p.address);
                }
                //Log.e("ZY","URI: " + str + ", Conf: " + confidence);
                //Log.d("ZY","成功获取视频封面！");
                Item item = new Item(confidence, p.address, targetBitmap);
                data.add(item);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ZY", "封面设置异常！");
            }
        }
        int len= itemList.size();
        Log.e("ZY", "itemList的长度为："+String.valueOf(len));
        adapter.updateItem(data);
        System.out.println("查询结束lyt");*/
    }

    private int whatPathType(String filePath) {
        File file = new File(filePath);
        String extension = "";

        int i = filePath.lastIndexOf('.');
        if (i >= 0) {
            extension = filePath.substring(i + 1);
        }

        if (extension.equalsIgnoreCase("jpg") ||
                extension.equalsIgnoreCase("jpeg") ||
                extension.equalsIgnoreCase("png")) {
            return 1;
        } else if (extension.equalsIgnoreCase("mp4") ||
                extension.equalsIgnoreCase("avi") ||
                extension.equalsIgnoreCase("mov")) {
            return 0;
        } else {
            return 2;
        }
    }

    private static Bitmap getVideoThumbnail(String videoPath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            //
            File file = new File(videoPath);
            if (file.exists() && file.canRead()) {
                // 文件存在并且可以读取
                Log.e("ZY","文件存在且可读");
            } else {
                Log.e("ZY","文件不存在或不可读");
                // 文件不存在或无法读取
            }
            //
            Thread.sleep(100);
            // 获取第一帧的图像
            Bitmap bitmap = retriever.getFrameAtTime(12);

            return bitmap;
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            retriever.release();
        }
        return null;
    }


/*    private Bitmap getBitmap(Uri uri) throws IOException {
            //Log.e("ZY","Uri："+uri);
            ContentResolver contentResolver = getContentResolver();
            String mimeType = contentResolver.getType(uri);
            //如果是视频通过MediaMetadataRetriever获取视频第一帧的bitmap,同时将文件uri保存到videoUri
            if(mimeType.startsWith("video/")){
                //videoUri[]存储相应视频的地址
                //videoUri[count]=uri;
                Log.e("ZY","返回了视频文件");
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(context, uri);
                Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                retriever.release();
                if(bitmap!=null){
                    Log.d("ZY","成功获取视频封面！");
                }
                return bitmap;
            }else{
                //如果是图片，将文件uri保存到photoUri，同时通过uri获取输入流，再将输入流解析为bitmap
                //photoUri[]存储相应图片的地址
                //photoUri[count]=uri;
                InputStream input = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                if(bitmap!=null){
                    Log.d("ZY","成功获取图片封面！");
                }
                return bitmap;
            }
    }*/

    private static pair[] getResults(String inputText) {
        //后端逻辑在这里实现


        pair[] results=new pair[3];//这行代码只是用来创建一个返回值，后端可以生成返回数组，这行代码自行去掉即可
        return results;
    }

    private void show(){
        Calendar c = Calendar.getInstance();
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(SearchActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        StringBuilder s=new StringBuilder();
                        s.append(year);
                        s.append('-');
                        s.append(monthOfYear+1);
                        s.append('-');
                        s.append(dayOfMonth);
                        String S=s.toString();
                        //显示时间
                        setText(flag1,flag2,S);
                    }
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                .get(Calendar.DAY_OF_MONTH)).show();
    }

    //用于选择的按钮改为用于显示日期，同时限制用户的日期选择：不得晚于当前时间，起始时间点不得晚于结束时间点等功能
    private void setText(boolean flag1,boolean flag2,String s){
        String now=getCurrentTime();
        Log.e("ZY",now);
        if(flag1&&!flag2){
            //不允许选择晚于系统日期的时间点
            if(now.compareTo(s)<0){
                Log.e("ZY",s);
                Toast.makeText(this, "请选择过去的时间点哦！", Toast.LENGTH_SHORT).show();
                return;
            }
            startTime=s;
            start.setText(s);
        }else if(flag2&&!flag1){
            if(startTime==null){
                Log.e("ZY",s);
                Toast.makeText(this, "请先选择起始时间点哦！", Toast.LENGTH_SHORT).show();
                return;
            }else{
                if(now.compareTo(s)>0&&startTime.compareTo(s)<0){
                    endTime=s;
                    end.setText(s);
                }else{
                    if(now.compareTo(s)<0){
                        Log.e("ZY",s);
                        Toast.makeText(this, "请选择过去的时间点哦！", Toast.LENGTH_SHORT).show();
                    }else if(startTime.compareTo(s)>0){
                        Log.e("ZY",s);
                        Toast.makeText(this, "结束时间点不可早于起始时间点哦！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return;
        }
    }

    private String getCurrentTime(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new  SimpleDateFormat("y-M-d");//   HH:mm:ss
        Date curDate=new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
