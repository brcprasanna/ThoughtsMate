package ram.king.com.makebharathi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ram.king.com.makebharathi.R;


public class AppUtil {


    public static boolean isInternetConnected(Context theActivity){
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager)theActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    public static String getPreferredLanguage(Context context, String key, String defaultVal)
    {
        SharedPreferences sharedPref;

        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE);
        return sharedPref.getString(key,defaultVal);

    }

    public static void putString(Context context,String key, String value) {
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();

    }

}

