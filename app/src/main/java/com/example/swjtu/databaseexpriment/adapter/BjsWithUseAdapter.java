package com.example.swjtu.databaseexpriment.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import android.widget.ToggleButton;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.basicManagerUI.TableWithUseActivity;
import com.example.swjtu.databaseexpriment.entity.BjsWithUse;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class BjsWithUseAdapter extends RecyclerView.Adapter<BjsWithUseAdapter.ViewHolder> {
    private static final String TAG = "BjsWithUseAdapter";
    private List<BjsWithUse> bjsWithUses;
    public ArrayList<Boolean> checked;
    private Context context;

    public BjsWithUseAdapter(List<BjsWithUse> bjsWithUses) {
        this.bjsWithUses = bjsWithUses;
        checked = new ArrayList<>();
        for (int i = 0; i < bjsWithUses.size(); i++) {
            checked.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_with_use, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bjsName.setText(bjsWithUses.get(position).getBjsName());
        holder.ID.setText("" + bjsWithUses.get(position).getId());
        holder.num.setText("" + bjsWithUses.get(position).getNum());
        holder.inUse.setText(bjsWithUses.get(position).getIsUse());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TableWithUseActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
                    return;
                View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_with_use_change, null);
                final EditText editNum = (EditText) dialog.findViewById(R.id.editNum);
                final EditText editBjsName = (EditText) dialog.findViewById(R.id.editBjsName);
                final ToggleButton toggleButton = (ToggleButton) dialog.findViewById(R.id.toggleUse);
                final String oldName = bjsWithUses.get(position).getBjsName();
                final int oldNum = bjsWithUses.get(position).getNum();
                final String oldUse = bjsWithUses.get(position).getIsUse();
                final boolean oldChecked = oldUse.equals("是") ? true : false;
                editNum.setText("" + oldNum);
                editBjsName.setText(oldName);
                toggleButton.setChecked(oldChecked);
                new AlertDialog.Builder(context).setView(dialog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = editBjsName.getText().toString().trim();
                        String newNum = editNum.getText().toString().trim();
                        boolean newChecked = toggleButton.isChecked();
                        if (!TextUtils.isDigitsOnly(newNum)) {
                            Toast.makeText(context, "序号应为整数", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<BjsWithUse> rights = DataSupport.select("*").where("bjsName = ?", newName).find(BjsWithUse.class);
                        if (rights.size() > 0 && rights.get(0).getId() != bjsWithUses.get(position).getId()) {
                            Toast.makeText(context, "编辑室已存在", Toast.LENGTH_SHORT).show();
                        } else {
                           // Toast.makeText(context, "newChecked:" + newChecked + ",oldChecked:" + oldChecked, Toast.LENGTH_SHORT).show();
                            if (!newName.equals(oldName) || !newNum.equals("" + oldNum) || newChecked != oldChecked) {
                                BjsWithUse rightWithOrder = new BjsWithUse();
                                rightWithOrder.setBjsName(newName);
                                rightWithOrder.setNum(Integer.parseInt(newNum));
                                rightWithOrder.setIsUse(newChecked ? "是" : "否");
                                rightWithOrder.updateAll("bjsName = ? and num = ?", oldName, "" + oldNum);
                                ((TableWithUseActivity) context).refreshRecycler();
                              //  Toast.makeText(context, "change", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });

        if (((TableWithUseActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        else holder.checkBoxDelete.setVisibility(View.GONE);

        Log.i(TAG, "checked[position]: " + checked.get(position) + ",position:" + position);
        holder.checkBoxDelete.setChecked(checked.get(position));
        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableWithUseActivity activity = (TableWithUseActivity) context;
                int firstPosition = ((LinearLayoutManager) (((TableWithUseActivity) context).recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager) (((TableWithUseActivity) context).recyclerView.getLayoutManager())).findLastVisibleItemPosition();
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
        return bjsWithUses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bjsName;
        TextView ID;
        TextView num;
        TextView inUse;
        CheckBox checkBoxDelete;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            bjsName = (TextView) itemView.findViewById(R.id.txt_bjs_name);
            ID = (TextView) itemView.findViewById(R.id.txtID);
            num = (TextView) itemView.findViewById(R.id.txtOrder);
            inUse = (TextView) itemView.findViewById(R.id.txtInUse);
            checkBoxDelete = (CheckBox) itemView.findViewById(R.id.checkbox_delete);
        }
    }

}
