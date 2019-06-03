package com.demo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.demo.R;
import com.demo.adapter.FactAdapter;
import com.demo.callback.InternetConnectionCallback;
import com.demo.callback.OnResponseListener;
import com.demo.model.FactData;
import com.demo.receiver.CheckConnectivity;
import com.demo.webservice.URLData;
import com.demo.webservice.WebserviceHelper;
import com.jude.easyrecyclerview.EasyRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/** This class in the main class in the application which initialize  main layout and make call to web service using json volley
 * This class also help in detecting the internet connectivity.
 *
 * Date created - 31-05-2019
 * Developed by - Nikesh
 * Amendment's any - No
 *
 * IMP Points - Initialise main layout
 *              Calling main API ref. getFacts()
 *              Check internet connectivity  using registerNetworkBroadcast()
 *              Initialise listing adapter

*/

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    android.widget.TextView tv_check_connection;
    com.demo.receiver.CheckConnectivity mNetworkReceiver;
    @Nullable
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @Nullable
    @BindView(R.id.mRecyclerview)
    EasyRecyclerView mRecyclerView;
    @Nullable
    @BindView(R.id.maintoolbar)
    Toolbar mainToolbar;
    @Nullable
    @BindView(R.id.viewEmpty)
    TextView viewEmpty;

    private Activity activity;
    private ArrayList<FactData> data = new ArrayList<>();
    private FactAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); // initialization of objects and varaibles

        registerNetworkBroadcast(); // registering application for internet checkup
        setToolbarAndCustomizeTitle(mainToolbar, getString(R.string.app_name));
        onRefresh();
    }

    /** API Calling */
    private void getFacts(boolean isToPullDown) {

        if (isToPullDown) {
            showSwipeProgress();
        }

        final String url = URLData.BASE_URL + URLData.GET_FACTS;

        new WebserviceHelper(activity, url, WebserviceHelper.METHOD_GET, new OnResponseListener() {
            @Override
            public void OnResponseFailure(JSONObject response) {
                hideSwipeProgress();
            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                hideSwipeProgress();
                new ParseFactResponse(response).execute();
            }
        });

    }

    private class ParseFactResponse extends AsyncTask<Void, Void, Void> {
        private JSONObject response = new JSONObject();

        ParseFactResponse(JSONObject response) {
            this.response = response;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(response.optJSONArray("rows").length()>0) {
                data.clear();
                JSONArray rows = response.optJSONArray("rows");
                for (int count = 0; count < rows.length(); count++) {
                    JSONObject datum = rows.optJSONObject(count);
                    FactData factData = new FactData();

                    // TODO : Can add multiple checking to avoid null in the UI
                    factData.setTitle(TextUtils.isEmpty(datum.optString("title")) ? "Title Not Available" : datum.optString("title"));
                    factData.setDescription(TextUtils.isEmpty(datum.optString("description")) ? "Description Not Available" : datum.optString
                            ("description"));
                    factData.setImageHref(TextUtils.isEmpty(datum.optString("imageHref")) ? "NA" : datum.optString("imageHref"));
                    if (!data.contains(factData))
                        data.add(factData);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(data.size()<=0){
                AlertDialog.Builder alertToExit = new AlertDialog.Builder(activity);
                alertToExit.setMessage("No data");
                alertToExit.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.finish();
                    }
                });
            }
            setToolbarAndCustomizeTitle(mainToolbar, response.optString("title") == null ? getString(R.string.app_name) : response.optString
                    ("title"));
            super.onPostExecute(aVoid);
            adapter = new FactAdapter(activity, data);
            mRecyclerView.setAdapter(adapter);  // adding data in the adapter
            adapter.notifyDataSetChanged();
        }
    }

    private void init() {
        mNetworkReceiver = new CheckConnectivity(internetConnectionCallback);
        ButterKnife.bind(this);
        activity = MainActivity.this;
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);


        refreshLayout.setOnRefreshListener(this);
        setAppearance();
        refreshLayout.setEnabled(true);

    }

    private void setAppearance() {
        refreshLayout.setColorSchemeResources(R.color.holo_red_light,
                R.color.holo_green_light,
                R.color.holo_orange_light,
                R.color.holo_blue_bright);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void showSwipeProgress() {
        refreshLayout.setRefreshing(true);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void hideSwipeProgress() {
        refreshLayout.setRefreshing(false);
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        getFacts(true);
    }

    private void setToolbarAndCustomizeTitle(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_launcher);
//        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    InternetConnectionCallback internetConnectionCallback = new InternetConnectionCallback() {
        @Override
        public void onInternetConnected(boolean isConnected) {
            getFacts(true);
        }

        @Override
        public void onInternetDisconnected(boolean isConnected) {

        }
    };


    /** registering for different API versions */
    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


    public void dialog(boolean value) {
        if (value) {
            tv_check_connection.setVisibility(android.view.View.GONE);
        } else {
            tv_check_connection.setVisibility(android.view.View.VISIBLE);
            tv_check_connection.setText(R.string.no_internet);
            tv_check_connection.setBackgroundColor(android.graphics.Color.RED);
            tv_check_connection.setTextColor(android.graphics.Color.WHITE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        try {
            if (mNetworkReceiver != null)
                unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alertToExit = new AlertDialog.Builder(activity);
        alertToExit.setMessage("Are you sure to exit?");
        alertToExit.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
    }
}
