package com.example.davidwillo.youwo.sport;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidwillo.youwo.LoginActivity;
import com.example.davidwillo.youwo.PagerActivity;
import com.example.davidwillo.youwo.R;
import com.example.davidwillo.youwo.application.MyApplication;
import com.example.davidwillo.youwo.network.HttpResult;
import com.example.davidwillo.youwo.network.NetworkException;
import com.example.davidwillo.youwo.network.NetworkUtil;
import com.example.davidwillo.youwo.network.SyncHelper;
import com.example.davidwillo.youwo.util.MyImageLoader;
import com.example.davidwillo.youwo.util.MyRoundProgress;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import android.net.NetworkInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class SportMainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private ImageButton iconCD;

    DrawerLayout drawerLayout = null;
    MyRoundProgress roundProgress;
    NavigationView navigationView;
    View fragment;
    TextView cityName, temperature, windLev, airQuality, stepET, compassDegree;
    ImageView compass, weatherPic;
    ConnectivityManager connectivityManager;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    SensorManager sensorManager;
    Sensor accelerrometerSensor, magneticSensor;

    public SportMainFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragment = inflater.inflate(R.layout.sport_main_fragment, container, false);

        findView(fragment);
        loadWeather();
        drawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });

        loadProgress();

        //侧边栏监听
        navigationView.setNavigationItemSelectedListener(this);

        loadHeader();

        initLocation();
        bindButton();
        initCompass();
        connectService();
        if (MyApplication.getInstance().getNeedQueryWeather())
            iconCD.callOnClick();
        return fragment;
    }


    private void findView(View fragment) {
        drawerLayout = (DrawerLayout) fragment.findViewById(R.id.drawer_layout);
        roundProgress = (MyRoundProgress) fragment.findViewById(R.id.roundProgress);
        navigationView = (NavigationView) fragment.findViewById(R.id.nav_view_sport);
        iconCD = (ImageButton) fragment.findViewById(R.id.iconCD);
        cityName = (TextView) fragment.findViewById(R.id.cityName);
        temperature = (TextView) fragment.findViewById(R.id.temperature);
        windLev = (TextView) fragment.findViewById(R.id.windLev);
        airQuality = (TextView) fragment.findViewById(R.id.airQuality);
        stepET = (TextView) fragment.findViewById(R.id.stepET);
        compassDegree = (TextView) fragment.findViewById(R.id.compassDegree);
        compass = (ImageView) fragment.findViewById(R.id.compass);
        weatherPic = (ImageView) fragment.findViewById(R.id.weather_pic);
    }

    // 恢复本地的天气数据
    private void loadWeather() {
        MyApplication myApplication = MyApplication.getInstance();
        cityName.setText(myApplication.getCityName());
        temperature.setText(myApplication.getTemperature());
        windLev.setText(myApplication.getWindLev());
        airQuality.setText(myApplication.getAirQuality());
        int Img1 = getResources().getIdentifier("a_" + myApplication.getWeatherImage(), "mipmap", getActivity().getPackageName());
        weatherPic.setImageResource(Img1);
    }

    private void loadHeader() {
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.username)).setText(MyApplication.getInstance().getUsername());
        MyImageLoader.displayImage(getContext(),"http://cdnq.duitang.com/uploads/item/201501/16/20150116145231_3xcYy.jpeg",
                ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.avatar)));
    }
    private void loadProgress() {
        //设置圆形进度条
        roundProgress.setMaxProgress(MyApplication.getInstance().getAim());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.setgoal) {
            Snackbar.make(fragment, "setgoal", Snackbar.LENGTH_SHORT).show();
            showChangeAim();
        } else if (id == R.id.history) {
            Snackbar.make(fragment, "history", Snackbar.LENGTH_SHORT).show();
            queryStepRecords();
        } else if (id == R.id.password) {
            Snackbar.make(fragment, "password", Snackbar.LENGTH_SHORT).show();
            showChangePassword();
        } else if (id == R.id.logout) {
            Snackbar.make(fragment, "logout", Snackbar.LENGTH_SHORT).show();
            MyApplication.getInstance().setLogout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.sync_sport) {
            Snackbar.make(fragment, "sync sport", Snackbar.LENGTH_SHORT).show();
            //SyncHelper.downloadStepRecord(getContext());
            SyncHelper.uploadStepRecord(getContext());
        }

        if (drawerLayout != null)
            drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, magneticSensor,
                SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, accelerrometerSensor,
                SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        //drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void bindButton() {
        iconCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.start();
                mLocationClient.requestLocation();
            }
        });
    }

    private String addr;

    //定位相关

    private void initLocation() {
        mLocationClient = new LocationClient(getContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps

        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            boolean errorflag = true;
            StringBuffer sb = new StringBuffer(256);
            String error_code;
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                errorflag = false;
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                errorflag = false;
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                errorflag = false;
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
            mLocationClient.stop();
            if (!errorflag) {
                addr = sb.toString().split("\n")[5].split(":")[1];
                Log.i("FinalAddr", addr);
                queryWeather();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "定位失败，请检查定位服务设置", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String a, int b){}
    }

    private void queryWeather() {
        connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getWeather();
                }
            }).start();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void getWeather() {
        String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
        HttpURLConnection connection = null;
        if (addr == null) return;
        String city = addr.split("省")[1].split("市")[0];
        Log.i("CityForQuery", city);
        try {
            Log.i("key", "Start connection");
            connection = (HttpURLConnection) ((new URL(url.toString()).openConnection()));
            connection.setRequestMethod("POST");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String request = URLEncoder.encode(city, "utf-8");
            //out.writeBytes("theCityCode=" + request + "&theUserID=f9d64987923e4deea67e2431e1a40fa6");
            out.writeBytes("theCityCode=" + request + "&theUserID=");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                Message message
                        = handler.obtainMessage(1);
                message.sendToTarget();
                //Toast.makeText(MainActivity.this, "连接失败，请重试", Toast.LENGTH_SHORT);
            } else {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Message message
                        = handler.obtainMessage(0);
                message.obj = parseXMLWithPull(response.toString());
                message.sendToTarget();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private ArrayList<String> parseXMLWithPull(String string) {
        Log.i("XML", string);
        ArrayList<String> answer = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(string));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("string")) {
                            String value = parser.nextText();
                            Log.v("XML", value);
                            answer.add(value);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return answer;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                Toast.makeText(getActivity().getApplicationContext(), "连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<String> response = (ArrayList<String>) message.obj;
            if (response.get(0).contains("查询结果为空")) {
                Toast.makeText(getActivity().getApplicationContext(), "查询结果为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.get(0).contains("发现错误：免费用户不能使用高速访问。")) {
                Toast.makeText(getActivity().getApplicationContext(), "免费用户不能使用高速访问", Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.get(0).contains("发现错误：免费用户24小时内访问超过规定数量。")) {
                Toast.makeText(getActivity().getApplicationContext(), "免费用户24小时内访问超过规定数量", Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.get(4).equals("今日天气实况：暂无实况")) {
                Toast.makeText(getActivity().getApplicationContext(), "暂无该城市天气实况", Toast.LENGTH_SHORT).show();
            } else {
                MyApplication myApplication = MyApplication.getInstance();
                cityName.setText(response.get(1));
                myApplication.setCityName(response.get(1));
                String info[] = response.get(4).split("；");
                temperature.setText(info[0].split("：")[2]);
                myApplication.setTemperature(info[0].split("：")[2]);
                windLev.setText(info[1].split("：")[1]);
                myApplication.setWindLev(info[1].split("：")[1]);
                airQuality.setText(response.get(5).split("。")[1]);
                myApplication.setAirQuality(response.get(5).split("。")[1]);
                String[] img1 = response.get(10).split("\\.");
                String[] img2 = response.get(11).split("\\.");
                myApplication.setWeatherImage(img1[0]);
                int Img1 = getResources().getIdentifier("a_" + img1[0], "mipmap", getActivity().getPackageName());
                int Img2 = getResources().getIdentifier("a_" + img2[0], "mipmap", getActivity().getPackageName());
                weatherPic.setImageResource(Img1);
                Toast.makeText(getActivity().getApplicationContext(), "获取本地天气成功", Toast.LENGTH_SHORT).show();
                //防止重复查询
                MyApplication.getInstance().setNeedQueryWeather(false);
            }
            super.handleMessage(message);
        }
    };

    //指南针相关
    private void getSensor() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    private void initCompass() {
        RotationDegree = 0;
        getSensor();
        compass.setRotation(RotationDegree);
    }

    float RotationDegree = 0;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] accValues = new float[3];
        float[] magValues = new float[3];

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = sensorEvent.values.clone();
                    break;
                default:
                    break;
            }
            float[] R = new float[9];
            float[] values = new float[3];
            sensorManager.getRotationMatrix(R, null, accValues, magValues);
            sensorManager.getOrientation(R, values);
            float tempDegree = (float) ((-1.0) * Math.toDegrees(values[0]));
            //System.out.println(tempDegree);
            if (Math.abs(RotationDegree - tempDegree) > 5)
                spin(tempDegree);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    private void spin(float tempDegree) {
        float realDegree = (float) ((-1.0) * tempDegree);
        if (realDegree < 0) realDegree += 360;
        float realDegreePlus = (float) (realDegree + 22.5);
        if (realDegreePlus >= 360) realDegreePlus -= 360;
        String dir = directions[(int) (realDegreePlus / 45)];
        compassDegree.setText(dir + (int) realDegree + "°");
        float fromdegree = RotationDegree;
        float todegree = tempDegree;
        if (fromdegree - todegree > 180) {
            fromdegree -= 360;
        } else if (todegree - fromdegree > 180) {
            fromdegree += 360;
        }
        RotateAnimation ra = new RotateAnimation(fromdegree, todegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        compass.startAnimation(ra);
        RotationDegree = tempDegree;
    }


    //计步服务相关
    MyStepCounterService stepService;

    private void connectService() {
        Intent intent = new Intent(getActivity(), MyStepCounterService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, sc, Context.BIND_AUTO_CREATE);
        oldstep = 0;
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            stepService = ((MyStepCounterService.MyBinder) (service)).getService();
            if (!MyApplication.getInstance().getAutoLogin()) {
                stepService.resetInitial();
                MyApplication.getInstance().setAutoLogin(true);
            }
            int step = stepService.getRealStep();
            stepET.setText("" + step);
            roundProgress.setTargetProgress(step);
            startStepCounter();
            //stepService.setName(username);
            stepService.loadHistory();
            stepService.startTimeCount();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            stepService = null;
        }
    };

    private void startStepCounter() {
        stephandler.post(runnable);
    }

    private int oldstep = 0;
    Handler stephandler = new Handler() {
        public void handleMessage(Message msg) {
            int realStep = msg.arg1;
            stepET.setText("" + realStep);
            if (oldstep != realStep) {
                oldstep = realStep;
                roundProgress.setTargetProgress(realStep);
            }
            stephandler.post(runnable);
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message msg = stephandler.obtainMessage();
            msg.arg1 = stepService.getRealStep();
            stephandler.sendMessage(msg);
        }
    };

    private void queryStepRecords() {
        stepService.save();
        Intent intent = new Intent(getContext(), StepHistory.class);
        startActivity(intent);
    }

    private void showChangeAim(){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View dialogview = factory.inflate(R.layout.mydialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogview);

        final EditText setStepET = (EditText) dialogview.findViewById(R.id.setStepET);
        setStepET.setText("" + MyApplication.getInstance().getAim());
        AlertDialog AD = builder
                .setPositiveButton("保存修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int aim = Integer.valueOf(setStepET.getText().toString());
                                roundProgress.setMaxProgress(aim);
                                MyApplication.getInstance().setAim(Integer.parseInt(setStepET.getText().toString()));
                                if (stepService != null) {
                                    stepService.setAim(aim);
                                }
                            }
                        })
                .setNegativeButton("放弃修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).create();
        AD.show();
    }

    private void showChangePassword(){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View dialogview = factory.inflate(R.layout.password_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogview);

        final EditText oldPassword = (EditText) dialogview.findViewById(R.id.password_dialog_old_password);
        final EditText newPassword = (EditText) dialogview.findViewById(R.id.password_dialog_new_password);
        AlertDialog AD = builder
                .setPositiveButton("保存修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String oldpass = oldPassword.getText().toString();
                                String newpass = newPassword.getText().toString();
                                if (TextUtils.isEmpty(oldpass)) {
                                    Toast.makeText(getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(newpass)) {
                                    Toast.makeText(getContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                                }

                                NetworkUtil.getAPI().modifypassword(MyApplication.getInstance().getUsername(), oldpass, newpass)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .map(new Function<HttpResult<String>, String>() {

                                            @Override
                                            public String apply(HttpResult<String> result) throws Exception {
                                                Log.d("LoginResult", result.toString());
                                                if (result.getResultCode() == 1)
                                                    return result.getData();
                                                else
                                                    throw new NetworkException(result.getResultMessage());

                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<String>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onNext(@NonNull String userName) {
                                                //((MyApplication) getApplication()).setUsername(userName);
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onComplete() {
                                                Toast.makeText(getContext(), "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                                                MyApplication.getInstance().setLogout();
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);

                                            }
                                        });
                            }
                        }).setNegativeButton("放弃修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).create();
        AD.show();
    }
}
