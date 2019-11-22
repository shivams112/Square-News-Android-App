package io.github.shivams112.squarenews.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Shivam
 */

public class AppUtils {

    public static boolean isInternetConnected(Context callerContext){
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) callerContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null) {
                if (activeNetInfo.isConnected()) {
                    isConnected = true;
                }
            }
        }
        return isConnected;
    }
}
