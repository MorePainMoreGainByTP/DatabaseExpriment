package com.example.swjtu.databaseexpriment.loginPage;

import android.app.DatePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.AllUsersActivity;
import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.Arguments;
import com.example.swjtu.databaseexpriment.entity.Manager;
import com.example.swjtu.databaseexpriment.entity.User;
import com.example.swjtu.databaseexpriment.userRights.GeneralUserRightsActivity;
import com.idescout.sql.SqlScoutServer;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static boolean isManager = true;
    private CheckBox checkBoxReservePass;
    private TextInputLayout userNameWrapper, passWrapper;
    private SharedPreferences sharedPreferences;
    private RadioButton managerRadio, userRadio;
    private EditText loginDate;

    private int userId = -1;

    private int num, days, lockDays;    //错误次数，退回天数，封锁天数

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
        getArguments();
    }

    private void getArguments() {
        List<Arguments> argumentsList = DataSupport.findAll(Arguments.class);
        if (argumentsList != null && argumentsList.size() > 0) {
            Arguments arguments = argumentsList.get(0);
            num = arguments.getNum();
            days = arguments.getDays();
            lockDays = arguments.getLockDays();
        } else {
            num = 3;
            days = 3;
            lockDays = 1;

            Arguments arguments = new Arguments();
            arguments.setDays(days);
            arguments.setLockDays(lockDays);
            arguments.setNum(num);
            arguments.save();

            Manager manager = new Manager();
            manager.setFaultTime(0);
            manager.setPassword("123");
            manager.setUserName("manager1");
            manager.setUnlockTime(Calendar.getInstance().getTime());
            manager.save();

            Manager manager2 = new Manager();
            manager2.setFaultTime(0);
            manager2.setPassword("123");
            manager2.setUserName("manager2");
            manager2.setUnlockTime(Calendar.getInstance().getTime());
            manager2.save();
        }
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
        loginDate = (EditText) findViewById(R.id.loginDate);
        managerRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginDate.setVisibility(View.VISIBLE);
                } else {
                    loginDate.setVisibility(View.GONE);
                }
            }
        });
        loginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        loginDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.create();
                datePickerDialog.show();
            }
        });
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

    public void login(View v) throws ParseException {
        if (managerRadio.isChecked())
            isManager = true;
        else isManager = false;

        hideKeyboard();
        String userStr = userNameWrapper.getEditText().getText().toString().trim();
        String passStr = passWrapper.getEditText().getText().toString().trim();
        String dateStr = loginDate.getText().toString().trim();
        if (isManager && (dateStr.equals("") || dateStr.equals("登录日期")) ) {
            Toast.makeText(this, "日期不能为空!", Toast.LENGTH_SHORT).show();
        } else if (userStr.equals("")) {
            userNameWrapper.setError("用户不能为空");
        } else if (passStr.equals("")) {
            passWrapper.setError("密码不能为空");
        } else if (isManager && !validateLoginDate(dateStr)) {
            Toast.makeText(this, "日期不合法!", Toast.LENGTH_SHORT).show();
        } else if (!validateUserName(userStr)) {
            userNameWrapper.setError("用户不存在");
            userNameWrapper.getEditText().selectAll();
        } else if (!validatePass(userStr, passStr)) {
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

    private boolean validateLoginDate(String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date loginDate = simpleDateFormat.parse(dateStr);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar2.setTime(loginDate);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        if (calendar2.compareTo(calendar1) <= 0) {
            calendar1.add(Calendar.DAY_OF_MONTH, -days - 1);
            if (calendar2.compareTo(calendar1) >= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean validateUserName(String userName) {
        if (isManager) {
            List<Manager> managerList = DataSupport.where("userName = ?", userName).find(Manager.class);
            if (managerList != null && managerList.size() > 0) {
                return true;
            }
        } else {
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
        if (isManager) {
            List<Manager> managerList = DataSupport.where("userName = ?", user).find(Manager.class);
            if (managerList != null && managerList.size() > 0) {
                Manager manager = managerList.get(0);
                if (manager.getFaultTime() >= num) {
                    Calendar calendar = Calendar.getInstance();
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(manager.getUnlockTime());
                    if (calendar1.compareTo(calendar) > 0) {
                        passWrapper.setError("账户被封锁，解锁日期:" + manager.getUnlockTime().toLocaleString());
                        return false;
                    } else {
                        manager.setFaultTime(0);
                        manager.save();
                    }
                }
                if (manager.getPassword().equals(pass)) {
                    manager.setFaultTime(0);
                    manager.save();
                    return true;
                } else {
                    manager.setFaultTime(manager.getFaultTime() + 1);
                    passWrapper.setError("密码错误，出错次数剩余：" + (num - manager.getFaultTime()) + "次");
                    if (manager.getFaultTime() >= num) {
                        Calendar calendar = Calendar.getInstance();
                        Log.i(TAG, "calendar: " + calendar.get(Calendar.DAY_OF_MONTH));
                        calendar.add(Calendar.DAY_OF_MONTH, lockDays);
                        Log.i(TAG, "calendar: " + calendar.get(Calendar.DAY_OF_MONTH));
                        manager.setUnlockTime(calendar.getTime());

                    }
                    manager.save();
                    return false;
                }
            }
        } else {
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
            return false;
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
}
