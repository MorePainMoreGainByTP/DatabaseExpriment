package com.example.swjtu.integrateddesign.rightManage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swjtu.integrateddesign.R;
import com.example.swjtu.integrateddesign.entity.Manager;

import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class ManagerRecyclerAdapter extends RecyclerView.Adapter<ManagerRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ManagerRecyclerAdapter";

    private List<Manager> managers;

    private Context context;

    public ManagerRecyclerAdapter(List<Manager> managers) {
        this.managers = managers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_managers, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.userName.setText(managers.get(position).getUserName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setItems(new String[]{"权限查看", "修改密码"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                context.startActivity(new Intent(context, ManagerRightsActivity.class).putExtra("manager", managers.get(position)));
                                break;
                            case 1:
                                ((RootPageActivity) context).startActivityForResult(new Intent(context, UpdatePassActivity.class).putExtra("manager", managers.get(position)), 2);
                                break;
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return managers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView rightArrow;
        CheckBox checkBoxDelete;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            userName = (TextView) itemView.findViewById(R.id.userName);
            rightArrow = (ImageView) itemView.findViewById(R.id.rightArrow);
            checkBoxDelete = (CheckBox) itemView.findViewById(R.id.checkBoxDelete);
        }
    }

}
