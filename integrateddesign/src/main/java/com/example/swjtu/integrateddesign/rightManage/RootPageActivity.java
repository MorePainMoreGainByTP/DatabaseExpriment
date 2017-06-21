package com.example.swjtu.integrateddesign.rightManage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.integrateddesign.R;
import com.example.swjtu.integrateddesign.entity.Manager;
import com.example.swjtu.integrateddesign.loginTech.LoginTechnologyActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/6/15.
 */

public class RootPageActivity extends AppCompatActivity {

    private static final String TAG = "RootPageActivity";

    private RecyclerView recyclerView;
    private LinearLayout bottomLayout;
    private CheckBox checkBoxSelectAll;

    private ManagerRecyclerAdapter managerRecyclerAdapter;
    private List<Manager> managerList = new ArrayList<>();
    private boolean isDeleting = false; //是否正在删除

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_page);
        setActionBar();
        getViews();
        initViews();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerManager);
        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);
        checkBoxSelectAll = (CheckBox) findViewById(R.id.checkBoxSelectAll);
    }

    private void initViews() {
        getManagers();
        managerRecyclerAdapter = new ManagerRecyclerAdapter(managerList);
        recyclerView.setAdapter(managerRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        checkBoxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectAll(isChecked);
            }
        });
    }

    private void getManagers() {
        //从数据库中获取用户
        managerList = DataSupport.findAll(Manager.class);
        Log.i(TAG, "用户列表: "+managerList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    managerList.clear();
                    getManagers();
                    managerRecyclerAdapter = new ManagerRecyclerAdapter(managerList);
                    recyclerView.setAdapter(managerRecyclerAdapter);
                    managerRecyclerAdapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (resultCode == 1) {//修改成功
                    managerList.clear();
                    getManagers();
                    managerRecyclerAdapter = new ManagerRecyclerAdapter(managerList);
                    recyclerView.setAdapter(managerRecyclerAdapter);
                    managerRecyclerAdapter.notifyDataSetChanged();
                }
        }
    }

    public void cancel(View v) {
        bottomLayout.setVisibility(GONE);
        setCheckBoxVisibility(false);
        selectAll(false);
        isDeleting = false;
        checkBoxSelectAll.setChecked(false);
    }

    public void delete(View v) {
        new AlertDialog.Builder(this).setMessage("是否删除所选用户？").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Integer> itemIndex = new ArrayList<>();
                ArrayList<String> deletedUsers = new ArrayList<>();
                int childCount = managerRecyclerAdapter.getItemCount();
                for (int i = 0; i < childCount; i++) {
                    View root = recyclerView.getChildAt(i);
                    if (((CheckBox) root.findViewById(R.id.checkBoxDelete)).isChecked()) {
                        itemIndex.add(i);
                        deletedUsers.add(((TextView) root.findViewById(R.id.userName)).getText().toString());
                    }
                }
                if (itemIndex.size() > 0) {
                    for (int i = itemIndex.size() - 1; i >= 0; i--) {
                        managerList.remove((int) itemIndex.get(i));
                    }
                    managerRecyclerAdapter.notifyDataSetChanged();
                    deleteUsersFromDB(deletedUsers);
                    checkBoxSelectAll.setChecked(false);
                } else {
                    Toast.makeText(RootPageActivity.this, "请选择要删除的用户！", Toast.LENGTH_SHORT).show();
                }
            }
        }).setCancelable(true).create().show();
    }

    //从数据库中删除用户
    private void deleteUsersFromDB(ArrayList<String> deletedUsers) {
        for (String userName : deletedUsers) {
            DataSupport.deleteAll(Manager.class, "userName = ?", userName);
        }
    }

    private void selectAll(boolean selected) {
        if (selected) {//选中全部
            int childCount = managerRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                ((CheckBox) root.findViewById(R.id.checkBoxDelete)).setChecked(true);
            }
        } else {//全部未选中
            int childCount = managerRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                ((CheckBox) root.findViewById(R.id.checkBoxDelete)).setChecked(false);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.root_page_menu, menu);
        return true;    //返回true使菜单可显示出来，否则不显示
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete:
                if (!isDeleting) {
                    isDeleting = true;
                    bottomLayout.setVisibility(View.VISIBLE);
                    setCheckBoxVisibility(true);
                }
                break;
            case R.id.logout:
                startActivity(new Intent(RootPageActivity.this, LoginTechnologyActivity.class).putExtra("logout", true));
                finish();
                break;
            case R.id.manageRights:
                startActivity(new Intent(this, ManageRightsActivity.class));
                break;
            case R.id.addManager:
                startActivityForResult(new Intent(this, AddNewUserActivity.class), 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCheckBoxVisibility(boolean visible) {
        if (visible) {
            int childCount = managerRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                root.findViewById(R.id.rightArrow).setVisibility(GONE);
                root.findViewById(R.id.checkBoxDelete).setVisibility(View.VISIBLE);
            }
        } else {
            int childCount = managerRecyclerAdapter.getItemCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                root.findViewById(R.id.rightArrow).setVisibility(View.VISIBLE);
                root.findViewById(R.id.checkBoxDelete).setVisibility(GONE);
            }
        }
    }

}
