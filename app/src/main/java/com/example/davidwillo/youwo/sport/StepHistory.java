package com.example.davidwillo.youwo.sport;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.davidwillo.youwo.R;
import com.example.davidwillo.youwo.application.MyApplication;
import com.example.davidwillo.youwo.network.model.StepRecord;
import com.example.davidwillo.youwo.util.MyImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StepHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    LineCharView lcv;
    SeekBar seekBar;
    DrawerLayout drawerLayout = null;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);
        checkMonthVector();
        lcv = (LineCharView) findViewById(R.id.stepLineChart);

        //seekBar = (SeekBar) findViewById(R.id.lineChartSizeBar);
        //seekBar.setMax(80);
        //seekBar.setProgress(40);
        setDrawer();
        showLineChart();
        setMonthXCoor();
        //setSeekBarListener();

    }

    public void setSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lcv.setYfactor(seekBar.getMax() - progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    List<String> x_coords;
    List<String> x_coord_values;

    private void showLineChart() {
        x_coords = new ArrayList<String>();
// 测试用数据
//        x_coords.add("11月2日");
//        x_coords.add("11月3日");
//        x_coords.add("11月4日");
//        x_coords.add("11月5日");
//        x_coords.add("11月6日");
//        x_coords.add("11月7日");
//        x_coords.add("11月8日");
//        x_coords.add("11月9日");
//        x_coords.add("11月10日");
//        x_coords.add("11月11日");
//        x_coords.add("11月12日");
//        x_coords.add("11月13日");
//        x_coords.add("11月14日");
//        x_coords.add("11月15日");
//        x_coords.add("11月16日");
//        x_coords.add("11月17日");


        x_coord_values = new ArrayList<String>();

//        x_coord_values.add("2000");
//        x_coord_values.add("1500");
//        x_coord_values.add("1200");
//        x_coord_values.add("1300");
//        x_coord_values.add("1490");
//        x_coord_values.add("2180");
//        x_coord_values.add("2200");
//        x_coord_values.add("2000");
//        x_coord_values.add("500");
//        x_coord_values.add("870");
//        x_coord_values.add("1331");
//        x_coord_values.add("2430");
//        x_coord_values.add("1260");
//        x_coord_values.add("230");
//        x_coord_values.add("560");
//        x_coord_values.add("1700");

        StepCountDB db = new StepCountDB(StepHistory.this);
        String name = MyApplication.getInstance().getUsername();
        List<StepRecord> records = db.query(name);
        Log.i("query!", records.toString());
        for (int i = 0; i < records.size(); i++) {
            String month = records.get(i).month;
            String day = records.get(i).day;
            String count = records.get(i).count;
            if (month.equals("-1")) continue;
            x_coords.add(month + "月" + day + "日");
            x_coord_values.add(count);
//            if (i == 0) {
//                x_coord_values.add(count);
//            } else {
//                String prestep = records.get(i - 1).count;
//                int prestepint = Integer.valueOf(prestep);
//                int nowstepint = Integer.valueOf(count);
//                if (prestepint < nowstepint || prestepint == nowstepint) {
//                    x_coord_values.add("" + (nowstepint - prestepint));
//                } else {
//                    x_coord_values.add(count);
//                }
//            }
        }

        lcv.setBgColor(Color.TRANSPARENT);
        lcv.setXytextsize(50);
        lcv.setXytextcolor(Color.WHITE);
        lcv.setValue(x_coords, x_coord_values);
    }

    private void setDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_history_layout);
        drawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });
        navigationView = (NavigationView)findViewById(R.id.nav_view_history);
        navigationView.setNavigationItemSelectedListener(this);
        loadHeader();
//        final List<Map<String, Object>> data = new ArrayList<>();
//        int[] icons = new int[]{R.mipmap.history, R.mipmap.history};
//        String[] letters = new String[]{"日度数据", "月度数据"};
//        for (int i = 0; i < 2; i++) {
//            Map<String, Object> temp = new LinkedHashMap<>();
//            temp.put("drawer_icon", icons[i]);
//            temp.put("drawer_button", letters[i]);
//            data.add(temp);
//        }
//        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.drawer_list_item,
//                new String[]{"drawer_icon", "drawer_button"}, new int[]{R.id.drawer_icon, R.id.drawer_button});
//        history_left_drawer.setAdapter(simpleAdapter);
//        history_left_drawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    lcv.setYfactor(40);
//                    seekBar.setMax(80);
//                    seekBar.setProgress(40);
//                    lcv.setValue(x_coords, x_coord_values);
//                    lcv.setXinit(lcv.getMinXinit());
//                }
//                if (i == 1) {
//                    showMonth();
//                }
//            }
//        });
    }

    String[] months = {"1月", "2月", "3月", "4月", "5月", "6月",
            "7月", "8月", "9月", "10月", "11月", "12月"};
    ArrayList<String> newMonthVector;
    List<String> x_coords1;
    List<String> x_coord_values1;

    void checkMonthVector() {
        Calendar calendar = Calendar.getInstance();
        int monthIndex = calendar.get(Calendar.MONTH) + 1; //得到为月份,再加一，使得最后一个月在最后
        newMonthVector = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthIndex %= 12;
            newMonthVector.add(months[monthIndex++]);
        }
    }

    void setMonthXCoor() {

        x_coords1 = new ArrayList<>();
        x_coord_values1 = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            x_coords1.add(newMonthVector.get(i));
        }
    }

    void resetMonthY() {
        x_coord_values1.clear();
        for (int i = 0; i < 12; i++) {
            x_coord_values1.add("0");
        }
    }

    void showMonth() {
        resetMonthY();
        for (int i = 0; i < x_coords.size(); i++) {
            if (x_coords.get(i).equals("")) continue;
            String tempMonth = x_coords.get(i).split("月")[0] + "月";
            int index = x_coords1.indexOf(tempMonth);
            int newvalue = Integer.valueOf(x_coord_values1.get(index)) + Integer.valueOf(x_coord_values.get(i));
            x_coord_values1.set(index, "" + newvalue);
        }
        //lcv.setYfactor(150);
        //seekBar.setMax(300);
        //seekBar.setProgress(150);
        lcv.setValue(x_coords1, x_coord_values1);
        lcv.setXinit(lcv.getMinXinit());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.day_history) {
//            lcv.setYfactor(40);
//            seekBar.setMax(80);
//            seekBar.setProgress(40);
            lcv.setValue(x_coords, x_coord_values);
            lcv.setXinit(lcv.getMinXinit());
            Snackbar.make(drawerLayout, "day history", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.month_history) {
            showMonth();
            Snackbar.make(drawerLayout, "month history", Snackbar.LENGTH_SHORT).show();
        }

        if (drawerLayout != null)
            drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadHeader() {
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.username)).setText(MyApplication.getInstance().getUsername());
        MyImageLoader.displayImage(this,"http://cdnq.duitang.com/uploads/item/201501/16/20150116145231_3xcYy.jpeg",
                ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.avatar)));
    }
}
