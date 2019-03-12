package hippo.com.powerts;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.RequiresDevice;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiWatcher;
import android.support.test.uiautomator.Until;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class YoukuTest {
    //MainActivity mActivity;
    //public final int HALF_HOUR = 30*60*1000;
    public final int HALF_HOUR = 10*1000;
    public final int TEN_MINUTE = 10*1000;
    public final int TAKE_PIC_COUNT = 1;
    static UiDevice mDevice;
    static Context mContext;
    static Context mTargetContext;
    private long timeout = 5000l;
    String youku_pkg = GlobalConst.PKG_YOUKU;
    private final String TAG_POWER="TAG_POWER";
    static BatteryManager manager;
    static int width;
    static int height;
    public final String TAG="File--";
    String filename = "a.txt";

    @Rule
    public RepeatRule rule = new RepeatRule();
    @BeforeClass
    public static void setUpClass(){

//        mActivity = InstrumentationRegistry.getInstrumentation().startActivitySync();
//        mActivity.findViewById(R.id.tv_power);
        mTargetContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        //mDevice.pressHome();
        mContext = InstrumentationRegistry.getContext();
        manager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
        try {
            if(!mDevice.isScreenOn())
                mDevice.wakeUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        height=mDevice.getDisplayHeight();
        width = mDevice.getDisplayWidth();

    }

    @Before
    public void setUp(){
        try {
            Thread.sleep(1000);
            //SystemClock.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"setUp Start");
        writeFile(filename,getCurrentTime()+" "+getCurrPower()+"set up\r\n");
        mDevice.pressHome();

    };



    //@Test
    public void test2(){
        Log.v(TAG,mDevice.getCurrentPackageName());
        Log.v(TAG,"test2Start");
        UiObject2 object2=mDevice.wait(Until.findObject(By.res("chrome")),timeout);
        object2.click();
        Log.v(TAG,"STop");
    }
    @SmallTest
    //@Test
    public void testKillApp(){
        try {
            mDevice.pressRecentApps();
            mDevice.waitForIdle();
            Thread.sleep(2000);
            //swipeToScreenLeft();
            swipeLeftKillapp();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testMusic(){
        waitByDesc("应用").click();
        openAppByText("Play 音乐");
        UiObject2 obj = waitByRes("com.google.android.music:id/play_pause_header");
        if(obj!=null){
            obj.click();
            try {
                mDevice.sleep();

                setsleeptime(HALF_HOUR);
                if(!mDevice.isScreenOn()){
                    mDevice.wakeUp();
                }
            obj.click();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
    @Test
    public void tapText() {
       WeChatAction a = new WeChatAction(mDevice);
       a.sendYuyinMsg();


    }


    @Test
    public void testWeChat(){
        WeChatAction mWeChatAction= new WeChatAction(mDevice);
        waitByDesc("应用").click();
        waitByText("微信").click();
        mDevice.waitForIdle();
        //openAppByText("微信");
        mWeChatAction.talktoMySelf();
        waitByRes("com.tencent.mm:id/aak").click();
        mDevice.waitForIdle();
        for(int i =0;i<5;i++){
            mWeChatAction.sendTakePicMsg();
            setsleeptime(10*1000);
        }
        for(int j = 0;j<10;j++){
            mWeChatAction.sendTextMsg();
            setsleeptime(10*1000);
        }


    }

    private UiObject2 waitByDesc(String res) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.desc(res)),timeout);
        return object2;
    }

    private UiObject2 waitByText(String text) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.text(text)),timeout);
        return object2;
    }

    private UiObject2 waitByRes(String res) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.res(res)),timeout);
        return object2;
    }

    private void openAppByText(String text) {
        mDevice.findObject(By.text(text)).click();

    }

    @Test
    public void testTakePic(){
        Log.v(TAG,"相机");
        waitByText("相机").click();
        //mDevice.findObject(By.text("Camera")).click();
        UiObject2 object2 =mDevice.wait(Until.findObject(By.res("com.android.camera2:id/photo_video_button")),timeout);
        for(int i =0;i<TAKE_PIC_COUNT;i++) {
            object2.click();
            setsleeptime(5*1000);
        }
        Log.v(TAG,getCurrPower()+"");
    }

    @Test
    public void testTakePicinCase(){
        for (int i =0;i<3;i++)
            testTakePic();
    }

    @Test
    public void testChrome(){
        Log.v(TAG,mDevice.getCurrentPackageName());
        Log.v(TAG,"testChrome Start");
        UiObject2 object2=mDevice.wait(Until.findObject(By.res("chrome")),timeout);
        object2.click();
        Log.v(TAG,"STop");
    }

    private void swipeToScreenLeft(){
        mDevice.swipe(width/2,height/2,width/1000,height/2,10);
    }
    private void swipeLeftKillapp(){
        mDevice.swipe(800,1700,8,1700,10);
    }
    private void swipePullDown(){
        mDevice.swipe(width/2,height/5,width/2,height/2,20);
    }



    @Test
    public void testTakeRecord(){
        Log.v(TAG,"takcRecord");
        mDevice.findObject(By.text("相机")).click();
        mDevice.wait(Until.findObject(By.res("com.android.camera2:id/photo_video_button")),timeout);
        UiObject2 object2 =mDevice.findObject(By.res("com.android.camera2:id/photo_video_button"));
        if(!"拍摄视频".equals(object2.getContentDescription())){
            mDevice.swipe(width/2,height/2,width/10,height/2,20);
        }
        object2.click();

        setsleeptime(10*1000);
        object2.click();
//        for(int i =0;i<15;i++) {
//            object2.click();
//            setsleeptime(30*1000);
//        }
        Log.v(TAG,getCurrPower()+"");
    }
    @Test
    public void testYouku(){
        mDevice.registerWatcher("youku", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                //好评 更新
                return false;
            }
        });
        doyoukutest();
        Log.v(TAG,getCurrPower()+"");

    }


    @Test
    public void testWeibo(){
        String weibo_pkg = GlobalConst.PKG_WEIBO;

        doweibotest(weibo_pkg);

        Log.v(TAG,getCurrPower()+"");

    }
    //@Test@Repeat(times =3)
    public void doweibotest(String pkg){
        startApp(pkg);
        Log.v(TAG,"current");
        mDevice.waitForWindowUpdate(pkg,5000);

        Log.v(TAG,"current");
        setsleeptime(2000);
        swipePullDown();
        lookWeibo();
        setsleeptime(TEN_MINUTE);
    }

    public void lookWeibo(){
        for(int i=0;i<5;i++){
            mDevice.swipe(width / 2, height / 2, width / 2, height / 6, 10);
            //mDevice.wait(Until.findObject(By.res("com.sina.weibo:id/lvUser")), timeout);
            UiObject2 object2 = mDevice.findObject(By.res("com.sina.weibo:id/lvUser")).getChildren().get(0);
            object2.click();
            setsleeptime(5000);
            mDevice.pressBack();
        }
    }

    public int getCurrPower(){
        int currentbatt = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.v(TAG_POWER,currentbatt+"");
        return currentbatt;
    }
    public void doyoukutest(){
        Log.v(TAG_POWER,"start record current power!");
        writeFile(filename,getCurrPower()+"doyoukutest\r\n");
        //getCurrPower();
        String btn_play = "com.youku.phone:id/item_b_program_info_play";
        String btn_click_search = "com.youku.phone:id/home_tool_bar_new";
        startApp(youku_pkg);
        //mDevice.findObject(By.pkg("com.youku.phone")).click();
        mDevice.waitForIdle(timeout);
        mDevice.wait(Until.findObject(By.res(btn_click_search)),timeout);
        mDevice.findObject(By.res("com.youku.phone:id/home_tool_bar_new")).click();
        //UiObject2 search = mDevice.findObject(By.res("com.youku.phone:id/et_widget_search_text_soku"));
        UiObject2 searchbtn = mDevice.wait(Until.findObject(By.res("com.youku.phone:id/et_widget_search_text_soku")),timeout);
        Log.v("Test",searchbtn!=null?"":"Null!!"+"searchbtn");
        if(searchbtn!=null)
            searchbtn.click();

        searchbtn.setText("天将雄狮");
        mDevice.findObject(By.res("com.youku.phone:id/tv_right")).click();
        mDevice.wait(Until.findObject(By.res(btn_play)),timeout);
        mDevice.findObject(By.res(btn_play)).click();
        setsleeptime(HALF_HOUR);
//        try {
//            mDevice.executeShellCommand("am force-stop "+youku_pkg );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //getCurrPower();
        writeFile(filename,getCurrentTime()+getCurrPower()+"doyoukutest\r\n");
    }

    public void startApp(String pkg){
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkg);
        mContext.startActivity(intent);
    }

    public void setsleeptime(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
//    @After
//    public void teardown() throws IOException {
//        mDevice.executeShellCommand("am force-stop "+youku_pkg );
//    }

    @After
    public void teardown(){
        testKillApp();
        writeFile(filename,getCurrentTime()+" "+getCurrPower()+"tear down\r\n");
        Log.v(TAG,"teardown call!");
    }

    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = df.format(new Date());
        return current;
    }

    public void writeFile(String fileName,String writestr) {
        int REQUEST_EXTERNAL_STORAGE =1;
        String[] PERMISSIONS_STORAGE={
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission= ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission3= ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission!= PackageManager.PERMISSION_GRANTED ||permission3!= PackageManager.PERMISSION_GRANTED){
            //ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            try {
                //String pkgtarget = mTargetContext.getPackageName();
                String pkg = mContext.getPackageName();
                //mDevice.executeShellCommand("pm grant "+pkgtarget+" android.permission.WRITE_EXTERNAL_STORAGE");
                mDevice.executeShellCommand("pm grant "+pkg+" android.permission.WRITE_EXTERNAL_STORAGE");
                mDevice.executeShellCommand("pm grant "+pkg+" android.permission.READ_EXTERNAL_STORAGE");
                //mDevice.executeShellCommand("pm grant "+pkgtarget+" android.permission.READ_EXTERNAL_STORAGE");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int permission2= ActivityCompat.checkSelfPermission(mTargetContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            Log.v("TAG",""+permission2);
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

    }
}
