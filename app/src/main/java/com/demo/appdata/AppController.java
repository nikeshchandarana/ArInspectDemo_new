package com.demo.appdata;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppController extends MultiDexApplication {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;
    public static final String TAG = AppController.class.getSimpleName();
    public static int MY_SOCKET_TIMEOUT_MS = 15000;

    public static boolean isInternetInterrupted;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static void hideKeyboard(Activity activity, EditText et) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new com.demo.webservice.LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static void handleVolleyError(Activity act,   VolleyError volleyError) {
        VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
        String message = "";
        boolean isToLogOut = false;
        try {
            JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
            JSONArray mData = null;

                message = new JSONObject(new String(volleyError.networkResponse.data)).optString("message");

            AppController.traceLog("vollyErrorTrace", responseObject + "");
            AppController.traceLog("VolleyErrorHandled", "error---->" + volleyError.getMessage());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            if (volleyError instanceof NetworkError) {
                message = "Internet is Offline";//act.getString(R.string.internet_is_offline);
            } else if (volleyError instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
            } else if (volleyError instanceof AuthFailureError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (volleyError instanceof NoConnectionError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            }
        }

        if (message != null) {
            try {
//                Snackbar.make(MainActivity.fab, message, Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();
                Toast toast = Toast.makeText(act, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception e) {
                AppController.traceLog("ERROR_VOLLEYERROR", message);
                Toast.makeText(act, message, Toast.LENGTH_SHORT).show();
            }

            }
    }

    public static void traceLog(String key, String value) {
        Log.i(key, value);
    }

}
