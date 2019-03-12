package hippo.com.powerts;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class YoukuTestXunhuan {
    //MainActivity mActivity;
    static UiDevice mDevice;
    static Context mContext;
    static Context mTargetContext;
    private long timeout = 2000l;
    String youku_pkg = "com.youku.phone";
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"setUp Start");
        writeFile(filename,getCurrPower()+"\r\n");
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
    public void testTakePic(){
        Log.v(TAG,"takcpic");
        mDevice.pressHome();
        mDevice.findObject(By.text("相机")).click();
        mDevice.wait(Until.findObject(By.res("com.android.camera2:id/photo_video_button")),timeout);
        UiObject2 object2 =mDevice.findObject(By.res("com.android.camera2:id/photo_video_button"));
        for(int i =0;i<5;i++) {
            object2.click();
            setsleeptime(5*1000);
        }
        Log.v(TAG,getCurrPower()+"");
    }

    @Test
    public void testChrome(){
        mDevice.pressHome();
        Log.v(TAG,mDevice.getCurrentPackageName());
        Log.v(TAG,"testChrome Start");
        UiObject2 object2=mDevice.wait(Until.findObject(By.res("chrome")),timeout);
        object2.click();
        Log.v(TAG,"STop");
    }



    @Test
    public void testTakeRecord(){
        Log.v(TAG,"takcRecord");
        mDevice.pressHome();
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
        doyoukutest();
        Log.v(TAG,getCurrPower()+"");

    }

    @Ignore
    @Test
    public void testWeibo(){
        String weibo_pkg = "com.sina.weibo";

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
        mDevice.swipe(width/2,height/5,width/2,height/2,20);
        lookWeibo();
        setsleeptime(10*60*1000);
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
        setsleeptime(12000);
        try {
            mDevice.executeShellCommand("am force-stop "+youku_pkg );
        } catch (IOException e) {
            e.printStackTrace();
        }
        //getCurrPower();
        writeFile(filename,getCurrPower()+"doyoukutest\r\n");
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

    public void youku2(){
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
//    @After
//    public void teardown() throws IOException {
//        mDevice.executeShellCommand("am force-stop "+youku_pkg );
//    }

    @Test@Repeat(times = 2)
    public void testXunhuan(){
        Log.v(TAG,"testXunhuan!");
        testTakePic();
        testYouku();
        Log.v(TAG,"testXunhuan END!");
    }

    @After
    public void teardown(){
        writeFile(filename,getCurrPower()+"\r\n");
        Log.v(TAG,"teardown call!");
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
