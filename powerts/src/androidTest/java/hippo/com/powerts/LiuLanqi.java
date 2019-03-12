package hippo.com.powerts;


import android.content.Context;
import android.os.BatteryManager;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
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


public class LiuLanqi extends BaseClass{
    @Test
    public void testLiulanQi(){
        openAppByText("浏览器");
        waitByRes("sogou.mobile.explorer.OS360:id/abe").click();
        waitByText("搜索或输入网址").click();
        setsleeptime(2000);
        waitByText("搜索或输入网址").setText("http://m.sohu.com");
        mDevice.pressEnter();
        setsleeptime(5000);
        for (int i=0 ;i<100;i++){
            waitByRes("indexFocus").swipe(Direction.DOWN,1.0f,500);
            setsleeptime(5000);
        }
    }
}
