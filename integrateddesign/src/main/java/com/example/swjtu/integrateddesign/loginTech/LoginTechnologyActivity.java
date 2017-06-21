package com.example.swjtu.integrateddesign.loginTech;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.swjtu.integrateddesign.BaseActivity;
import com.example.swjtu.integrateddesign.MainActivity;
import com.example.swjtu.integrateddesign.R;
import com.example.swjtu.integrateddesign.entity.Arguments;
import com.example.swjtu.integrateddesign.entity.Manager;
import com.example.swjtu.integrateddesign.entity.Root;
import com.example.swjtu.integrateddesign.rightManage.RootPageActivity;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tangpeng on 2017/6/15.
 */

public class LoginTechnologyActivity extends BaseActivity {

    private static final String TAG = "LoginTechnologyActivity";

    public static boolean isRoot = true;
    private CheckBox checkBoxReservePass;
    private TextInputLayout userNameWrapper, passWrapper;
    private SharedPreferences sharedPreferences;
    private RadioButton rootRadio, managerRadio;
    private EditText loginDate;

    private int num, days, lockDays;    //错误次数，退回天数，封锁天数
    private int userId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tech);
        sharedPreferences = getSharedPreferences("user_pass", MODE_PRIVATE);
        getViews();
        setViews();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
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
        }
        Log.i(TAG, "argumentsList:" + argumentsList);

        Log.i(TAG, "Root:" + DataSupport.findAll(Root.class));
    }

    private void getViews() {
        userNameWrapper = (TextInputLayout) findViewById(R.id.userNameWrapper);
        passWrapper = (TextInputLayout) findViewById(R.id.passWrapper);
        checkBoxReservePass = (CheckBox) findViewById(R.id.checkBoxReservePass);
        rootRadio = (RadioButton) findViewById(R.id.radioManager);
        managerRadio = (RadioButton) findViewById(R.id.radioUser);
        loginDate = (EditText) findViewById(R.id.loginDate);
        rootRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(LoginTechnologyActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        isRoot = sharedPreferences.getBoolean("isRoot", true);
        if (isRoot) {
            rootRadio.setChecked(true);
        } else {
            managerRadio.setChecked(true);
        }
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
            userNameWrapper.getEditText().setText(userStr);
            passWrapper.getEditText().setText(passStr);
        } else {
            checkBoxReservePass.setChecked(false);
        }
        if (logout) {
            passWrapper.getEditText().setText("");
            userNameWrapper.getEditText().setText(sharedPreferences.getString("userName", ""));
        }
    }

    public void login(View v) throws ParseException {
        if (rootRadio.isChecked())
            isRoot = true;
        else isRoot = false;

        hideKeyboard();
        String userStr = userNameWrapper.getEditText().getText().toString().trim();
        String passStr = passWrapper.getEditText().getText().toString().trim();
        String dateStr = loginDate.getText().toString().trim();
        if (dateStr.equals("") || dateStr.equals("登录日期")) {
            Toast.makeText(this, "日期不能为空!", Toast.LENGTH_SHORT).show();
        } else if (userStr.equals("")) {
            userNameWrapper.setError("用户不能为空");
        } else if (passStr.equals("")) {
            passWrapper.setError("密码不能为空");
        } else if (!validateLoginDate(dateStr)) {
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
                editor.putBoolean("isRoot", isRoot);
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
        //calendar2.add(Calendar.DAY_OF_MONTH,-days);
        if (calendar2.compareTo(calendar1) <= 0) {
            calendar1.add(Calendar.DAY_OF_MONTH, -days - 1);
            if (calendar2.compareTo(calendar1) >= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean validateUserName(String userName) {
        if (isRoot) {
            List<Root> rootList = DataSupport.where("userName = ?", userName).find(Root.class);
            if (rootList != null && rootList.size() > 0) {
                return true;
            }
        } else {
            List<Manager> managerList = DataSupport.where("userName = ?", userName).find(Manager.class);
            if (managerList != null && managerList.size() > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePass(String user, String pass) {
        if (isRoot) {
            List<Root> rootList = DataSupport.where("userName = ?", user).find(Root.class);
            if (rootList != null && rootList.size() > 0) {
                Root root = rootList.get(0);
                //root.setFaultTime(0);
                //root.setUnlockTime(Calendar.getInstance().getTime());
                if (root.getFaultTime() >= num) {
                    Calendar calendar = Calendar.getInstance();
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(root.getUnlockTime());
                    if (calendar1.compareTo(calendar) > 0) {
                        passWrapper.setError("账户被封锁，解锁日期:" + root.getUnlockTime().toLocaleString());
                        return false;
                    } else {
                        root.setFaultTime(0);
                        root.save();
                    }
                }
                if (root.getPassword().equals(pass)) {
                    root.setFaultTime(0);
                    root.save();
                    return true;
                } else {
                    root.setFaultTime(root.getFaultTime() + 1);
                    passWrapper.setError("密码错误，出错次数剩余：" + (num - root.getFaultTime()) + "次");
                    if (root.getFaultTime() >= num) {
                        Calendar calendar = Calendar.getInstance();
                        Log.i(TAG, "calendar: " + calendar.get(Calendar.DAY_OF_MONTH));
                        calendar.add(Calendar.DAY_OF_MONTH, lockDays);
                        Log.i(TAG, "calendar: " + calendar.get(Calendar.DAY_OF_MONTH));
                        root.setUnlockTime(calendar.getTime());

                    }
                    root.save();
                    return false;
                }
            }
        } else {
            List<Manager> managerList = DataSupport.where("userName = ? and password = ?", user, pass).find(Manager.class);
            if (managerList != null && managerList.size() > 0) {
                return true;
            } else {
                passWrapper.setError("密码错误");
            }
        }
        return false;
    }

    //登录成功
    private void doLogin(String userStr, String passStr) {
        if (isRoot)
            startActivity(new Intent(this, RootPageActivity.class));
        else
            startActivity(new Intent(this, ManagerPageActivity.class));
        //.putExtra("user", new User(userId, userStr, passStr)));
        finish();
        MainActivity.hasLogin = true;

        Intent intent = new Intent("com.example.swjtu.integrateddesign.LOGIN_SUCCESS").putExtra("userName", userStr).putExtra("isRoot", isRoot);
        sendBroadcast(intent);
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
