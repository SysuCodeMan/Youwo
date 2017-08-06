package com.example.davidwillo.youwo.life;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidwillo.youwo.LoginActivity;
import com.example.davidwillo.youwo.PagerActivity;
import com.example.davidwillo.youwo.R;
import com.example.davidwillo.youwo.application.MyApplication;
import com.example.davidwillo.youwo.life.account.AccountDBdao;
import com.example.davidwillo.youwo.life.account.Chart;
import com.example.davidwillo.youwo.life.account.SpecificData;
import com.example.davidwillo.youwo.life.express.Content;
import com.example.davidwillo.youwo.life.express.Express;
import com.example.davidwillo.youwo.life.express.ExpressHandleData;
import com.example.davidwillo.youwo.life.express.ExpressHandleWeb;
import com.example.davidwillo.youwo.life.express.ScrollListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.davidwillo.youwo.network.HttpResult;
import com.example.davidwillo.youwo.network.NetworkException;
import com.example.davidwillo.youwo.network.NetworkUtil;
import com.example.davidwillo.youwo.network.SyncHelper;
import com.example.davidwillo.youwo.util.MessageEvent;
import com.example.davidwillo.youwo.util.MyImageLoader;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.davidwillo.youwo.R.id.express;


public class LifeMainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout = null;
    View fragment;
    private Context mContext;
    MyApplication myApplication;

    private ImageButton account;
    private ImageButton account2;
    private ImageButton express2;

    private String username;

    private Intent intent = null;
    AccountDBdao accountDBdao;

    private TextView mTextViewTime;
    private Button mButtonAddNodes;

    private Button cancel;
    private Button add;

    private float todayTotalOut;
    private int varlue1;
    private int varlue2;

    private float totalOut;
    private float totalInto;

    private LayoutInflater inflater;

    private EditText mEditTextMoney;
    private EditText mEditTextRemark;
    private TextView mEditTextTime;
    private Spinner mSpinnerType;

    private String time;
    private float money;
    private String type;
    private boolean earning;
    private String remark;
    private float income;
    private float eating;
    private float transport;
    private float commodity;
    private float social;

    NavigationView navigationView;

    private boolean open_dialog = false;

    private static final String[] types = {"收入", "餐饮", "交通", "日用", "社交"};

    int pictures[] = {R.mipmap.expenditure, R.mipmap.eating, R.mipmap.transport, R.mipmap.commodity, R.mipmap.social};
    List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    Map<String, Object> listItem;
    private SimpleAdapter simpleAdapter;

    private float today_income;
    private float today_eating;
    private float today_transport;
    private float today_commodity;
    private float today_social;

    private ListView listview;

    private FragmentManager manager;
    private FragmentTransaction ft;

    private final String[] record = {String.valueOf(today_income), String.valueOf(today_eating),
            String.valueOf(today_transport), String.valueOf(today_commodity), String.valueOf(today_social)};

    private boolean canVis = true;  //判断当前历史记录能不能显示
    private Button btnTrue;
    private EditText etExpressNum;
    private TextView tvClear;

    private ScrollListView lvSearchResult;
    private ListView lvExpressData;
    private ScrollView svSearchList;
    private ExpressHandleData handleData;  //处理搜索记录

    public LifeMainFragment() {
        super();  // Required empty public constructor
    }

    void loadGlobal() {
        accountDBdao = new AccountDBdao(getContext().getApplicationContext());
        totalOut = accountDBdao.fillTotalOut(username);
        totalInto = accountDBdao.fillTotalInto(username);

        //time = GetTime();
        todayTotalOut = accountDBdao.fillTodayOut(username, time);

        income = accountDBdao.fillTypeIn(username, "收入");
        eating = 0 - accountDBdao.fillTypeOut(username, "餐饮");
        transport = 0 - accountDBdao.fillTypeOut(username, "交通");
        commodity = 0 - accountDBdao.fillTypeOut(username, "日用");
        social = 0 - accountDBdao.fillTypeOut(username, "社交");

        today_income = accountDBdao.fillTodayTypeIn(username, "收入", time);
        today_eating = 0 - accountDBdao.fillTodayTypeOut(username, "餐饮", time);
        today_transport = 0 - accountDBdao.fillTodayTypeOut(username, "交通", time);
        today_commodity = 0 - accountDBdao.fillTodayTypeOut(username, "日用", time);
        today_social = 0 - accountDBdao.fillTodayTypeOut(username, "社交", time);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = getFragmentManager();
        this.mContext = getActivity();
        time = GetTime();
        handleData = new ExpressHandleData(mContext);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.life_main_fragment, container, false);

        final RelativeLayout account_part = (RelativeLayout)fragment.findViewById(R.id.account_part);
        final LinearLayout express_part = (LinearLayout)fragment.findViewById(R.id.express_part);

        fragment.findViewById(express).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account_part.setVisibility(View.GONE);
                express_part.setVisibility(View.VISIBLE);

            }
        });

        drawerLayout = (DrawerLayout)fragment.findViewById(R.id.drawer_life_layout);
        account = (ImageButton)fragment.findViewById(R.id.account);
        account2 = (ImageButton)fragment.findViewById(R.id.account2);
        express2 = (ImageButton)fragment.findViewById(R.id.express2);

        btnTrue = (Button) fragment.findViewById(R.id.btn_true);
        etExpressNum = (EditText) fragment.findViewById(R.id.et_express_num);
        tvClear = (TextView) fragment.findViewById(R.id.tv_clear);
        lvSearchResult = (ScrollListView) fragment.findViewById(R.id.lv_search_result);
        lvExpressData = (ListView) fragment.findViewById(R.id.lv_express_data);
        svSearchList = (ScrollView) fragment.findViewById(R.id.sv_search_list);

        drawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });

        account.setClickable(false);
        express2.setClickable(false);
        account2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account_part.setVisibility(View.VISIBLE);
                express_part.setVisibility(View.GONE);
            }
        });

        findView();

        username = myApplication.getInstance().getUsername();


        loadGlobal();

        if (today_income > 0) {
            record[0] = "+" + String.valueOf(today_income);
        } else {
            record[0] = String.valueOf(today_income);
        }
        record[1] = String.valueOf(today_eating);
        record[2] = String.valueOf(today_transport);
        record[3] = String.valueOf(today_commodity);
        record[4] = String.valueOf(today_social);

        for (int i = 0; i < types.length; i++) {
            listItem = new HashMap<String, Object>();
            listItem.put("pictures", pictures[i]);
            listItem.put("types", types[i]);
            listItem.put("record", record[i]);
            data.add(listItem);
        }

        simpleAdapter = new SimpleAdapter(mContext, data, R.layout.item, new String[]{"pictures", "types", "record"},
                new int[]{R.id.pictures, R.id.text, R.id.number});
        listview.setAdapter(simpleAdapter);

        if (username == null) {
            intent = new Intent(mContext, PagerActivity.class);
            startActivity(intent);
        } else {
            mTextViewTime.setText(GetTime().split(";")[0]);

            accountDBdao = new AccountDBdao(getContext().getApplicationContext());
            totalOut = accountDBdao.fillTotalOut(username);
            totalInto = accountDBdao.fillTotalInto(username);
            todayTotalOut = accountDBdao.fillTodayOut(username, time);

            SetValue(totalOut, totalInto);

            inflater = LayoutInflater.from(mContext);
        }

        mButtonAddNodes = (Button) fragment.findViewById(R.id.bt_main_addnotes);

        mButtonAddNodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_dialog = true;
                LayoutInflater factory = LayoutInflater.from(mContext);
                final View dialog_view = factory.inflate(R.layout.activity_addnodes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("记一笔");
                builder.setView(dialog_view);
                mEditTextMoney = (EditText) dialog_view.findViewById(R.id.et_money);
                mEditTextRemark = (EditText) dialog_view.findViewById(R.id.et_remark);
                mEditTextTime = (TextView) dialog_view.findViewById(R.id.et_add_time);

                add = (Button) dialog_view.findViewById(R.id.add_button);
                cancel = (Button) dialog_view.findViewById(R.id.cancel_button);

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                time = year + "/" + month + "/" + day;
                mEditTextTime.setText(time);


                mSpinnerType = (Spinner) dialog_view.findViewById(R.id.sp_type);

                ArrayAdapter<String> adapterType = new ArrayAdapter<String>(mContext,
                        R.layout.addnodes_earnings, types);

                adapterType
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mSpinnerType.setAdapter(adapterType);


                builder.create();
                final AlertDialog alertDialog = builder.show();

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mEditTextMoney.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity().getApplicationContext(), "金额不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            money = Float.parseFloat(mEditTextMoney.getText().toString()
                                    .trim());

                            time = GetTime();
                            type = mSpinnerType.getSelectedItem().toString();


                            if (mSpinnerType.getSelectedItem().toString().equals("收入")) {
                                earning = true;
                            } else {
                                earning = false;
                            }

                            remark = mEditTextRemark.getText().toString().trim();

                            accountDBdao = new AccountDBdao(getActivity().getApplicationContext());
                            accountDBdao.add(time, money, type, earning, remark, username);

                            totalOut = accountDBdao.fillTotalOut(username);
                            totalInto = accountDBdao.fillTotalInto(username);
                            todayTotalOut = accountDBdao.fillTodayOut(username, time);


                            SetValue(totalInto, totalOut);

                            if (mSpinnerType.getSelectedItem().toString().equals("收入")) {
                                income = accountDBdao.fillTypeIn(username, type);
                                today_income = accountDBdao.fillTodayTypeIn(username, type, time);
                                record[0] = "+" + String.valueOf(today_income);
                            } else if (mSpinnerType.getSelectedItem().toString().equals("餐饮")) {
                                eating = 0 - accountDBdao.fillTypeOut(username, type);
                                today_eating = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[1] = String.valueOf(today_eating);
                            } else if (mSpinnerType.getSelectedItem().toString().equals("交通")) {
                                transport = 0 - accountDBdao.fillTypeOut(username, type);
                                today_transport = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[2] = String.valueOf(today_transport);

                            } else if (mSpinnerType.getSelectedItem().toString().equals("日用")) {
                                commodity = 0 - accountDBdao.fillTypeOut(username, type);
                                today_commodity = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[3] = String.valueOf(today_commodity);
                            } else {
                                social = 0 - accountDBdao.fillTypeOut(username, type);
                                today_social = 0 - accountDBdao.fillTodayTypeOut(username, type, time);
                                record[4] = String.valueOf(today_social);

                            }

                            for (int i = 0; i < types.length; i++) {
                                data.remove(0);
                                listItem = new HashMap<String, Object>();
                                listItem.put("pictures", pictures[i]);
                                listItem.put("types", types[i]);
                                listItem.put("record", record[i]);
                                data.add(listItem);
                            }
                            simpleAdapter.notifyDataSetChanged();

                            Toast.makeText(getActivity().getApplicationContext(), "添加账单条目成功", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        intent = new Intent(getActivity(), SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "收入");
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "餐饮");
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "交通");
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getActivity(), SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "日用");
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getActivity(), SpecificData.class);
                        intent.putExtra("name", username);
                        intent.putExtra("title", "社交");
                        startActivity(intent);
                        break;
                }
            }
        });

        //  点击快递查询按钮
        btnTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = etExpressNum.getText().toString().trim();
                if (str.equals("")) {
                    Toast.makeText(mContext,"快递单号不能为空噢!", Toast.LENGTH_SHORT).show();
                    return;
                }
                canVis = false;
                checkExpress(str);
            }
        });

        //  点击快递清除历史记录按钮
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleData.deleteData();
                handleData.queryData("", lvSearchResult);
            }
        });

        //  编辑框点击事件
        etExpressNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleData.queryData("", lvSearchResult) == 0) {
                    disView(0);
                } else {
                    disView(1);
                }
            }
        });

        etExpressNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println("setOnFocusChangeListener");

                if (hasFocus && canVis) { //得到焦点事件
                    if (handleData.queryData("", lvSearchResult) == 0) {
                        disView(0);
                    } else {
                        disView(1);
                    }
                } else {//隐藏键盘，隐藏列表
                    InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(etExpressNum.getWindowToken(), 0);
                    disView(0);
                }
            }
        });


        // 点击listView收缩
        lvExpressData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                disView(0);
            }
        });

        //  历史记录点击事件
        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                canVis = false;
                TextView textView = (TextView) view.findViewById(R.id.tv_record_text);
                String name = textView.getText().toString();
                checkExpress(name);
            }
        });

        handleData = new ExpressHandleData(mContext);

        navigationView = (NavigationView) fragment.findViewById(R.id.nav_view_life);


        //侧边栏监听
        navigationView.setNavigationItemSelectedListener(this);

        loadHeader();
        //navigationView.setNavigationItemSelectedListener(this);
        return fragment;
    }

    private void loadHeader() {
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.username)).setText(MyApplication.getInstance().getUsername());
        MyImageLoader.displayImage(getContext(),"http://cdnq.duitang.com/uploads/item/201501/16/20150116145231_3xcYy.jpeg",
                ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.avatar)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            String str = data.getStringExtra("code");
            checkExpress(str);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int myear = c.get(Calendar.YEAR);
        int mmonth = c.get(Calendar.MONTH);
        int mday = c.get(Calendar.DAY_OF_MONTH);
        if (id == R.id.change_date) {
            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {

                    time = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                    Toast.makeText(getContext(), "Show record for " + time, Toast.LENGTH_SHORT).show();
                    reloadAll();
                }
            }, myear, mmonth, mday).show();
        } else if (id == R.id.cost_percent) {
            //loadGlobal();
            reloadAll();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putFloat("totalOut", totalOut);
            bundle.putFloat("totalInto", totalInto);
            bundle.putFloat("income", income);
            bundle.putFloat("eating", eating);
            bundle.putFloat("transport", transport);
            bundle.putFloat("commdity", commodity);
            bundle.putFloat("social", social);
            intent.putExtras(bundle);

            intent.setClass(getActivity(), Chart.class);
            startActivity(intent);
        } else if (id == R.id.password_change) {
            Snackbar.make(fragment,"password_change", Snackbar.LENGTH_SHORT).show();
            showChangePassword();
        } else if (id == R.id.log_out) {
            Snackbar.make(fragment,"log_out", Snackbar.LENGTH_SHORT).show();
            MyApplication.getInstance().setLogout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.sync_life) {
            Snackbar.make(fragment, "sync life", Snackbar.LENGTH_SHORT).show();
            SyncHelper.uploadLifeAccount(getContext());

//            SyncHelper.downloadLifeExpress(getContext());
//            Express express = new Express();
//            List<Content> datalist = express.getContent();
//            SyncHelper.uploadLifeExpress(getContext(),datalist);
        }

        if (drawerLayout != null )
            drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetTime();
        reloadAll();
