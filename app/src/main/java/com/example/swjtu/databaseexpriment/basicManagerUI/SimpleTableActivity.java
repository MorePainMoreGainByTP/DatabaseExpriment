package com.example.swjtu.databaseexpriment.basicManagerUI;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.SimpleRightAdapter;
import com.example.swjtu.databaseexpriment.entity.SimpleRight;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/27.
 */

public class SimpleTableActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ActionBar actionBar;
    public LinearLayout addNewLayout, deleteLayout;
    private EditText editRightName;

    private SimpleRightAdapter simpleRightAdapter;
    private List<SimpleRight> simpleRightList;
    public int selectedCount = 0;
    private int allCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_table);
        setActionBar();
        getViews();
        initData();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerSimpleTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editRightName = (EditText) findViewById(R.id.editRightName);
        deleteLayout = (LinearLayout) findViewById(R.id.deleteLayout);
        addNewLayout = (LinearLayout) findViewById(R.id.addNewLayout);
        deleteLayout.setVisibility(GONE);
    }

    private void initData() {
        simpleRightList = DataSupport.findAll(SimpleRight.class);
        simpleRightAdapter = new SimpleRightAdapter(simpleRightList);
        recyclerView.setAdapter(simpleRightAdapter);
        allCount = simpleRightList.size();
        updateAllCount();
    }

    private void refreshRecycler() {
        simpleRightList.clear();
        simpleRightList = DataSupport.findAll(SimpleRight.class);
        simpleRightAdapter = new SimpleRightAdapter(simpleRightList);
        recyclerView.setAdapter(simpleRightAdapter);
        allCount = simpleRightList.size();
    }

    /**
     * @param v 添加新的权限
     */
    public void onAddNewRight(View v) {
        String rightName = editRightName.getText().toString().trim();
        if (!TextUtils.isEmpty(rightName)) {
            List<SimpleRight> simpleRights = DataSupport.select("*").where("bjsName = ?", rightName).find(SimpleRight.class);
            if (simpleRights.size() > 0) {
                Toast.makeText(this, "权限已存在", Toast.LENGTH_SHORT).show();
            } else {
                SimpleRight simpleRight = new SimpleRight();
                simpleRight.setBjsName(rightName);
                simpleRight.save();
                editRightName.setText("");
                //hideKeyboard();
                simpleRightAdapter.checked.add(false);
                simpleRightList.add(simpleRight);
                simpleRightAdapter.notifyItemInserted(simpleRightList.size() - 1);
                recyclerView.scrollToPosition(simpleRightList.size() - 1);
                updateAllCount();
                allCount = simpleRightList.size();
            }
        } else {
            Toast.makeText(this, "权限不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param v 删除所选权限
     */
    public void onDeleteRights(View v) {
        if (selectedCount == 0) {
            Toast.makeText(this, "无选中项", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = simpleRightAdapter.checked.size() - 1; i >= 0; i--) {
                //View view = recyclerView.getChildAt(i);
                //CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_delete);
                if (simpleRightAdapter.checked.get(i)) {
                    String bjsName = simpleRightList.get(i).getBjsName();
                    DataSupport.deleteAll(SimpleRight.class, "bjsName = ?", bjsName);
                    simpleRightAdapter.checked.remove(i);
                    simpleRightList.remove(i);
                }
            }
            simpleRightAdapter.notifyDataSetChanged();
            // addNewLayout.setVisibility(View.VISIBLE);
            // deleteLayout.setVisibility(View.INVISIBLE);
            selectedCount = 0;
            updateSelectedCount();
        }
    }

    /**
     * @param v 取消删除权限
     */
    public void onCancelDelete(View v) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
        int position = linearManager.findLastVisibleItemPosition();
        refreshRecycler();
        recyclerView.scrollToPosition(position);
        addNewLayout.setVisibility(View.VISIBLE);
        deleteLayout.setVisibility(View.INVISIBLE);
        updateAllCount();
    }

    private void updateAllCount() {
        actionBar.setTitle("总共有" + allCount + "项");
    }

    public void updateSelectedCount() {
        actionBar.setTitle("已选中" + selectedCount + "项");
    }

    private void showCheckBox() {
        for (int i = 0; i < allCount; i++) {
            View view = recyclerView.getChildAt(i);
            if (view != null)
                view.findViewById(R.id.checkbox_delete).setVisibility(View.VISIBLE);
        }
    }

    private void clearSelected() {
        for (int i = 0; i < simpleRightAdapter.checked.size(); i++) {
            simpleRightAdapter.checked.set(i, false);
        }
        selectedCount = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete:
                if (deleteLayout.getVisibility() == View.VISIBLE)
                    break;
                clearSelected();
                updateSelectedCount();
                addNewLayout.setVisibility(View.INVISIBLE);
                deleteLayout.setVisibility(View.VISIBLE);
                //Toast.makeText(this, "allCount:" + allCount, Toast.LENGTH_SHORT).show();
                showCheckBox();
                break;
        }
        return true;
    }

    //隐藏软键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
