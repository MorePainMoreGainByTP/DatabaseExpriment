package com.example.swjtu.databaseexpriment.basicManagerUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
    }

    public void onSimple(View v) {
        startActivity(new Intent(this, SimpleTableActivity.class));
    }

    public void onOrdered(View v) {
        startActivity(new Intent(this, TableWithOrderActivity.class));
    }
}
