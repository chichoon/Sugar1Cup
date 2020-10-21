package com.example.a0319;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    TextView status, received;
    Button BTon, BToff, BTconnect, BTsave;

    BluetoothAdapter BTAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<String> listDevices;

    Handler BTHandler;
    ConnectedBluetoothThread thread;
    BluetoothDevice BTdevice;
    BluetoothSocket BTsocket;
    String readMessage;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ConnectFTP ConnectFTP;
    private final String TAG = "Connect FTP";

    String filename = "default";
    int saveflag, sendflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView)findViewById(R.id.stat);
        received = (TextView)findViewById(R.id.receive);
        BTsave = (Button)findViewById(R.id.save);
        BTon = (Button) findViewById(R.id.ON);
        BToff = (Button) findViewById(R.id.OFF);
        BTconnect = (Button) findViewById(R.id.connect);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        ConnectFTP = new ConnectFTP();

        if(saveflag == 0) {
            BTsave.setText("save");
        }
        else{
            BTsave.setText("stop");
        }
        checkPermission();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectFTP.ftpConnect("114.70.22.242", "ubuntu", "wjswk!105", 21);

                String cpudir = ConnectFTP.ftpGetDirectory() + "/L1105/CJY/binfiles"; //컴퓨터
                String anddir = MainActivity.this.getExternalFilesDir("/ardsaves").getAbsolutePath(); //폰
                while(true){
                    if (sendflag == 1) {
                        ConnectFTP.ftpUploadFile(anddir + "/" + filename + ".txt", filename + ".txt", cpudir);
                        sendflag = 0;
                    }
                }
            }
        }).start();
        BTon.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                bluetoothOn();
            }
        });
        BToff.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                bluetoothOff();
            }
        });
        BTconnect.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                pairedDev();
            }
        });

        BTsave.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if(saveflag == 0) {
                    final EditText name = new EditText(MainActivity.this);
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Set Filename")
                            .setMessage("Filename :")
                            .setView(name)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    filename = name.getText().toString();
                                    String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                    String contents = now + " : "  + readMessage;
                                    WriteFile(filename, contents);
                                    saveflag = 1;
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false);
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }
                else{
                    saveflag = 0;
                    sendflag = 1;
                    Toast.makeText(getApplicationContext(), "기록 중단", Toast.LENGTH_LONG).show();
                }
            }
        });

        BTHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    received.setText(readMessage);
                    if(saveflag == 1){
                        //String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        //String contents = now + " : "  + readMessage;
                        WriteFile(filename, readMessage);
                    }
                }
            }
        };
    }

    public void checkPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } //낮은 버전에서는 자동적으로 무시되고 높은 버전에서만 실행되는 코드이므로
        //빨간 밑줄이 있어도 컴파일 자체는 정상적으로 진행됩니다
    }

    void WriteFile(String filen, String content){
        File dir = MainActivity.this.getExternalFilesDir("/ardsaves");
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir.getAbsolutePath()+"/"+filen+".txt", true));
            //Toast.makeText(getApplicationContext(), "기록 중", Toast.LENGTH_SHORT);
            writer.write(content);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_SHORT);
        }
    }

    void bluetoothOn() {
        if(BTAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            if (BTAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
                status.setText("활성화");
            }
            else {
                Toast.makeText(getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }
    void bluetoothOff() {
        if (BTAdapter.isEnabled()) {
            BTAdapter.disable();
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            status.setText("비활성화");
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                    status.setText("활성화");
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                    status.setText("비활성화");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void pairedDev() {
        if (BTAdapter.isEnabled()) {
            pairedDevices = BTAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                listDevices = new ArrayList<String>();
                for (BluetoothDevice device :pairedDevices) {
                    listDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = listDevices.toArray(new CharSequence[listDevices.size()]);
                listDevices.toArray(new CharSequence[listDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : pairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                BTdevice = tempDevice;
                break;
            }
        }
        try {
            BTsocket = BTdevice.createRfcommSocketToServiceRecord(BT_UUID);
            BTsocket.connect();
            thread = new ConnectedBluetoothThread(BTsocket);
            thread.start();
            BTHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[10];
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
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
