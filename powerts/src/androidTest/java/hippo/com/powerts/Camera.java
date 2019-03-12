package hippo.com.powerts;


import android.support.test.uiautomator.Direction;

import org.junit.Test;


public class Camera extends BaseClass{
    @Test
    public void testCamera(){
        openAppByText("相机");
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
