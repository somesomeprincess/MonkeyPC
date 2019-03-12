package hippo.com.powerts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public class CommonUtils {
    Context mContext;

    public CommonUtils() {
    }

    public static void setsleeptime(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CommonUtils(Context context) {
        mContext = context;
    }

    public boolean isNetworkOk(){
        ConnectivityManager connectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities cap = connectManager.getNetworkCapabilities(connectManager.getActiveNetwork());
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


}
