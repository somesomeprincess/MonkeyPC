package hippo.com.powerts;

import android.content.Context;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static android.content.ContentValues.TAG;

@RunWith(Parameterized.class)
public class BaseClass {

    @Parameterized.Parameters
    public static Object[][] data(){return new Object[10][0];}


    public final int HALF_HOUR = 30*15*1000;
    public final int TEN_MINUTE = 10*60*1000;
    public final int TAKE_PIC_COUNT = 20;
    public final int WEIBO_COUNT = 30;
    static UiDevice mDevice;
    static Context mContext;
    static Context mTargetContext;
    private long timeout = 5000l;
    private final String TAG_POWER="TAG_POWER";
    static int width;
    static int height;
    @Rule
    public TestName names = new TestName();
    @Rule
    public RepeatRule rule = new RepeatRule();

    @BeforeClass
    public static void setUpClass(){
        mTargetContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mContext = InstrumentationRegistry.getContext();
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
        setsleeptime(1000);
        Log.v(TAG,"setUp Start");
        boolean home = mDevice.pressHome();
        Log.v(TAG,"setup----"+names.getMethodName()+home);
    }

    public void setsleeptime(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @After
    public void teardown(){
        Log.v(TAG,"teardown---"+names.getMethodName());
        testKillApp();
        //writeFile(filename,getCurrentTime()+" "+getCurrPower()+"tear down\r\n");
        Log.v(TAG,"teardown call!");
    }

    public UiObject2 waitByRes(String res) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.res(res)),timeout);
        return object2;
    }

    public void openAppByText(String text) {
        waitByText(text).click();

    }
    public UiObject2 waitByText(String text) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.text(text)),timeout);
        return object2;
    }

    public void testKillApp(){
        try {
            mDevice.pressRecentApps();
            mDevice.waitForIdle();
            Thread.sleep(2000);
            //swipeToScreenLeft();
            //swipeLeftKillapp();
            mDevice.findObject(By.res("com.android.launcher3:id/clear_all_button")).click();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
