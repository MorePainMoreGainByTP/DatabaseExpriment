package com.example.swjtu.integrateddesign.rightManage;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.swjtu.integrateddesign.R;
import com.example.swjtu.integrateddesign.entity.Manager;


/**
 * Created by tangpeng on 2017/4/9.
 */

public class UpdatePassActivity extends AppCompatActivity {

    private TextInputLayout managerNameWrapper, oldPassWrapper, newPassWrapper;
    private Manager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);
        Intent intent = getIntent();
        manager = (Manager) intent.getSerializableExtra("manager");
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
        managerNameWrapper = (TextInputLayout) findViewById(R.id.userNameWrapper);
        oldPassWrapper = (TextInputLayout) findViewById(R.id.oldPassWrapper);
        newPassWrapper = (TextInputLayout) findViewById(R.id.newPassWrapper);
        managerNameWrapper.setHint("用户名");
        oldPassWrapper.setHint("旧密码");
        newPassWrapper.setHint("新密码");
        managerNameWrapper.getEditText().setText(manager.getUserName());
        oldPassWrapper.getEditText().setText(manager.getPassword());
    }

    public void updatePass(View v) {
        String newPass = newPassWrapper.getEditText().getText().toString().trim();
        if (!newPass.equals("")) {
            if (newPass.equals(oldPassWrapper.getEditText().getText().toString().trim())) {
                newPassWrapper.setError("新密码与旧密码相同");
            } else {
                try {
                    Manager manager2 = new Manager();
                    manager2.setPassword(newPass);
                    manager2.updateAll(" userName = ?", manager.getUserName());
                    setResult(1, getIntent());
                    finish();
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    newPassWrapper.setError("修改失败");
                }
            }
        } else {
            newPassWrapper.setError("密码不能为空");
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
