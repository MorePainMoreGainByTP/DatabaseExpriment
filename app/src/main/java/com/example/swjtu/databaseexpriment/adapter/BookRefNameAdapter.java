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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.basicManagerUI.BookRefNameActivity;
import com.example.swjtu.databaseexpriment.entity.BjsWithUse;
import com.example.swjtu.databaseexpriment.entity.BookRefName;
import com.example.swjtu.databaseexpriment.entity.BookType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class BookRefNameAdapter extends RecyclerView.Adapter<BookRefNameAdapter.ViewHolder> {
    private static final String TAG = "BookRefNameAdapter";
    private List<BookRefName> bookRefNames;
    public ArrayList<Boolean> checked;
    private Context context;

    public BookRefNameAdapter(List<BookRefName> bookRefNames) {
        this.bookRefNames = bookRefNames;
        checked = new ArrayList<>();
        for (int i = 0; i < bookRefNames.size(); i++) {
            checked.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_ref_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bjsName.setText(bookRefNames.get(position).getBjsName());
        holder.ID.setText("" + bookRefNames.get(position).getId());
        holder.bookType.setText(bookRefNames.get(position).getBookType());
        holder.shuM.setText(bookRefNames.get(position).getShuM());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BookRefNameActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
                    return;
                View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_book_ref_name, null);
                final Spinner spinnerBjs = (Spinner) dialog.findViewById(R.id.spinnerBjs);
                final Spinner spinnerBookType = (Spinner) dialog.findViewById(R.id.spinnerBookType);
                final EditText shuM = (EditText) dialog.findViewById(R.id.txtShuM);
                List<BjsWithUse> bjsWithUses = DataSupport.select("*").where("isUse = ?", "是").find(BjsWithUse.class);
                final List<String> bjsNames = new ArrayList<String>();
                for (BjsWithUse bjsWithUse : bjsWithUses) {
                    bjsNames.add(bjsWithUse.getBjsName());
                }
                spinnerBjs.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, bjsNames));

                final List<BookType> bookTypes = DataSupport.findAll(BookType.class);
                final List<String> bookType = new ArrayList<String>();
                for (BookType bookType1 : bookTypes) {
                    bookType.add(bookType1.getBookType());
                }
                spinnerBookType.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, bookType));
                Log.i(TAG, "spinnerBjs: " + bjsNames);
                Log.i(TAG, "spinnerBookType: " + bookType);

                final String oldBjsName = bookRefNames.get(position).getBjsName();
                final String oldBookType = bookRefNames.get(position).getBookType();
                final String oldShuM = bookRefNames.get(position).getShuM();
                Log.i(TAG, "oldBjsName: " + oldBjsName);
                Log.i(TAG, "oldBookType: " + oldBookType);
                Log.i(TAG, "oldShuM: " + oldShuM);

                int bjsPosition = 0;
                for (int i = 0; i < bjsNames.size(); i++) {
                    if (bjsNames.get(i).equals(oldBjsName)) {
                        bjsPosition = i;
                        break;
                    }
                }
                spinnerBjs.setSelection(bjsPosition);
                int bookTypePosition = 0;
                for (int i = 0; i < bookTypes.size(); i++) {
                    if (bookType.get(i).equals(oldBookType)) {
                        bookTypePosition = i;
                        break;
                    }
                }
                spinnerBookType.setSelection(bookTypePosition);
                Log.i(TAG, "bjsPosition: " + bjsPosition + "\t" + "bookTypePosition: " + bookTypePosition);
                shuM.setText(oldShuM);

                new AlertDialog.Builder(context).setView(dialog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newBjsName = bjsNames.get(spinnerBjs.getSelectedItemPosition());
                        String newBookType = bookType.get(spinnerBookType.getSelectedItemPosition());
                        String newShuM = shuM.getText().toString().trim();
                        Log.i(TAG, "newBjsName: " + newBjsName);
                        Log.i(TAG, "newBookType: " + newBookType);
                        Log.i(TAG, "newShuM: " + newShuM);
                        if (TextUtils.isEmpty(newShuM)) {
                            Toast.makeText(context, "书名为空", Toast.LENGTH_SHORT).show();
                        } else {
                            List<BookRefName> bookRefNames1 = DataSupport.select("*").where("shuM = ?", newShuM).find(BookRefName.class);
                            if (bookRefNames1.size() > 0 && bookRefNames1.get(0).getId() != bookRefNames.get(position).getId()) {
                                Toast.makeText(context, "书名已存在", Toast.LENGTH_SHORT).show();
                            } else {
                                // Toast.makeText(context, "newChecked:" + newChecked + ",oldChecked:" + oldChecked, Toast.LENGTH_SHORT).show();
                                if (!newBjsName.equals(oldBjsName) || !newBookType.equals("" + oldBookType) || !newShuM.equals(oldShuM)) {
                                    BookRefName bookRefName = new BookRefName();
                                    bookRefName.setBookType(newBookType);
                                    bookRefName.setBjsName(newBjsName);
                                    bookRefName.setShuM(newShuM);
                                    bookRefName.updateAll("shuM = ?", oldShuM);
                                    ((BookRefNameActivity) context).refreshRecycler();
                                    //  Toast.makeText(context, "change", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });

        if (((BookRefNameActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        else holder.checkBoxDelete.setVisibility(View.GONE);

        Log.i(TAG, "checked[position]: " + checked.get(position) + ",position:" + position);
        holder.checkBoxDelete.setChecked(checked.get(position));
        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BookRefNameActivity activity = (BookRefNameActivity) context;
                int firstPosition = ((LinearLayoutManager) (((BookRefNameActivity) context).recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager) (((BookRefNameActivity) context).recyclerView.getLayoutManager())).findLastVisibleItemPosition();
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
        return bookRefNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bjsName;
        TextView bookType;
        TextView shuM;
        TextView ID;
        CheckBox checkBoxDelete;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            bjsName = (TextView) itemView.findViewById(R.id.txtBjsName);
            bookType = (TextView) itemView.findViewById(R.id.txtBookType);
            shuM = (TextView) itemView.findViewById(R.id.txtShuM);
            ID = (TextView) itemView.findViewById(R.id.txtID);
            checkBoxDelete = (CheckBox) itemView.findViewById(R.id.checkbox_delete);
        }
    }

}
