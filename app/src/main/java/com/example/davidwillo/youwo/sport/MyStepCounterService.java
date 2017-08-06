package com.example.davidwillo.youwo.sport;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.example.davidwillo.youwo.application.MyApplication;
import com.example.davidwillo.youwo.network.model.StepRecord;

import java.util.Calendar;
import java.util.List;

public class MyStepCounterService extends Service implements SensorEventListener {
    int steps;
    int aim;
    int initialSteps;
    boolean flag; //用于判断是否已经发出notification
    List<StepRecord> historys;
    String name;
    SensorManager sensorManager;
    Sensor stepCount;
    StepCountDB db = new StepCountDB(MyStepCounterService.this);
    public static final String PREFERENCE_NAME = "settings";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public void getLocalSettings() {
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        name = MyApplication.getInstance().getUsername();
        aim = MyApplication.getInstance().getAim();
        initialSteps = MyApplication.getInstance().getInitialStep();
    }

    public MyStepCounterService() {
        steps = 0;
        name = "Test";
    }

    public void setName(String _name) {
        name = _name;
    }

    @Override
    public void onCreate() {
        flag = false;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCount = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(MyStepCounterService.this, stepCount, SensorManager.SENSOR_DELAY_NORMAL);
        getLocalSettings();
        //startTimeCount();
        //loadHistory(); done when on connected
    }

    public final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        MyStepCounterService getService() {
            return MyStepCounterService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimeCount();
        loadHistory();
        getLocalSettings();
        updateWidget();
        System.out.println("Start Command" + name);
        return START_STICKY;
    }

    public int getSteps() {
        //save();
        if (historys != null && historys.size() >= 1)
            return steps;
        else
            return 0;
    }

    public void setAim(int _aim) {
        aim = _aim;
        flag = false;
    }

    public int getYesterdayStep() {
        //loadHistory();
        if (historys != null && historys.size() > 1)
            return Integer.valueOf(historys.get(historys.size() - 2).count);
        else return 0;
    }

    public int getRealStep() {
        int today = getSteps();
        int yesterday = initialSteps;
        //int yesterday = getYesterdayStep();
        if (today < yesterday) {
            return today;
        }
        return today - yesterday;
    }

    public void updateWidget() {
        Intent intent = new Intent("WIDGETUPDATE");
        Bundle bundle = new Bundle();
        int realstep = getRealStep();
        bundle.putString("step_count", "" + realstep);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        if (realstep >= aim && flag == false) {
            Intent intent1 = new Intent("com.example.davidwillo.youwo.sport.stepStaticReceiver");
            sendBroadcast(intent1);
            flag = true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps = (int) event.values[0];
        System.out.println("Step:" + steps);
        updateWidget();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private TimeCount time;

    public void startTimeCount() {
        time = new TimeCount(3000, 1000); // 更新数据库频率 可调
        time.start();
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            time.cancel();
            save();
            startTimeCount();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    public void save() {
        //DateFormat formatter = SimpleDateFormat.getDateInstance();
        Calendar cal = Calendar.getInstance();
        //String year = ""+cal.get(Calendar.YEAR);
        String month = "" + (cal.get(Calendar.MONTH) + 1);
        String day = "" + cal.get(Calendar.DAY_OF_MONTH);
        String realSteps = "" + getRealStep();
        if (!db.checkExist(name, month, day)) {
            db.insert2DB(name, month, day, realSteps);
        } else {
            db.update2DB(name, month, day, realSteps);
        }
        //Log.i("Tick", "tick");
        // 跨天后重新加载历史，重新计算新一天步数
        if (historys.size() >= 1 && !day.equals(historys.get(historys.size() - 1).day)) {
            resetInitial();
            loadHistory();
        }
        System.out.println("tick");
    }


    public void loadHistory() {
        historys = db.query(name);
        // 如果没有历史记录
        if (historys.size() == 0 && !name.equals("Test")) {
            db.insert2DB(name, "-1", "-1", "" + steps);
            MyApplication.getInstance().setInitialStep(steps); // 计数值
        }
        initialSteps = MyApplication.getInstance().getInitialStep();
        save();
    }

    @Override
    public void onDestroy() {
        System.out.println("Service Destroy!");
        super.onDestroy();
    }

    // 登录或者跨天时候调用
    public void resetInitial() {
        initialSteps = steps;
        MyApplication.getInstance().setInitialStep(steps);
    }

}
