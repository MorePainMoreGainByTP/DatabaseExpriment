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

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rights);
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
