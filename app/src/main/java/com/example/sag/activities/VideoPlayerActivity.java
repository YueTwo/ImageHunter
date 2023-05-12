package com.example.sag.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import com.example.sag.R;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener{
        private VideoView videoView;
        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video_player);
            videoView = (VideoView) findViewById(R.id.videoPlayer);
            Button play = (Button) findViewById(R.id.play);
            Button pause = (Button) findViewById(R.id.pause);
            Button replay = (Button) findViewById(R.id.replay);
            play.setOnClickListener(this);
            pause.setOnClickListener(this);
            replay.setOnClickListener(this);
            Intent intent = getIntent();
            // 从 Intent 中获取文件路径
            String filePath = intent.getStringExtra("FILE_PATH");
            initVideoPath(filePath); // 初始化VideoView
        }
        private void initVideoPath(String filePath) {
            //File file = new File(pathStr);
            videoView.setVideoPath(filePath); // 指定视频文件的路径
            videoView.start();
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play:
                    if (!videoView.isPlaying()) {
                        videoView.start(); // 播放
                    }
                    break;
                case R.id.pause:
                    if (videoView.isPlaying()) {
                        videoView.pause(); // 暂停播放
                    }
                    break;
                case R.id.replay:
                    //Toast.makeText(this,"你点击了重放按钮！",Toast.LENGTH_LONG).show();
                    if(!videoView.isPlaying()){
                        videoView.start();
                    }
                    videoView.resume(); // 重新播放
                    break;
            }
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (videoView!=null) {
                videoView.suspend();
            }
        }
    }
