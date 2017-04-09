package com.example.swjtu.databaseexpriment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.adapter.AllUsersRecyclerAdapter;
import com.example.swjtu.databaseexpriment.addNewUser.AddNewUserActivity;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.User;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class AllUsersActivity extends AppCompatActivity {
    private static final String TAG = "AllUsersActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Button addUser;
    private LinearLayout bottomLayout;
    private CheckBox checkBoxSelectAll;

    private AllUsersRecyclerAdapter allUsersRecyclerAdapter;
    private ArrayList<User> userNames = new ArrayList<>();
    private boolean isDeleting = false; //是否正在删除

    private MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        setActionBar();
        getViews();
        initViews();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void getViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerAllUsers);
        addUser = (Button) findViewById(R.id.btnAddUser);
        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);
        checkBoxSelectAll = (CheckBox) findViewById(R.id.checkBoxSelectAll);
    }

    private void initViews() {
        swipeRefreshLayout.setColorSchemeResources(new int[]{R.color.red, R.color.colorBlue});
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCourses();
            }
        });
        getUserNames();
        allUsersRecyclerAdapter = new AllUsersRecyclerAdapter(userNames);
        recyclerView.setAdapter(allUsersRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectAll(isChecked);
            }
        });
    }

    private void getUserNames() {
        //从数据库中获取用户
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from users", new String[]{});
        while (cursor != null && cursor.moveToNext()) {
            User user = new User(cursor.getString(1), cursor.getString(2));
            userNames.add(user);
        }
        if (cursor != null)
            cursor.close();
    }

    public void addUser(View v) {
        startActivityForResult(new Intent(this, AddNewUserActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    User user = new User(data.getStringExtra("userName"), data.getStringExtra("pass"));
                    userNames.add(user);
                    allUsersRecyclerAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public void cancel(View v) {
        addUser.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(GONE);
        setCheckBoxVisibility(false);
        selectAll(false);
        isDeleting = false;
        checkBoxSelectAll.setChecked(false);
    }

    public void delete(View v) {
        new AlertDialog.Builder(this).setMessage("是否删除所选用户？").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Integer> itemIndex = new ArrayList<>();
                ArrayList<String> deletedUsers = new ArrayList<>();
                int childCount = allUsersRecyclerAdapter.getItemCount();
                Log.i(TAG, "onClick: childCount "+childCount);
                for (int i = 0; i < childCount; i++) {
                    View root = recyclerView.getChildAt(i);
                    if (((CheckBox) root.findViewById(R.id.checkBoxDelete)).isChecked()) {
                        itemIndex.add(i);
                        deletedUsers.add(((TextView) root.findViewById(R.id.userName)).getText().toString());
                    }
                }
                if (itemIndex.size() > 0) {
                    Log.i(TAG, itemIndex+"\n"+deletedUsers);
                    Log.i(TAG, "userNames.size():"+userNames.size());
                    Log.i(TAG, "userNames: "+userNames);
                    for(User user:userNames){
                        Log.i(TAG, "user: "+user);
                    }
                    for (int i = itemIndex.size()-1; i >= 0; i--) {
                        userNames.remove((int)itemIndex.get(i));
                    }
                    allUsersRecyclerAdapter.notifyDataSetChanged();
                    deleteUsersFromDB(deletedUsers);
                    checkBoxSelectAll.setChecked(false);
                } else {
                    Toast.makeText(AllUsersActivity.this, "请选择要删除的用户！", Toast.LENGTH_SHORT).show();
                }
            }
        }).setCancelable(true).create().show();
    }

    //从数据库中删除用户
    private void deleteUsersFromDB(ArrayList<String> deletedUsers) {

    }

    private void selectAll(boolean selected) {
        if (selected) {//选中全部
            int childCount = allUsersRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                ((CheckBox) root.findViewById(R.id.checkBoxDelete)).setChecked(true);
            }
        } else {//全部未选中
            int childCount = allUsersRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                ((CheckBox) root.findViewById(R.id.checkBoxDelete)).setChecked(false);
            }
        }
    }


    private void refreshCourses() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //停止刷新
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_all_users, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                if (!isDeleting) {
                    isDeleting = true;
                    addUser.setVisibility(GONE);
                    bottomLayout.setVisibility(View.VISIBLE);
                    setCheckBoxVisibility(true);
                }
                break;
            case R.id.search:
                break;
            case R.id.logout:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCheckBoxVisibility(boolean visible) {
        if (visible) {
            int childCount = allUsersRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                root.findViewById(R.id.rightArrow).setVisibility(GONE);
                root.findViewById(R.id.checkBoxDelete).setVisibility(View.VISIBLE);
            }
        } else {
            int childCount = allUsersRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                root.findViewById(R.id.rightArrow).setVisibility(View.VISIBLE);
                root.findViewById(R.id.checkBoxDelete).setVisibility(GONE);
            }
        }
    }


    private long lastTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Math.abs(System.currentTimeMillis() - lastTime) < 2000) {
                finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                lastTime = System.currentTimeMillis();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mySQLiteOpenHelper != null)
            mySQLiteOpenHelper.close();
    }
}
