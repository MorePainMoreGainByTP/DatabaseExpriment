package com.example.swjtu.databaseexpriment.addNewUser;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class AddNewUserActivity extends AppCompatActivity {
    private static final String TAG = "AddNewUserActivity";

    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private TextInputLayout userNameInputLayout, passInputLayout, passAgainInputLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        setActionBar();
        getViews();
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
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
        hideKeyboard();
        String userName = userNameInputLayout.getEditText().getText().toString().trim();
        String pass = passInputLayout.getEditText().getText().toString().trim();
        String passAgain = passAgainInputLayout.getEditText().getText().toString().trim();
        if (!userName.equals("")) {
            if (!pass.equals("")) {
                if (pass != (passAgain)) {
                    if (insertIntoDB(userName, pass) == 2) {
                        userNameInputLayout.setError("该用户名已存在");
                        Toast.makeText(this, "用户名已存在，添加失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        intent.putExtra("userName", userName);
                        intent.putExtra("pass", pass);
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

    private int insertIntoDB(String userName, String pass) {
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from users where user_name = ?", new String[]{userName});
        if (cursor != null && cursor.moveToNext()) {
            return 2;   //改用户已经存在
        }
        mySQLiteOpenHelper.getReadableDatabase().execSQL("insert into users values(null,?,?)", new String[]{userName, pass});
        return 1;   //插入成功
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

    //隐藏软键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
