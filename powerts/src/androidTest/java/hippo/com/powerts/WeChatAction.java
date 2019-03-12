package hippo.com.powerts;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.view.KeyEvent;

import java.util.Random;

import static hippo.com.powerts.CommonUtils.setsleeptime;

public class WeChatAction {

    UiDevice mDevice;
    long timeout = 2000l;

    public WeChatAction(UiDevice device) {
        mDevice = device;
    }

    public void talktoMySelf() {
        UiObject2 me =waitByText("我");
        mDevice.waitForIdle();
        me.click();
        mDevice.waitForIdle();
        UiObject2 myname = waitByRes("com.tencent.mm:id/cba");
        mDevice.waitForIdle();
        waitByText("我").click();
        mDevice.waitForIdle();
        waitByText("通讯录").click();
        mDevice.waitForIdle();
        String tx = myname.getText();
        waitByText(tx).click();
        mDevice.waitForIdle();
        waitByText("发消息").click();
        mDevice.waitForIdle();
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
    public void sendTakePicMsg() {

        waitByText("拍摄").click();
        //shot
        mDevice.waitForIdle();
        waitByRes("com.tencent.mm:id/cad").click();
        setsleeptime(2000);
        mDevice.waitForIdle();
        // pic select
        waitByRes("com.tencent.mm:id/vd").click();
        setsleeptime(10000);
    }
    public void sendTextMsg() {

        UiObject2 input=mDevice.wait(Until.findObject(By.clazz("android.widget.EditText")),timeout);
        input.click();
        setsleeptime(2000);

        generateText();
        setsleeptime(2000);
        waitByText("发送").click();

    }

    public void generateText(){

        int MAX_LETTER = 12;
        int MIN_LETTER = 1;
        Random random = new Random();
        //int randNumber =rand.nextInt(MAX - MIN + 1) + MIN;
        //(54-29+1)
        int totalletter = random.nextInt(MAX_LETTER-MIN_LETTER+1)+MIN_LETTER;
        //29-54 A-Z
        for (int i =0;i<totalletter;i++){
            int singlelettCnt = random.nextInt(6);
            for (int j =0;j<singlelettCnt;j++){
                int singleletter =random.nextInt(24)+29;
                mDevice.pressKeyCode(singleletter);
            }
            mDevice.pressKeyCode(KeyEvent.KEYCODE_SPACE);
            //setsleeptime(5000);
            mDevice.pressKeyCode(KeyEvent.KEYCODE_ENTER);

        }
    }

    public void sendYuyinMsg(){
        waitByDesc("切换到按住说话").click();
        mDevice.waitForIdle();
        UiObject2 speak = waitByDesc("按住说话");
        speak.longClick();
        setsleeptime(3000);
    }
}
