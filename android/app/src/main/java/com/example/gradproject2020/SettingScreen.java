package com.example.gradproject2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class SettingScreen  extends Fragment {

    public SettingScreen(){
        //empty constructor
    }

    //View
    Button btn_name, btn_gender, btn_height, btn_weight, btn_activ, btn_age;
    TextView txt_name, txt_gender, txt_height, txt_weight, txt_kcal, txt_age, txt_activity;

    //Functions
    Intent Scr_Setting;
    pref Pref;

    //Resources
    String username;
    boolean usergender; //false : 남, true : 여
    float userweight, userheight, userkcal, useractiv, usersugar;
    int userage;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_setting_screen, container, false);
        btn_name = view.findViewById(R.id.btn_name);
        btn_gender = view.findViewById(R.id.btn_gender);
        btn_height = view.findViewById(R.id.btn_height);
        btn_age = view.findViewById(R.id.btn_age);
        btn_weight = view.findViewById(R.id.btn_weight);
        btn_activ = view.findViewById(R.id.btn_activ);

        txt_name = view.findViewById(R.id.txt_name);
        txt_gender = view.findViewById(R.id.txt_gender);
        txt_height = view.findViewById(R.id.txt_height);
        txt_age = view.findViewById(R.id.txt_age);
        txt_weight = view.findViewById(R.id.txt_weight);
        txt_kcal = view.findViewById(R.id.txt_kcal);
        txt_activity = view.findViewById(R.id.txt_activity);

        Pref = new pref();
        Pref.load_personal();

        txt_name.setText(username);
        txt_height.setText(Float.toString(userheight));
        txt_weight.setText(Float.toString(userweight));
        txt_age.setText(Integer.toString(userage));
        if(usergender) txt_gender.setText("여성");
        else txt_gender.setText("남성");
        if(useractiv == 1.2f) txt_activity.setText("활동 없음");
        else if (useractiv == 1.3f) txt_activity.setText("약간의 활동");
        else if (useractiv == 1.5f) txt_activity.setText("정상 활동");
        else txt_activity.setText("격렬한 운동");
        txt_kcal.setText(Float.toString(userkcal));

        btn_name.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder alert_correct = new AlertDialog.Builder(getActivity());
                final EditText name = new EditText(getActivity());
                alert_correct.setTitle("이름 변경")
                        .setView(name)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                username = name.getText().toString();
                                Pref.save_name(username);
                                txt_name.setText(username);
                                txt_kcal.setText(Float.toString(userkcal));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
                AlertDialog alertDialog = alert_correct.create();
                alertDialog.show();
            }
        });

        btn_gender.setOnClickListener(new View.OnClickListener(){
            boolean choice;
            @Override
            public void onClick(View view){
                final String[] items = new String[] {"남성", "여성"};
                AlertDialog.Builder alert_correct = new AlertDialog.Builder(getActivity());
                alert_correct.setTitle("성별 변경")
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0) choice = false;
                                else choice = true;
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                usergender = choice;
                                Pref.save_gender(usergender);
                                if(usergender) txt_gender.setText("여성");
                                else txt_gender.setText("남성");
                                txt_kcal.setText(Float.toString(userkcal));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
                AlertDialog alertDialog = alert_correct.create();
                alertDialog.show();
            }
        });

        btn_height.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder alert_correct = new AlertDialog.Builder(getActivity());
                final EditText height = new EditText(getActivity());
                height.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                alert_correct.setTitle("키 변경")
                        .setView(height)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userheight = Float.parseFloat(height.getText().toString());
                                Pref.save_height(userheight);
                                txt_height.setText(Float.toString(userheight));
                                txt_kcal.setText(Float.toString(userkcal));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
                AlertDialog alertDialog = alert_correct.create();
                alertDialog.show();
            }
        });

        btn_weight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder alert_correct = new AlertDialog.Builder(getActivity());
                final EditText weight = new EditText(getActivity());
                weight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                alert_correct.setTitle("몸무게 변경")
                        .setView(weight)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userweight = Float.parseFloat(weight.getText().toString());
                                Pref.save_weight(userweight);
                                txt_weight.setText(Float.toString(userweight));
                                txt_kcal.setText(Float.toString(userkcal));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
                AlertDialog alertDialog = alert_correct.create();
                alertDialog.show();
            }
        });

        btn_age.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder alert_correct = new AlertDialog.Builder(getActivity());
                final EditText age = new EditText(getActivity());
                age.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert_correct.setTitle("나이 변경")
                        .setView(age)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userage = Integer.parseInt(age.getText().toString());
                                Pref.save_age(userage);
                                txt_age.setText(Float.toString(userage));
                                txt_kcal.setText(Float.toString(userkcal));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
                AlertDialog alertDialog = alert_correct.create();
                alertDialog.show();
            }
        });

        btn_activ.setOnClickListener(new View.OnClickListener(){
            int index;
            @Override
            public void onClick(View view){
                final String[] items = new String[] {"활동 없음", "약간의 활동", "정상 활동", "격렬한 운동"};
                AlertDialog.Builder alert_correct = new AlertDialog.Builder(getActivity());
                alert_correct.setTitle("활동량 변경")
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                index = which;
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(index){
                                    case 0:
                                        useractiv = 1.2f;
                                        break;
                                    case 1:
                                        useractiv = 1.3f;
                                        break;
                                    case 2:
                                        useractiv = 1.5f;
                                        break;
                                    case 3:
                                        useractiv = 1.75f;
                                        break;
                                }
                                Pref.save_activ(useractiv);
                                if(useractiv == 1.2f) txt_activity.setText("활동 없음");
                                else if (useractiv == 1.3f) txt_activity.setText("약간의 활동");
                                else if (useractiv == 1.5f) txt_activity.setText("정상 활동");
                                else txt_activity.setText("격렬한 운동");
                                txt_kcal.setText(Float.toString(userkcal));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
                AlertDialog alertDialog = alert_correct.create();
                alertDialog.show();
            }
        });

        return view;
    }

    class pref{
        float calc_kcal(){
            float kcal = 0; //final kcal
            float weight_avg = 0; //average weight
            float weight_ = 0; //modified weight with average
            if(usergender){
                weight_avg = userheight*userheight*21/10000;
                weight_ = weight_avg + ((userweight - weight_avg)*0.25f);
                kcal = 665 + (9.6f*weight_) + (1.8f*userheight) - (4.7f*userage);

            }
            else{
                weight_avg = userheight*userheight*22/10000;
                weight_ = weight_avg + ((userweight - weight_avg)*0.25f);
                kcal = 664 + (13.7f*weight_) + (5f*userheight) - (6.8f*userage);
            }

            return kcal*useractiv;
        }
        void save_name(String name){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            edit.putString("name", name);
            edit.apply();
        }
        void save_gender(Boolean gender){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            edit.putBoolean("gender", gender);
            userkcal = calc_kcal();
            usersugar = userkcal/40;
            edit.putFloat("kcal", userkcal);
            edit.putFloat("sugar", usersugar);
            edit.apply();
        }
        void save_height(float height){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            edit.putFloat("height", height);
            userkcal = calc_kcal();
            usersugar = userkcal/40;
            edit.putFloat("kcal", userkcal);
            edit.putFloat("sugar", usersugar);
            edit.apply();
        }
        void save_weight(float weight){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            edit.putFloat("weight", weight);
            userkcal = calc_kcal();
            usersugar = userkcal/40;
            edit.putFloat("kcal", userkcal);
            edit.putFloat("sugar", usersugar);
            edit.apply();
        }
        void save_activ(float activ){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            edit.putFloat("activity", activ);
            userkcal = calc_kcal();
            usersugar = userkcal/40;
            edit.putFloat("kcal", userkcal);
            edit.putFloat("sugar", usersugar);
            edit.apply();
        }
        void save_age(int age){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            edit.putInt("age", age);
            userkcal = calc_kcal();
            usersugar = userkcal/40;
            edit.putFloat("kcal", userkcal);
            edit.putFloat("sugar", usersugar);
            edit.apply();
        }
        void load_personal(){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            username = data_personal.getString("name", "김건국");
            userheight = data_personal.getFloat("height", 170f);
            userweight = data_personal.getFloat("weight", 60f);
            usergender = data_personal.getBoolean("gender", false);
            useractiv = data_personal.getFloat("activity", 1.5f);
            userage = data_personal.getInt("age", 20);
            userkcal = data_personal.getFloat("kcal", 0);
            usersugar = data_personal.getFloat("sugar", 0);
        }
    }
}
