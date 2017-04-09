package com.example.swjtu.databaseexpriment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private TextInputLayout userNameWrapper, passWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("管理员登录");
        setSupportActionBar(toolbar);
        getViews();
        setViews();
    }

    private void getViews() {
        userNameWrapper = (TextInputLayout) findViewById(R.id.userNameWrapper);
        passWrapper = (TextInputLayout) findViewById(R.id.passWrapper);
    }

    private void setViews() {
        userNameWrapper.setHint("用户名");
        passWrapper.setHint("密码");
    }

    public void login(View v) {
        hideKeyboard();
        String userStr = userNameWrapper.getEditText().getText().toString().trim();
        String passStr = passWrapper.getEditText().getText().toString().trim();
        if (userStr.equals("")) {
            userNameWrapper.setError("用户不能为空");
        } else if (passStr.equals("")) {
            passWrapper.setError("密码不能为空");
        } else if (!validateUserName(userStr)) {
            userNameWrapper.setError("用户不存在");
        } else if (!validatePass(passStr)) {
            passWrapper.setError("密码错误");
        } else {
            doLogin();
        }
    }

    private boolean validateUserName(String userName) {
            return userName.equals("2014112217");
    }

    private boolean validatePass(String pass) {
        return pass.equals("123");
    }

    //登录成功
    private void doLogin() {
        Log.i(TAG, "doLogin: ");
        startActivity(new Intent(this,AllUsersActivity.class));
        finish();
    }

    //隐藏软键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
