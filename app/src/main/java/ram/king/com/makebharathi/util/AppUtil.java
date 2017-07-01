package ram.king.com.makebharathi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class AppUtil {


    public AppUtil() {
    }

    public boolean isInternetConnected(Context theActivity){
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager)theActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }
}
