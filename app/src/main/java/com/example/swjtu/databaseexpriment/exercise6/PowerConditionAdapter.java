package com.example.swjtu.databaseexpriment.exercise6;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class PowerConditionAdapter extends RecyclerView.Adapter<PowerConditionAdapter.ViewHolder> {

    private static final String TAG = "PowerConditionAdapter";
    public List<List<String>> conditionStrings = new ArrayList<>();
    private Context context;

    public int count = 0;
    public List<Boolean> checkedList = new ArrayList<>();

    public PowerConditionAdapter(Context context) {
        this.context = context;
        Toast.makeText(context, "PowerConditionAdapter", Toast.LENGTH_SHORT).show();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_power_retrieve, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.logicOption.setText(conditionStrings.get(position).get(0));
        holder.field.setText(conditionStrings.get(position).get(1));
        holder.operator.setText(conditionStrings.get(position).get(2));
        holder.value.setText(conditionStrings.get(position).get(3));

        holder.checkBoxPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.checkBoxPower.getVisibility() == View.VISIBLE) {
                    checkedList.set(position, isChecked);
                    count++;
                }
            }
        });
        holder.checkBoxPower.setChecked(checkedList.get(position));
    }

    @Override
    public int getItemCount() {
        return conditionStrings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView logicOption;
        TextView field;
        TextView operator;
        TextView value;
        CheckBox checkBoxPower;

        public ViewHolder(View itemView) {
            super(itemView);
            logicOption = (TextView) itemView.findViewById(R.id.txt_logic_option);
            field = (TextView) itemView.findViewById(R.id.txt_field);
            operator = (TextView) itemView.findViewById(R.id.txt_operator);
            value = (TextView) itemView.findViewById(R.id.txt_value);
            checkBoxPower = (CheckBox) itemView.findViewById(R.id.check_power);
        }
    }

    public void addConditionItem(String... condition) {
        List<String> strings = new ArrayList<>();
        for (String str : condition) {
            strings.add(str);
        }
        checkedList.add(false);
        conditionStrings.add(strings);
        notifyItemInserted(conditionStrings.size() - 1);
        if (((StudentInfoActivity) context).recyclerView == null) {
            Toast.makeText(context, "recyclerView 为空", Toast.LENGTH_SHORT).show();
        } else
            ((StudentInfoActivity) context).recyclerView.scrollToPosition(conditionStrings.size() - 1);
    }

    public void deleteCheckedItem() {
        for (int j = conditionStrings.size() - 1; j >= 0; j--) {
            if (checkedList.get(j)) {
                conditionStrings.remove(j);
                checkedList.remove(j);
                count--;
            }
        }
        if(conditionStrings.size() > 0)
        conditionStrings.get(0).set(0, "");
        notifyDataSetChanged();
    }
}
