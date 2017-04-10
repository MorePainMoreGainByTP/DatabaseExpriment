package com.example.swjtu.databaseexpriment.addRight;

import android.content.Context;
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

public class AddRightActivity extends AppCompatActivity {

    private TextInputLayout rightNOInput, rightNameInput, rightModuleInput, winNameInput;
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_right);
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
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
        rightNOInput = (TextInputLayout) findViewById(R.id.rightNOWrapper);
        rightNameInput = (TextInputLayout) findViewById(R.id.rightNameWrapper);
        rightModuleInput = (TextInputLayout) findViewById(R.id.rightModuleWrapper);
        winNameInput = (TextInputLayout) findViewById(R.id.winNameWrapper);
        rightNOInput.setHint("权限编号");
        rightNameInput.setHint("权限名称");
        rightModuleInput.setHint("权限所属模块名");
        winNameInput.setHint("权限所属页面名");
    }

    public void addNewRight(View v) {
        String rightNO = rightNOInput.getEditText().getText().toString().trim();
        String rightName = rightNameInput.getEditText().getText().toString().trim();
        String rightModule = rightModuleInput.getEditText().getText().toString().trim();
        String winName = winNameInput.getEditText().getText().toString().trim();
        if (isEmpty(rightNO)) {
            rightNOInput.setError("权限编号不能为空");
        } else if (isEmpty(rightName)) {
            rightNameInput.setError("权限名称不能为空");
        } else if (isEmpty(rightModule)) {
            rightModuleInput.setError("权限所属模块名不能为空");
        } else if (isEmpty(winName)) {
            winNameInput.setError("权限所属页不能为空");
        } else {
            try {
                int rightNo = Integer.parseInt(rightNO);
                mySQLiteOpenHelper.getReadableDatabase().execSQL("insert into rights values(null,?,?,?,?)", new Object[]{rightNo, rightName, rightModule, winName});
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                finish();
            } catch (NumberFormatException e) {
                rightNOInput.setError("权限编号为整数");
            } catch (Exception e) {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean isEmpty(String str) {
        return str == null || str.equals("");
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
