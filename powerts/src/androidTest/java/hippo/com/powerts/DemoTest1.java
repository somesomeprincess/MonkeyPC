package hippo.com.powerts;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DemoTest1 {
    @Test
    public void testPrint(){
        Log.v("testPrint","testPrint");
        System.out.print("testPrint");
    }
    @Test
    public void testJump(){
        Log.v("testPrint","testPrint");
        System.out.print("testPrint");
    }
}
