package com.example.swjtu.databaseexpriment.exercise6;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.swjtu.databaseexpriment.R;
import com.kelin.scrollablepanel.library.ScrollablePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/5/10.
 */

public class InputInterfaceActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private ScrollablePanel dataTable;
    private ScrollTableAdapter scrollTableAdapter;

    private List<List<String>> data;
    private int allCount = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_interface);
        setActionBar();
        getViews();
        initData();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        updateTitle();
    }

    private void updateTitle(){
        actionBar.setTitle("共有"+allCount+"项");
    }

    private void getViews(){
        dataTable = (ScrollablePanel)findViewById(R.id.data_table);
    }

    private void initData(){
        data = new ArrayList<>();
        fillData();
        scrollTableAdapter = new ScrollTableAdapter(data);
        dataTable.setPanelAdapter(scrollTableAdapter);
    }

    private void fillData(){
        for(int i = 0 ; i < 15 ;i++){
            List<String> list = new ArrayList<>();
            for(int j = 0 ; j < 10 ;j ++){
                list.add("J"+j);
            }
            data.add(list);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_interface,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_menu:
                break;
            case R.id.sort_menu:
                break;
            case R.id.add_menu:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
