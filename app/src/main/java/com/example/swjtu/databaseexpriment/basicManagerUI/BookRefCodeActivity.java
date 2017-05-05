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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.BookRefCodeAdapter;
import com.example.swjtu.databaseexpriment.entity.BjsWithUse;
import com.example.swjtu.databaseexpriment.entity.BookRefCode;
import com.example.swjtu.databaseexpriment.entity.BookType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/27.
 */

public class BookRefCodeActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ActionBar actionBar;
    public LinearLayout addNewLayout, deleteLayout;
    private EditText editShuM;
    private Spinner spinnerBjsID, spinnerBookCode;

    private BookRefCodeAdapter bookRefCodeAdapter;
    private List<BookRefCode> bookRefCodeList;
    public int selectedCount = 0;
    private int allCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ref_code);
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
        editShuM = (EditText) findViewById(R.id.editShuM);
        spinnerBjsID = (Spinner) findViewById(R.id.spinnerBjsID);
        spinnerBookCode = (Spinner) findViewById(R.id.spinnerBookCode);
        deleteLayout = (LinearLayout) findViewById(R.id.deleteLayout);
        addNewLayout = (LinearLayout) findViewById(R.id.addNewLayout);
        deleteLayout.setVisibility(GONE);
    }

    private void initData() {
        bookRefCodeList = DataSupport.findAll(BookRefCode.class);
        bookRefCodeAdapter = new BookRefCodeAdapter(bookRefCodeList);
        recyclerView.setAdapter(bookRefCodeAdapter);
        allCount = bookRefCodeList.size();
        initSpinner();
        updateAllCount();
    }

    private void initSpinner() {
        List<BjsWithUse> bjsWithUses = DataSupport.select("*").where("isUse = ?", "是").find(BjsWithUse.class);
        List<Integer> bjsIDs = new ArrayList<>();
        for (BjsWithUse bjsWithUse : bjsWithUses) {
            bjsIDs.add(bjsWithUse.getId());
        }
        spinnerBjsID.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, bjsIDs));

        List<BookType> bookTypes = DataSupport.findAll(BookType.class);
        List<String> bookCodes = new ArrayList<>();
        for (BookType bookType1 : bookTypes) {
            bookCodes.add(bookType1.getCode());
        }
        spinnerBookCode.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookCodes));
    }

    public void refreshRecycler() {
        bookRefCodeList.clear();
        bookRefCodeList = DataSupport.findAll(BookRefCode.class);
        bookRefCodeAdapter = new BookRefCodeAdapter(bookRefCodeList);
        recyclerView.setAdapter(bookRefCodeAdapter);
        allCount = bookRefCodeList.size();
    }

    /**
     * @param v 添加新的书名
     */
    public void onAddNewRight(View v) {
        String shuM = editShuM.getText().toString().trim();
        int bjsNameID = (Integer) spinnerBjsID.getSelectedItem();
        String bookCode = (String) spinnerBookCode.getSelectedItem();

        if (!TextUtils.isEmpty(shuM)) {
            List<BookRefCode> bookRefNames = DataSupport.select("*").where("shuM = ?", shuM).find(BookRefCode.class);
            if (bookRefNames.size() > 0) {
                Toast.makeText(this, "书名已存在", Toast.LENGTH_SHORT).show();
            } else {
                BookRefCode bookRefName = new BookRefCode();
                bookRefName.setBjsNameID(bjsNameID);
                bookRefName.setBookTypeCode(bookCode);
                bookRefName.setShuM(shuM);
                bookRefName.save();
                editShuM.setText("");
                bookRefCodeAdapter.checked.add(false);
                bookRefCodeList.add(bookRefName);
                bookRefCodeAdapter.notifyItemInserted(bookRefCodeList.size() - 1);
                recyclerView.scrollToPosition(bookRefCodeList.size() - 1);
                allCount = bookRefCodeList.size();
                updateAllCount();
            }
        } else {
            Toast.makeText(this, "书名不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param v 删除所选书名
     */
    public void onDeleteRights(View v) {
        if (selectedCount == 0) {
            Toast.makeText(this, "无选中项", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = bookRefCodeAdapter.checked.size() - 1; i >= 0; i--) {
                if (bookRefCodeAdapter.checked.get(i)) {
                    String shuM = bookRefCodeList.get(i).getShuM();
                    int id = bookRefCodeList.get(i).getId();
                    DataSupport.deleteAll(BookRefCode.class, "shuM = ? and id = ?", shuM, "" + id);
                    bookRefCodeAdapter.checked.remove(i);
                    bookRefCodeList.remove(i);
                }
            }
            bookRefCodeAdapter.notifyDataSetChanged();
            selectedCount = 0;
            updateSelectedCount();
        }
    }

    /**
     * @param v 取消删除书名
     */
    public void onCancelDelete(View v) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
        int position = linearManager.findLastVisibleItemPosition();
        refreshRecycler();
        recyclerView.scrollToPosition(position);
        addNewLayout.setVisibility(View.VISIBLE);
        deleteLayout.setVisibility(View.GONE);
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
        for (int i = 0; i < bookRefCodeAdapter.checked.size(); i++) {
            bookRefCodeAdapter.checked.set(i, false);
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
                addNewLayout.setVisibility(View.GONE);
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
