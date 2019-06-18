package com.example.homework;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MusicMainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView MusicNameView;
    private TextView SingerNameView;
    private MyImageView SingerImageView;

    private String down_url;
    private String name;
    private String Singer;
    private String artist_img;

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_main);

        Intent intent = getIntent();
        down_url = intent.getStringExtra("down_url");
        artist_img = intent.getStringExtra("artist_img");
        Singer = intent.getStringExtra("Singer");
        name = intent.getStringExtra("name");

        MusicNameView = findViewById(R.id.MusicName);
        SingerNameView = findViewById(R.id.SingerName);
        SingerImageView = findViewById(R.id.singerImage);

        // 设置内容
        MusicNameView.setText(name);
        SingerNameView.setText(Singer);

        Button btn_download = findViewById(R.id.download_btn);

        btn_download.setOnClickListener(this);

        do_some();

        Intent intent1 = new Intent(this, DownloadService.class);
        startService(intent1); // 启动
        bindService(intent1, connection, BIND_AUTO_CREATE);

        // 申请权限
        if (ContextCompat.checkSelfPermission(MusicMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicMainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_btn:
                set_down_listen();

                break;
            default:
                break;
        }
    }

    private void set_down_listen() {
        // downloadFile();
        String url = down_url;
        downloadBinder.startDownload(url);
    }

    /**
     * 创建时，执行此文件，就不写在onCreate里面了。
     */
    private void do_some() {
        if (!artist_img.equals("")) {
            Log.i("mp3", "do_some: " + down_url);
            SingerImageView.setImageURL(artist_img);
        } else {
            Toast.makeText(MusicMainActivity.this, "由于此首歌曲没有图片，所以无法显示", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
