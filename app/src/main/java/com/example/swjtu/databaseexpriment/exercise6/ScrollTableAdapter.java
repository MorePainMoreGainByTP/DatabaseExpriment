package com.example.swjtu.databaseexpriment.exercise6;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;
import com.kelin.scrollablepanel.library.PanelAdapter;

import java.util.List;

/**
 * Created by tangpeng on 2017/5/10.
 */

public class ScrollTableAdapter extends PanelAdapter {

    private List<List<String>> data;

    public ScrollTableAdapter(List<List<String>> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return data.get(0).size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
        String title = data.get(row).get(column);
        TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
        titleViewHolder.titleTextView.setText(title);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScrollTableAdapter.TitleViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scroll_table_item_title, parent, false));
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.title);
        }
    }
}
