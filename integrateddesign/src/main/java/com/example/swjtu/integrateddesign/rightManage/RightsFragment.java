package com.example.swjtu.integrateddesign.rightManage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.example.swjtu.integrateddesign.R;
import com.example.swjtu.integrateddesign.adapter.RightModuleRecyclerAdapter;
import com.example.swjtu.integrateddesign.entity.Manager;
import com.example.swjtu.integrateddesign.entity.Right;

import org.litepal.crud.DataSupport;

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

    Manager manager;
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
        manager = ((ManagerRightsActivity) getActivity()).getManager();
        initRightList(allocated);
    }

    //重新从数据库中获取权限
    public void initRightList(boolean allocated) {
        rightList = new ArrayList<>();
        if (manager != null) {
            if (!allocated) {//查询用户未分配的权限
                rightList = DataSupport.findAll(Right.class);
                Log.i(TAG, "所有权限： "+rightList);
                Log.i(TAG, "manager权限： "+manager.getRightList());
                for (Right right:manager.getRightList()){
                    for(Right right2:rightList){
                        if(right.getID() == right2.getID()){
                            rightList.remove(right2);
                            break;
                        }
                    }
                }
            } else {//查询用户已分配的权限
                rightList = manager.getRightList();
            }
        } else {
            Toast.makeText(getActivity(), "读取权限失败！", Toast.LENGTH_SHORT).show();
        }
        rightModuleRecyclerAdapter = new RightModuleRecyclerAdapter(rightList, true);
        recyclerView.setAdapter(rightModuleRecyclerAdapter);
        rightModuleRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToManager) {
        super.setUserVisibleHint(isVisibleToManager);
        Log.i(TAG, "setManagerVisibleHint: isVisibleToManager " + isVisibleToManager + ",DATA_CHANGED:" + DATA_CHANGED);
        if (isVisibleToManager) {
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
                new AlertDialog.Builder(getActivity()).setMessage("删除所选权限？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeRights(rightID);
                        initRightList(allocated);
                        DATA_CHANGED = true;
                        selectAll.setChecked(false);
                    }
                }).setNegativeButton("否", null).create().show();
            } else {
                new AlertDialog.Builder(getActivity()).setMessage("分配所选权限？").setPositiveButton("是", new DialogInterface.OnClickListener() {
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
        Log.i(TAG, "分配权限前用户数据: "+manager);
        handler.sendEmptyMessage(1);
        List<Right> rightList = new ArrayList<>();
        for (int id : rightID) {
            List<Right> rightList1 = DataSupport.where("id = ?", id + "").find(Right.class);
            if (rightList1.size() > 0)
                rightList.add(rightList1.get(0));
        }
        if (manager != null) {
            for (Right right : rightList) {
                manager.addRight2List(right);
            }
            manager.saveRightList();
            manager.save();
            Log.i(TAG, "分配权限后用户数据: "+DataSupport.findAll(Manager.class,manager.getId()));
        } else {
            Toast.makeText(getActivity(), "分配失败，请重试！", Toast.LENGTH_SHORT).show();
        }
        handler.sendEmptyMessage(2);
    }

    private void removeRights(ArrayList<Integer> rightID) {
        Log.i(TAG, "移除权限前用户数据: "+manager);
        handler.sendEmptyMessage(0);
        if (manager != null) {
            for (int id : rightID) {
                for (Right right : manager.getRightList()) {
                    if (right.getID() == id) {
                        manager.getRightList().remove(right);
                        break;
                    }
                }
            }
            manager.saveRightList();
            manager.save();
            Log.i(TAG, "移除权限后用户数据: "+DataSupport.findAll(Manager.class,manager.getId()));
        } else {
            Toast.makeText(getActivity(), "移除失败，请重试！", Toast.LENGTH_SHORT).show();
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
