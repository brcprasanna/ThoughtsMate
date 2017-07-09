package ram.king.com.makebharathi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ram.king.com.makebharathi.R;


public class AppUtil {


    public static boolean isInternetConnected(Context activity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String getString(Context context, String key, String defaultVal)
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

    public static boolean getBoolean(Context context, String key, boolean defaultVal)
    {
        SharedPreferences sharedPref;

        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key,defaultVal);

    }

    public static void putBoolean(Context context,String key, boolean value) {
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

}

