package com.example.davidwillo.youwo.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by davidwillo on 6/4/17.
 */

public class NetworkUtil {
    static private NetworkUtil mInstance;
    static private Context context;
    static private OkHttpClient okHttpClient;
    static private Retrofit retrofit;
    static private NetworkAPI networkAPI;

    static private String baseUrl = "http://172.18.68.103:8080/";

    private NetworkUtil(Context context) {
        initOkhttpIntercept();
        initRetrofit();
        this.context = context;
    }
    public static void initNetwork() {
        initOkhttpIntercept();
        initRetrofit();
    }

    public static synchronized NetworkUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkUtil(context);
        }
        return mInstance;
    }

    public static NetworkAPI getAPI() {
        return networkAPI;
    }

    public static void initOkhttpIntercept() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public static void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient).build();
        networkAPI = retrofit.create(NetworkAPI.class);

    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String newbaseUrl) {
        baseUrl = newbaseUrl;
    }


}
