package com.example.swjtu.databaseexpriment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.Right;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class RightModuleRecyclerAdapter extends RecyclerView.Adapter<RightModuleRecyclerAdapter.ViewHolder> {

    List<Right> rightList;

    public RightModuleRecyclerAdapter(List<Right> rightList) {
        this.rightList = rightList;
    }

    @Override
    public RightModuleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_right_module, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RightModuleRecyclerAdapter.ViewHolder holder, int position) {
        holder.rightItem.setAdapter(new OneRightRecyclerAdapter(rightList));
        holder.rightModule.setText(rightList.get(position).getModule());
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
        return rightList.size();
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
