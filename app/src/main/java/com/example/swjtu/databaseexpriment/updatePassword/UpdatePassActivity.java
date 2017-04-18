package com.example.swjtu.databaseexpriment.updatePassword;

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

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.User;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class UpdatePassActivity extends AppCompatActivity {

    private TextInputLayout userNameWrapper, oldPassWrapper, newPassWrapper;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass);
        Intent intent = getIntent();
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
        user = (User) intent.getSerializableExtra("user");
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
        userNameWrapper = (TextInputLayout) findViewById(R.id.userNameWrapper);
        oldPassWrapper = (TextInputLayout) findViewById(R.id.oldPassWrapper);
        newPassWrapper = (TextInputLayout) findViewById(R.id.newPassWrapper);
        userNameWrapper.setHint("用户名");
        oldPassWrapper.setHint("旧密码");
        newPassWrapper.setHint("新密码");
        userNameWrapper.getEditText().setText(user.getUserName());
        oldPassWrapper.getEditText().setText(user.getPassword());
    }

    public void updatePass(View v) {
        String newPass = newPassWrapper.getEditText().getText().toString().trim();
        if (!newPass.equals("")) {
            if (newPass.equals(oldPassWrapper.getEditText().getText().toString().trim())) {
                newPassWrapper.setError("密码与旧密码相同");
            } else {
                try {
                    mySQLiteOpenHelper.getReadableDatabase().execSQL("update users set password = ? where user_name = ?",
                            new String[]{newPass, user.getUserName()});
                    setResult(1,getIntent());
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
