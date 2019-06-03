package com.demo.callback;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface OnTaskCompleted {
    void onTaskSuccess(JSONObject jsonObject);
    void onTaskFailure(VolleyError error);
}
