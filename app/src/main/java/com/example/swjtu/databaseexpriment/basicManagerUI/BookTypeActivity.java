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
import com.example.swjtu.databaseexpriment.adapter.BookTypeAdapter;
import com.example.swjtu.databaseexpriment.entity.BookType;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/27.
 */

public class BookTypeActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ActionBar actionBar;
    public LinearLayout addNewLayout, deleteLayout;
    private EditText editBookType, editCode;

    private BookTypeAdapter bookTypeAdapter;
    private List<BookType> bookTypeList;
    public int selectedCount = 0;
    private int allCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_type);
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
        editBookType = (EditText) findViewById(R.id.editBookType);
        editCode = (EditText) findViewById(R.id.editCode);
        deleteLayout = (LinearLayout) findViewById(R.id.deleteLayout);
        addNewLayout = (LinearLayout) findViewById(R.id.addNewLayout);
        deleteLayout.setVisibility(GONE);
    }

    private void initData() {
        bookTypeList = DataSupport.select("*").order("code").find(BookType.class);
        bookTypeAdapter = new BookTypeAdapter(bookTypeList);
        recyclerView.setAdapter(bookTypeAdapter);
        allCount = bookTypeList.size();
        updateAllCount();
    }

    public void refreshRecycler() {
        bookTypeList.clear();
        bookTypeList = DataSupport.select("*").order("code").find(BookType.class);
        bookTypeAdapter = new BookTypeAdapter(bookTypeList);
        recyclerView.setAdapter(bookTypeAdapter);
        allCount = bookTypeList.size();
    }

    /**
     * @param v 添加新的编辑室
     */
    public void onAddNewRight(View v) {
        String bookType = editBookType.getText().toString().trim();
        String code = editCode.getText().toString().trim();
        if (!TextUtils.isEmpty(bookType) && !TextUtils.isEmpty(code)) {
            List<BookType> bookTypes = DataSupport.select("*").where("bookType = ?", bookType).find(BookType.class);
            if (bookTypes.size() > 0) {
                Toast.makeText(this, "图书类型已存在", Toast.LENGTH_SHORT).show();
            } else {
                BookType bookType1 = new BookType();
                bookType1.setBookType(bookType);
                bookType1.setCode(code);
                bookType1.save();
                editBookType.setText("");
                editCode.setText("");
                int position = bookTypeList.size();
                for (int i = 0; i < bookTypeList.size(); i++) {
                    if (bookTypeList.get(i).getCode().compareTo(code) > 0) {
                        position = i;
                        break;
                    }
                }
                bookTypeAdapter.checked.add(false);
                if (position == bookTypeList.size()) {
                    bookTypeList.add(bookType1);
                    bookTypeAdapter.notifyItemInserted(bookTypeList.size() - 1);
                    recyclerView.scrollToPosition(bookTypeList.size() - 1);
                } else {
                    bookTypeList.add(position, bookType1);
                    bookTypeAdapter.notifyItemInserted(position);
                    recyclerView.scrollToPosition(position);
                }
                updateAllCount();
                allCount = bookTypeList.size();
            }
        } else {
            Toast.makeText(this, "编码与图书类型不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param v 删除所选编辑室
     */
    public void onDeleteRights(View v) {
        if (selectedCount == 0) {
            Toast.makeText(this, "无选中项", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = bookTypeAdapter.checked.size() - 1; i >= 0; i--) {
                if (bookTypeAdapter.checked.get(i)) {
                    String bookType = bookTypeList.get(i).getBookType();
                    String code = bookTypeList.get(i).getCode();
                    DataSupport.deleteAll(BookType.class, "bookType = ? and code = ?", bookType, "" + code);
                    bookTypeAdapter.checked.remove(i);
                    bookTypeList.remove(i);
                }
            }
            bookTypeAdapter.notifyDataSetChanged();
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
        for (int i = 0; i < bookTypeAdapter.checked.size(); i++) {
            bookTypeAdapter.checked.set(i, false);
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
