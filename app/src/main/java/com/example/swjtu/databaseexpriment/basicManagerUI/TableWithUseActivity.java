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
import android.widget.ToggleButton;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.BjsWithUseAdapter;
import com.example.swjtu.databaseexpriment.entity.BjsWithUse;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/27.
 */

public class TableWithUseActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ActionBar actionBar;
    public LinearLayout addNewLayout, deleteLayout;
    private EditText editRightName, editNum;
    private ToggleButton toggleUse;

    private BjsWithUseAdapter bjsWithUseAdapter;
    private List<BjsWithUse> bjsWithUses;
    public int selectedCount = 0;
    private int allCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_with_use);
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
        editRightName = (EditText) findViewById(R.id.editBjsName);
        editNum = (EditText) findViewById(R.id.editNum);
        toggleUse = (ToggleButton) findViewById(R.id.toggleUse);
        deleteLayout = (LinearLayout) findViewById(R.id.deleteLayout);
        addNewLayout = (LinearLayout) findViewById(R.id.addNewLayout);
        deleteLayout.setVisibility(GONE);
    }

    private void initData() {
        bjsWithUses = DataSupport.select("*").order("num").find(BjsWithUse.class);
        bjsWithUseAdapter = new BjsWithUseAdapter(bjsWithUses);
        recyclerView.setAdapter(bjsWithUseAdapter);
        allCount = bjsWithUses.size();
        updateAllCount();
    }

    public void refreshRecycler() {
        bjsWithUses.clear();
        bjsWithUses = DataSupport.select("*").order("num").find(BjsWithUse.class);
        bjsWithUseAdapter = new BjsWithUseAdapter(bjsWithUses);
        recyclerView.setAdapter(bjsWithUseAdapter);
        allCount = bjsWithUses.size();
    }

    /**
     * @param v 添加新的编辑室
     */
    public void onAddNewRight(View v) {
        String rightName = editRightName.getText().toString().trim();
        String num = editNum.getText().toString().trim();
        if (!TextUtils.isEmpty(rightName) && !TextUtils.isEmpty(num)) {
            List<BjsWithUse> rightWithOrders = DataSupport.select("*").where("bjsName = ?", rightName).find(BjsWithUse.class);
            if (rightWithOrders.size() > 0) {
                Toast.makeText(this, "编辑室已存在", Toast.LENGTH_SHORT).show();
            } else {
                BjsWithUse rightWithOrder = new BjsWithUse();
                rightWithOrder.setBjsName(rightName);
                rightWithOrder.setNum(Integer.parseInt(num));
                rightWithOrder.setIsUse(toggleUse.isChecked()?"是":"否");
                rightWithOrder.save();
                editRightName.setText("");
                editNum.setText("");
                int position = bjsWithUses.size();
                for (int i = 0; i < bjsWithUses.size(); i++) {
                    if (bjsWithUses.get(i).getNum() > Integer.parseInt(num)) {
                        position = i;
                        break;
                    }
                }
                bjsWithUseAdapter.checked.add(false);
                if (position == bjsWithUses.size()) {
                    bjsWithUses.add(rightWithOrder);
                    bjsWithUseAdapter.notifyItemInserted(bjsWithUses.size() - 1);
                    recyclerView.scrollToPosition(bjsWithUses.size() - 1);
                } else {
                    bjsWithUses.add(position, rightWithOrder);
                    bjsWithUseAdapter.notifyItemInserted(position);
                    recyclerView.scrollToPosition(position);
                }
                updateAllCount();
                allCount = bjsWithUses.size();
            }
        } else {
            Toast.makeText(this, "编辑室与序号不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param v 删除所选编辑室
     */
    public void onDeleteRights(View v) {
        if (selectedCount == 0) {
            Toast.makeText(this, "无选中项", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = bjsWithUseAdapter.checked.size() - 1; i >= 0; i--) {
                if (bjsWithUseAdapter.checked.get(i)) {
                    String bjsName = bjsWithUses.get(i).getBjsName();
                    int num = bjsWithUses.get(i).getNum();
                    DataSupport.deleteAll(BjsWithUse.class, "bjsName = ? and num = ?", bjsName, "" + num);
                    bjsWithUseAdapter.checked.remove(i);
                    bjsWithUses.remove(i);
                }
            }
            bjsWithUseAdapter.notifyDataSetChanged();
            selectedCount = 0;
            updateSelectedCount();
        }
    }

    /**
     * @param v 取消删除编辑室
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
        for (int i = 0; i < bjsWithUseAdapter.checked.size(); i++) {
            bjsWithUseAdapter.checked.set(i, false);
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
