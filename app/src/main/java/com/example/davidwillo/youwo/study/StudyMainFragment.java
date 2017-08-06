package com.example.davidwillo.youwo.study;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.davidwillo.youwo.LoginActivity;
import com.example.davidwillo.youwo.R;
import com.example.davidwillo.youwo.application.MyApplication;
import com.example.davidwillo.youwo.network.HttpResult;
import com.example.davidwillo.youwo.network.NetworkException;
import com.example.davidwillo.youwo.network.NetworkUtil;
import com.example.davidwillo.youwo.network.SyncHelper;
import com.example.davidwillo.youwo.network.model.Course;
import com.example.davidwillo.youwo.util.MessageEvent2;
import com.example.davidwillo.youwo.util.MyImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class StudyMainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout = null;
    View fragment;




    public static final String PREFERENCE_NAME = "settings";
    private static final int UpdateTimetable = 1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    private TextView timetable_title, timetable_hint;
    private GridView timetable_gridview;
    private CookieManager cookieManager;
    private String username;
    private String getURL = "";
    private TimetableAdapter timetableAdapter;

    NavigationView navigationView;

   private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UpdateTimetable:
                    timetable_hint.setVisibility(View.GONE);
                    timetable_gridview.setVisibility(View.VISIBLE);
                    ArrayList<Course> courses = (ArrayList<Course>) msg.obj;
                    timetableAdapter.UpdateTimeTable(courses);
            }
        }
    };
    private boolean scrolllock;




    public StudyMainFragment() {
        super();
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
          fragment = inflater.inflate(R.layout.study_main_fragment, container, false);
          drawerLayout = (DrawerLayout)fragment.findViewById(R.id.drawer_study_layout);
          drawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
              @Override
              public void onFocusChange(View v, boolean hasFocus) {
                  if (hasFocus) {
                      drawerLayout.closeDrawer(Gravity.LEFT);
                  }
              }
          });


        sharedPreferences = getActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        username = MyApplication.getInstance().getUsername();
        cookieManager = CookieManager.getInstance();
        timetableAdapter = new TimetableAdapter(getContext(), username);
        findView();
        timetable_gridview.setAdapter(timetableAdapter);

        scrolllock = false;
        timetable_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (scrolllock == false) {
                    ArrayList<Course> courses = timetableAdapter.getCourses();
                    if (courses.get(position).getLength() != 0) {
                        int index = courses.get(position).getMyindex();
                        String title = courses.get(index).getName();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", title);
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), HomeworkActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });
        timetable_gridview.setFastScrollEnabled(false);



        navigationView = (NavigationView) fragment.findViewById(R.id.nav_view_study);

        navigationView.setNavigationItemSelectedListener(this);

        loadHeader();
        return fragment;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.check_course_table) {
            if (!NetworkIsAvaliable()) {
                System.out.println("断网");
                Toast.makeText(getActivity(), "哎哟好像断网了(╯﹏╰)", Toast.LENGTH_SHORT).show();
            } else if (!isLogin()) {
                Login();
            } else {
                ShowDialog();
            }
        } else if (id == R.id.check_score) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ScoreActivity.class);
            startActivity(intent);
            System.out.println("123");
        } else if (id == R.id.password_change) {
            Snackbar.make(fragment,"password_change", Snackbar.LENGTH_SHORT).show();
            showChangePassword();
        } else if (id == R.id.log_out) {
            Snackbar.make(fragment,"log_out", Snackbar.LENGTH_SHORT).show();
            MyApplication.getInstance().setLogout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.sync_study) {
            Snackbar.make(fragment, "sync study", Snackbar.LENGTH_SHORT).show();
            SyncHelper.uploadCourseRecord(getContext());
            SyncHelper.uploadHomeworkRecord(getContext());
        }

        if (drawerLayout != null )
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        //drawerLayout.closeDrawer(Gravity.LEFT);
    }



    private void loadHeader() {
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.username)).setText(MyApplication.getInstance().getUsername());
        MyImageLoader.displayImage(getContext(),"http://cdnq.duitang.com/uploads/item/201501/16/20150116145231_3xcYy.jpeg",
                ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.avatar)));
    }




    private void ShowDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View DialogView = factory.inflate(R.layout.timetable_selecte_term, null);
        builder.setView(DialogView);
        builder.setTitle("选择学期");
        final Spinner year = (Spinner) DialogView.findViewById(R.id.selectYear);
        final Spinner term = (Spinner) DialogView.findViewById(R.id.selectTerm);
        final String[] years = getYears();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        year.setAdapter(adapter);
        getURL = "http://wjw.sysu.edu.cn/api/timetable_new?";
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedYear = year.getSelectedItem().toString();
                String selectedTerm = term.getSelectedItem().toString();
                if (selectedYear == "" || selectedTerm == "") {
                    selectedYear = years[0];
                    selectedTerm = "";
                }
                getURL += "year=" + selectedYear + "&term=";
                String queryTerm = "";
                switch (selectedTerm) {
                    case "":
                        queryTerm = "";
                        break;
                    case "第一学期":
                        queryTerm = "1";
                        break;
                    case "第二学期":
                        queryTerm = "2";
                        break;
                    case "第三学期":
                        queryTerm = "3";
                        break;
                }
                getURL += queryTerm;
                timetable_title.setText(selectedYear + "学年 " + selectedTerm);
                System.out.println("URL:" + getURL);
                RequestData(getURL);
            }
        });
        builder.create();
        builder.show();
    }

    private String[] getYears() {
        String[] years = new String[4];
        SimpleDateFormat formater = new SimpleDateFormat("yyyy");
        Date currentDate = new Date(System.currentTimeMillis());
        String currentYear = formater.format(currentDate);
        System.out.println(currentYear);
        int now;
        now = Integer.parseInt(currentYear);
        ;
        ;
        for (int i = 0; i < 4; i++) {
            years[i] = Integer.toString(now) + "-" + Integer.toString(now + 1);
            now--;
        }
        return years;
    }

    private boolean NetworkIsAvaliable() {
        ConnectivityManager manager = (ConnectivityManager)getActivity().getSystemService((Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo != null) {
            return networkinfo.isAvailable();
        }
        return false;
    }

    private void RequestData(final String getURL) {
        timetable_gridview.setVisibility(View.INVISIBLE);
        timetable_hint.setVisibility(View.VISIBLE);
        final String cookie = cookieManager.getCookie("http://wjw.sysu.edu.cn/");
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String data;
                    URL url = new URL(getURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", cookie);
                    if (connection.getResponseCode() == 200) {
                        InputStream stream = connection.getInputStream();
                        data = ConvertInputStreamToString(stream);
                        getTimetable(data);
                    } else {
                        System.out.println("Request Failure!:" + connection.getResponseCode());
                    }
                } catch (MalformedURLException e) {
                    System.out.println("URL has problem");
                } catch (IOException e) {
                    System.out.println("IOException");
                }
            }
        }.start();
    }

    private void getTimetable(String data) {
        org.jsoup.nodes.Document doc = Jsoup.parse(data);
        Elements elements = doc.getElementsByTag("tr");
        ArrayList<Course> courses = new ArrayList<Course>();
        for (int i = 0; i < 128; i++) {
            courses.add(new Course("", "", "", "", 0, 0, -1));
        }
        Elements days = elements.get(0).getElementsByTag("td");
        for (int i = 0; i < 8; i++) {
            if (!days.get(i).html().equals("&nbsp;"))
                courses.get(i).setName(days.get(i).html());
        }
        for (int i = 1; i <= 15; i++) {
            Elements course = elements.get(i).getElementsByTag("td");
            courses.get(8 * i).setName(course.get(0).html());
            int index = 1;
            for (int j = 1; j < course.size(); j++) {
                while (courses.get(8 * i + index).getLength() != 0) {
                    index++;
                }
                if (course.get(j).className().equals("tab_1")) {
                    int length = Integer.parseInt(course.get(j).attr("rowspan"));
                    int num = index + 8 * i;
                    for (int nextRow = 0; nextRow < length; nextRow++) {
                        courses.get(num + nextRow * 8).setLength(length);
                        courses.get(num + nextRow * 8).setMyindex(8 * i + index);
                        courses.get(num + nextRow * 8).setPlace(nextRow);
                        System.out.println(courses.get(num + nextRow * 8).getName());
                        System.out.println(courses.get(num + nextRow * 8).getPlace());
                    }
                    String[] details = course.get(j).html().split("<br>");
                    Course targetCourse = courses.get(num);
                    targetCourse.setName(details[0]);
                    targetCourse.setClassroom(details[1]);
                    targetCourse.setTime(details[2]);
                    targetCourse.setPeriod(details[3]);
                }
                index++;
            }
        }
        Message message = new Message();
        message.what = UpdateTimetable;
        message.obj = courses;
        handler.sendMessage(message);
    }

    private void Login() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), StudyLoginActivity.class);
        startActivityForResult(intent, 0);
    }

    private String ConvertInputStreamToString(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String result = "";
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                sb.append(line + '\n');
            }
        } catch (IOException e) {
            System.out.println("Problem Occur When Analayze The Stream!");
        }
        result = sb.toString();
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShowDialog();
    }

    private boolean isLogin() {
        String cookie = cookieManager.getCookie("http://wjw.sysu.edu.cn/");
        Log.v("test", "cookie is:" + cookie);
        if (cookie == null) {
            System.out.println("Cookie:is null");
            return false;
        } else {
            return cookie.contains("sno");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadEvent(MessageEvent2 messageEvent){
        timetableAdapter = new TimetableAdapter(getContext(), MyApplication.getInstance().getUsername());
        timetable_gridview.setAdapter(timetableAdapter);
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

    private void findView() {
        timetable_title = (TextView)fragment.findViewById(R.id.timetable_title);
        timetable_hint = (TextView)fragment.findViewById(R.id.timetable_hint);
        timetable_gridview = (GridView)fragment.findViewById(R.id.timetable_gridview);
    }
}
