package com.example.gradproject2020;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class BTScreen extends Fragment {

    //View
    TextView txt_blton, txt_paired;
    Button btn_pairing, btn_measure, btn_alertcancel, btn_unpair;
    View view;
    ImageView img_sugar;
    TextView txt_sugar;
    Switch swi_straw;
    Intent Scr_BT;

    //View_alert
    View alertview, alertsendview, alertaddview;
    TextView txt_alert, txt_alertsend;
    Button btn_alertsendcancel, btn_addcancel, btn_addok;
    ImageView img_alert, img_alert_send;

    //flag
    boolean bltonflag = false, pairedflag = false, strawflag = false, measureflag = false, alertflag = false;
    boolean sendflag = false, threadflag = false, alertclosedflag = true, sendfinalresult = false;
    int gradflag = 0, dataindex = 0, todayindex = 0, lastbevrep;
    final int req_BT = 1;

    //variables
    String username;
    float userkcal, userkcal_today, usersugar, usersugar_today;
    String anddir, jsondir, filename, datelastupdate, lastbev;
    String[] dataarr, dataarr1;

    //Class
    connectBT bt;
    ConnectFTP ftp;
    jsonclass js;
    Socket socket;
    DataInputStream dis;

    //bluetooth variables
    BluetoothAdapter BTAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<String> listDevices;

    Handler BTHandler, alerthandler, sendhandler, sockethandler;
    BluetoothDevice BTdevice;
    BluetoothSocket BTsocket;
    AlertDialog.Builder builder, builder2, builder3;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    boolean btconnect = false;
    boolean saveflag = false;

    FileOutputStream fos = null;

    public BTScreen() {
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_btscreen, container, false);
        txt_blton = view.findViewById(R.id.txt_blton);
        txt_paired = view.findViewById(R.id.txt_paired);
        btn_pairing = view.findViewById(R.id.btn_pairing);
        btn_measure = view.findViewById(R.id.btn_measure);
        btn_unpair = view.findViewById(R.id.btn_unpair);
        img_sugar = view.findViewById(R.id.img_sugar);
        txt_sugar = view.findViewById(R.id.txt_sugar);
        swi_straw = view.findViewById(R.id.swi_straw);

        alertview = inflater.inflate(R.layout.alert_bt, container, false);
        alertsendview = inflater.inflate(R.layout.alert_send, container, false);
        alertaddview = inflater.inflate(R.layout.alert_adddata, container, false);
        txt_alert = alertview.findViewById(R.id.txt_alert);
        txt_alertsend = alertsendview.findViewById(R.id.txt_alert_send);
        img_alert = alertview.findViewById(R.id.img_alert);
        img_alert_send = alertsendview.findViewById(R.id.img_alert_send);
        btn_alertcancel = alertview.findViewById(R.id.btn_alertcancel);
        btn_alertsendcancel = alertsendview.findViewById(R.id.btn_alertsendcancel);
        btn_addcancel = alertaddview.findViewById(R.id.btn_cancel);
        btn_addok = alertaddview.findViewById(R.id.btn_ok2);

        String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        jsondir = getContext().getExternalFilesDir("/json").getAbsolutePath(); //폰
        filename = "/" + now + ".json";

        Handler handler = new Handler(Looper.getMainLooper());

        anddir = getActivity().getExternalFilesDir("/resources").getAbsolutePath(); //폰
        File file = new File(anddir + "/temp.bin");

        builder = new AlertDialog.Builder(getActivity());
        builder2 = new AlertDialog.Builder(getActivity());
        builder.setView(alertview)
                .setCancelable(false);
        AlertDialog alert = builder.create();

        builder3 = new AlertDialog.Builder(getActivity());
        builder3.setTitle("음료수를 찾는 중입니다...")
                .setMessage("서버에서 데이터 수신 중입니다")
                .setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        bt = new connectBT();
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        ftp = new ConnectFTP();
        js = new jsonclass();
        js.load_pref();

        Glide.with(getContext()).load(R.drawable.loadingscreen2).into(img_alert);
        Glide.with(getContext()).load(R.drawable.loadingscreen2).into(img_alert_send);
        txt_alert.setText("컵을 기울여 음료를 마시세요");
        txt_alertsend.setText("음료수 측정 중...");

        btn_pairing.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt.bluetoothOn()) {
                    txt_blton.setText("ON");
                    bltonflag = true;

                    bt.connectSelectedDevice("HC-06");
                    if (btconnect) {
                        txt_paired.setText("YES");
                        pairedflag = true;
                    } else {
                        txt_paired.setText("NO");
                        pairedflag = false;
                    }
                } else {
                    txt_blton.setText("OFF");
                    bltonflag = false;
                }
            }
        });

        btn_unpair.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                bt.bluetoothOff();
                txt_blton.setText("OFF");
                txt_paired.setText("NO");
            }
        });

        btn_measure.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    fos = new FileOutputStream(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!bltonflag) {
                    Toast.makeText(getContext().getApplicationContext(), "블루투스 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!pairedflag) {
                        Toast.makeText(getContext().getApplicationContext(), "기기 연결을 확인하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        alert.show();
                        alertclosedflag = false;

                        btn_alertcancel.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                saveflag = false;
                                measureflag = false;
                                alertflag = false;
                                alert.cancel();
                                if(sendflag) {
                                    sendhandler.sendEmptyMessage(3);
                                }
                            }
                        });
                        alertflag = true;

                        txt_alert.setText("컵을 기울여 음료를 마시세요");

                        alerthandler = new Handler() {
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    if (measureflag) {
                                        txt_alert.setText("마시는 동작 감지됨");
                                        alertview.invalidate();
                                        if (saveflag) {
                                            try {
                                                fos.close();
                                                txt_alert.setText("파일 저장 완료, 창을 닫아주세요");
                                                sendflag = true;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            alerthandler.sendEmptyMessage(1);
                                        }
                                    } else if(strawflag){
                                        txt_alert.setText("빨대 모드 사용 중입니다");
                                        measureflag = true;
                                        alertview.invalidate();
                                        new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                                            @Override
                                            public void run() {
                                                try {
                                                    fos.close();
                                                    txt_alert.setText("파일 저장 완료, 창을 닫아주세요");
                                                    sendflag = true;
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 5000);
                                    }
                                    else {
                                        alerthandler.sendEmptyMessage(1);
                                    }
                                }
                            }
                        };
                        alerthandler.sendEmptyMessage(1);
                    }
                }
            }
        });

        swi_straw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext().getApplicationContext(), "빨대 모드로 변경합니다.", Toast.LENGTH_SHORT).show();
                    strawflag = true;
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "빨대 모드를 종료합니다.", Toast.LENGTH_SHORT).show();
                    strawflag = false;
                }
            }
        });

        BTHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == BT_MESSAGE_READ) { //BT Message obtained
                    try {
                        if (alertflag) {
                            js.gradientcheck((byte[]) msg.obj);
                        }
                        if (measureflag && !saveflag) {
                            js.rgbsave((byte[]) msg.obj);
                        }
                        else if(measureflag && strawflag){
                            js.rgbsave((byte[]) msg.obj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        sendhandler = new Handler(Looper.getMainLooper()){
            public void handleMessage(android.os.Message msg){
                if(msg.what == 3) {
                    js.sendfile("temp.bin", 3);
                    if (!threadflag) {
                        handler.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                sendhandler.sendEmptyMessage(3);
                            }
                        }, 500);
                    }
                }
                else if(msg.what == 4) {
                    builder3.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            js.trainresult();
                        }
                    }, 10000);
                }
                else if(msg.what == 5){
                    js.sendfile(lastbev + "_0.bin", 5);
                    if (!threadflag) {
                        handler.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                sendhandler.sendEmptyMessage(5);
                            }
                        }, 500);
                    }
                }
                else if(msg.what == 6){
                    js.sendfile(lastbev + "_" + lastbevrep + ".bin", 6);
                    if (!threadflag) {
                        handler.postDelayed(new Runnable(){
                            @Override
                            public void run(){
                                sendhandler.sendEmptyMessage(6);
                            }
                        }, 500);
                    }
                }
            }
        };
        return view;
    }

    public class bev {
        private int index;
        private String name;
        private double kcal;
        private double sugar;
        private int repeat;

        public int getIndex(){
            return index;
        }
        public String getName() {
            return name;
        }

        public double getKcal() {
            return kcal;
        }

        public double getSugar() {
            return sugar;
        }

        public int getRepeat(){
            return repeat;
        }

        public void setIndex(int index){
            this.index = index;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setKcal(double kcal) {
            this.kcal = kcal;
        }

        public void setSugar(double sugar) {
            this.sugar = sugar;
        }

        public void setRepeat(int repeat){
            this.repeat = repeat;
        }
    }

    public class jsonclass {
        public JSONObject loadJSON(String path) {
            try {
                File jsonfile = new File(path);
                if (!jsonfile.exists()) {
                    JSONArray tempary = new JSONArray();
                    JSONObject tempobj = new JSONObject();
                    try{
                        tempobj.put("Beverages", tempary);
                    } catch(JSONException e){
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
                try{
                    JSONObject obj = new JSONObject(responce);
                    return obj;
                } catch (JSONException e){
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        void load_pref() {
            //load preferences - today's data
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            userkcal = data_personal.getFloat("kcal", 1500);
            userkcal_today = data_personal.getFloat("kcal_today", 0);
            usersugar = data_personal.getFloat("sugar", 1000);
            usersugar_today = data_personal.getFloat("sugar_today", 0);
            datelastupdate = data_personal.getString("lastdate", "1970-01-01");
            dataindex = data_personal.getInt("index", 0);
            todayindex = data_personal.getInt("todayindex", 0);
        }

        void json_save(String path, bev Bev) {
            //all beverages : anddir + "/allbev.json"
            //today beverages : jsondir + "/(date).json"

            JSONObject oldobj = loadJSON(path);
            JSONObject newobj = new JSONObject();
            try {
                JSONArray newarr = oldobj.getJSONArray("Beverages");
                newobj.put("index", Integer.toString(Bev.index));
                newobj.put("name", Bev.name);
                newobj.put("kcal", Float.toString((float) Bev.kcal));
                newobj.put("sugar", Float.toString((float) Bev.sugar));
                newobj.put("repeat", Integer.toString(Bev.repeat));
                newarr.put(newobj);
                oldobj.put("Beverages", newarr);
            }catch (JSONException e){
                e.printStackTrace();
            }

            writeJsonFile(path, oldobj.toString());
        }


        public void writeJsonFile(String path, String json) {
            File file = new File(path);
            BufferedWriter bufferedWriter = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(file);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(json);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        void gradientcheck(byte[] obj) {
            //calculate accX, accY, accZ, ... to start identifying bev
            short m_AcX, m_AcY, m_AcZ;
            m_AcX = (short) ((obj[2] << 8) | (obj[1]));
            m_AcY = (short) ((obj[4] << 8) | (obj[3]));
            m_AcZ = (short) ((obj[6] << 8) | (obj[5]));
            System.out.println("X :" + Short.toString(m_AcX) + ", Y :" + Short.toString(m_AcY) + ", Z :" + Short.toString(m_AcZ));
            if (alertflag) { //AlertDialog opened
                if (!measureflag) {
                    if ((m_AcY < -2000) || (m_AcY > 10000)) {
                        if (gradflag == 0) gradflag = 1;
                        else if (gradflag == 1) gradflag = 2;
                        else if (gradflag == 2) measureflag = true;
                    }
                } else if (measureflag) {
                    if ((m_AcY > 250) && (m_AcY < 5000)) {
                        if (gradflag == 3) gradflag = 2;
                        else if (gradflag == 2) gradflag = 1;
                        else if (gradflag == 1) gradflag = 0;
                        else if (gradflag == 0) saveflag = true;
                    }
                }
            }
        }

        void rgbsave(byte[] obj) {
            try {
                fos.write(0x02); //start of the obj
                fos.write(obj[17]);
                fos.write(obj[18]);
                fos.write(obj[19]);
                fos.write(obj[20]);
                fos.write(obj[21]);
                fos.write(obj[22]);
                fos.write(obj[23]);
                fos.write(obj[24]);
                fos.write(0x03); //end of the obj
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void sendfile(String filen, int msg){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!ftp.ftpConnect("192.168.0.11", "taeil", "120415", 21)) {
                        threadflag = false;
                    } else {
                        String dir = ftp.ftpGetDirectory();

                        if (!ftp.ftpUploadFile(anddir + "/temp.bin", filen, dir)) {
                            threadflag = false;
                        } else {
                            threadflag = true;
                            if(msg == 3) sendhandler.sendEmptyMessage(4);
                        }
                    }
                }
            }).start();
        }

        void trainresult(){
            sockethandler = new Handler(Looper.getMainLooper());

            Thread socketthread = new Thread() {
                public void run() {
                    try {
                        socket = new Socket("192.168.0.11", 9999);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF("안드로이드에서 서버로 연결요청");
                    } catch (IOException e) {

                    }
                    try {
                        byte[] byteArr = new byte[1000];
                        InputStream is = socket.getInputStream();
                        int readByteCount = is.read(byteArr);
                        String data = new String(byteArr, 0, readByteCount, "UTF-8");
                        dataarr = data.split("/");
                        System.out.println(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            };
            socketthread.start();

            builder2.setTitle("예측된 음료수 목록")
                    .setItems(dataarr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String nm = dataarr[which].split(" ")[0];
                            JSONObject obj = js.findinjson(nm, "name", anddir + "/allbev.json"); //from allbev
                            try {
                                bev Bev = new bev();
                                Bev.setIndex(Integer.parseInt(String.valueOf(obj.get("index"))));
                                Bev.setKcal(Float.parseFloat(String.valueOf(obj.get("kcal"))));
                                Bev.setSugar(Float.parseFloat(String.valueOf(obj.get("sugar"))));
                                Bev.setName(String.valueOf(obj.get("name")));
                                Bev.setRepeat(Integer.parseInt(String.valueOf(obj.get("repeat"))));
                                lastbev = Bev.getName();
                                lastbevrep = Bev.getRepeat();

                                if(!changejson(nm, "name", Bev, true)){
                                    bev Bev2 = new bev();
                                    Bev2.setIndex(Bev.getIndex());
                                    Bev2.setKcal(Bev.getKcal() * 2.5);
                                    Bev2.setName(Bev.getName());
                                    Bev2.setSugar(Bev.getSugar() * 2.5);
                                    Bev2.setRepeat(Bev.getRepeat());

                                    js.json_save(jsondir + filename, Bev);
                                }
                                changejson(nm, "name", Bev, false);

                                todayindex++;
                                dataindex++;
                                userkcal_today += (float) Bev.getKcal() * 2.5;
                                usersugar_today += (float) Bev.getSugar() * 2.5;
                                datelastupdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                js.saveindex(todayindex, dataindex, userkcal_today, usersugar_today, datelastupdate);
                                Toast.makeText(getContext(), "저장 완료했습니다", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                dialog.dismiss();

                                sendhandler.sendEmptyMessage(6);
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    })
                    .setPositiveButton("목록에 없음", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){

                            AlertDialog.Builder dataadd = new AlertDialog.Builder(getActivity());
                            final EditText edit_kcal = alertaddview.findViewById(R.id.edit_kcal);
                            final EditText edit_sugar = alertaddview.findViewById(R.id.edit_sugar);
                            final EditText edit_name = alertaddview.findViewById(R.id.edit_name);
                            edit_kcal.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                            edit_sugar.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

                            dataadd.setTitle("데이터 추가")
                                    .setView(alertaddview);
                            AlertDialog addalert = dataadd.create();

                            btn_addok.setOnClickListener(new Button.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    bev Bev = new bev();
                                    Bev.setIndex(dataindex);
                                    Bev.setKcal(Float.parseFloat(edit_kcal.getText().toString()));
                                    Bev.setSugar(Float.parseFloat(edit_sugar.getText().toString()));
                                    Bev.setName(edit_name.getText().toString());
                                    lastbev = Bev.getName();
                                    Bev.setRepeat(0);
                                    js.json_save(anddir + "/allbev.json", Bev);
                                    Bev.setIndex(todayindex);
                                    Bev.setKcal(Float.parseFloat(edit_kcal.getText().toString()) * 2.5);
                                    Bev.setSugar(Float.parseFloat(edit_sugar.getText().toString()) * 2.5);
                                    js.json_save(jsondir + filename, Bev);

                                    todayindex++;
                                    dataindex++;
                                    userkcal_today += (float)Bev.getKcal();
                                    usersugar_today += (float)Bev.getSugar();
                                    datelastupdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                    js.saveindex(todayindex, dataindex, userkcal_today, usersugar_today, datelastupdate);
                                    Toast.makeText(getContext(), "저장 완료했습니다", Toast.LENGTH_SHORT).show();
                                    addalert.cancel();
                                    dialog.dismiss();
                                    sendhandler.sendEmptyMessage(5);
                                }
                            });

                            btn_addcancel.setOnClickListener(new Button.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    Toast.makeText(getContext(), "저장 취소되었습니다", Toast.LENGTH_SHORT).show();
                                    addalert.cancel();
                                }
                            });
                            addalert.show();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            threadflag = false;
                        }
                    })
                    .setCancelable(false);
            AlertDialog alertdata = builder2.create();
            alertdata.show();
        }

        void saveindex(int todayindex, int dataindex, Float kcal_today, Float sugar_today, String datelastupdate){
            SharedPreferences data_personal = getContext().getSharedPreferences("data_personal", MODE_PRIVATE);
            SharedPreferences.Editor edit = data_personal.edit();
            edit.putInt("todayindex", todayindex);
            edit.putInt("index", dataindex);
            edit.putFloat("kcal_today", kcal_today);
            edit.putFloat("sugar_today", sugar_today);
            edit.putString("lastdate", datelastupdate);
            edit.apply();
        }

        JSONObject findinjson(String bevname, String find, String path){
            JSONObject searchObject = null;
            String name;
            JSONObject obj = loadJSON(path);
            try {
                JSONArray array = obj.getJSONArray("Beverages");
                for(int i = 0; i<array.length(); i++) {
                    JSONObject currobject = array.getJSONObject(i);
                    name = currobject.getString(find);
                    if(name.equals(bevname)){
                        searchObject = currobject;
                        System.out.println("Found");
                        break;
                    }
                }
                return searchObject;
            }catch (JSONException e){
                e.printStackTrace();
            }
            System.out.println("not found");
            return null;
        }

        boolean changejson(String bevname, String find, bev Bev, boolean bool){
            JSONObject searchObject = null;
            String name;
            if(bool) { //save for today json
                JSONObject obj = loadJSON(jsondir + filename);
                try {
                    JSONArray array = obj.getJSONArray("Beverages");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject currobject = array.getJSONObject(i);
                        name = currobject.getString(find);
                        if (name.equals(bevname)) {
                            searchObject = currobject;
                            currobject.put("kcal", Float.parseFloat(String.valueOf(searchObject.get("kcal"))) + Bev.getKcal() * 2.5);
                            currobject.put("sugar", Float.parseFloat(String.valueOf(searchObject.get("sugar"))) + Bev.getSugar() * 2.5);
                            currobject.put("repeat", Integer.parseInt(String.valueOf(searchObject.get("repeat"))) + 1);
                            array.put(i, currobject);
                            obj.put("Beverages", array);
                            writeJsonFile(jsondir + filename, obj.toString());
                            return true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                JSONObject obj = loadJSON(anddir + "/allbev.json");
                try {
                    JSONArray array = obj.getJSONArray("Beverages");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject currobject = array.getJSONObject(i);
                        name = currobject.getString(find);
                        if (name.equals(bevname)) {
                            searchObject = currobject;
                            currobject.put("repeat", Integer.parseInt(String.valueOf(searchObject.get("repeat"))) + 1);
                            array.put(i, currobject);
                            obj.put("Beverages", array);
                            writeJsonFile(anddir + "/allbev.json", obj.toString());

                            return true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    public class connectBT {
        boolean bluetoothOn() {
            if (BTAdapter == null) {
                Toast.makeText(getContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (BTAdapter.isEnabled()) {
                    Toast.makeText(getContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(getContext(), "블루투스 기능을 켭니다.", Toast.LENGTH_SHORT).show();
                    Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    getActivity().startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
                    return true;
                }
            }
        }

        void bluetoothOff() {
            if (BTAdapter.isEnabled()) {
                BTAdapter.disable();
                Toast.makeText(getContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        void connectSelectedDevice(String selectedDeviceName) {
            pairedDevices = BTAdapter.getBondedDevices();
            for (BluetoothDevice tempDevice : pairedDevices) {
                if (selectedDeviceName.equals(tempDevice.getName())) {
                    BTdevice = tempDevice;
                    break;
                }
            }
            try {
                BTsocket = BTdevice.createRfcommSocketToServiceRecord(BT_UUID);
                BTsocket.connect();
                connectBT.ConnectedBluetoothThread thread = new connectBT.ConnectedBluetoothThread(BTsocket);
                thread.start();
                BTHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
                btconnect = true;
            } catch (IOException e) {
                btconnect = false;
                Toast.makeText(getContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        private class ConnectedBluetoothThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;

            public ConnectedBluetoothThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
                float pct = (userkcal_today/userkcal)*100;
                if(pct <= 70) write("2");
                else write("0");
            }

            public void run() {
                byte[] buffer = new byte[26];
                int bytes;

                while (true) {
                    try {
                        bytes = mmInStream.available();
                        if (bytes != 0) {
                            SystemClock.sleep(100);
                            bytes = mmInStream.available();
                            bytes = mmInStream.read(buffer, 0, bytes);
                            BTHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            }

            public void write(String str) {
                byte[] bytes = str.getBytes();
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Toast.makeText(getContext().getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
