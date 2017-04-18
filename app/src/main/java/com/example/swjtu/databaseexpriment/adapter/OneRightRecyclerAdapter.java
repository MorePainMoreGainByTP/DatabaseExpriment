package com.example.swjtu.databaseexpriment.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.Right;
import com.example.swjtu.databaseexpriment.winName.UnknownActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class OneRightRecyclerAdapter extends RecyclerView.Adapter<OneRightRecyclerAdapter.ViewHolder> {

    List<Right> rightList;
    List<Right> childList;
    private Context context;
    private boolean showCheckBox = false;

    public OneRightRecyclerAdapter(List<Right> rightList, String rightModule, boolean showCheckBox) {
        this.rightList = rightList;
        childList = new ArrayList<>();
        for (int i = 0; i < rightList.size(); i++) {
            if (rightList.get(i).getModule().equals(rightModule)) {
                childList.add(rightList.get(i));
            }
        }
        this.showCheckBox = showCheckBox;
    }

    @Override
    public OneRightRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_one_right, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OneRightRecyclerAdapter.ViewHolder holder, int position) {
        final Right right = childList.get(position);
        holder.txtOneRight.setText(right.getRightName());
        if (showCheckBox) {
            holder.checkBoxSelected.setVisibility(View.VISIBLE);
        } else {
            holder.checkBoxSelected.setVisibility(View.INVISIBLE);
        }
        if (!showCheckBox)
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (right.getModule().length() > 3) {
                        context.startActivity(new Intent(context, UnknownActivity.class).putExtra("winName", right.getRightName()));
                    } else {
                        Toast.makeText(context, "该功能未开放", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxSelected;
        TextView txtOneRight;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBoxSelected = (CheckBox) itemView.findViewById(R.id.checkBoxSelected);
            txtOneRight = (TextView) itemView.findViewById(R.id.txtOneRight);
            linearLayout = (LinearLayout) itemView;
        }
    }
}
