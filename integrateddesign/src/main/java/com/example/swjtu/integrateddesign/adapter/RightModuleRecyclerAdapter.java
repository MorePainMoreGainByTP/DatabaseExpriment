package com.example.swjtu.integrateddesign.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swjtu.integrateddesign.R;
import com.example.swjtu.integrateddesign.entity.Right;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class RightModuleRecyclerAdapter extends RecyclerView.Adapter<RightModuleRecyclerAdapter.ViewHolder> {

    List<Right> rightList;
    private Context context;
    List<String> rightModuleList;
    private boolean showCheckBox = false;

    public RightModuleRecyclerAdapter(List<Right> rightList, boolean showCheckBox) {
        this.rightList = rightList;
        this.showCheckBox = showCheckBox;
        updateRightModuleList();
    }

    //将权限按所属模块分类
    public void updateRightModuleList() {
        Collections.sort(rightList, new Comparator<Right>() {
            @Override
            public int compare(Right o1, Right o2) {
                return o1.getModule().compareTo(o2.getModule());
            }
        });

        if (rightModuleList == null)
            rightModuleList = new ArrayList<>();
        else rightModuleList.clear();

        if (rightList.size() > 0) {
            int index = 0;
            String currStr = rightList.get(index).getModule();
            while (index < rightList.size()) {
                rightModuleList.add(currStr);
                while (rightList.get(index).getModule().equals(currStr)) {
                    index++;
                    if (index >= rightList.size())
                        break;
                }
                if (index >= rightList.size())
                    break;
                currStr = rightList.get(index).getModule();
            }
        }
    }


    @Override
    public RightModuleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_right_module, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RightModuleRecyclerAdapter.ViewHolder holder, int position) {
        holder.rightModule.setText(rightModuleList.get(position));
        OneRightRecyclerAdapter oneRightRecyclerAdapter = new OneRightRecyclerAdapter(rightList, rightModuleList.get(position), showCheckBox);
        holder.rightItem.setAdapter(oneRightRecyclerAdapter);
        oneRightRecyclerAdapter.notifyDataSetChanged();
        holder.rightItem.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.expanded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.rightItem.setVisibility(View.VISIBLE);
                } else {
                    holder.rightItem.setVisibility(GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rightModuleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox expanded;
        RecyclerView rightItem;
        LinearLayout linearLayout;
        TextView rightModule;

        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearItem);
            expanded = (CheckBox) itemView.findViewById(R.id.checkBoxExpanded);
            rightModule = (TextView) itemView.findViewById(R.id.rightModule);
            rightItem = (RecyclerView) itemView.findViewById(R.id.recyclerRightItem);
        }
    }
}
