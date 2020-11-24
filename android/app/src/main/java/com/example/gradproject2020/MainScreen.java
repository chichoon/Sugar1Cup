package com.example.gradproject2020;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MainScreen extends Fragment {

    //function
    pref Pref;
    View view;
    ImageView img_sugar;
    TextView txt_sugar, txt_pct;
    Button btn_graph;
    float pct;

    //variables
    String username, datelastupdate;
    float userkcal, userkcal_today, usersugar, usersugar_today;
    String jsondir; //android folder direction
    String filename; //android .json file name
    boolean graphflag = false; //false: sugar, true: kcal
    int dataindex = 0, todayindex = 0;

    public MainScreen() {
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main_screen, container, false);
        View marker = inflater.inflate(R.layout.tvcontent, container, false);
        TextView tvContent = marker.findViewById(R.id.tvContent);

        String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Pref = new pref();
        Pref.load_pref();
        if (!now.equals(datelastupdate)) {
            todayindex = 0;
            Pref.change_date();
        }

        /*
        //for debugging reset
        SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
        SharedPreferences.Editor edit = data_personal.edit();
        edit.putInt("todayindex", 0);
        edit.putInt("index", 0);
        edit.putFloat("kcal_today", 0);
        edit.putFloat("sugar_today", 0);
        edit.putString("lastdate", "1970-01-01");
        edit.apply();
         */

        jsondir = getContext().getExternalFilesDir("/json").getAbsolutePath(); //폰
        filename = "/" + now + ".json";

        img_sugar = view.findViewById(R.id.img_sugar);
        txt_sugar = view.findViewById(R.id.txt_sugar);
        btn_graph = view.findViewById(R.id.btn_graph);
        txt_pct = view.findViewById(R.id.txt_pct);

        PieChart chart = view.findViewById(R.id.chart);
        Description description = new Description();
        description.setTextSize(15);

        ArrayList<PieEntry> array_kcal = new ArrayList<PieEntry>();
        array_kcal = Pref.load_today();
        ArrayList<PieEntry> array_sugar = new ArrayList<PieEntry>();
        array_sugar = Pref.load_sugar();

        PieDataSet dataset_kcal = new PieDataSet(array_kcal, "kcal");
        dataset_kcal.setColors(ColorTemplate.PASTEL_COLORS);
        PieDataSet dataset_sugar = new PieDataSet(array_sugar, "sugar");
        dataset_sugar.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data_kcal = new PieData((dataset_kcal));
        PieData data_sugar = new PieData((dataset_sugar));

        description.setText("오늘 섭취 칼로리"); //라벨
        chart.setDescription(description);
        chart.setData(data_kcal);
        chart.setTouchEnabled(true);

        pct = (userkcal_today / userkcal) * 100;
        txt_pct.setText(Float.toString(userkcal_today) + " kcal / " + Float.toString(userkcal) + " kcal, " + pct + "% 섭취");
        if (pct <= 50) {
            Glide.with(getContext()).load(R.drawable.icon_smile).into(img_sugar);
            txt_sugar.setText("열량 섭취량이 적당해요");
        } else if (pct >= 50 && pct <= 100) {
            Glide.with(getContext()).load(R.drawable.icon_neut).into(img_sugar);
            txt_sugar.setText("열량 섭취량이 보통이에요");
        } else {
            Glide.with(getContext()).load(R.drawable.icon_bad).into(img_sugar);
            txt_sugar.setText("열량 섭취량이 위험해요");
        }
        btn_graph.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!graphflag) {
                    pct = (userkcal_today / userkcal) * 100;
                    if (pct <= 50) {
                        Glide.with(getContext()).load(R.drawable.icon_smile).into(img_sugar);
                        txt_sugar.setText("열량 섭취량이 적당해요");
                    } else if (pct >= 50 && pct <= 100) {
                        Glide.with(getContext()).load(R.drawable.icon_neut).into(img_sugar);
                        txt_sugar.setText("열량 섭취량이 보통이에요");
                    } else {
                        Glide.with(getContext()).load(R.drawable.icon_bad).into(img_sugar);
                        txt_sugar.setText("열량 섭취량이 위험해요");
                    }
                    description.setText("오늘 섭취 칼로리"); //라벨
                    chart.setDescription(description);
                    chart.setData(data_kcal);
                    chart.setTouchEnabled(true);
                    chart.invalidate();

                    btn_graph.setText("당분 그래프 확인");
                    txt_pct.setText(Float.toString(userkcal_today) + " kcal / " + Float.toString(userkcal) + " kcal, " + pct + "% 섭취");
                    graphflag = true;
                } else {
                    float pct = (usersugar_today / usersugar) * 100;
                    if (pct <= 50) {
                        Glide.with(getContext()).load(R.drawable.icon_smile).into(img_sugar);
                        txt_sugar.setText("적당량의 당분을 먹고 있어요");
                    } else if (pct >= 50 && pct <= 100) {
                        Glide.with(getContext()).load(R.drawable.icon_neut).into(img_sugar);
                        txt_sugar.setText("당분 섭취를 가급적 자제하세요");
                    } else {
                        Glide.with(getContext()).load(R.drawable.icon_bad).into(img_sugar);
                        txt_sugar.setText("당분 섭취를 줄이세요");
                    }
                    description.setText("오늘 섭취 당분"); //라벨
                    chart.setDescription(description);
                    chart.setData(data_sugar);
                    chart.setTouchEnabled(true);
                    chart.invalidate();

                    btn_graph.setText("열량 그래프 확인");
                    txt_pct.setText(Float.toString(usersugar_today) + " g / " + Float.toString(usersugar) + " g, " + pct + "% 섭취");
                    graphflag = false;
                }
            }
        });
        return view;
    }

    class pref {
        void load_pref() {
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            username = data_personal.getString("name", "김건국");
            userkcal = data_personal.getFloat("kcal", 1500);
            userkcal_today = data_personal.getFloat("kcal_today", 0);
            usersugar = data_personal.getFloat("sugar", 30);
            usersugar_today = data_personal.getFloat("sugar_today", 0);
            datelastupdate = data_personal.getString("lastdate", "1970-01-01");
            dataindex = data_personal.getInt("index", 0);
            todayindex = data_personal.getInt("todayindex", 0);
        }

        void change_date(){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            float temp_k, temp_s;

            temp_k = data_personal.getFloat("kcal_6", 0);
            edit.putFloat("kcal_7", temp_k);
            temp_k = data_personal.getFloat("kcal_5", 0);
            edit.putFloat("kcal_6", temp_k);
            temp_k = data_personal.getFloat("kcal_4", 0);
            edit.putFloat("kcal_5", temp_k);
            temp_k = data_personal.getFloat("kcal_3", 0);
            edit.putFloat("kcal_4", temp_k);
            temp_k = data_personal.getFloat("kcal_2", 0);
            edit.putFloat("kcal_3", temp_k);
            temp_k = data_personal.getFloat("kcal_1", 0);
            edit.putFloat("kcal_2", temp_k);
            temp_k = data_personal.getFloat("kcal_today", 0);
            edit.putFloat("kcal_1", temp_k);
            edit.putFloat("kcal_today", 0);

            temp_s = data_personal.getFloat("sugar_6", 0);
            edit.putFloat("sugar_7", temp_s);
            temp_s = data_personal.getFloat("sugar_5", 0);
            edit.putFloat("sugar_6", temp_s);
            temp_s = data_personal.getFloat("sugar_4", 0);
            edit.putFloat("sugar_5", temp_s);
            temp_s = data_personal.getFloat("sugar_3", 0);
            edit.putFloat("sugar_4", temp_s);
            temp_s = data_personal.getFloat("sugar_2", 0);
            edit.putFloat("sugar_3", temp_s);
            temp_s = data_personal.getFloat("sugar_1", 0);
            edit.putFloat("sugar_2", temp_s);
            temp_k = data_personal.getFloat("sugar_today", 0);
            edit.putFloat("sugar_1", temp_k);
            edit.putFloat("sugar_today", 0);

            userkcal_today = 0;
            usersugar_today = 0;
        }

        public JSONObject loadJSON(String path) {
            try {
                File jsonfile = new File(path);
                if (!jsonfile.exists()) {
                    JSONArray tempary = new JSONArray();
                    JSONObject tempobj = new JSONObject();
                    try {
                        tempobj.put("Beverages", tempary);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String str = tempobj.toString();
                    FileWriter filewriter = new FileWriter(jsonfile);
                    BufferedWriter bufferedWriter = new BufferedWriter(filewriter);
                    bufferedWriter.write(str);
                    bufferedWriter.close();
                }
                FileReader filereader = new FileReader(jsonfile);
                BufferedReader bufferedReader = new BufferedReader(filereader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                String responce = stringBuilder.toString();
                try {
                    JSONObject obj = new JSONObject(responce);
                    return obj;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        ArrayList<PieEntry> load_today() {
            ArrayList<PieEntry> arr = new ArrayList<>();
            try {
                JSONObject jsonObject = loadJSON(jsondir + filename);
                JSONArray array = jsonObject.getJSONArray("Beverages");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    arr.add(new PieEntry(Float.parseFloat(String.valueOf(object.get("kcal"))), String.valueOf(object.get("name"))));
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                arr.add(new PieEntry(1, "Null"));
            }
            return arr;
        }

        ArrayList<PieEntry> load_sugar() {
            ArrayList<PieEntry> arr = new ArrayList<>();
            try {
                JSONObject jsonObject = loadJSON(jsondir + filename);
                JSONArray array = jsonObject.getJSONArray("Beverages");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    arr.add(new PieEntry(Float.parseFloat(String.valueOf(object.get("sugar"))), String.valueOf(object.get("name"))));
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                arr.add(new PieEntry(1, "Null"));
            }
            return arr;
        }
    }
}