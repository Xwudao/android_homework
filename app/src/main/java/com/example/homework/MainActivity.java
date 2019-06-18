package com.example.homework;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView result_view;
    private List<Music> musicList;
    private EditText kw_edit;
    private LinearLayoutManager layoutManager;
    private MainActivity that = this;

    private ProgressDialog progressDialog;

    private ListView music_list_view;
    private MusicAdapter musicAdapter;

    private String kw; // 搜索关键词

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_search = findViewById(R.id.search_button);
        kw_edit = findViewById(R.id.search_src_text);

        btn_search.setOnClickListener(this);
        // result_view = findViewById(R.id.result_text);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                kw = kw_edit.getText().toString();
                // 判断是否为空
                if (kw.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("温馨提示：");
                    dialog.setMessage("请输入关键词");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                    return;
                }

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("搜索中...");
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                do_http();
                break;
            default:
                break;
        }
    }

    private void do_http() {
        new Thread(new Runnable() {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url("http://api.misiai.com/android_music/?kw=" + kw)
                    .build();
            Response response;
            String responseData;

            @Override
            public void run() {
                try {
                    response = client.newCall(request).execute();
                    assert response.body() != null;
                    responseData = response.body().string();
                    parseJson(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("执行失败");
                }
            }
        }).start();
    }

    private void parseJson(final String data) {
        Gson gson = new Gson();
        musicList = gson.fromJson(data, new TypeToken<List<Music>>() {
        }.getType());
        for (Music music : musicList) {
            Log.d("Music", "parseJson: " + music.getAuthor());
        }
        progressDialog.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // music adapter
                musicAdapter = new MusicAdapter(MainActivity.this, R.layout.music_item, musicList);
                music_list_view = findViewById(R.id.music_list);
                music_list_view.setAdapter(musicAdapter);


                // list view 的点击事件
                music_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String down_url = musicList.get(position).getUrl();
                        String Singer = musicList.get(position).getAuthor();
                        String name = musicList.get(position).getTitle();
                        String artist_img = musicList.get(position).getPic();

                        Intent intent = new Intent(MainActivity.this, MusicMainActivity.class);
                        intent.putExtra("down_url", down_url);
                        intent.putExtra("Singer", Singer);
                        intent.putExtra("name", name);
                        intent.putExtra("artist_img", artist_img);
                        startActivity(intent);
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
