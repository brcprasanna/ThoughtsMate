package ram.king.com.makebharathi.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.activity.PostDetailActivity;


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

    public static void getDynamicLink(final Activity context) {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(context.getIntent())
                .addOnSuccessListener(context, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String postkey = deepLink.toString().substring(deepLink.toString().lastIndexOf('/'));
                            Intent intent = new Intent(context, PostDetailActivity.class);
                            intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postkey);
                            context.startActivity(intent);
                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("prasanna", "getDynamicLink:onFailure", e);
                    }
                });
    }

}

