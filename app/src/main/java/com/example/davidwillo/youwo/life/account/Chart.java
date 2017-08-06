package com.example.davidwillo.youwo.life.account;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.davidwillo.youwo.R;

import java.util.ArrayList;


/**
 * Created by limin on 2017/5/18.
 */
public class Chart extends AppCompatActivity {
    public static final String PREFERENCE_NAME = "SaveSetting";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String username;
    private TextView totalInto;
    private TextView totalOut;

    private Float out;
    private Float in;
    private Float income;
    private Float eating;
    private Float transport;
    private Float commodity;
    private Float social;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_chart);

        Bundle bundle = this.getIntent().getExtras();
        out = bundle.getFloat("totalOut");
        in = bundle.getFloat("totalInto");
        income = Math.abs(bundle.getFloat("income"));
        eating = Math.abs(bundle.getFloat("eating"));
        transport = Math.abs(bundle.getFloat("transport"));
        commodity = Math.abs(bundle.getFloat("commdity"));
        social = Math.abs(bundle.getFloat("social"));


        findView();
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = sharedPreferences.getString("username", null);


        MyPieChart pieChart = (MyPieChart) findViewById(R.id.chart);
        ArrayList<Float> values = new ArrayList<>();
        values.add(income);
        values.add(eating);
        values.add(transport);
        values.add(commodity);
        values.add(social);
        pieChart.setValues(values);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(164, 222, 208));
        colors.add(Color.rgb(165, 195, 105));
        colors.add(Color.rgb(133, 208, 235));
        colors.add(Color.rgb(247, 155, 101));
        colors.add(Color.rgb(224, 113, 113));
        pieChart.setColors(colors);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("收入");
        labels.add("餐饮");
        labels.add("交通");
        labels.add("日用");
        labels.add("社交");
        pieChart.setLabels(labels);

        totalOut.setText(out.toString());
        totalInto.setText(in.toString());
    }

    private void findView() {
        totalInto = (TextView) findViewById(R.id.totol_income);
        totalOut = (TextView) findViewById(R.id.total_expenditure);
    }


    // 解决转屏问题
    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
        if (newConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {

        }
    }

}
