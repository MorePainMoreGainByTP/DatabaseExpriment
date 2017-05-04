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
import com.example.swjtu.databaseexpriment.basicManagerUI.BookTypeActivity;
import com.example.swjtu.databaseexpriment.entity.BookType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class BookTypeAdapter extends RecyclerView.Adapter<BookTypeAdapter.ViewHolder> {

    private static final String TAG = "BookTypeAdapter";

    private List<BookType> bookTypes;
    public ArrayList<Boolean> checked;
    private Context context;

    public BookTypeAdapter(List<BookType> bookTypes) {
        this.bookTypes = bookTypes;
        checked = new ArrayList<>();
        for (int i = 0; i < bookTypes.size(); i++) {
            checked.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bookType.setText(bookTypes.get(position).getBookType());
        holder.ID.setText("" + bookTypes.get(position).getId());
        holder.code.setText(bookTypes.get(position).getCode());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BookTypeActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
                    return;
                View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_book_type, null);
                final EditText editCode = (EditText) dialog.findViewById(R.id.editCode);
                final EditText editBookType = (EditText) dialog.findViewById(R.id.editBookType);
                final String oldName = bookTypes.get(position).getBookType();
                final String oldCode = bookTypes.get(position).getCode();
                editCode.setText(oldCode);
                editBookType.setText(oldName);
                new AlertDialog.Builder(context).setView(dialog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = editBookType.getText().toString().trim();
                        String newCode = editCode.getText().toString().trim();
                        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newCode)) {
                            Toast.makeText(context, "编码与图书类型为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<BookType> bookTypesTemp = DataSupport.select("*").where("bookType = ?", newName).find(BookType.class);
                        if (bookTypesTemp.size() > 0 && bookTypesTemp.get(0).getId() != bookTypes.get(position).getId()) {
                            Toast.makeText(context, "图书类型已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!newName.equals(oldName) || !newCode.equals("" + oldCode)) {
                                BookType rightWithOrder = new BookType();
                                rightWithOrder.setBookType(newName);
                                rightWithOrder.setCode(newCode);
                                rightWithOrder.updateAll("bookType = ? and code = ?", oldName, "" + oldCode);
                                ((BookTypeActivity) context).refreshRecycler();
                            }
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });

        if (((BookTypeActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        else holder.checkBoxDelete.setVisibility(View.GONE);

        Log.i(TAG, "checked[position]: " + checked.get(position) + ",position:" + position);
        holder.checkBoxDelete.setChecked(checked.get(position));
        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BookTypeActivity activity = (BookTypeActivity) context;
                int firstPosition = ((LinearLayoutManager) (((BookTypeActivity) context).recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager) (((BookTypeActivity) context).recyclerView.getLayoutManager())).findLastVisibleItemPosition();
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
        return bookTypes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookType;
        TextView ID;
        TextView code;
        CheckBox checkBoxDelete;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            bookType = (TextView) itemView.findViewById(R.id.txtBookType);
            ID = (TextView) itemView.findViewById(R.id.txtID);
            code = (TextView) itemView.findViewById(R.id.txtCode);
            checkBoxDelete = (CheckBox) itemView.findViewById(R.id.checkbox_delete);
        }
    }

}
