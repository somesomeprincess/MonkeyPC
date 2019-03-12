package hippo.com.powerts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;

//@RunWith(Parameterized.class)
@RunWith(Parameterized.class)
@Suite.SuiteClasses({DemoTest1.class,DemoTest1.class})
public class XuhangTestPara {
    @Parameterized.Parameters
    public static Object[][] data(){
        return new Object[10][0];
    }

    @Test
    public void test1(){
        DemoTest1 d = new DemoTest1();


    }

    @Test
    public void test2(){
        DemoTest1 d = new DemoTest1();


    }
}
