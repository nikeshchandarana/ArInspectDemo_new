package com.demo.receiver;





import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckConnectivity extends BroadcastReceiver {
    private static com.demo.callback.InternetConnectionCallback internetConnectionCallback;
    public CheckConnectivity(){}

    public CheckConnectivity(com.demo.callback.InternetConnectionCallback internetConnectionCallback) {
        this.internetConnectionCallback = internetConnectionCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            final String className = context.getClass().getSimpleName();
            if (className != null) {
                if (className.equalsIgnoreCase("Splashscreen")) {
                    if (isOnline(context)) { // connected
                        new com.demo.activity.MainActivity().dialog(true);
                    } else { // disconnected
                        new com.demo.activity.MainActivity().dialog(false);
                        com.demo.appdata.AppController.isInternetInterrupted = true;

                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(final Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            //should check null because in airplane mode it will be null
            boolean isAppOnline = (netInfo != null && netInfo.isConnected());
            if(isAppOnline){
                this.internetConnectionCallback.onInternetConnected(isAppOnline);
            }else{
                this.internetConnectionCallback.onInternetDisconnected(isAppOnline);
            }
            return isAppOnline;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
