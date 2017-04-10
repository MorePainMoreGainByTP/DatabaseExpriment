package com.example.swjtu.databaseexpriment.userRights;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import static android.content.ContentValues.TAG;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class RightsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public static boolean DATA_CHANGED = false;

    private static final String KEY_ALLOCATED = "key_allocated";
    List<Right> rightList;

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

    public void initRightList(boolean allocated) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity(), "DBExperiment.db3", null, 1);
        User user = ((UserRightsActivity) getActivity()).getUser();
        if (rightList == null)
            rightList = new ArrayList<>();
        else rightList.clear();
        if (!allocated) {
            //查询用户未分配的所有权限
            Cursor cursor = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select rights.id,rights.right_NO,rights.right_name,rights.right_module,rights.win_name from users,rights " +
                    "where rights.id not in (select user_rights.right_ID from user_rights where users.id = user_rights.user_ID and users.user_name = ?)", new String[]{user.getUserName()});
            while (cursor != null && cursor.moveToNext()) {
                Right right = new Right(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                rightList.add(right);
            }
            if (cursor != null)
                cursor.close();
        } else {
            //查询用户已分配的所有权限
            Cursor cursor2 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select rights.id, rights.right_NO,rights.right_name,rights.right_module,rights.win_name from users,rights " +
                    "where rights.id in (select user_rights.right_ID from user_rights where users.id = user_rights.user_ID and users.user_name = ?)", new String[]{user.getUserName()});
            while (cursor2 != null && cursor2.moveToNext()) {
                Right right = new Right(cursor2.getInt(0), cursor2.getInt(1), cursor2.getString(2), cursor2.getString(3), cursor2.getString(4));
                rightList.add(right);
            }
            if (cursor2 != null)
                cursor2.close();
        }
        //查询所有权限
        Cursor cursor3 = mySQLiteOpenHelper.getReadableDatabase().rawQuery("select * from rights", new String[]{});
        while (cursor3 != null && cursor3.moveToNext()) {
            Log.i(TAG, "initRightList: " + cursor3.getInt(0) + cursor3.getInt(1) + cursor3.getString(2) + cursor3.getString(3) + cursor3.getString(4));
        }
        if (rightModuleRecyclerAdapter != null) {
            rightModuleRecyclerAdapter.notifyDataSetChanged();
            rightModuleRecyclerAdapter.updateRightModuleList();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
//        if (DATA_CHANGED) {
//            initRightList(allocated);
//            if (rightModuleRecyclerAdapter != null) {
//                rightModuleRecyclerAdapter.notifyDataSetChanged();
//                rightModuleRecyclerAdapter.updateRightModuleList();
//            }
//            DATA_CHANGED = false;
//        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "onHiddenChanged: hidden " + hidden);
        if (!hidden) {

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if (DATA_CHANGED) {
                initRightList(allocated);
                if (rightModuleRecyclerAdapter != null) {
                    rightModuleRecyclerAdapter.notifyDataSetChanged();
                    rightModuleRecyclerAdapter.updateRightModuleList();
                }
                DATA_CHANGED = false;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        allocated = bundle.getBoolean(KEY_ALLOCATED);
        initRightList(allocated);
        View root = inflater.inflate(R.layout.fragment_rights, null);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerRightsModule);
        rightModuleRecyclerAdapter = new RightModuleRecyclerAdapter(rightList);
        recyclerView.setAdapter(rightModuleRecyclerAdapter);
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
        Toast.makeText(getActivity(), txtAddOrDelete.getText().toString(), Toast.LENGTH_SHORT).show();
        ArrayList<Integer> rightID = new ArrayList<>();
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
                    TextView rightTxt = (TextView) oneRight.findViewById(R.id.txtOneRight);
                    String rightStr = rightTxt.getText().toString().trim();
                    for (Right right : rightList) {
                        if (right.getRightName().equals(rightStr)) {
                            rightID.add(right.getID());
                            break;
                        }
                    }
                }
            }
        }
        if (rightID.size() > 0) {
            if (allocated) {//已分配权限，对应删除操作
                removeRights(rightID);
            } else {
                allocateRights(rightID);
            }
            initRightList(allocated);
            DATA_CHANGED = true;
        } else {
            Toast.makeText(getActivity(), "请选择权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void allocateRights(ArrayList<Integer> rightID) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity(), "DBExperiment.db3", null, 1);
        mySQLiteOpenHelper.getReadableDatabase().execSQL("PRAGMA foreign_keys = ON");
        User user = ((UserRightsActivity) getActivity()).getUser();
        int userID = user.getID();
        for (int id : rightID) {
            mySQLiteOpenHelper.getReadableDatabase().execSQL("insert into user_rights values(null,?,?)", new Object[]{userID, id});
        }
    }

    private void removeRights(ArrayList<Integer> rightID) {
        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(getActivity(), "DBExperiment.db3", null, 1);
        mySQLiteOpenHelper.getReadableDatabase().execSQL("PRAGMA foreign_keys = ON");
        User user = ((UserRightsActivity) getActivity()).getUser();
        int userID = user.getID();
        for (int id : rightID) {
            mySQLiteOpenHelper.getReadableDatabase().execSQL("delete from user_rights where user_ID = ? and right_ID = ?", new Object[]{userID, id});
        }
    }
}