//        mTextViewTime.setText(GetTime());
//
//        username = myApplication.getInstance().getUsername();
//
//        totalOut = accountDBdao.fillTotalOut(username);
//        totalInto = accountDBdao.fillTotalInto(username);
//        SetValue(totalInto, totalOut);
//        todayTotalOut = accountDBdao.fillTodayOut(username, GetTime());
//
//        today_income = accountDBdao.fillTodayTypeIn(username, "收入", time);
//        today_eating = 0 - accountDBdao.fillTodayTypeOut(username, "餐饮", time);
//        today_transport = 0 - accountDBdao.fillTodayTypeOut(username, "交通", time);
//        today_commodity = 0 - accountDBdao.fillTodayTypeOut(username, "日用", time);
//        today_social = 0 - accountDBdao.fillTodayTypeOut(username, "社交", time);
//
//        if (today_income > 0) {
//            record[0] = "+" + String.valueOf(today_income);
//        } else {
//            record[0] = String.valueOf(today_income);
//        }
//        record[1] = String.valueOf(today_eating);
//        record[2] = String.valueOf(today_transport);
//        record[3] = String.valueOf(today_commodity);
//        record[4] = String.valueOf(today_social);
//
//        for (int i = 0; i < types.length; i++) {
//            data.remove(0);
//            listItem = new HashMap<String, Object>();
//            listItem.put("pictures", pictures[i]);
//            listItem.put("types", types[i]);
//            listItem.put("record", record[i]);
//            data.add(listItem);
//        }
//        simpleAdapter.notifyDataSetChanged();
    }
    private void reloadAll() {
        mTextViewTime.setText(time.split(";")[0]);

        username = myApplication.getInstance().getUsername();

        totalOut = accountDBdao.fillTotalOut(username);
        totalInto = accountDBdao.fillTotalInto(username);
        SetValue(totalInto, totalOut);
        todayTotalOut = accountDBdao.fillTodayOut(username, time);

        today_income = accountDBdao.fillTodayTypeIn(username, "收入", time);
        today_eating = 0 - accountDBdao.fillTodayTypeOut(username, "餐饮", time);
        today_transport = 0 - accountDBdao.fillTodayTypeOut(username, "交通", time);
        today_commodity = 0 - accountDBdao.fillTodayTypeOut(username, "日用", time);
        today_social = 0 - accountDBdao.fillTodayTypeOut(username, "社交", time);

        if (today_income > 0) {
            record[0] = "+" + String.valueOf(today_income);
        } else {
            record[0] = String.valueOf(today_income);
        }
        record[1] = String.valueOf(today_eating);
        record[2] = String.valueOf(today_transport);
        record[3] = String.valueOf(today_commodity);
        record[4] = String.valueOf(today_social);

        for (int i = 0; i < types.length; i++) {
            data.remove(0);
            listItem = new HashMap<String, Object>();
            listItem.put("pictures", pictures[i]);
            listItem.put("types", types[i]);
            listItem.put("record", record[i]);
            data.add(listItem);
        }
        simpleAdapter.notifyDataSetChanged();
    }

    public String GetTime() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String time = year + "/" + month + "/" + day+ ";" + Long.toString(System.currentTimeMillis()/1000);
        return time;
    }

    public void SetValue(float totalInto, float totalOut) {

        if ((totalInto - totalOut) < 0) {
            varlue1 = 0;
            varlue2 = 1;
        } else if (totalInto == totalOut) {
            varlue1 = 1;
            varlue2 = 1;
        } else {
            varlue1 = (int) totalInto;
            varlue2 = (int) totalOut;
        }

    }

    private void findView() {
        account = (ImageButton) fragment.findViewById(R.id.account);
        mTextViewTime = (TextView) fragment.findViewById(R.id.tv_main_time);
        listview = (ListView) fragment.findViewById(R.id.list);
        cancel = (Button) fragment.findViewById(R.id.cancel_button);
        add = (Button) fragment.findViewById(R.id.add_button);
    }

    // 执行查询
    void checkExpress(final String str) {
        disView(0);
        handleData.insertData(str);
        //  隐藏键盘
        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(etExpressNum.getWindowToken(), 0);
        etExpressNum.setText(str);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadExpress(str);
            }
        }).start();
    }

    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == SUCCESS) {
                Express express = (Express) msg.getData().getSerializable("json");
                if (express != null) {
                    setData(express);
                }
                List<Content> datalist = express.getContent();
                //SyncHelper.uploadLifeExpress(getContext(),datalist);
                disView(0);
                canVis = true;
            } else {
                Toast.makeText(mContext,msg.getData().getString("mess"), Toast.LENGTH_SHORT).show();
                disView(0);
                canVis = true;
            }
        }
    };

    // 线程解析快递
    private void loadExpress(String pid) {
        ExpressHandleWeb handleWeb = new ExpressHandleWeb();
        String webCode = "";
        try {
            webCode = handleWeb.posturl("http://m.kuaidi100.com/autonumber/auto?num=" + pid, "utf-8");
            if (!webCode.contains("comCode")) {
                sendMessToHandler(ERROR, "快递单号错误!");
                return;
            }
        } catch (Exception me) {
            System.out.println(me.getMessage() + "获取快递类型异常");
            sendMessToHandler(ERROR, me.getMessage());
        }

        Pattern p = Pattern.compile("comCode\":\"(\\w+)\"");
        Matcher matcher = p.matcher(webCode);
        while (matcher.find()) { //每一个快递公司
            //解析json
            String url = "http://www.kuaidi.com/index-ajaxselectcourierinfo-" + pid + "-" + matcher.group(1) + ".html";
            String temp = handleWeb.posturl(url, "utf-8");
            if (parseJson(temp)) {
                return;
            }
        }
        sendMessToHandler(ERROR, "查询失败,请确认单号!");
    }

    private boolean parseJson(String json) {
        Express express = new Express();
        try {
            JSONObject object = new JSONObject(json);
            if (object.getBoolean("success")) {
                express.setName(decodeUnicode(object.getString("company")));
                express.setLogoUrl("http://www.kuaidi.com" + object.getString("ico"));
                express.setOfficialUrl(object.getString("url"));

                JSONArray allContent = object.getJSONArray("data");
                for (int i = 0; i < allContent.length(); i++) {
                    JSONObject data = allContent.getJSONObject(i);
                    String time = data.getString("time");
                    String context = decodeUnicode(data.getString("context"));

                    final Content content = new Content();
                    content.setTime(time);
                    content.setContext(context);
                    express.content.add(content);  //添加进对象；
                }
                List<Content> datalist = express.getContent();
                //SyncHelper.uploadLifeExpress(getContext(),datalist);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("json", express);
                msg.setData(bundle);
                msg.what = SUCCESS;
                handler.sendMessage(msg);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendMessToHandler(ERROR, e.getMessage());
            return false;
        }
    }

    public void sendMessToHandler(int messId, String mess) {
        Message message = new Message();
        message.what = messId;
        Bundle bundle = new Bundle();
        bundle.putString("mess", mess);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private void setData(Express express) {
        MessListAdapter adapter = new MessListAdapter(mContext, express.getContent());
        lvExpressData.setAdapter(adapter);
        List<Content> datalist = express.getContent();
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    // 历史记录的显示隐藏动画
    private void disView(int mode) {
        if (mode == 0 && svSearchList.getVisibility() == View.VISIBLE) {
            svSearchList.setVisibility(View.GONE);
            svSearchList.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_top_out));
        } else if (mode == 1 && svSearchList.getVisibility() == View.GONE) {
            svSearchList.setVisibility(View.VISIBLE);
            svSearchList.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_top_in));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadEvent(MessageEvent messageEvent){
        GetTime();
        reloadAll();
    }

    private void showChangePassword(){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View dialogview = factory.inflate(R.layout.password_dialog_layout, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(dialogview);

        final EditText oldPassword = (EditText) dialogview.findViewById(R.id.password_dialog_old_password);
        final EditText newPassword = (EditText) dialogview.findViewById(R.id.password_dialog_new_password);
        android.app.AlertDialog AD = builder
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
