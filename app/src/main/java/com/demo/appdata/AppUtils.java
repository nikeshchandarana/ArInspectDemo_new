package com.demo.appdata;

import android.content.Context;
import android.widget.Toast;

public class AppUtils   {
 public static void showToast(final Context activity, final int type, final String message) {
        switch (type) {
            case AppConstant.TOAST_TYPE_ERROR:
                es.dmoral.toasty.Toasty.error(activity.getApplicationContext(), message, Toast.LENGTH_SHORT, true).show();
                break;
            case AppConstant.TOAST_TYPE_INFO:
                es.dmoral.toasty.Toasty.info(activity.getApplicationContext(), message, Toast.LENGTH_SHORT, true).show();
                break;
            case AppConstant.TOAST_TYPE_SUCCESS:
                es.dmoral.toasty.Toasty.success(activity.getApplicationContext(), message, Toast.LENGTH_SHORT, true).show();
                break;
        }
    }
   public class AppConstant {
      public static final int TOAST_TYPE_ERROR = 100, TOAST_TYPE_SUCCESS = 200, TOAST_TYPE_INFO = 101;}
}
