package com.example.swjtu.databaseexpriment.addRight;

import android.database.Cursor;
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
        rightModuleInput.setHint("所属模块");
        winNameInput.setHint("所属页面");
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
            rightModuleInput.setError("所属模块名不能为空");
        } else if (isEmpty(winName)) {
            winNameInput.setError("所属页不能为空");
        } else {
            try {
                int rightNo = Integer.parseInt(rightNO);
                if (!rightIsExist(rightNo)) {
                    if (!nameIsExist(rightName)) {
                        mySQLiteOpenHelper.getReadableDatabase().execSQL("insert into rights values(null,?,?,?,?)", new Object[]{rightNo, rightName, rightModule, winName});
                        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                        setResult(1);
                        finish();
                    } else {
                        Toast.makeText(this, "权限名重复", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "编号重复", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                rightNOInput.setError("权限编号为整数");
            } catch (Exception e) {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean rightIsExist(int NO) {
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from rights where right_no = ?", new String[]{NO + ""});
        if (cursor != null && cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    private boolean nameIsExist(String name) {
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from rights where right_name = ?", new String[]{name});
        if (cursor != null && cursor.moveToNext()) {
            return true;
        }
        return false;
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

}
