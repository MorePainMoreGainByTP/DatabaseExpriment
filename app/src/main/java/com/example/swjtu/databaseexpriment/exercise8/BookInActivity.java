package com.example.swjtu.databaseexpriment.exercise8;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.swjtu.databaseexpriment.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/6/2.
 */

public class BookInActivity extends AppCompatActivity {
    private static final String TAG = "BookInActivity";
    private RecyclerView recyclerView;
    private BookInRecyclerAdapter bookInRecyclerAdapter;
    private List<List<String>> contents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("2017年图书入库统计表");
        setSupportActionBar(toolbar);
        getData();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerBookIn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookInRecyclerAdapter = new BookInRecyclerAdapter(contents);
        recyclerView.setAdapter(bookInRecyclerAdapter);
    }

    private void getData() {
        contents = new ArrayList<>();
        Cursor cursor = DataSupport.findBySQL("select bk.shuH,bk.shuM,bk.zuoZhe,bk.dJ,v.allrkcs,bk.dJ*v.allrkcs" +
                " from Book bk,(select shuH,sum(rkcs) allrkcs from BookIn group by shuH) v" +
                " where bk.shuH = v.shuH");

        Log.i(TAG, "getData: 1" + " rowNum:" + cursor.getCount());
        int allRkcs = 0;
        double allPrice = 0;
        while (cursor.moveToNext()) {
            Log.i(TAG, "sqlite_master: 书号：" + cursor.getString(0) + "  书名：" + cursor.getString(1) + "  作者：" + cursor.getString(2) + "  单价：" + cursor.getDouble(3)
                    + "  入库数：" + cursor.getInt(4) + "  总码样：" + cursor.getDouble(5));
            List<String> strings = new ArrayList<>();
            strings.add(cursor.getString(0));
            strings.add(cursor.getString(1));
            strings.add(cursor.getString(2));
            strings.add(String.format("%.2f", cursor.getDouble(3)));
            strings.add(cursor.getInt(4) + "");
            strings.add(String.format("%.2f", cursor.getDouble(5)));
            allRkcs += cursor.getInt(4);
            allPrice += cursor.getDouble(5);
            contents.add(strings);
        }
        List<String> strings = new ArrayList<>();
        strings.add("合计");
        strings.add("");
        strings.add("");
        strings.add("");
        strings.add("" + allRkcs);
        strings.add(String.format("%.2f", allPrice));
        contents.add(strings);
        cursor.close();

        /*
        Log.i(TAG, "getData: 2");
        Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from BookIn", new String[]{});
        Log.i(TAG, "getData: 3");
        while (cursor != null && cursor.moveToNext()) {
            Log.i(TAG, "id: " + cursor.getInt(0) + " 日期：" + new Date(cursor.getInt(1)).toString() + " 书号：" + cursor.getString(2) + "  入库册数：" + cursor.getInt(3));
        }
        Log.i(TAG, "getData: 4");

        if (cursor != null)
            cursor.close();
            */
    }


}
