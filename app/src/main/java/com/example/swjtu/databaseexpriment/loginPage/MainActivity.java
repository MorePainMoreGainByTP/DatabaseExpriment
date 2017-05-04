package com.example.swjtu.databaseexpriment.loginPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.example.swjtu.databaseexpriment.AllUsersActivity;
import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.User;
import com.example.swjtu.databaseexpriment.userRights.GeneralUserRightsActivity;
import com.idescout.sql.SqlScoutServer;

public class MainActivity extends AppCompatActivity {

    public static boolean isManager = true;
    private static final String TAG = "MainActivity";
    private CheckBox checkBoxReservePass;
    private TextInputLayout userNameWrapper, passWrapper;
    private SharedPreferences sharedPreferences;
    private RadioButton managerRadio, userRadio;

    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || getIntent().getBooleanExtra("logout", false)) {
            SqlScoutServer.create(this, getPackageName());
        }
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("user_pass", MODE_PRIVATE);
        getViews();
        setViews();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
        hideTop();
    }

    //使状态栏透明，内容延伸到状态栏
    private void hideTop() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decoView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE; //两者结合才能让主题内容占用系统状态栏
            decoView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void getViews() {
        userNameWrapper = (TextInputLayout) findViewById(R.id.userNameWrapper);
        passWrapper = (TextInputLayout) findViewById(R.id.passWrapper);
        checkBoxReservePass = (CheckBox) findViewById(R.id.checkBoxReservePass);
        managerRadio = (RadioButton) findViewById(R.id.radioManager);
        userRadio = (RadioButton) findViewById(R.id.radioUser);
    }

    private void setViews() {
        isManager = sharedPreferences.getBoolean("isManager", true);
        if (isManager)
            managerRadio.setChecked(true);
        else userRadio.setChecked(true);

        userNameWrapper.setHint("用户名");
        passWrapper.setHint("密码");
        boolean logout = false;
        if (getIntent() != null) {
            logout = getIntent().getBooleanExtra("logout", false);
        }
        boolean reservePass = sharedPreferences.getBoolean("reservePass", false);
        if (reservePass) {
            checkBoxReservePass.setChecked(true);
            String userStr = sharedPreferences.getString("userName", "");
            String passStr = sharedPreferences.getString("pass", "");
            if (!userStr.equals("") && !passStr.equals("")) {
                userNameWrapper.getEditText().setText(userStr);
                passWrapper.getEditText().setText(passStr);
            }
        } else {
            checkBoxReservePass.setChecked(false);
        }
        if (logout) {
            passWrapper.getEditText().setText("");
            userNameWrapper.getEditText().selectAll();
        }
    }

    public void login(View v) {
        if (managerRadio.isChecked())
            isManager = true;
        else isManager = false;

        hideKeyboard();
        String userStr = userNameWrapper.getEditText().getText().toString().trim();
        String passStr = passWrapper.getEditText().getText().toString().trim();
        if (userStr.equals("")) {
            userNameWrapper.setError("用户不能为空");
        } else if (passStr.equals("")) {
            passWrapper.setError("密码不能为空");
        } else if (!validateUserName(userStr)) {
            userNameWrapper.setError("用户不存在");
            userNameWrapper.getEditText().selectAll();
        } else if (!validatePass(userStr, passStr)) {
            passWrapper.setError("密码错误");
            passWrapper.getEditText().selectAll();
        } else {
            doLogin(userStr, passStr);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (checkBoxReservePass.isChecked()) {
                editor.putBoolean("reservePass", true);
                editor.putString("userName", userStr);
                editor.putString("pass", passStr);
                editor.putBoolean("isManager", isManager);
            } else {
                editor.putBoolean("reservePass", false);
            }
            editor.apply();
        }
    }

    private boolean validateUserName(String userName) {
        if (isManager)
            return userName.equals("2014112217");
        else {
            MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
            Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from users where user_name = ?", new String[]{userName});
            if (cursor != null && cursor.moveToNext()) {
                return true;
            }
            if (cursor != null)
                cursor.close();
            else {
                Log.i(TAG, "用户：没有查询结果");
            }
        }
        return false;
    }

    private boolean validatePass(String user, String pass) {
        if (isManager)
            return pass.equals("123");
        else {
            MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
            Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from users where user_name = ? and password = ?", new String[]{user, pass});
            if (cursor != null && cursor.moveToNext()) {
                userId = cursor.getInt(0);
                return true;
            }
            if (cursor != null)
                cursor.close();
            else {
                Log.i(TAG, "用户：没有查询结果");
            }
        }
        return false;
    }

    //登录成功
    private void doLogin(String userStr, String passStr) {
        Log.i(TAG, "doLogin: ");
        if (isManager)
            startActivity(new Intent(this, AllUsersActivity.class));
        else
            startActivity(new Intent(this, GeneralUserRightsActivity.class).putExtra("user", new User(userId, userStr, passStr)));
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

    private void testDB() {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from users", new String[]{});
        while (cursor != null && cursor.moveToNext()) {
            Log.i(TAG, "用户：" + cursor.getInt(0) + "," + cursor.getString(1) + "," + cursor.getString(2));
        }
        if (cursor != null)
            cursor.close();
        else {
            Log.i(TAG, "用户：没有查询结果");
        }
        Cursor cursor1 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from rights", new String[]{});
        while (cursor1 != null && cursor1.moveToNext()) {
            Log.i(TAG, "权限：" + cursor1.getInt(0) + "," + cursor1.getInt(1) + "," + cursor1.getString(2) + "," + cursor1.getString(3) + "," + cursor1.getString(4));
        }
        if (cursor1 != null)
            cursor1.close();
        else {
            Log.i(TAG, "权限：没有查询结果");
        }
        Cursor cursor2 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from user_rights", new String[]{});
        while (cursor2 != null && cursor2.moveToNext()) {
            Log.i(TAG, "权限分配：" + cursor2.getInt(0) + "," + cursor2.getInt(1) + "," + cursor2.getInt(2));
        }
        if (cursor2 != null)
            cursor2.close();
        else {
            Log.i(TAG, "权限分配：没有查询结果");
        }
    }
}
