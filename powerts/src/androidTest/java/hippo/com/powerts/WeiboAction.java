package hippo.com.powerts;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.view.KeyEvent;

import java.util.Random;

import static hippo.com.powerts.CommonUtils.setsleeptime;
import static hippo.com.powerts.PowerAutoTest.height;
import static hippo.com.powerts.PowerAutoTest.width;

public class WeiboAction {

    UiDevice mDevice;
    long timeout = 2000l;
    int CountlookWeibo = 5;

    public WeiboAction(UiDevice device) {
        mDevice = device;
    }
    public WeiboAction(UiDevice device,int count) {
        mDevice = device;
        CountlookWeibo = count;
    }
    public void clickHome() {
//        UiObject2 button_bar =mDevice.wait(Until.findObject(By.res("com.sina.weibo:id/main_radio")),timeout);
//        UiObject2 home = button_bar.findObjects(By.clazz("android.view.View")).get(0);
        UiObject2 home = mDevice.findObject(By.desc("微博"));
        home.click();
    }

    public UiObject2 waitByDesc(String res) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.desc(res)),timeout);
        return object2;
    }

    public UiObject2 waitByText(String text) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.text(text)),timeout);
        return object2;
    }

    public UiObject2 waitByRes(String res) {
        UiObject2 object2 =mDevice.wait(Until.findObject(By.res(res)),timeout);
        return object2;
    }

    public void lookWeibo(){
        for(int i=0;i<CountlookWeibo;i++){
            swipePushUp();
            //mDevice.wait(Until.findObject(By.res("com.sina.weibo:id/lvUser")), timeout);
            mDevice.waitForIdle();
            UiObject2 object2 = mDevice.findObject(By.res("com.sina.weibo:id/lvUser")).getChildren().get(0);
            object2.click();
            mDevice.waitForIdle();
            setsleeptime(5000);
            mDevice.pressBack();
            swipePushUp();
            mDevice.waitForIdle();
        }
    }
    public void swipePushUp(){
        mDevice.swipe(width / 2, height / 2, width / 2, height / 100, 10);
    }

    public void swipePullDown(){
        mDevice.swipe(width/2,height/5,width/2,height/2,20);
    }
}
