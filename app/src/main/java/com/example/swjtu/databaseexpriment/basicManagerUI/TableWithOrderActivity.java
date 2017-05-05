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
import com.example.swjtu.databaseexpriment.adapter.RightWithOrderAdapter;
import com.example.swjtu.databaseexpriment.entity.RightWithOrder;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/27.
 */

public class TableWithOrderActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ActionBar actionBar;
    public LinearLayout addNewLayout, deleteLayout;
    private EditText editRightName, editNum;

    private RightWithOrderAdapter rightWithOrderAdapter;
    private List<RightWithOrder> rightWithOrderList;
    public int selectedCount = 0;
    private int allCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_with_order);
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
        deleteLayout = (LinearLayout) findViewById(R.id.deleteLayout);
        addNewLayout = (LinearLayout) findViewById(R.id.addNewLayout);
        deleteLayout.setVisibility(GONE);
    }

    private void initData() {
        rightWithOrderList = DataSupport.select("*").order("num").find(RightWithOrder.class);
        rightWithOrderAdapter = new RightWithOrderAdapter(rightWithOrderList);
        recyclerView.setAdapter(rightWithOrderAdapter);
        allCount = rightWithOrderList.size();
        updateAllCount();
    }

    public void refreshRecycler() {
        rightWithOrderList.clear();
        rightWithOrderList = DataSupport.select("*").order("num").find(RightWithOrder.class);
        rightWithOrderAdapter = new RightWithOrderAdapter(rightWithOrderList);
        recyclerView.setAdapter(rightWithOrderAdapter);
        allCount = rightWithOrderList.size();
    }

    /**
     * @param v 添加新的编辑室
     */
    public void onAddNewRight(View v) {
        String rightName = editRightName.getText().toString().trim();
        String num = editNum.getText().toString().trim();
        if (!TextUtils.isEmpty(rightName) && !TextUtils.isEmpty(num)) {
            List<RightWithOrder> rightWithOrders = DataSupport.select("*").where("bjsName = ?", rightName).find(RightWithOrder.class);
            if (rightWithOrders.size() > 0) {
                Toast.makeText(this, "编辑室已存在", Toast.LENGTH_SHORT).show();
            } else {
                RightWithOrder rightWithOrder = new RightWithOrder();
                rightWithOrder.setBjsName(rightName);
                rightWithOrder.setNum(Integer.parseInt(num));
                rightWithOrder.save();
                editRightName.setText("");
                editNum.setText("");
                int position = rightWithOrderList.size();
                for (int i = 0; i < rightWithOrderList.size(); i++) {
                    if (rightWithOrderList.get(i).getNum() > Integer.parseInt(num)) {
                        position = i;
                        break;
                    }
                }
                rightWithOrderAdapter.checked.add(false);
                if (position == rightWithOrderList.size()) {
                    rightWithOrderList.add(rightWithOrder);
                    rightWithOrderAdapter.notifyItemInserted(rightWithOrderList.size() - 1);
                    recyclerView.scrollToPosition(rightWithOrderList.size() - 1);
                } else {
                    rightWithOrderList.add(position, rightWithOrder);
                    rightWithOrderAdapter.notifyItemInserted(position);
                    recyclerView.scrollToPosition(position);
                }
                allCount = rightWithOrderList.size();
                updateAllCount();
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
            for (int i = rightWithOrderAdapter.checked.size() - 1; i >= 0; i--) {
                if (rightWithOrderAdapter.checked.get(i)) {
                    String bjsName = rightWithOrderList.get(i).getBjsName();
                    int num = rightWithOrderList.get(i).getNum();
                    DataSupport.deleteAll(RightWithOrder.class, "bjsName = ? and num = ?", bjsName, "" + num);
                    rightWithOrderAdapter.checked.remove(i);
                    rightWithOrderList.remove(i);
                }
            }
            rightWithOrderAdapter.notifyDataSetChanged();
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
        for (int i = 0; i < rightWithOrderAdapter.checked.size(); i++) {
            rightWithOrderAdapter.checked.set(i, false);
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
