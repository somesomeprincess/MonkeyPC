package hippo.com.xuhangtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainPowerActivity extends AppCompatActivity implements PowerBroadcastReceiver.MyListener{
    String recordFile = "a.txt";
    PowerBroadcastReceiver receiver;
    private boolean mIsStart = true;
    private Toast mToast;
    BatteryManager manager;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    showToast(getCurrent());
                    if (mIsStart) {
                        // 因为Toast.LENGTH_SHORT的默认值是2000
                        mHandler.sendEmptyMessageDelayed(0, 1900);

                }
                break;
                case 1:
                    removeMessages(1);
                    String batterymsg=getCurrentTime()+" "+getCurrentBattery()+"\r\n";
                    writeFile(recordFile,batterymsg);
                    mHandler.sendEmptyMessageDelayed(1,120*1000);
                    break;
            }

        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mHandler.sendEmptyMessage(0);
        manager = (BatteryManager) getApplicationContext().getSystemService(Context.BATTERY_SERVICE);
        registPowerReceiver();
    }

    public void registPowerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new PowerBroadcastReceiver();
        registerReceiver(receiver,filter);
        receiver.setBattReceiverListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mIsStart = false;
    }
    private int getCurrentBattery(){
        //manager = (BatteryManager) getApplicationContext().getSystemService(Context.BATTERY_SERVICE);
        int currentbatt = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return currentbatt;
    }

    /**
     * 获取当前电流
     */
    private String getCurrent() {
        String result = "null";
        try {
            Class systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getDeclaredMethod("get", String.class);
            String platName = (String) get.invoke(null, "ro.hardware");
            if (platName.startsWith("mt") || platName.startsWith("MT")) {
                String filePath = "/sys/class/power_supply/battery/device/FG_Battery_CurrentConsumption";
                // MTK平台该值不区分充放电，都为负数，要想实现充放电电流增加广播监听充电状态即可
                result = "当前电流为：" + Math.round(getMeanCurrentVal(filePath, 5, 0) / 10.0f) + "mA";
                result += ", 电压为：" + readFile("/sys/class/power_supply/battery/batt_vol", 0) + "mV";
            } else if (platName.startsWith("qcom")) {
                String filePath ="/sys/class/power_supply/battery/current_now";
                int current = Math.round(getMeanCurrentVal(filePath, 5, 0) / 10.0f);
                int voltage = readFile("/sys/class/power_supply/battery/voltage_now", 0) / 1000;
                // 高通平台该值小于0时电池处于放电状态，大于0时处于充电状态
                if (current < 0) {
                    result = "充电电流为：" + (-current) + "mA, 电压为：" + voltage + "mV";
                } else {
                    result = "放电电流为：" + current + "mA, 电压为：" + voltage + "mV";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取平均电流值
     * 获取 filePath 文件 totalCount 次数的平均值，每次采样间隔 intervalMs 时间
     */
    private float getMeanCurrentVal(String filePath, int totalCount, int intervalMs) {
        float meanVal = 0.0f;
        if (totalCount <= 0) {
            return 0.0f;
        }
        for (int i = 0; i < totalCount; i++) {
            try {
                float f = Float.valueOf(readFile(filePath, 0));
                meanVal += f / totalCount;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (intervalMs <= 0) {
                continue;
            }
            try {
                Thread.sleep(intervalMs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return meanVal;
    }

    private int readFile(String path, int defaultValue) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(
                    path));
            int i = Integer.parseInt(bufferedReader.readLine(), 10);
            bufferedReader.close();
            return i;
        } catch (Exception localException) {
        }
        return defaultValue;
    }

    private void showToast(String content) {
        if (mToast == null) {
            mToast = Toast.makeText(MainPowerActivity.this, content+"handler", Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
    public void removeFile(String filename){
        File sdcard=Environment.getExternalStorageDirectory();
        File file=new File(sdcard,filename);
        if(file.exists())
            file.delete();
    }

    public void writeFile(String fileName,String writestr) {
        int REQUEST_EXTERNAL_STORAGE =1;
        String[] PERMISSIONS_STORAGE={
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission=ActivityCompat.checkSelfPermission(MainPowerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainPowerActivity.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
        String TAG="File--";
        File file = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdcardDir = Environment.getExternalStorageDirectory();
            file = new File(sdcardDir,fileName);
            Log.v(TAG,file.getAbsolutePath());
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Log.v(TAG,"no SD Card dir");
            return;
        }

        try {
            //FileOutputStream fout = openFileOutput("fi",MODE_APPEND);
            FileOutputStream fout = new FileOutputStream(file,true);
            byte [] bytes = writestr.getBytes();
            fout.write(bytes);
            fout.close();
            Log.v(TAG,"Already write!!!!!!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG,e.toString());
        }
        finally {

        }

    }

    public void onbtnclick(View v){
        //writeFile("aaa");
        removeFile(recordFile);
        mHandler.sendEmptyMessage(1);
    }


    @Override
    public void onListen(int level) {
        if(level<10){
            Toast.makeText(this,getCurrentTime()+level+"Broadcast",Toast.LENGTH_SHORT).show();
            writeFile(recordFile,getCurrentTime()+" "+level+"Broadcast"+"\r\n");
        }


        else if(level%10==0)
        {
            Toast.makeText(this,getCurrentTime()+level+"Broadcast",Toast.LENGTH_SHORT).show();
            writeFile(recordFile,getCurrentTime()+" "+level+"Broadcast"+"\r\n");
        }

    }


    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = df.format(new Date());
        return current;
    }

}
