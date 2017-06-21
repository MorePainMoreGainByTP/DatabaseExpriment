package com.example.swjtu.databaseexpriment.manageRights;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.RightModuleRecyclerAdapter;
import com.example.swjtu.databaseexpriment.addRight.AddRightActivity;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.Right;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/18.
 */

public class ManageRightsActivity extends AppCompatActivity {
    private static final String TAG = "ManageRightsActivity";

    private RecyclerView recyclerView;
    List<Right> rightList;
    private RightModuleRecyclerAdapter rightModuleRecyclerAdapter;
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rights);
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
        setActionBar();
        getViews();
        initData();
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerManageRights);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initData() {
        rightList = new ArrayList<>();
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from rights", new String[]{});
        while (cursor != null && cursor.moveToNext()) {
            rightList.add(new Right(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        rightModuleRecyclerAdapter = new RightModuleRecyclerAdapter(rightList, true);
        recyclerView.setAdapter(rightModuleRecyclerAdapter);
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public void deleteRights(View v) {
        final ArrayList<Integer> rightID = new ArrayList<>();
        int childCount = recyclerView.getChildCount();
        //收集被选中的权限
        for (int i = 0; i < childCount; i++) {
            View root = recyclerView.getChildAt(i);
            RecyclerView oneRight = (RecyclerView) root.findViewById(R.id.recyclerRightItem);
            int grandCount = oneRight.getChildCount();
            for (int j = 0; j < grandCount; j++) {
                View grandRoot = oneRight.getChildAt(j);
                CheckBox checkBoxSelected = (CheckBox) grandRoot.findViewById(R.id.checkBoxSelected);
                if (checkBoxSelected.isChecked()) {
                    TextView rightTxt = (TextView) oneRight.findViewById(R.id.txtOneRight);
                    String rightStr = rightTxt.getText().toString().trim();
                    for (Right right : rightList) {
                        if (right.getRightName().equals(rightStr)) {
                            rightID.add(right.getID());
                            break;
                        }
                    }
                }
            }
        }
        if (rightID.size() > 0) {
            new AlertDialog.Builder(this).setMessage("删除所选权限？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mySQLiteOpenHelper.getReadableDatabase().execSQL("PRAGMA foreign_keys = ON");
                    for (int id : rightID) {
                        mySQLiteOpenHelper.getReadableDatabase().execSQL("delete from rights where id = ?", new Object[]{id});
                    }
                    initData();
                }
            }).setNegativeButton("否", null).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_rights, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.addRight:
                startActivityForResult(new Intent(this, AddRightActivity.class), 1);
                break;
            case R.id.checkTables:
                Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from rights", new String[]{});
                Log.i(TAG, "权限表: ");
                while (cursor != null && cursor.moveToNext()) {
                    Log.i(TAG, "权限：" + cursor.getInt(0) + "," + cursor.getInt(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + "," + cursor.getString(4));
                }

                cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from users", new String[]{});
                Log.i(TAG, "用户表: ");
                while (cursor != null && cursor.moveToNext()) {
                    Log.i(TAG, "用户：" + cursor.getInt(0) + "," + cursor.getString(1) + "," + cursor.getString(2));
                }

                cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from user_rights", new String[]{});
                Log.i(TAG, "权限分配表: ");
                while (cursor != null && cursor.moveToNext()) {
                    Log.i(TAG, "分配：" + cursor.getInt(0) + "," + cursor.getInt(1) + "," + cursor.getInt(2));
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    initData();
                }
                break;
        }
    }
}
