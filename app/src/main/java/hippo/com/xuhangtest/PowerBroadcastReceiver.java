package hippo.com.xuhangtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class PowerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            mMyListener.onListen(level);
        }


    }
    MyListener mMyListener;
    public interface MyListener{
        public void onListen(int level);
    }
    public void setBattReceiverListener(MyListener listener){
        mMyListener = listener;
    }
}
