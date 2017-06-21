package com.example.swjtu.databaseexpriment.userRights;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.RightModuleRecyclerAdapter;
import com.example.swjtu.databaseexpriment.dbUtilities.MySQLiteOpenHelper;
import com.example.swjtu.databaseexpriment.entity.Right;
import com.example.swjtu.databaseexpriment.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class RightsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "RightsFragment";

    public static boolean DATA_CHANGED = false;

    private static final String KEY_ALLOCATED = "key_allocated";
    List<Right> rightList;

    ProgressDialog dialog;
    RightModuleRecyclerAdapter rightModuleRecyclerAdapter;
    RecyclerView recyclerView;
    CheckBox selectAll;
    TextView txtAddOrDelete;
    boolean allocated;

    public static RightsFragment newInstance(boolean allocated) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_ALLOCATED, allocated);
        RightsFragment fragment = new RightsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRightList(allocated);
    }

    //重新从数据库中获取权限
    public void initRightList(boolean allocated) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity(), "DBExperiment.db3", null, 1);
        User user = ((UserRightsActivity) getActivity()).getUser();
        rightList = new ArrayList<>();
        if (!allocated) {//查询用户未分配的权限
            int count = 0;
            Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select distinct rights.id,rights.right_NO,rights.right_name,rights.right_module,rights.win_name from rights " +
                    "where rights.id not in (select user_rights.right_ID from users,user_rights where users.id = user_rights.user_ID and users.user_name = ?)", new String[]{user.getUserName()});
            while (cursor != null && cursor.moveToNext()) {
                Right right = new Right(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                rightList.add(right);
                count++;
            }
            Log.i(TAG, "未分配权限: " + rightList + "\n数据表行数：" + count);
            if (cursor != null)
                cursor.close();
        } else {//查询用户已分配的权限
            int count = 0;
            Cursor cursor2 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select distinct rights.id, rights.right_NO, rights.right_name, rights.right_module, rights.win_name from users,user_rights,rights " +
                    "where users.id = user_rights.user_ID and user_rights.right_ID = rights.id and users.user_name = ?", new String[]{user.getUserName()});
            while (cursor2 != null && cursor2.moveToNext()) {
                Right right = new Right(cursor2.getInt(0), cursor2.getInt(1), cursor2.getString(2), cursor2.getString(3), cursor2.getString(4));
                rightList.add(right);
                count++;
            }
            Log.i(TAG, "已分配权限: " + rightList + "\n数据表行数：" + count);
            if (cursor2 != null)
                cursor2.close();
        }
        rightModuleRecyclerAdapter = new RightModuleRecyclerAdapter(rightList, true);
        recyclerView.setAdapter(rightModuleRecyclerAdapter);
        rightModuleRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "setUserVisibleHint: isVisibleToUser " + isVisibleToUser + ",DATA_CHANGED:" + DATA_CHANGED);
        if (isVisibleToUser) {
            if (DATA_CHANGED) {
                initRightList(allocated);
                DATA_CHANGED = false;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rights, null);

        Bundle bundle = getArguments();
        allocated = bundle.getBoolean(KEY_ALLOCATED);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerRightsModule);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        selectAll = (CheckBox) root.findViewById(R.id.checkBoxSelectAll);
        selectAll.setOnCheckedChangeListener(this);

        txtAddOrDelete = (TextView) root.findViewById(R.id.txtAddOrDelete);
        txtAddOrDelete.setOnClickListener(this);

        if (allocated) {
            txtAddOrDelete.setText("删除");
        } else {
            txtAddOrDelete.setText("分配");
        }
        return root;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                RecyclerView oneRight = (RecyclerView) root.findViewById(R.id.recyclerRightItem);
                int grandCount = oneRight.getChildCount();
                for (int j = 0; j < grandCount; j++) {
                    View grandRoot = oneRight.getChildAt(j);
                    CheckBox checkBoxSelected = (CheckBox) grandRoot.findViewById(R.id.checkBoxSelected);
                    checkBoxSelected.setChecked(true);
                }
            }
        } else {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View root = recyclerView.getChildAt(i);
                RecyclerView oneRight = (RecyclerView) root.findViewById(R.id.recyclerRightItem);
                int grandCount = oneRight.getChildCount();
                for (int j = 0; j < grandCount; j++) {
                    View grandRoot = oneRight.getChildAt(j);
                    CheckBox checkBoxSelected = (CheckBox) grandRoot.findViewById(R.id.checkBoxSelected);
                    checkBoxSelected.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {   //删除或分配权限
        final ArrayList<Integer> rightID = new ArrayList<>();
        int childCount = recyclerView.getChildCount();
        //收集被选中的权限
        for (int i = 0; i < childCount; i++) {
            View root = recyclerView.getChildAt(i);
            RecyclerView oneRight = (RecyclerView) root.findViewById(R.id.recyclerRightItem);
            int grandCount = oneRight.getChildCount();
            for (int j = 0; j < grandCount; j++) {
                View grandRoot = oneRight.getChildAt(j);
                CheckBox checkBoxSelected = (CheckBox) grandRoot.findViewById(R.id.checkBoxSelected);
                if (checkBoxSelected.isChecked()) {
                    TextView rightTxt = (TextView) grandRoot.findViewById(R.id.txtOneRight);
                    String rightStr = rightTxt.getText().toString().trim();
                    for (Right right : rightList) {
                        if (right.getRightName().equals(rightStr)) {
                            rightID.add(right.getID());
                        }
                    }
                }
            }
        }
        if (rightID.size() > 0) {
            if (allocated) {//已分配权限，对应删除操作
                new AlertDialog.Builder(getActivity()).setMessage("是否删除所选权限？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeRights(rightID);
                        initRightList(allocated);
                        DATA_CHANGED = true;
                        selectAll.setChecked(false);
                    }
                }).setNegativeButton("否", null).create().show();
            } else {
                new AlertDialog.Builder(getActivity()).setMessage("是否分配所选权限？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        allocateRights(rightID);
                        initRightList(allocated);
                        DATA_CHANGED = true;
                        selectAll.setChecked(false);
                    }
                }).setNegativeButton("否", null).create().show();
            }
            Log.i(TAG, "已选权限: " + rightID.toString());
        } else {
            Toast.makeText(getActivity(), "请选择权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void allocateRights(ArrayList<Integer> rightID) {
        handler.sendEmptyMessage(1);
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity(), "DBExperiment.db3", null, 1);
        mySQLiteOpenHelper.getReadableDatabase().execSQL("PRAGMA foreign_keys = ON");
        User user = ((UserRightsActivity) getActivity()).getUser();
        int userID = user.getID();
        for (int id : rightID) {
            mySQLiteOpenHelper.getReadableDatabase().execSQL("insert into user_rights values(null,?,?)", new Object[]{userID, id});
        }
        handler.sendEmptyMessage(2);
    }

    private void removeRights(ArrayList<Integer> rightID) {
        handler.sendEmptyMessage(0);
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity(), "DBExperiment.db3", null, 1);
        mySQLiteOpenHelper.getReadableDatabase().execSQL("PRAGMA foreign_keys = ON");
        User user = ((UserRightsActivity) getActivity()).getUser();
        int userID = user.getID();
        for (int id : rightID) {
            mySQLiteOpenHelper.getReadableDatabase().execSQL("delete from user_rights where user_ID = ? and right_ID = ?", new Object[]{userID, id});
        }
        handler.sendEmptyMessage(2);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showProgress("正在删除");
                    break;
                case 1:
                    showProgress("正在分配");
                    break;
                case 2:
                    dismissDialog();
                    break;
            }
        }
    };

    private void showProgress(String info) {
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(info);
            dialog.create();
        }
        dialog.setMessage(info);
        dialog.show();
    }

    private void dismissDialog() {
        {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
