package com.example.swjtu.databaseexpriment.basicManagerUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;

/**
 * Created by tangpeng on 2017/4/27.
 */

public class BasicInfoRepositoryActivity extends AppCompatActivity {
    private static final String TAG = "BasicInfoRepositoryActi";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info_repository);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
        setActionBar();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    public void onSimple(View v) {
        startActivity(new Intent(this, SimpleTableActivity.class));
    }

    public void onOrdered(View v) {
        startActivity(new Intent(this, TableWithOrderActivity.class));
    }

    public void onInUse(View v) {
        startActivity(new Intent(this, TableWithUseActivity.class));
    }

    public void onCode(View v) {
        startActivity(new Intent(this, BookTypeActivity.class));
    }

    public void onCodeRef(View v) {
        startActivity(new Intent(this, BookRefCodeActivity.class));
    }

    public void onPrimaryKey(View v) {
        startActivity(new Intent(this, BookRefKeyActivity.class));
    }

    public void onRefName(View v) {
        startActivity(new Intent(this, BookRefNameActivity.class));
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
}
