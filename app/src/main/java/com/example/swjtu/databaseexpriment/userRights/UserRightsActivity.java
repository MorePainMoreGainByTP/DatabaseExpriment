package com.example.swjtu.databaseexpriment.userRights;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.UserRightsViewPagerAdapter;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.Right;
import com.example.swjtu.databaseexpriment.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class UserRightsActivity extends AppCompatActivity {
    private static final String TAG = "UserRightsActivity";

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private List<RightsFragment> rightsFragments;
    private ArrayList<String> tabTitles;

    private ArrayList<Right> allocatedRightList, unallocatedRightList;
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rights);
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
        setActionBar();
        getViews();
        intiData();
    }

    private void setActionBar() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(user.getUserName());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getViews() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void intiData() {
        /*
        allocatedRightList = new ArrayList<>();
        unallocatedRightList = new ArrayList<>();
        try {
            initRightList();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "intiData: 权限查询出错 ", e);
        }*/
        rightsFragments = new ArrayList<>();
        rightsFragments.add(RightsFragment.newInstance(true));  //已分配权限
        rightsFragments.add(RightsFragment.newInstance(false));  //未分配权限
        tabTitles = new ArrayList<>();
        tabTitles.add("已分配权限");
        tabTitles.add("未分配权限");
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(1)));
        UserRightsViewPagerAdapter viewPagerAdapter = new UserRightsViewPagerAdapter(getSupportFragmentManager(), rightsFragments, tabTitles);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);   //需在setAdapter之后，第二个参数设置是否 自动刷新fragment的布尔值
    }
/*
    private void initRightList() {
        //查询用户未分配的所有权限
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select rights.id,rights.right_NO,rights.right_name,rights.right_module,rights.win_name from users,rights " +
                "where rights.id not in (select user_rights.right_ID from user_rights where users.id = user_rights.user_ID and users.user_name = ?)", new String[]{user.getUserName()});
        while (cursor != null && cursor.moveToNext()) {
            Right right = new Right(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            unallocatedRightList.add(right);
        }
        if (cursor != null)
            cursor.close();
        //查询用户已分配的所有权限
        Cursor cursor2 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select rights.id, rights.right_NO,rights.right_name,rights.right_module,rights.win_name from users,rights " +
                "where rights.id in (select user_rights.right_ID from user_rights where users.id = user_rights.user_ID and users.user_name = ?)", new String[]{user.getUserName()});
        while (cursor2 != null && cursor2.moveToNext()) {
            Right right = new Right(cursor2.getInt(0), cursor2.getInt(1), cursor2.getString(2), cursor2.getString(3), cursor2.getString(4));
            allocatedRightList.add(right);
        }
        if (cursor2 != null)
            cursor2.close();
        //查询所有权限
        Cursor cursor3 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from rights", new String[]{});
        while (cursor3 != null && cursor3.moveToNext()) {
            Log.i(TAG, "initRightList: " + cursor3.getInt(0) + cursor3.getInt(1) + cursor3.getString(2) + cursor3.getString(3) + cursor3.getString(4));
        }
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public User getUser() {
        return user;
    }
}
