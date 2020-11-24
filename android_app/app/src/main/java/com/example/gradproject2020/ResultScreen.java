package com.example.gradproject2020;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

public class ResultScreen extends Fragment {

    View view, alert_graph;
    String jsondir; //android folder direction
    String filename; //android .json file name
    Load load;
    Button btn_chart, btn_graph;
    PieChart piechart;
    MaterialCalendarView materialCalendarView;

    ArrayList<PieEntry> array_kcal_p, array_sugar_p;

    float day7_s, day6_s, day5_s, day4_s, day3_s, day2_s, day1_s, today_s;
    float day7_k, day6_k, day5_k, day4_k, day3_k, day2_k, day1_k, today_k;

    Button btn_ok;
    boolean graphflag = false, alertgraphflag = false;

    public ResultScreen() {
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_result_screen, container, false);
        alert_graph = inflater.inflate(R.layout.alert_graph, container, false);
        materialCalendarView = view.findViewById(R.id.calendarView);
        btn_graph = alert_graph.findViewById(R.id.btn_graph1);
        btn_ok = alert_graph.findViewById(R.id.btn_ok1);
        btn_chart = view.findViewById(R.id.btn_chart);
        piechart = alert_graph.findViewById(R.id.chartview);
        BarChart chart = view.findViewById(R.id.chartview);
        jsondir = getContext().getExternalFilesDir("/json").getAbsolutePath(); //폰

        final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
        load = new Load();
        load.load_weekly();


        day5_k = (float)1423.8;
        day6_k = (float)1017.1;
        day3_k = (float)1658.7;
        day2_k = (float)1514.2;
        day1_k = (float)1217.7;
        day5_s = (float)346.8;
        day6_s = (float)457.1;
        day3_s = (float)131.1;
        day2_s = (float)134.8;
        day1_s = (float)589.4;


        Description description = new Description();
        description.setTextSize(15);

        ArrayList<BarEntry> array_kcal = new ArrayList<BarEntry>();
        array_kcal = load.load_kcal();
        ArrayList<BarEntry> array_sugar = new ArrayList<BarEntry>();
        array_sugar = load.load_sugar();

        BarDataSet dataset_kcal = new BarDataSet(array_kcal, "kcal");
        dataset_kcal.setColors(ColorTemplate.PASTEL_COLORS);
        BarDataSet dataset_sugar = new BarDataSet(array_sugar, "sugar");
        dataset_sugar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data_kcal = new BarData((dataset_kcal));
        BarData data_sugar = new BarData((dataset_sugar));

        data_kcal.setBarWidth(1f);
        data_sugar.setBarWidth(1f);

        description.setText("일주일간 섭취 칼로리");
        chart.setDescription(description);
        chart.setData(data_kcal);
        chart.setTouchEnabled(true);

        ArrayList<CalendarDay> array = load.getdates();
        materialCalendarView.addDecorators(new EventDecorator(Color.RED, array));

