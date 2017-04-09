package com.example.swjtu.databaseexpriment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class AllUsersActivity extends AppCompatActivity {

    private static final String TAG = "AllUsersActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        setActionBar();
        Log.i(TAG, "onCreate: ");
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private long lastTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Math.abs(System.currentTimeMillis() - lastTime) < 2000) {
                finish();
            } else {
                lastTime = System.currentTimeMillis();
            }
        }
        return true;
    }
}
