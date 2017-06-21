package com.example.swjtu.integrateddesign.rightManage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.swjtu.integrateddesign.R;
import com.example.swjtu.integrateddesign.entity.Manager;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class AddNewUserActivity extends AppCompatActivity {
    private static final String TAG = "AddNewUserActivity";

    private TextInputLayout userNameInputLayout, passInputLayout, passAgainInputLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        setActionBar();
        getViews();

    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getViews() {
        userNameInputLayout = (TextInputLayout) findViewById(R.id.userNameWrapper);
        passInputLayout = (TextInputLayout) findViewById(R.id.passWrapper);
        passAgainInputLayout = (TextInputLayout) findViewById(R.id.passAgainWrapper);

        userNameInputLayout.setHint("用户名");
        passInputLayout.setHint("密码");
        passAgainInputLayout.setHint("确认密码");
    }

    //先数据库中添加新用户
    public void addNewUser(View v) {
        //hideKeyboard();
        String userName = userNameInputLayout.getEditText().getText().toString().trim();
        String pass = passInputLayout.getEditText().getText().toString().trim();
        String passAgain = passAgainInputLayout.getEditText().getText().toString().trim();
        if (!userName.equals("")) {
            if (!pass.equals("")) {
                if (pass.equals(passAgain)) {
                    List<Manager> managerList = DataSupport.where("userName = ?", userName).find(Manager.class);
                    if (managerList.size() > 0) {
                        userNameInputLayout.setError("该用户名已存在");
                    } else {
                        Manager manager = new Manager();
                        manager.setUserName(userName);
                        manager.setPassword(pass);
                        manager.saveRightList();
                        manager.save();
                        Intent intent = getIntent();
                        setResult(1, intent);
                        finish();
                    }
                } else {
                    passInputLayout.setError("两次密码不相同");
                }
            } else {
                passAgainInputLayout.setError("密码不能为空");
            }
        } else {
            userNameInputLayout.setError("用户名不能为空");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
