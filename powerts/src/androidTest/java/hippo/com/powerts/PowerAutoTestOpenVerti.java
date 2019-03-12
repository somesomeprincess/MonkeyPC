package hippo.com.powerts;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiWatcher;
import android.support.test.uiautomator.Until;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;

//@RunWith(AndroidJUnit4.class)
@RunWith(Parameterized.class)
public class PowerAutoTestOpenVerti {

    @Parameterized.Parameters
    public static Object[][] data(){
        return new Object[10][0];
    }
    //MainActivity mActivity;
    //public final int HALF_HOUR = 30*60*1000;
    public final String TAG="PowerAutoTest";
    public final int HALF_HOUR = 30*15*1000;
    public final int TEN_MINUTE = 10*60*1000;
    public final int TAKE_PIC_COUNT = 20;
    public final int WEIBO_COUNT = 30;
    public final int INTERVAL = 5*1000;
    static UiDevice mDevice;
    static Context mContext;
    static Context mTargetContext;
    private long timeout = 5000l;
    private final String TAG_POWER="TAG_POWER";
    static BatteryManager manager;
    static int width;
    static int height;

    static ContentResolver mResolver;
    private static final String AUTHORITY="hippo.com.xuhangtest.db.PowerProvider";
    private static final Uri POWERPERCENT_URI = Uri.parse("content://"+AUTHORITY+"/power");
//    public static Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            requery();
//        }
//    };


