package com.demo.webservice;

import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.demo.appdata.AppController;
import com.demo.appdata.AppUtils;
import com.demo.callback.OnResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WebserviceHelper {
    public static final int METHOD_GET = 1; // identify API Method

    public static final String TAG = AppController.class
            .getSimpleName();

    /**
     * @param activity               activity of calling API
     * @param url                    API url
     * @param onResponseListener     OnResponseListener callback for success/failure response
     */

    public WebserviceHelper(final Activity activity, final String url, final int methodType,
                            OnResponseListener onResponseListener) {


        switch (methodType) {
            case METHOD_GET:
                doGet(activity, url, onResponseListener);
                break;

        }
    }

    private static void doGet(final Activity activity, final String url, final OnResponseListener onResponseListener) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {

                        AppController.traceLog("URL : ", url);
                        AppController.traceLog("Response : ", response + "");

                        try {
                            onResponseListener.OnResponseSuccess(response);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showToast(activity, AppUtils.AppConstant.TOAST_TYPE_ERROR, e.getMessage());
                            onResponseListener.OnResponseFailure(response);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                onResponseListener.OnResponseFailure(new JSONObject());
                AppController.handleVolleyError(activity, volleyError);
            }

        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put("Accept", "application/json");
                return map;

            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                AppController.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                TAG);
    }
}