        btn_chart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (graphflag) {
                    description.setText("일주일간 섭취 칼로리"); //라벨
                    chart.setDescription(description);
                    chart.setData(data_kcal);
                    chart.setTouchEnabled(true);
                    chart.invalidate();

                    btn_chart.setText("당분 그래프 확인");
                    graphflag = false;
                } else {
                    description.setText("일주일간 섭취 당분"); //라벨
                    chart.setDescription(description);
                    chart.setData(data_sugar);
                    chart.setTouchEnabled(true);
                    chart.invalidate();

                    btn_chart.setText("열량 그래프 확인");
                    graphflag = true;
                }
            }
        });
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String dat = FORMATTER.format(date.getDate());
                File jsonfile = new File(jsondir + "/" + dat + ".json");
                if (!jsonfile.exists()) {
                    Toast.makeText(getContext(), "해당 날짜의 자료가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<PieEntry> array_kcal_p = new ArrayList<PieEntry>();
                    array_kcal_p = load.load_kcal_pi(jsondir + "/" + dat + ".json");
                    ArrayList<PieEntry> array_sugar_p = new ArrayList<PieEntry>();
                    array_sugar_p = load.load_sugar_pi(jsondir + "/" + dat + ".json");

                    PieDataSet dataset_kcal_p = new PieDataSet(array_kcal_p, "kcal");
                    dataset_kcal_p.setColors(ColorTemplate.PASTEL_COLORS);
                    PieDataSet dataset_sugar_p = new PieDataSet(array_sugar_p, "sugar");
                    dataset_sugar_p.setColors(ColorTemplate.PASTEL_COLORS);

                    PieData data_kcal_p = new PieData((dataset_kcal_p));
                    PieData data_sugar_p = new PieData((dataset_sugar_p));

                    description.setText("오늘 섭취 칼로리"); //라벨
                    piechart.setDescription(description);
                    piechart.setData(data_kcal_p);
                    piechart.setTouchEnabled(true);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    if (alert_graph.getParent() != null)
                        ((ViewGroup) alert_graph.getParent()).removeView(alert_graph);
                    builder.setView(alert_graph)
                            .setCancelable(false);
                    AlertDialog alert = builder.create();

                    btn_graph.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            if(alertgraphflag){
                                description.setText("섭취 칼로리"); //라벨
                                btn_graph.setText("당분 보기");
                                piechart.setDescription(description);
                                piechart.setData(data_kcal_p);
                                piechart.setTouchEnabled(true);
                                piechart.invalidate();
                                alertgraphflag = false;
                            }
                            else{
                                description.setText("섭취 당분"); //라벨
                                btn_graph.setText("칼로리 보기");
                                piechart.setDescription(description);
                                piechart.setData(data_sugar_p);
                                piechart.setTouchEnabled(true);
                                piechart.invalidate();
                                alertgraphflag = true;
                            }
                        }
                    });
                    btn_ok.setOnClickListener(new Button.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            alert.cancel();
                        }
                    });
                    alert.show();
                }
                if (date == null) {
                    System.out.println("no selection");
                }
            }
        });

        materialCalendarView.addDecorators();

        return view;
    }

    class Load {
        void load_weekly() {
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            day7_k = data_personal.getFloat("kcal_7", 0);
            day6_k = data_personal.getFloat("kcal_6", 0);
            day5_k = data_personal.getFloat("kcal_5", 0);
            day4_k = data_personal.getFloat("kcal_4", 0);
            day3_k = data_personal.getFloat("kcal_3", 0);
            day2_k = data_personal.getFloat("kcal_2", 0);
            day1_k = data_personal.getFloat("kcal_1", 0);
            today_k = data_personal.getFloat("kcal_today", 0);

            day7_s = data_personal.getFloat("sugar_7", 0);
            day6_s = data_personal.getFloat("sugar_6", 0);
            day5_s = data_personal.getFloat("sugar_5", 0);
            day4_s = data_personal.getFloat("sugar_4", 0);
            day3_s = data_personal.getFloat("sugar_3", 0);
            day2_s = data_personal.getFloat("sugar_2", 0);
            day1_s = data_personal.getFloat("sugar_1", 0);
            today_s = data_personal.getFloat("sugar_today", 0);

        }

        ArrayList<BarEntry> load_kcal() {
            ArrayList<BarEntry> arr = new ArrayList<>();
            arr.add(new BarEntry(0, day7_k));
            arr.add(new BarEntry(1, day6_k));
            arr.add(new BarEntry(2, day5_k));
            arr.add(new BarEntry(3, day4_k));
            arr.add(new BarEntry(4, day3_k));
            arr.add(new BarEntry(5, day2_k));
            arr.add(new BarEntry(6, day1_k));
            arr.add(new BarEntry(7, today_k));

            return arr;
        }

        ArrayList<BarEntry> load_sugar() {
            ArrayList<BarEntry> arr = new ArrayList<>();
            arr.add(new BarEntry(0, day7_s));
            arr.add(new BarEntry(1, day6_s));
            arr.add(new BarEntry(2, day5_s));
            arr.add(new BarEntry(3, day4_s));
            arr.add(new BarEntry(4, day3_s));
            arr.add(new BarEntry(5, day2_s));
            arr.add(new BarEntry(6, day1_s));
            arr.add(new BarEntry(7, today_s));

            return arr;
        }

        ArrayList<CalendarDay> getdates() {
            ArrayList<CalendarDay> arr = new ArrayList<>();
            File jsonfile = new File(jsondir);
            String[] str = jsonfile.list();
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < str.length; i++) {
                try {
                    Date d = transFormat.parse(str[i].split(".json")[0]);
                    arr.add(CalendarDay.from(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return arr;
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

        ArrayList<PieEntry> load_kcal_pi(String path) {
            ArrayList<PieEntry> arr = new ArrayList<>();
            try {
                JSONObject jsonObject = loadJSON(path);
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

        ArrayList<PieEntry> load_sugar_pi(String path) {
            ArrayList<PieEntry> arr = new ArrayList<>();
            try {
                JSONObject jsonObject = loadJSON(path);
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

    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }
}
