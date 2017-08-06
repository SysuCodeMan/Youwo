package com.example.davidwillo.youwo.application;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by davidwillo on 6/6/17.
 */

public class MyApplication extends Application {

    private SharedPreferences sharedPreferences;
    private String userName;


    private int aim; //目标步数
    private int initialStep; //初始步数

    private String cityName;
    private String temperature;
    private String windLev;
    private String airQuality;
    private String weatherImage;
    private Boolean needQueryWeather;

    private Boolean autoLogin = true;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLogin", false)) {
            userName = sharedPreferences.getString("userName", "");
        }
        initialStep = sharedPreferences.getInt("initialStep", 0);
        aim = Integer.valueOf(sharedPreferences.getInt("aim", 10000));
        cityName = sharedPreferences.getString("cityName", "广州");
        temperature = sharedPreferences.getString("temperature", "17℃");
        windLev = sharedPreferences.getString("winLev", "西北风 3级");
        airQuality = sharedPreferences.getString("airQuality", "空气质量：良");
        weatherImage = sharedPreferences.getString("weatherImage","0");
        needQueryWeather = true;
    }


    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        sharedPreferences.edit().putString("userName", userName).commit();
        this.userName = userName;
    }

    public int getAim() {
        return aim;
    }

    public void setAim(int aim) {
        sharedPreferences.edit().putInt("aim", aim).commit();
        this.aim = aim;
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        sharedPreferences.edit().putString("cityName", cityName).commit();
        this.cityName = cityName;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        sharedPreferences.edit().putString("temperature", temperature).commit();
        this.temperature = temperature;
    }

    public String getWindLev() {
        return windLev;
    }

    public void setWindLev(String windLev) {
        sharedPreferences.edit().putString("windLev", windLev).commit();
        this.windLev = windLev;
    }

    public String getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(String airQuality) {
        sharedPreferences.edit().putString("airQuality", airQuality).commit();
        this.airQuality = airQuality;
    }

    public String getWeatherImage() {
        return weatherImage;
    }

    public void setWeatherImage(String weatherImage) {
        sharedPreferences.edit().putString("weatherImage", weatherImage).commit();
        this.weatherImage = weatherImage;
    }

    public Boolean getNeedQueryWeather() {
        return needQueryWeather;
    }

    public void setNeedQueryWeather(Boolean needQueryWeather) {
        this.needQueryWeather = needQueryWeather;
    }

    public int getInitialStep() {
        return initialStep;
    }

    public void setInitialStep(int initialStep) {
        sharedPreferences.edit().putInt("initialStep", initialStep).commit();
        this.initialStep = initialStep;
    }

    public Boolean getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(Boolean autoLogin) {
        this.autoLogin = autoLogin;
    }


    public void setLogout() {
        sharedPreferences.edit().putBoolean("isLogin", false).commit();
    }
}
