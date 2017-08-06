package com.example.davidwillo.youwo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.davidwillo.youwo.application.MyApplication;
import com.example.davidwillo.youwo.network.HttpResult;
import com.example.davidwillo.youwo.network.NetworkException;
import com.example.davidwillo.youwo.network.NetworkUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private boolean islogin = true;
    private boolean errorflag = true;
    private UserDB userDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final UserDB userDB = new UserDB(this);
        checkPermission();


        Log.d("check exit", ""+getIntent().getBooleanExtra("exit", false));
        if (getIntent().getBooleanExtra("exit", false)) {
            Log.d("check exit!", ""+getIntent().getBooleanExtra("exit", false));
            LoginActivity.this.finish();
            return;
        }


        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("isLogin", false)){
            Intent intent = new Intent(LoginActivity.this, PagerActivity.class);
            MyApplication.getInstance().setAutoLogin(true);
            startActivity(intent);
        }


        final TextInputLayout usernameInput = (TextInputLayout)findViewById(R.id.user_name_login);
        final TextInputEditText usernameEdit = (TextInputEditText) usernameInput.getEditText();
        final TextInputLayout passwordInput = (TextInputLayout)findViewById(R.id.password_login);
        final TextInputEditText passwordEdit = (TextInputEditText) passwordInput.getEditText();



        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    usernameInput.setError(null);
                    errorflag = true;
                } else if (!checkUsername(s.toString())) {
                    usernameInput.setError("用户名必须为6到20位数字与字母组合");
                    errorflag = true;
                } else {
                    usernameInput.setError(null);
                    errorflag = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    passwordInput.setError(null);
                    errorflag = true;
                } else if (s.length() < 8) {
                    passwordInput.setError("密码不得小于8位");
                    errorflag = true;
                } else {
                    passwordInput.setError(null);
                    errorflag = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Button loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (errorflag) return;
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (!userDB.queryName(username)) {
                    if (userDB.confirmUser(username, password)) {
                        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean("isLogin", true).commit();
                        MyApplication.getInstance().setUsername(username);
                        Intent intent = new Intent(LoginActivity.this, PagerActivity.class);
                        MyApplication.getInstance().setAutoLogin(false);
                        startActivity(intent);
                    } else {
                        passwordInput.setError("密码错误");
                    }
                } else {
                    usernameInput.setError("用户不存在");
                }
            }
        });

        final Button registerButton = (Button)findViewById(R.id.register_button);
        final TextView registerSwitcher = (TextView)findViewById(R.id.register_text);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (errorflag) return;
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (userDB.queryName(username)) {
                    userDB.insertData(username, password);
                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                } else {
                    usernameInput.setError("用户名已存在");
                }
                //registerSwitcher.callOnClick();
            }
        });




        registerSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (islogin) {
                    registerButton.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.GONE);
                    registerSwitcher.setText("已有账号？点击登录");
                    islogin = false;
                } else {
                    registerButton.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                    registerSwitcher.setText("没有账号？点击注册");
                    islogin = true;
                }

            }
        });
    }

    public static boolean checkUsername(String username){
        String regex = "([a-zA-Z0-9]{6,20})";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        return m.matches();
    }

    private static String[] PERMISSIONS_Array = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

    private void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CONTACTS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS_Array, 1);
        }
    }


}
