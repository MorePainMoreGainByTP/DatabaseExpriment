package com.example.swjtu.databaseexpriment.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.basicManagerUI.SimpleTableActivity;
import com.example.swjtu.databaseexpriment.entity.SimpleRight;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class SimpleRightAdapter extends RecyclerView.Adapter<SimpleRightAdapter.ViewHolder> {

    private static final String TAG = "SimpleRightAdapter";

    private List<SimpleRight> simpleRights;
    public ArrayList<Boolean> checked;
    private Context context;

    public SimpleRightAdapter(List<SimpleRight> simpleRights) {
        this.simpleRights = simpleRights;
        checked = new ArrayList<>();
        for (int i = 0; i < simpleRights.size(); i++) {
            checked.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_simple_table, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.rightName.setText(simpleRights.get(position).getBjsName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((SimpleTableActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
                    return;
                View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_simple_change, null);
                final EditText editText = (EditText) dialog.findViewById(R.id.editChange);
                final String oldName = simpleRights.get(position).getBjsName();
                editText.setText(oldName);
                new AlertDialog.Builder(context).setView(dialog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = editText.getText().toString().trim();
                        List<SimpleRight> rights = DataSupport.select("*").where("bjsName = ?", newName).find(SimpleRight.class);
                        if (rights.size() > 0) {
                            Toast.makeText(context, "该权限已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!newName.equals(oldName)) {
                                simpleRights.get(position).setBjsName(newName);
                                SimpleRightAdapter.this.notifyDataSetChanged();
                                SimpleRight simpleRight = new SimpleRight();
                                simpleRight.setBjsName(newName);
                                simpleRight.updateAll("bjsName = ?", oldName);

                            }
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });

        if (((SimpleTableActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        else holder.checkBoxDelete.setVisibility(View.GONE);

        Log.i(TAG, "checked[position]: " + checked.get(position) + ",position:" + position);
        holder.checkBoxDelete.setChecked(checked.get(position));
        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SimpleTableActivity activity = (SimpleTableActivity) context;
                int firstPosition = ((LinearLayoutManager) (((SimpleTableActivity) context).recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager) (((SimpleTableActivity) context).recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                if (firstPosition <= position && position <= lastPosition) {
                    checked.set(position, isChecked);
                    Log.i(TAG, "checked[position] change: " + checked.get(position) + ",position:" + position);
                    if (isChecked) {
                        activity.selectedCount++;
                        activity.updateSelectedCount();
                    } else {
                        activity.selectedCount--;
                        activity.updateSelectedCount();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return simpleRights.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rightName;
        CheckBox checkBoxDelete;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            rightName = (TextView) itemView.findViewById(R.id.txt_bjs_name);
            checkBoxDelete = (CheckBox) itemView.findViewById(R.id.checkbox_delete);
        }
    }

}
