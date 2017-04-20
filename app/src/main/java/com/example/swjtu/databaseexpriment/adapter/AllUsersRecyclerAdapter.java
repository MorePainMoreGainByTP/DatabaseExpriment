package com.example.swjtu.databaseexpriment.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.AllUsersActivity;
import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.User;
import com.example.swjtu.databaseexpriment.updatePassword.UpdatePassActivity;
import com.example.swjtu.databaseexpriment.userRights.UserRightsActivity;

import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class AllUsersRecyclerAdapter extends RecyclerView.Adapter<AllUsersRecyclerAdapter.ViewHolder> {

    private static final String TAG = "AllUsersRecyclerAdapter";
    
    private List<User> userNames;

    private Context context;

    public AllUsersRecyclerAdapter(List<User> userNames) {
        this.userNames = userNames;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_all_users, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.userName.setText(userNames.get(position).getUserName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setItems(new String[]{"权限查看", "修改密码"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Log.i(TAG, "position: "+position);
                                context.startActivity(new Intent(context, UserRightsActivity.class).putExtra("user", userNames.get(position)));
                                break;
                            case 1:
                                ((AllUsersActivity) context).startActivityForResult(new Intent(context, UpdatePassActivity.class).putExtra("user", userNames.get(position)), 2);
                                break;
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userNames.size();
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
