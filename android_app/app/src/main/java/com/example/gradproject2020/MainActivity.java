package com.example.gradproject2020;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity implements FragmentCallback{
    //sources
    Intent Scr_Loading;
    Toolbar toolbar;
    Fragment frg_main, frg_BT, frg_result, frg_setting;
    DrawerLayout drawer;
    NavigationView navigationView;

    //request code for Intents
    final int req_Loading = 0;

    //flags
    int quitflag = 0; //1: force quit (fail to load FTP)
    boolean backflag = false; //back button flag

    //directions
    String anddir; //android folder direction

    //etc resources
    TextView nav_name, nav_txt_kcalpct;
    String username;
    pref Pref;
    float userkcal, userkcal_today, usersugar, usersugar_today;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.blank, R.string.blank);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Pref = new pref();
        Pref.load_pref();

        frg_main = new MainScreen();
        frg_BT = new BTScreen();
        frg_result = new ResultScreen();
        frg_setting = new SettingScreen();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.frg_border, frg_main);
        fragmentTransaction.commit();

        anddir = MainActivity.this.getExternalFilesDir("/resources").getAbsolutePath(); //폰

        Scr_Loading = new Intent(this, LoadingScreen.class);
        Scr_Loading.putExtra("anddir", anddir);

        startActivityForResult(Scr_Loading, req_Loading);

        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        nav_name = navView.findViewById(R.id.nav_txt_name);
        nav_txt_kcalpct = navView.findViewById(R.id.nav_txt_kcalpct);
        nav_name.setText(username);
        float pct = (userkcal_today/userkcal)*100;
        nav_txt_kcalpct.setText(String.valueOf(pct));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_main:
                            onChangedFragment(1, null);
                            break;
                        case R.id.nav_blt:
                            onChangedFragment(2, null);
                            break;
                        case R.id.nav_result:
                            onChangedFragment(3, null);
                            break;
                        case R.id.nav_setting:
                            onChangedFragment(4, null);
                            break;
                        case R.id.nav_reset:
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("저장된 json 데이터를 삭제하시겠습니까?")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String p = MainActivity.this.getExternalFilesDir("/json").getAbsolutePath();
                                            Pref.deletepath(p);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setCancelable(false);
                            AlertDialog alertDialog = alert.create();
                            alertDialog.show();
                            break;
                    }
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == req_Loading){
            quitflag = data.getIntExtra("quitflag", quitflag);
            if(quitflag == 1){
                finishAffinity();
                System.runFinalization();
                System.exit(0);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(backflag){
                super.onBackPressed();
                finishAffinity();
                System.runFinalization();
                System.exit(0);
                finish();
            }
            backflag = true;
            Toast.makeText(MainActivity.this,"뒤로가기를 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    backflag = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navi_menu, menu);
        return true;
    }

    @Override
    public void onChangedFragment(int position, Bundle bundle) {
        Fragment fragment = null;

        switch (position){
            case 1:
                fragment = frg_main;
                break;
            case 2:
                fragment = frg_BT;
                break;
            case 3:
                fragment = frg_result;
                break;
            case 4:
                fragment = frg_setting;
                break;
            default:
                break;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frg_border, fragment);
        fragmentTransaction.commit();
    }

    public void checkPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    void WriteFile(String filen, String content){
        File dir = MainActivity.this.getExternalFilesDir("/json");
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir.getAbsolutePath()+"/"+filen+".json", true));
            writer.write(content);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    class pref{
        void load_pref(){
            SharedPreferences data_personal = getSharedPreferences("data_personal", MODE_PRIVATE);
            username = data_personal.getString("name", "김건국");
            userkcal = data_personal.getFloat("kcal", 1500);
            userkcal_today = data_personal.getFloat("kcal_today", 1000);
            usersugar = data_personal.getFloat("sugar", 30);
            usersugar_today = data_personal.getFloat("sugar_today", 10);
        }
        void deletepath(String path){
            File dir = new File(path);
            File[] childFileList = dir.listFiles();

            if(dir.exists()){
                for(File childFile : childFileList){
                    if(childFile.isDirectory()){
                        deletepath(childFile.getAbsolutePath());
                    }
                    else{
                        childFile.delete();
                    }
                }
            }
        }
    }
}
