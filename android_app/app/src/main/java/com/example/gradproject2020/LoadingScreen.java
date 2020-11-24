package com.example.gradproject2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class LoadingScreen extends AppCompatActivity {

    //class for function call
    private ConnectFTP ConnectFTP;
    Resources res;

    //flags
    int quitflag = 0;

    //directions
    String cpudir, anddir;

    //etc resources
    TextView txt_loading;
    ImageView img_loading1, img_loading2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        ConnectFTP = new ConnectFTP();
        res = getResources();

        txt_loading = findViewById(R.id.txt_loading);
        img_loading1 = findViewById(R.id.img_loading1);
        img_loading2 = findViewById(R.id.img_loading2);

        anddir = getIntent().getStringExtra("anddir");

        Glide.with(this).load(R.drawable.loadingscreen).into(img_loading1);
        Glide.with(this).load(R.drawable.loading2).into(img_loading2);

        Loadingh5();
    }

    private void Loadingh5(){
        new Thread(new Runnable() {
            @Override
            public void run() {/*
                txt_loading.setText("서버에 연결 중...");
                if(!ConnectFTP.ftpConnect("114.70.22.242", "ubuntu", "wjswk!105", 21)){
                    txt_loading.setText("연결에 실패했습니다.");
                    quitflag = 1;
                    Loading();
                }

                cpudir = ConnectFTP.ftpGetDirectory() + "/L1105/CJY/gradproj"; //컴퓨터
                txt_loading.setText("데이터 업데이트 중...");

                if(!ConnectFTP.ftpDownloadFile(cpudir+"/model.h5", anddir+"/model.h5")){
                    txt_loading.setText("파일 다운로드에 실패했습니다.");
                    quitflag = 1;
                    Loading();
                }
                */
                txt_loading.setText("로딩 완료");
                Loading();
            }
        }).start();
    }
    private void Loading(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent Scr_main = new Intent();
                Scr_main.putExtra("quitflag", quitflag);
                setResult(RESULT_OK, Scr_main);
                finish();
            }
        }, 2000);
    }
}
