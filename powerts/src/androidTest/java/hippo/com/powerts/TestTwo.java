package hippo.com.powerts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class TestTwo {
    private String pkgName_Huawei ="com.google.android.GoogleCamera";
    private String pkgName2="com.android.camera";
    static UiDevice mDevice;
    Context mContext;
    Context mTargetContext;
    @Rule
    public RepeatRule rule = new RepeatRule();

    @BeforeClass
    public static void beforeClass(){
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    }
    @Before
    public void before(){
        mContext = InstrumentationRegistry.getContext();
        mTargetContext = InstrumentationRegistry.getTargetContext();
        mDevice.pressHome();
    }

    @After
    public void after(){

        closeapp(mDevice, pkgName_Huawei);
    }

    @Test
    public void caseContact(){
        String pkg_Royole = "com.android.contacts";
        try {
            if(!mDevice.isScreenOn()){
                mDevice.wakeUp();
            }

        startapp(pkg_Royole);
        mDevice.waitForWindowUpdate(pkg_Royole,5*2000);

        mDevice.sleep();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test@Repeat(times = 3)
    public void case11(){
        try {
            if(!mDevice.isScreenOn()){
                mDevice.wakeUp();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        startapp(pkgName_Huawei);
        mDevice.waitForWindowUpdate(pkgName_Huawei,5*2000);
//        CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            cameraManager.getCameraIdList();
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        cameraManager.getCameraCharacteristics();
//

    }
    @Test
    public void case1(){
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void case2(){
        startapp(pkgName_Huawei);

    }
    @Test
    public void case3(){
        Log.v("File --","this is case3");
        try {
            Thread.sleep(60*1000);
            writeFile("a.txt","this is Tese case\r\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void case4(){
        try {
//            Class cls = Class.forName("hippo.com.xuhangtest.MainActivity");
//            Method mthd = cls.getDeclaredMethod("writeFile",String.class,String.class);
//            //mthd.invoke();
//            Field field = cls.getDeclaredField("recordFile");
            Class cls = Class.forName("hippo.com.xuhangtest.UtilDemo");
            String a = cls.getName();
            Log.v("Case4",a);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Case4",e.toString());
        }

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
                String pkgtarget = mTargetContext.getPackageName();
                String pkg = mContext.getPackageName();
                mDevice.executeShellCommand("pm grant "+pkgtarget+" android.permission.WRITE_EXTERNAL_STORAGE");
                mDevice.executeShellCommand("pm grant "+pkg+" android.permission.WRITE_EXTERNAL_STORAGE");
                mDevice.executeShellCommand("pm grant "+pkg+" android.permission.READ_EXTERNAL_STORAGE");
                mDevice.executeShellCommand("pm grant "+pkgtarget+" android.permission.READ_EXTERNAL_STORAGE");
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
            Log.v(TAG,"Already write!!!!!!--TestTwo");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG,e.toString());
        }

    }

    private void startapp(String pkg){
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkg);
        mContext.startActivity(intent);

    }

    private void closeapp(UiDevice device,String pkg){
        try {
            device.executeShellCommand("am force-stop "+pkg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
