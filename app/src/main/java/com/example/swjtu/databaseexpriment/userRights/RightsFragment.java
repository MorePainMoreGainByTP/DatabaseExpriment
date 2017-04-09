package com.example.swjtu.databaseexpriment.userRights;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.adapter.RightModuleRecyclerAdapter;
import com.example.swjtu.databaseexpriment.entity.Right;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class RightsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener,View.OnClickListener {

    private static final String KEY_RIGHT = "key_right";
    private static final String KEY_ALLOCATED = "key_allocated";
    List<Right> rightList;

    RecyclerView recyclerView;
    CheckBox selectAll;
    TextView txtAddOrDelete;

    public static RightsFragment newInstance(ArrayList<Right> rights, boolean allocated) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_RIGHT, rights);
        bundle.putBoolean(KEY_ALLOCATED, allocated);
        RightsFragment fragment = new RightsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        rightList = (List<Right>) bundle.getSerializable(KEY_RIGHT);
        View root = inflater.inflate(R.layout.fragment_rights, null);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerRightsModule);
        recyclerView.setAdapter(new RightModuleRecyclerAdapter(rightList));
        selectAll = (CheckBox) root.findViewById(R.id.checkBoxSelectAll);
        selectAll.setOnCheckedChangeListener(this);
        txtAddOrDelete = (TextView) root.findViewById(R.id.txtAddOrDelete);
        txtAddOrDelete.setOnClickListener(this);
        if (bundle.getBoolean(KEY_ALLOCATED)) {
            txtAddOrDelete.setText("添加");
        } else {
            txtAddOrDelete.setText("删除");
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
                    checkBoxSelected.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        ArrayList<ArrayList<Integer>> childIndex = new ArrayList<>();
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
    }
}
