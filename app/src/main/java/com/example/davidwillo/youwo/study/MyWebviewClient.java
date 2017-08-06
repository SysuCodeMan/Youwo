package com.example.davidwillo.youwo.study;

import android.app.Activity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebviewClient extends WebViewClient {
    CookieManager cookieManager = CookieManager.getInstance();
    Activity context;

    MyWebviewClient(Activity context) {
        this.context = context;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String cookie = cookieManager.getCookie(url);
        if (hasLogin(cookie)) {
            context.setResult(0);
            context.finish();
        }
    }

    private boolean hasLogin(String cookie) {
        return cookie != null && cookie.contains("sno");
    }
}
