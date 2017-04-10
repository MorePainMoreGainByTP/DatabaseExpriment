package com.example.swjtu.databaseexpriment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.Right;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class OneRightRecyclerAdapter extends RecyclerView.Adapter<OneRightRecyclerAdapter.ViewHolder> {

    List<Right> rightList;
    List<Right> childList;

    public OneRightRecyclerAdapter(List<Right> rightList,String rightModule) {
        this.rightList = rightList;
        childList = new ArrayList<>();
        for(int i = 0 ; i < rightList.size();i++){
            if(rightList.get(i).getModule().equals(rightModule)){
                childList.add(rightList.get(i));
            }
        }
    }

    @Override
    public OneRightRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_one_right, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OneRightRecyclerAdapter.ViewHolder holder, int position) {
        Right right = childList.get(position);
        holder.txtOneRight.setText(right.getRightName());
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxSelected;
        TextView txtOneRight;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBoxSelected = (CheckBox) itemView.findViewById(R.id.checkBoxSelected);
            txtOneRight = (TextView) itemView.findViewById(R.id.txtOneRight);
        }
    }
}
