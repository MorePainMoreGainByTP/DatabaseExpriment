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

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.basicManagerUI.TableWithOrderActivity;
import com.example.swjtu.databaseexpriment.entity.RightWithOrder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class RightWithOrderAdapter extends RecyclerView.Adapter<RightWithOrderAdapter.ViewHolder> {

    private static final String TAG = "RightWithOrderAdapter";
    private List<RightWithOrder> rightWithOrders;
    public ArrayList<Boolean> checked;
    private Context context;

    public RightWithOrderAdapter(List<RightWithOrder> rightWithOrders) {
        this.rightWithOrders = rightWithOrders;
        checked = new ArrayList<>();
        for (int i = 0; i < rightWithOrders.size(); i++) {
            checked.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_with_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bjsName.setText(rightWithOrders.get(position).getBjsName());
        holder.ID.setText(""+rightWithOrders.get(position).getId());
        holder.num.setText(""+rightWithOrders.get(position).getNum());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TableWithOrderActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
                    return;
                View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_with_order_change, null);
                final EditText editNum = (EditText) dialog.findViewById(R.id.editNum);
                final EditText editBjsName = (EditText) dialog.findViewById(R.id.editBjsName);
                final String oldName = rightWithOrders.get(position).getBjsName();
                final int oldNum = rightWithOrders.get(position).getNum();
                editNum.setText(""+oldNum);
                editBjsName.setText(oldName);
                new AlertDialog.Builder(context).setView(dialog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = editBjsName.getText().toString().trim();
                        String newNum = editNum.getText().toString().trim();
                        if (TextUtils.isDigitsOnly(newNum)) {
                            Toast.makeText(context, "序号为整数", Toast.LENGTH_SHORT).show();
                        }
                        List<RightWithOrder> rights = DataSupport.select("*").where("bjsName = ? and num = ?", newName, newNum).find(RightWithOrder.class);
                        if (rights.size() > 0) {
                            Toast.makeText(context, "该权限已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!newName.equals(oldName)) {
                                rightWithOrders.get(position).setBjsName(newName);
                                RightWithOrderAdapter.this.notifyDataSetChanged();
                                RightWithOrder rightWithOrder = new RightWithOrder();
                                rightWithOrder.setBjsName(newName);
                                rightWithOrder.setNum(Integer.parseInt(newNum));
                                rightWithOrder.updateAll("bjsName = ? and num = ?", oldName, ""+oldNum);
                            }
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });

        if (((TableWithOrderActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        else holder.checkBoxDelete.setVisibility(View.GONE);

        Log.i(TAG, "checked[position]: " + checked.get(position) + ",position:" + position);
        holder.checkBoxDelete.setChecked(checked.get(position));
        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableWithOrderActivity activity = (TableWithOrderActivity) context;
                int firstPosition = ((LinearLayoutManager) (((TableWithOrderActivity) context).recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager) (((TableWithOrderActivity) context).recyclerView.getLayoutManager())).findLastVisibleItemPosition();
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
        return rightWithOrders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bjsName;
        TextView ID;
        TextView num;
        CheckBox checkBoxDelete;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            bjsName = (TextView) itemView.findViewById(R.id.txt_bjs_name);
            ID = (TextView) itemView.findViewById(R.id.txtID);
            num = (TextView) itemView.findViewById(R.id.txtOrder);
            checkBoxDelete = (CheckBox) itemView.findViewById(R.id.checkbox_delete);
        }
    }

}
