package com.demo.callback;

public interface InternetConnectionCallback {
    void onInternetConnected(boolean isConnected);
    void onInternetDisconnected(boolean isConnected);
}
