package com.example.swjtu.databaseexpriment.userRights;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.RightModuleRecyclerAdapter;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.Right;
import com.example.swjtu.databaseexpriment.entity.User;
import com.example.swjtu.databaseexpriment.loginPage.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class GeneralUserRightsActivity extends AppCompatActivity {
    private static final String TAG = "UserRightsActivity";

    private RecyclerView recyclerView;

    private List<Right> rightList;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_user_rights);
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
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerGeneralRights);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void intiData() {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "DBExperiment.db3", null, 1);
        rightList = new ArrayList<>();
        //查询用户已分配的权限
        int count = 0;
        Cursor cursor2 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select distinct rights.id, rights.right_NO, rights.right_name, rights.right_module, rights.win_name from users,user_rights,rights " +
                "where users.id = user_rights.user_ID and user_rights.right_ID = rights.id and users.user_name = ?", new String[]{user.getUserName()});
        while (cursor2 != null && cursor2.moveToNext()) {
            Right right = new Right(cursor2.getInt(0), cursor2.getInt(1), cursor2.getString(2), cursor2.getString(3), cursor2.getString(4));
            rightList.add(right);
            count++;
        }
        Log.i(TAG, "已分配权限: " + rightList + "\n数据表行数：" + count);
        if (cursor2 != null)
            cursor2.close();
        recyclerView.setAdapter(new RightModuleRecyclerAdapter(rightList, false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(new Intent(this, MainActivity.class).putExtra("logout", true));
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
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
