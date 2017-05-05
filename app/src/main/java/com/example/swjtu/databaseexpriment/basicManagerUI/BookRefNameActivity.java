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
import com.example.swjtu.databaseexpriment.adapter.BookRefNameAdapter;
import com.example.swjtu.databaseexpriment.entity.BjsWithUse;
import com.example.swjtu.databaseexpriment.entity.BookRefName;
import com.example.swjtu.databaseexpriment.entity.BookType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/27.
 */

public class BookRefNameActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ActionBar actionBar;
    public LinearLayout addNewLayout, deleteLayout;
    private EditText editShuM;
    private Spinner spinnerBjs, spinnerBookType;

    private BookRefNameAdapter bookRefNameAdapter;
    private List<BookRefName> bookRefNameList;
    public int selectedCount = 0;
    private int allCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ref_name);
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
        spinnerBjs = (Spinner) findViewById(R.id.spinnerBjs);
        spinnerBookType = (Spinner) findViewById(R.id.spinnerBookType);
        deleteLayout = (LinearLayout) findViewById(R.id.deleteLayout);
        addNewLayout = (LinearLayout) findViewById(R.id.addNewLayout);
        deleteLayout.setVisibility(GONE);
    }

    private void initData() {
        bookRefNameList = DataSupport.findAll(BookRefName.class);
        bookRefNameAdapter = new BookRefNameAdapter(bookRefNameList);
        recyclerView.setAdapter(bookRefNameAdapter);
        allCount = bookRefNameList.size();
        initSpinner();
        updateAllCount();
    }

    private void initSpinner() {
        List<BjsWithUse> bjsWithUses = DataSupport.select("*").where("isUse = ?", "是").find(BjsWithUse.class);
        List<String> shuMs = new ArrayList<String>();
        for (BjsWithUse bjsWithUse : bjsWithUses) {
            shuMs.add(bjsWithUse.getBjsName());
        }
        spinnerBjs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shuMs));

        List<BookType> bookTypes = DataSupport.findAll(BookType.class);
        List<String> bookType = new ArrayList<String>();
        for (BookType bookType1 : bookTypes) {
            bookType.add(bookType1.getBookType());
        }
        spinnerBookType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bookType));
    }

    public void refreshRecycler() {
        bookRefNameList.clear();
        bookRefNameList = DataSupport.findAll(BookRefName.class);
        bookRefNameAdapter = new BookRefNameAdapter(bookRefNameList);
        recyclerView.setAdapter(bookRefNameAdapter);
        allCount = bookRefNameList.size();
    }

    /**
     * @param v 添加新的书名
     */
    public void onAddNewRight(View v) {
        String shuM = editShuM.getText().toString().trim();
        String bjsName = (String) spinnerBjs.getSelectedItem();
        String bookTpeStr = (String) spinnerBookType.getSelectedItem();

        if (!TextUtils.isEmpty(shuM)) {
            List<BookRefName> bookRefNames = DataSupport.select("*").where("shuM = ?", shuM).find(BookRefName.class);
            if (bookRefNames.size() > 0) {
                Toast.makeText(this, "书名已存在", Toast.LENGTH_SHORT).show();
            } else {
                BookRefName bookRefName = new BookRefName();
                bookRefName.setBjsName(bjsName);
                bookRefName.setBookType(bookTpeStr);
                bookRefName.setShuM(shuM);
                bookRefName.save();
                editShuM.setText("");
                bookRefNameAdapter.checked.add(false);
                bookRefNameList.add(bookRefName);
                bookRefNameAdapter.notifyItemInserted(bookRefNameList.size() - 1);
                recyclerView.scrollToPosition(bookRefNameList.size() - 1);
                updateAllCount();
                allCount = bookRefNameList.size();
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
            for (int i = bookRefNameAdapter.checked.size() - 1; i >= 0; i--) {
                if (bookRefNameAdapter.checked.get(i)) {
                    String shuM = bookRefNameList.get(i).getShuM();
                    int id = bookRefNameList.get(i).getId();
                    DataSupport.deleteAll(BookRefName.class, "shuM = ? and id = ?", shuM, "" + id);
                    bookRefNameAdapter.checked.remove(i);
                    bookRefNameList.remove(i);
                }
            }
            bookRefNameAdapter.notifyDataSetChanged();
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
        for (int i = 0; i < bookRefNameAdapter.checked.size(); i++) {
            bookRefNameAdapter.checked.set(i, false);
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
