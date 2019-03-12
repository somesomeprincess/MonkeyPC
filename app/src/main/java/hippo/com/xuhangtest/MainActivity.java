package hippo.com.xuhangtest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import hippo.com.xuhangtest.dao.XuhangPref;
import hippo.com.xuhangtest.db.DBManager;
import hippo.com.xuhangtest.db.DatabaseHelper;
import hippo.com.xuhangtest.module.PowerPercent;
import hippo.com.xuhangtest.service.GTLogo;
import hippo.com.xuhangtest.utils.CMDUtils2;
import hippo.com.xuhangtest.utils.ToastUtil;

public class MainActivity extends AppCompatActivity implements PowerBroadcastReceiver.MyListener,View.OnClickListener{
    // Android6.x之后，需要由用户明确授权的权限，放在MainActivity里提前做申请交互
    private static final int GETBATTDINGSHI = 1;
    private static final int GETBATTERY = 2;
    private static final int REQUEST_NEED_PERMISSION = 101;
    // 悬浮窗的权限是特殊权限，需要单独处理
    private static final int REQUEST_FLOAT_VIEW = 102;
    private static boolean isFloatViewAllowed = XuhangPref.getXuhangPref().getBoolean(XuhangPref.FLOAT_ALLOWED,false);
    String recordFile = "a.txt";
    String testPkg = "hippo.com.powerts";
    String testClass = "Wendingxing129Test";
    String testmtd = "testTakePic";
    private TextView tv;
    private TextView tv_tips;
    private EditText et_power;
    private Button btn_xuanfuqiu;
    private Button btn_liulanqi;
    PowerBroadcastReceiver receiver;
    BatteryManager manager;
    private Toast mToast;
    private boolean mIsStart = true;
    DBManager mDBManager;
    ContentResolver mResolver;
    final String AUTHORITY="hippo.com.xuhangtest.db.PowerProvider";
    final Uri POWERPERCENT_URI = Uri.parse("content://"+AUTHORITY+"/power");
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    showToast(getCurrent());
                    if (mIsStart) {
                        // 因为Toast.LENGTH_SHORT的默认值是2000
                        mHandler.sendEmptyMessageDelayed(0, 1900);

                    }
                    break;
                case GETBATTDINGSHI:
                    removeMessages(1);
                    String batterymsg=getCurrentTime()+" "+getCurrentBattery()+"dingshiqi\r\n";
                    //writeFile(recordFile,batterymsg);
                    writeToDB(getCurrentTime(),getCurrentBattery()+"","dingshiqi");
                    mHandler.sendEmptyMessageDelayed(1,120*1000);
                    break;
                case GETBATTERY:
                    int cutBattery = getCurrentBattery();
                    addData(et_power,cutBattery+"\r\n");
                    break;
                case 3:
                    String tip = (String) msg.obj;
                    tv_tips.setText("Power Path :"+tip);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        registPowerReceiver();
        //initDb();
        mDBManager = new DBManager(this);


        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        hasPermission = hasPermission && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        hasPermission = hasPermission && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);

        hasPermission = hasPermission && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE},
                    REQUEST_NEED_PERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_NEED_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权了就可以保存了，do nothing即可
                    // 接收了危险权限后，再弹出悬浮窗处理特殊权限，这样还可以避过ACTION_MANAGE_OVERLAY_PERMISSION不支持API23之前的问题
                    if (! isFloatViewAllowed)
                    {
                        requestAlertWindowPermission();
                    }
                    // 在授权后，需要将之前没权限创建的目录重新创建一次
                    //Env.init();
                } else {
                    ToastUtil.ShowLongToast(XuhangAPP.getContext(),
                            "Permission not enough. Please consider granting it this permission.");
                }
            }
        }

    }


    private void initDb() {
        DatabaseHelper dbHelper = new DatabaseHelper(this,"test_power");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
    }

    private void initView() {
        tv = findViewById(R.id.tv_power);
        et_power = findViewById(R.id.et_power);
        tv_tips = findViewById(R.id.tv_tip);
        btn_xuanfuqiu= findViewById(R.id.btn_xuanfuqiu);
        btn_xuanfuqiu.setOnClickListener(this);
    }

    public void requestRoot(String pkgpath){
        Process p = null;
        DataOutputStream os = null;
        String command = "chmod 777" + pkgpath;
        int status = -100;
        try {
            p = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
            Log.v("TAG",status+"----requestRoot");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                p.destroy();
            }
        }
    }

    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        try{
            startActivityForResult(intent, REQUEST_FLOAT_VIEW);
        }
        catch(Exception e)
        {
            // 有的定制系统会抛异常，这样的系统也不需要额外的悬浮窗授权
        }

    }

    public void registPowerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new PowerBroadcastReceiver();
        registerReceiver(receiver,filter);
        receiver.setBattReceiverListener(this);
    }

    public void onCleanClick(View v){
        cleanDB();
    }

    private void cleanDB() {
        mDBManager.droptable("power");
        Toast.makeText(MainActivity.this,"Clean Data",Toast.LENGTH_SHORT).show();
    }


    private void queryDB() {
        if(mResolver ==null){
            mResolver = getContentResolver();
        }
        Cursor cursor = mResolver.query(POWERPERCENT_URI,new String[]{"_id","power"},null,null,null);
        while (cursor.moveToNext()){
            Log.v("MainActivity",cursor.getString(1));
        }
        Log.v("MainActivity",cursor.getCount()+"");
        cursor.close();
    }

    public void onBtnClick(View v){
        int batt = getCurrentBattery();
        //et_power.setText(batt+"\n");
        addData(et_power,batt+"\n");
        new UiautomatorThread().start();

        removeFile(recordFile);
        mHandler.sendEmptyMessage(GETBATTDINGSHI);
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
            mToast = Toast.makeText(MainActivity.this, content+"handler", Toast.LENGTH_SHORT);
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
        int permission= ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
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
            Log.v(TAG,"Already write!!!!!!--MainActivity");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG,e.toString());
        }
        updateUITips(file.getAbsolutePath());
    }

    private void updateUITips(String s) {
        Message msg = Message.obtain();
        msg.obj=s;
        msg.what=3;
        mHandler.sendMessage(msg);
    }


    public void addData(EditText et,String str){
        int index = et.getSelectionStart();
        Editable editable = et.getText();
        editable.insert(index,str);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        mDBManager.closeDB();
    }

    private int getCurrentBattery(){
        manager = (BatteryManager) getApplicationContext().getSystemService(Context.BATTERY_SERVICE);
        int currentbatt = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return currentbatt;
    }



    @Override
    public void onListen(int level) {
        if(level<10){
            Toast.makeText(this,getCurrentTime()+level+"Broadcast",Toast.LENGTH_SHORT).show();
            //writeFile(recordFile,getCurrentTime()+" "+level+"Broadcast"+"\r\n");
            writeToDB(getCurrentTime(),level+"","Broadcast");
        }


        else if(level%10==0)
        {
            Toast.makeText(this,getCurrentTime()+level+"Broadcast",Toast.LENGTH_SHORT).show();
            //writeFile(recordFile,getCurrentTime()+" "+level+"Broadcast"+"\r\n");
            writeToDB(getCurrentTime(),level+"","Broadcast");
        }

    }

    private void writeToDB(String currentTime, String level,String addform){

        mResolver = getContentResolver();
        //mContext.getContentResolver().registerContentObserver(POWERPERCENT_URI,true,new PowerObserver(handler));
        //getContentResolver().registerContentObserver(POWERPERCENT_URI,true, new PowerObserver(null));

        ContentValues value=new ContentValues();
        PowerPercent p = new PowerPercent(currentTime,level,addform);
        value.put("time",p.curtime);
        value.put("power",p.powerpct);
        value.put("addform",p.addform);
        Uri uri= mResolver.insert(POWERPERCENT_URI,value);
        if(uri !=null){
            Log.v("TAG",uri.toString());
        }
    }


    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = df.format(new Date());
        return current;
    }

    public void onLiulanqiClick(View v){
        testClass = "LiuLanqi";
        new UiautomatorThread().start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_xuanfuqiu:
                Intent intent = new Intent(this, GTLogo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(this.getString(R.string.xuanfuqiuopen)==btn_xuanfuqiu.getText().toString()){
                    startService(intent);
                    btn_xuanfuqiu.setText(R.string.xuanfuqiuclose);
                }
                else {
                    stopService(intent);
                    btn_xuanfuqiu.setText(R.string.xuanfuqiuopen);
                }
                break;

        }
    }

    class UiautomatorThread extends Thread{
        @Override
        public void run() {
            super.run();

            //String cmd = generateCommand(testPkg,testClass,testmtd);
            String cmd = generateCommand(testPkg,testClass);
            CMDUtils2.CMD_Result rs = CMDUtils2.runCMD(cmd,true,true);
            Log.e("TAG",rs.success+rs.error);
            mHandler.sendEmptyMessage(GETBATTERY);
        }
//
//        public  String generateCommand(String pkgName, String clsName, String mtdName) {
//            String command = "am instrument -w -r  --user 0 -e debug false -e class "
//                    + pkgName + "." + clsName + "#" + mtdName + " "
//                    + pkgName + ".test/android.support.test.runner.AndroidJUnitRunner";
//            Log.e("test1: ", command);
//            return command;
//        }

        public  String generateCommand(String pkgName, String clsName) {
            String command = "am instrument -w -r  --user 0 -e debug false -e class "
                    + pkgName + "." + clsName + " "
                    + pkgName + ".test/android.support.test.runner.AndroidJUnitRunner";
            Log.e("test1: ", command);
            return command;
        }



    }
    public class PowerObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public PowerObserver(Handler handler) {
            super(handler);
        }
    }
}
