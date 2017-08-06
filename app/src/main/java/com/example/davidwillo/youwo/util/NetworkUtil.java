package com.example.davidwillo.youwo.util;


import android.webkit.CookieManager;

/**
 * Created by ChrisChan on 2017/8/6.
 */

public class NetworkUtil {
    private static CookieManager cookieManager = CookieManager.getInstance();

    public static boolean isLogin() {
        String cookie = cookieManager.getCookie("http://wjw.sysu.edu.cn/");
        return cookie != null && cookie.contains("sno");
    }

    public static void removeCookie() {
        cookieManager.setCookie("http://wjw.sysu.edu.cn/", null);
    }
}
