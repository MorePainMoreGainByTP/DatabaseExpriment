package com.example.swjtu.databaseexpriment.exercise8;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.Book;
import com.example.swjtu.databaseexpriment.entity.BookIn;
import com.example.swjtu.databaseexpriment.entity.BookRestore;
import com.example.swjtu.databaseexpriment.exercise8.adapter.MyPrintDocumentAdapter;

import org.litepal.crud.DataSupport;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/6/2.
 */

public class TableTechActivity extends AppCompatActivity {

    private static final String TAG = "TableTechActivity";
    private Spinner spinnerBookType;
    private RecyclerView recyclerView;

    private TableTechRecyclerAdapter tableTechRecyclerAdapter;
    private List<List<String>> contents = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_tech);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("图书库存一览表");
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recylerTableTech);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinnerBookType = (Spinner) findViewById(R.id.spinnerBookType);
        spinnerBookType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                retrieveByBookType((String) spinnerBookType.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        retrieveByBookType("全部");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, "uncaughtException: ", e);
            }
        });
    }


    private void retrieveByBookType(String bookType) {
//        List<Book> bookList = DataSupport.findAll(Book.class);
//        Log.i(TAG, "size: " + bookList.size() + "  bookList: " + bookList);
//        List<BookIn> bookIns = DataSupport.findAll(BookIn.class);
//        Log.i(TAG, "size: " + bookIns.size() + "  bookIns: " + bookIns);
//        List<BookRestore> bookRestores = DataSupport.findAll(BookRestore.class);
//        Log.i(TAG, "size: " + bookRestores.size() + "  bookRestores: " + bookRestores);
        contents = null;
        contents = new ArrayList<>();
        Cursor cursor = null;
        if (bookType.equals("全部"))
            cursor = DataSupport.findBySQL("select bk.id,bk.shuH,bk.shuM,bk.zuoZhe,bk.tSFL,bk.kB,br.cS,bk.dJ,br.cS*bk.dJ" +
                    " from Book bk,BookRestore br where bk.shuH = br.shuH");
        else
            cursor = DataSupport.findBySQL("select bk.id,bk.shuH,bk.shuM,bk.zuoZhe,bk.tSFL,bk.kB,br.cS,bk.dJ,br.cS*bk.dJ" +
                    " from Book bk,BookRestore br where bk.shuH = br.shuH and bk.tSFL = ?", bookType);
        int sumBook = 0;
        double sumMaYang = 0;
        while (cursor.moveToNext()) {
            List<String> stringList = new ArrayList<>();
            stringList.add(cursor.getString(1));
            stringList.add(cursor.getString(2));
            stringList.add(cursor.getString(3));
            stringList.add(cursor.getString(4));
            stringList.add(cursor.getString(5));
            stringList.add(cursor.getInt(6) + "");
            stringList.add(String.format("%.2f", cursor.getDouble(7)));
            stringList.add(String.format("%.2f", cursor.getDouble(8)));
            sumBook += cursor.getInt(6);
            sumMaYang += cursor.getDouble(8);
//            Log.i(TAG, "cursor: " + " id:" + cursor.getInt(0) + " 书号:" + cursor.getString(1) + " 书名:" + cursor.getString(2) + " 作者:" + cursor.getString(3)
//                    + " 图书分类:" + cursor.getString(4) + " 开本:" + cursor.getString(5) + " 库存:" + cursor.getInt(6) + " 单价:"
//                    + cursor.getDouble(7) + " 总码样:" + cursor.getDouble(8));
            contents.add(stringList);
            // Log.i(TAG, "contents: "+contents);
        }
        List<String> stringList = new ArrayList<>();
        stringList.add("合计");
        stringList.add("");
        stringList.add("");
        stringList.add("");
        stringList.add("");
        stringList.add("" + sumBook);
        stringList.add("");
        stringList.add(String.format("%.2f", sumMaYang));
        contents.add(stringList);
        tableTechRecyclerAdapter = null;
        tableTechRecyclerAdapter = new TableTechRecyclerAdapter(contents);
        recyclerView.setAdapter(tableTechRecyclerAdapter);
        tableTechRecyclerAdapter.notifyDataSetChanged();
    }

    private void addData2DB() {//向数据库中添加基本数据

        for (int i = 0; i < 9; i++) {
            Book book = new Book();
            switch (i) {
                case 0:
                    book.setShuH("ISBN 7-81057-472-8/O.026");
                    book.setShuM("摩擦学设计100例");
                    book.setZuoZhe("周仲荣");
                    book.settSFL("数理科学和化学");
                    book.setkB("16");
                    book.setdJ(55);
                    break;
                case 1:
                    book.setShuH("ISBN 7-81057-397-7/U.037");
                    book.setShuM("电气化铁道高电压绝缘与试验技术");
                    book.setZuoZhe("刘明光");
                    book.settSFL("交通运输");
                    book.setkB("16");
                    book.setdJ(30);
                    break;
                case 2:
                    book.setShuH("ISBN 7-81057-407-8/F.034");
                    book.setShuM("中国金融市场历史与现状分析");
                    book.setZuoZhe("贾志永");
                    book.settSFL("经济");
                    book.setkB("大32");
                    book.setdJ(14.5);
                    break;
                case 3:
                    book.setShuH("ISBN 7-81057-434-5/TD.244");
                    book.setShuM("岩体爆破的块度理论及其应用");
                    book.setZuoZhe("张继春");
                    book.settSFL("矿业工程");
                    book.setkB("大32");
                    book.setdJ(11);
                    break;
                case 4:
                    book.setShuH("ISBN 7-81057-438-8/TU.203");
                    book.setShuM("线路工程");
                    book.setZuoZhe("邓午天");
                    book.settSFL("建筑科学");
                    book.setkB("16");
                    book.setdJ(33);
                    break;
                case 5:
                    book.setShuH("ISBN 7-81057-417-5/TU.201");
                    book.setShuM("桥梁工程");
                    book.setZuoZhe("强士中");
                    book.settSFL("建筑科学");
                    book.setkB("16");
                    book.setdJ(33);
                    break;
                case 6:
                    book.setShuH("ISBN 7-81057-528-7/G.034");
                    book.setShuM("大学生毕业设计全程指导——机械类");
                    book.setZuoZhe("刘思宁");
                    book.settSFL("文化·科学·教育·体育");
                    book.setkB("16");
                    book.setdJ(25);
                    break;
                case 7:
                    book.setShuH("ISBN 7-81057-504-X/U.033");
                    book.setShuM("交通工程测量学实习指导书");
                    book.setZuoZhe("白迪谋");
                    book.settSFL("交通运输");
                    book.setkB("16");
                    book.setdJ(8);
                    break;
                case 8:
                    book.setShuH("ISBN 7-81057-495-7/O.027");
                    book.setShuM("工科化学");
                    book.setZuoZhe("童志平");
                    book.settSFL("数理科学和化学");
                    book.setkB("大32");
                    book.setdJ(16);
                    break;
            }
            book.save();
        }


        for (int i = 0; i < 9; i++) {
            BookRestore bookRestore = new BookRestore();
            switch (i) {
                case 0:
                    bookRestore.setShuH("ISBN 7-81057-472-8/O.026");
                    bookRestore.setcS(233);
                    break;
                case 1:
                    bookRestore.setShuH("ISBN 7-81057-397-7/U.037");
                    bookRestore.setcS(100);
                    break;
                case 2:
                    bookRestore.setShuH("ISBN 7-81057-407-8/F.034");
                    bookRestore.setcS(2);
                    break;
                case 3:
                    bookRestore.setShuH("ISBN 7-81057-434-5/TD.244");
                    bookRestore.setcS(5);
                    break;
                case 4:
                    bookRestore.setShuH("ISBN 7-81057-438-8/TU.203");
                    bookRestore.setcS(3);
                    break;
                case 5:
                    bookRestore.setShuH("ISBN 7-81057-417-5/TU.201");
                    bookRestore.setcS(25);
                    break;
                case 6:
                    bookRestore.setShuH("ISBN 7-81057-504-X/U.033");
                    bookRestore.setcS(200);
                    break;
                case 7:
                    bookRestore.setShuH("ISBN 7-81057-528-7/G.034");
                    bookRestore.setcS(22);
                    break;
                case 8:
                    bookRestore.setShuH("ISBN 7-81057-495-7/O.027");
                    bookRestore.setcS(89);
                    break;
            }
            bookRestore.save();
        }


        for (int i = 0; i < 33; i++) {
            BookIn bookIn = new BookIn();
            switch (i) {
                case 0:
                    bookIn.setrKRQ(Date.valueOf("2017-4-4").getTime());
                    bookIn.setShuH("ISBN 7-81057-472-8/O.026");
                    bookIn.setrKCS(200);
                    break;
                case 1:
                    bookIn.setrKRQ(Date.valueOf("2017-11-18").getTime());
                    bookIn.setShuH("ISBN 7-81057-397-7/U.037");
                    bookIn.setrKCS(20);
                    break;
                case 2:
                    bookIn.setrKRQ(Date.valueOf("2017-10-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-495-7/O.027");
                    bookIn.setrKCS(36);
                    break;
                case 3:
                    bookIn.setrKRQ(Date.valueOf("2017-11-18").getTime());
                    bookIn.setShuH("ISBN 7-81057-504-X/U.033");
                    bookIn.setrKCS(20);
                    break;
                case 4:
                    bookIn.setrKRQ(Date.valueOf("2017-10-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-472-8/O.026");
                    bookIn.setrKCS(4);
                    break;
                case 5:
                    bookIn.setrKRQ(Date.valueOf("2017-11-25").getTime());
                    bookIn.setShuH("ISBN 7-81057-407-8/F.034");
                    bookIn.setrKCS(5);
                    break;
                case 6:
                    bookIn.setrKRQ(Date.valueOf("2017-10-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-472-8/O.026");
                    bookIn.setrKCS(57);
                    break;
                case 7:
                    bookIn.setrKRQ(Date.valueOf("2017-10-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-495-7/O.027");
                    bookIn.setrKCS(36);
                    break;
                case 8:
                    bookIn.setrKRQ(Date.valueOf("2017-1-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-472-8/O.026");
                    bookIn.setrKCS(2);
                    break;
                case 9:
                    bookIn.setrKRQ(Date.valueOf("22017-1-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-504-X/U.033");
                    bookIn.setrKCS(11);
                    break;
                case 10:
                    bookIn.setrKRQ(Date.valueOf("2017-10-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-472-8/O.026");
                    bookIn.setrKCS(190);
                    break;
                case 11:
                    bookIn.setrKRQ(Date.valueOf("2017-1-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-504-X/U.033");
                    bookIn.setrKCS(100);
                    break;
                case 12:
                    bookIn.setrKRQ(Date.valueOf("2017-1-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-438-8/TU.203");
                    bookIn.setrKCS(12);
                    break;
                case 13:
                    bookIn.setrKRQ(Date.valueOf("2017-1-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-528-7/G.034");
                    bookIn.setrKCS(65);
                    break;
                case 14:
                    bookIn.setrKRQ(Date.valueOf("2007-11-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-397-7/U.037");
                    bookIn.setrKCS(3);
                    break;
                case 15:
                    bookIn.setrKRQ(Date.valueOf("2017-4-4").getTime());
                    bookIn.setShuH("ISBN 7-81057-407-8/F.034");
                    bookIn.setrKCS(66);
                    break;
                case 16:
                    bookIn.setrKRQ(Date.valueOf("2017-1-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-397-7/U.037");
                    bookIn.setrKCS(35);
                    break;
                case 17:
                    bookIn.setrKRQ(Date.valueOf("2017-7-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-407-8/F.034");
                    bookIn.setrKCS(30);
                    break;
                case 18:
                    bookIn.setrKRQ(Date.valueOf("2017-7-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-434-5/TD.244");
                    bookIn.setrKCS(50);
                    break;
                case 19:
                    bookIn.setrKRQ(Date.valueOf("2017-3-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-438-8/TU.203");
                    bookIn.setrKCS(10);
                    break;
                case 20:
                    bookIn.setrKRQ(Date.valueOf("2010-11-12").getTime());
                    bookIn.setShuH("ISBN 7-81057-434-5/TD.244");
                    bookIn.setrKCS(7);
                    break;
                case 21:
                    bookIn.setrKRQ(Date.valueOf("2017-2-19").getTime());
                    bookIn.setShuH("ISBN 7-81057-528-7/G.034");
                    bookIn.setrKCS(30);
                    break;
                case 22:
                    bookIn.setrKRQ(Date.valueOf("2017-2-19").getTime());
                    bookIn.setShuH("ISBN 7-81057-495-7/O.027");
                    bookIn.setrKCS(6);
                    break;
                case 23:
                    bookIn.setrKRQ(Date.valueOf("2017-7-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-417-5/TU.201");
                    bookIn.setrKCS(20);
                    break;
                case 24:
                    bookIn.setrKRQ(Date.valueOf("2017-11-19").getTime());
                    bookIn.setShuH("ISBN 7-81057-504-X/U.033");
                    bookIn.setrKCS(17);
                    break;
                case 25:
                    bookIn.setrKRQ(Date.valueOf("2017-3-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-434-5/TD.244");
                    bookIn.setrKCS(11);
                    break;
                case 26:
                    bookIn.setrKRQ(Date.valueOf("2017-3-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-495-7/O.027");
                    bookIn.setrKCS(566);
                    break;
                case 27:
                    bookIn.setrKRQ(Date.valueOf("2017-8-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-504-X/U.033");
                    bookIn.setrKCS(31);
                    break;
                case 28:
                    bookIn.setrKRQ(Date.valueOf("2017-8-17").getTime());
                    bookIn.setShuH("ISBN 7-81057-504-X/U.033");
                    bookIn.setrKCS(5);
                    break;
                case 29:
                    bookIn.setrKRQ(Date.valueOf("2017-4-4").getTime());
                    bookIn.setShuH("ISBN 7-81057-528-7/G.034");
                    bookIn.setrKCS(33);
                    break;
                case 30:
                    bookIn.setrKRQ(Date.valueOf("2017-4-4").getTime());
                    bookIn.setShuH("ISBN 7-81057-438-8/TU.203");
                    bookIn.setrKCS(24);
                    break;
                case 31:
                    bookIn.setrKRQ(Date.valueOf("2017-11-4").getTime());
                    bookIn.setShuH("ISBN 7-81057-495-7/O.027");
                    bookIn.setrKCS(5);
                    break;
                case 32:
                    bookIn.setrKRQ(Date.valueOf("2017-4-4").getTime());
                    bookIn.setShuH("ISBN 7-81057-417-5/TU.201");
                    bookIn.setrKCS(5);
                    break;
            }
            bookIn.save();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.table_tech_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.printStore:
                doPrintStore();
                break;
            case R.id.bookInTable:
                startActivity(new Intent(this, BookInActivity.class));
                break;
            case R.id.bookInPic:
                startActivity(new Intent(this, BookInPicActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doPrintStore() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        String jobName = "printBookRestore";
        printManager.print(jobName, new MyPrintDocumentAdapter(this, contents), null);
    }
}