    @Rule
    public TestName names = new TestName();
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
        mResolver = mContext.getContentResolver();
        //mContext.getContentResolver().registerContentObserver(POWERPERCENT_URI,true,new PowerObserver(handler));
        mContext.getContentResolver().registerContentObserver(POWERPERCENT_URI,true, new PowerObserver(null));

    }

    @Before
    public void setUp(){
        setsleeptime(1000);
        Log.v(TAG,"setUp Start");
        //writeFile(filename,getCurrentTime()+" "+getCurrPower()+"set up\r\n");
        insertIntoDB( getPercentInstance(getCurrentTime(),getCurrPower()+"",names.getMethodName()+" Start"));
        boolean home = mDevice.pressHome();
        Log.v(TAG,"setup----"+names.getMethodName()+home);
    }

    private void insertIntoDB(Object percent) {
        try {
            String ftime = (String) percent.getClass().getField("curtime").get(percent);
            String faddform = (String) percent.getClass().getField("addform").get(percent);
            String fpercent = (String) percent.getClass().getField("powerpct").get(percent);
            ContentValues value=new ContentValues();
            value.put("time",ftime);
            value.put("power",fpercent);
            value.put("addform",faddform);
            mResolver.insert(POWERPERCENT_URI,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getPercentInstance(String time,String percent,String addform){
        try {
//            Class<?> c2=mContext.getClassLoader().loadClass("hippo.com.xuhangtest.module.PowerPercent");
//            Class<?> c = Class.forName("java.lang.String");
            Class<?> cls = Class.forName("hippo.com.powerts.module.PowerPercent");
            Constructor<?> cons = cls.getConstructor(String.class,String.class,String.class);

            Object p = cons.newInstance(time,percent,addform);
            return p;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //@Test
    public void test2(){
        Log.v(TAG,mDevice.getCurrentPackageName());
        Log.v(TAG,"test2Start");
        UiObject2 object2=mDevice.wait(Until.findObject(By.res("chrome")),timeout);
        object2.click();
        Log.v(TAG,"STop");
    }

    @Test
    public void testKillApp(){
        try {
            mDevice.pressRecentApps();
            mDevice.waitForIdle();
            Thread.sleep(2000);
            swipeUpKillapp();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void kill_all_app(){
        try {
            mDevice.pressRecentApps();
            mDevice.waitForIdle();
            Thread.sleep(2000);
            mDevice.findObject(By.res("com.android.launcher3:id/clear_all_button")).click();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testMusic(){
        //waitByDesc("应用").click();
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

    //@Test
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

    @Test
    public void testTaobao(){
        waitByText("手机淘宝").click();
        setsleeptime(2000);
        swipePullDown();
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
        waitByText(text).click();

    }

    //OK
    @Test
    public void testTakePic(){
        Log.v(TAG,"相机");
        waitByText("相机").click();
        //mDevice.findObject(By.text("Camera")).click();
        String id_kuaimen = "org.codeaurora.snapcam:id/shutter_button";
        UiObject2 object2 =mDevice.wait(Until.findObject(By.res(id_kuaimen)),timeout);
        for(int i =0;i<TAKE_PIC_COUNT;i++) {
            object2.click();
            setsleeptime(INTERVAL);
        }
        Log.v(TAG,getCurrPower()+"");
    }

    //@Test
    public void testTakePicinCase(){
        for (int i =0;i<3;i++)
            testTakePic();
    }

    //@Test
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

    public void swipePullDown(){
        mDevice.swipe(width/2,height/5,width/2,height/2,20);
    }
    private void swipeLeftKillapp(){
        mDevice.swipe(800,1700,8,1700,10);
    }
    private void swipeUpKillapp(){
        mDevice.swipe((int) (0.7*width), (int) (0.7*height),(int) (0.7*width),height/2000,10);
    }

    //@Test
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

    //@Test
    public void testTrigger(){
        mDevice.registerWatcher("weibo", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                Log.v("watcher","checkForCondition");
                if(mDevice.hasObject(By.text("以后再说"))){
                    UiObject2 btn = mDevice.findObject(By.text("以后再说"));
                    btn.click();
                    return true;
                }

                return false;
            }
        });
        mDevice.findObject(By.text("微博")).click();
        Log.v("watcher","hasAnyWatcherTriggered "+mDevice.hasAnyWatcherTriggered());

    }

    @Test
    public void testWeibo(){
        addWeiboWatcher();
        String weibo_pkg = GlobalConst.PKG_WEIBO;
        doweibotest(weibo_pkg);
        Log.v(TAG,getCurrPower()+"");
    }

    public void doweibotest(String pkg){
        swipeToScreenLeft();
        startAppWithName("微博");
        mDevice.waitForWindowUpdate(pkg,INTERVAL);
        Log.v(TAG,"waitForWindowUpdate");
        mDevice.waitForIdle();
        Log.v(TAG,"waitForIdle");
        WeiboAction wbAction = new WeiboAction(mDevice,WEIBO_COUNT);
        Log.v(TAG,"sleep 2000 & clickHome");
        setsleeptime(2000);
        wbAction.clickHome();
        Log.v(TAG,"clickHome");
        setsleeptime(2000);
        wbAction.swipePullDown();
        Log.v(TAG,"swipePullDown");
        removeWatcher("weibo");
        wbAction.lookWeibo();
        setsleeptime(TEN_MINUTE);
    }

    @Test
    public void testWB(){
        swipeToScreenLeft();
        startAppWithName("微博");
        mDevice.waitForIdle();
        waitByDesc("微博").click();

    }

    public void removeWatcher(String name){
        mDevice.removeWatcher(name);
    }

    public void addWeiboWatcher(){
        mDevice.registerWatcher("weibo", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                Log.v(TAG,"checkForCondition");
                if(mDevice.hasObject(By.text("以后再说"))){
                    UiObject2 btn = mDevice.findObject(By.text("以后再说"));
                    btn.click();
                    return true;
                }
                else if(mDevice.hasObject(By.text("给我们评分"))){
                    waitByText("不了，谢谢").click();

                    removeWatcher("weibo");
                    return true;
                }
                return false;
            }
        });
    }

    public int getCurrPower(){
        int currentbatt = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.v(TAG_POWER,currentbatt+"");
        return currentbatt;
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
    }

    public void doyoukutest(){
        Log.v(TAG_POWER,"start record current power!");
        String btn_play = "com.youku.phone:id/item_b_program_info_play";
        String btn_click_search = "com.youku.phone:id/home_tool_bar_new";
        //startAppWithIntent(GlobalConst.PKG_YOUKU);
        openAppByText("优酷视频");
        mDevice.waitForIdle(timeout);
        waitByRes(btn_click_search).click();
        UiObject2 searchbtn = mDevice.wait(Until.findObject(By.res("com.youku.phone:id/et_widget_search_text_soku")),timeout);
        Log.v("Test",searchbtn!=null?"":"Null!!"+"searchbtn");
        if(searchbtn!=null)
            searchbtn.click();
        searchbtn.setText("天将雄狮");
        waitByRes("com.youku.phone:id/tv_right").click();
        //mDevice.findObject(By.res("com.youku.phone:id/tv_right")).click();
        waitByRes(btn_play).click();
        setsleeptime(HALF_HOUR);
    }

    public void startAppWithIntent(String pkg){
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkg);
        mContext.startActivity(intent);

        Log.v(TAG,"startAppWithIntent");
    }

    public void startAppWithName(String name){
        waitByText(name).click();
        mDevice.waitForIdle();
    }

    public void setsleeptime(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void tapText() {
        WeChatAction a = new WeChatAction(mDevice);
        a.sendYuyinMsg();
    }
    @After
    public void teardown(){
        Log.v(TAG,"teardown---"+names.getMethodName());
        testKillApp();
        //writeFile(filename,getCurrentTime()+" "+getCurrPower()+"tear down\r\n");
        insertIntoDB( getPercentInstance(getCurrentTime(),getCurrPower()+"",names.getMethodName()+" End"));
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
