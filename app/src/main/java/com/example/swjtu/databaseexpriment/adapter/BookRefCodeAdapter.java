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
import com.example.swjtu.databaseexpriment.basicManagerUI.BookRefCodeActivity;
import com.example.swjtu.databaseexpriment.entity.BjsWithUse;
import com.example.swjtu.databaseexpriment.entity.BookRefCode;
import com.example.swjtu.databaseexpriment.entity.BookType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class BookRefCodeAdapter extends RecyclerView.Adapter<BookRefCodeAdapter.ViewHolder> {
    private static final String TAG = "BookRefCodeAdapter";
    private List<BookRefCode> bookRefKeyList;
    public ArrayList<Boolean> checked;
    private Context context;

    public BookRefCodeAdapter(List<BookRefCode> bookRefKeyList) {
        this.bookRefKeyList = bookRefKeyList;
        checked = new ArrayList<>();
        for (int i = 0; i < bookRefKeyList.size(); i++) {
            checked.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_ref_code, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bjsNameID.setText("" + bookRefKeyList.get(position).getBjsNameID());
        holder.ID.setText("" + bookRefKeyList.get(position).getId());
        holder.bookCode.setText(bookRefKeyList.get(position).getBookTypeCode());
        holder.shuM.setText(bookRefKeyList.get(position).getShuM());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BookRefCodeActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
                    return;
                View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_book_ref_code, null);
                final Spinner spinnerBjsID = (Spinner) dialog.findViewById(R.id.spinnerBjsID);
                final Spinner spinnerBookCode = (Spinner) dialog.findViewById(R.id.spinnerBookCode);
                final EditText shuM = (EditText) dialog.findViewById(R.id.txtShuM);
                List<BjsWithUse> bjsWithUses = DataSupport.select("*").where("isUse = ?", "是").find(BjsWithUse.class);
                final List<Integer> bjsIDs = new ArrayList<>();
                for (BjsWithUse bjsWithUse : bjsWithUses) {
                    bjsIDs.add(bjsWithUse.getId());
                }
                spinnerBjsID.setAdapter(new ArrayAdapter<Integer>(context, android.R.layout.simple_list_item_1, bjsIDs));

                final List<BookType> bookTypes = DataSupport.findAll(BookType.class);
                final List<String> bookCodes = new ArrayList<>();
                for (BookType bookType1 : bookTypes) {
                    bookCodes.add(bookType1.getCode());
                }
                spinnerBookCode.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, bookCodes));
                Log.i(TAG, "spinnerBjsID: " + bjsIDs);
                Log.i(TAG, "spinnerBookCode: " + bookCodes);

                final int oldBjsID = bookRefKeyList.get(position).getBjsNameID();
                final String oldBookCode = bookRefKeyList.get(position).getBookTypeCode();
                final String oldShuM = bookRefKeyList.get(position).getShuM();
                Log.i(TAG, "oldBjsID: " + oldBjsID);
                Log.i(TAG, "oldBookCode: " + oldBookCode);
                Log.i(TAG, "oldShuM: " + oldShuM);

                int bjsPosition = 0;
                for (int i = 0; i < bjsIDs.size(); i++) {
                    if (bjsIDs.get(i) == oldBjsID) {
                        bjsPosition = i;
                        break;
                    }
                }
                spinnerBjsID.setSelection(bjsPosition);
                int bookCodePosition = 0;
                for (int i = 0; i < bookTypes.size(); i++) {
                    if (bookCodes.get(i).equals(oldBookCode)) {
                        bookCodePosition = i;
                        break;
                    }
                }
                spinnerBookCode.setSelection(bookCodePosition);
                Log.i(TAG, "bjsPosition: " + bjsPosition + "\t" + "bookCodePosition: " + bookCodePosition);
                shuM.setText(oldShuM);

                new AlertDialog.Builder(context).setView(dialog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int newBjsID = bjsIDs.get(spinnerBjsID.getSelectedItemPosition());
                        String newBookCode = bookCodes.get(spinnerBookCode.getSelectedItemPosition());
                        String newShuM = shuM.getText().toString().trim();
                        Log.i(TAG, "newBjsName: " + newBjsID);
                        Log.i(TAG, "newBookCode: " + newBookCode);
                        Log.i(TAG, "newShuM: " + newShuM);
                        if (TextUtils.isEmpty(newShuM)) {
                            Toast.makeText(context, "书名为空", Toast.LENGTH_SHORT).show();
                        } else {
                            List<BookRefCode> bookRefKeyList1 = DataSupport.select("*").where("shuM = ?", newShuM).find(BookRefCode.class);
                            if (bookRefKeyList1.size() > 0 && bookRefKeyList1.get(0).getId() != bookRefKeyList.get(position).getId()) {
                                Toast.makeText(context, "书名已存在", Toast.LENGTH_SHORT).show();
                            } else {
                                // Toast.makeText(context, "newChecked:" + newChecked + ",oldChecked:" + oldChecked, Toast.LENGTH_SHORT).show();
                                if (newBjsID != oldBjsID || !newBookCode.equals(oldBookCode) || !newShuM.equals(oldShuM)) {
                                    BookRefCode bookRefKey = new BookRefCode();
                                    bookRefKey.setBookTypeCode(newBookCode);
                                    bookRefKey.setBjsNameID(newBjsID);
                                    bookRefKey.setShuM(newShuM);
                                    bookRefKey.updateAll("shuM = ?", oldShuM);
                                    ((BookRefCodeActivity) context).refreshRecycler();
                                    //  Toast.makeText(context, "change", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
            }
        });

        if (((BookRefCodeActivity) context).deleteLayout.getVisibility() == View.VISIBLE)
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        else holder.checkBoxDelete.setVisibility(View.GONE);

        Log.i(TAG, "checked[position]: " + checked.get(position) + ",position:" + position);
        holder.checkBoxDelete.setChecked(checked.get(position));
        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BookRefCodeActivity activity = (BookRefCodeActivity) context;
                int firstPosition = ((LinearLayoutManager) (((BookRefCodeActivity) context).recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                int lastPosition = ((LinearLayoutManager) (((BookRefCodeActivity) context).recyclerView.getLayoutManager())).findLastVisibleItemPosition();
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
        return bookRefKeyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bjsNameID;
        TextView bookCode;
        TextView shuM;
        TextView ID;
        CheckBox checkBoxDelete;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            bjsNameID = (TextView) itemView.findViewById(R.id.txtBjsNameID);
            bookCode = (TextView) itemView.findViewById(R.id.txtBookCode);
            shuM = (TextView) itemView.findViewById(R.id.txtShuM);
            ID = (TextView) itemView.findViewById(R.id.txtID);
            checkBoxDelete = (CheckBox) itemView.findViewById(R.id.checkbox_delete);
        }
    }

}
