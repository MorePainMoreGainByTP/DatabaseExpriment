package com.example.swjtu.databaseexpriment.exercise8;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;

import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class BookInRecyclerAdapter extends RecyclerView.Adapter<BookInRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<List<String>> contents;

    public BookInRecyclerAdapter(List<List<String>> contents) {
        this.contents = contents;
    }

    @Override
    public BookInRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_book_in, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookInRecyclerAdapter.ViewHolder holder, int position) {
        List<String> row = contents.get(position);
        holder.bookNo.setText(row.get(0));
        holder.bookName.setText(row.get(1));
        holder.author.setText(row.get(2));
        holder.price.setText(row.get(3));
        holder.rkcs.setText(row.get(4));
        holder.zongMaYang.setText(row.get(5));
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookNo;
        TextView bookName;
        TextView author;
        TextView price;
        TextView rkcs;
        TextView zongMaYang;

        public ViewHolder(View itemView) {
            super(itemView);
            bookNo = (TextView) itemView.findViewById(R.id.bookNo);
            bookName = (TextView) itemView.findViewById(R.id.bookName);
            author = (TextView) itemView.findViewById(R.id.author);
            rkcs = (TextView) itemView.findViewById(R.id.bookInNum);
            price = (TextView) itemView.findViewById(R.id.price);
            zongMaYang = (TextView) itemView.findViewById(R.id.zongMaYang);
        }
    }

}
