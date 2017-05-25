package com.example.swjtu.databaseexpriment.exercise6;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.Student;

import java.sql.Date;
import java.util.List;

/**
 * Created by tangpeng on 2017/4/9.
 */

public class FirstStudentAdapter extends RecyclerView.Adapter<FirstStudentAdapter.ViewHolder> {

    private static final String TAG = "FirstStudentAdapter";
    private List<Student> studentList;
    private boolean isFirstPage = true;
    private Context context;

    private int lastPosition;   //上一个高亮的位置
    private int thisPosition;   //本次高亮的位置

    public FirstStudentAdapter(List<Student> studentList, boolean isFirstPage) {
        this.studentList = studentList;
        this.isFirstPage = isFirstPage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = null;
        if (isFirstPage) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_first_student, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_second_student, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (isFirstPage) {
            holder.txt1.setText(studentList.get(position).getId() + "");
            holder.txt2.setText(studentList.get(position).getName());
            holder.txt3.setText(studentList.get(position).getSex());
            holder.txt4.setText(studentList.get(position).getStuNo() + "");
            holder.txt5.setText(studentList.get(position).getSchoolName());
            holder.txt6.setText(studentList.get(position).getMajor());
        } else {
            holder.txt1.setText(studentList.get(position).getGrade() + "");
            holder.txt2.setText(studentList.get(position).getClassNo() + "");
            //Date enrollDate = studentList.get(position).getEnrollmentDate();
            holder.txt3.setText(new Date(studentList.get(position).getEnrollmentDate()).toString());
            holder.txt4.setText(studentList.get(position).getStudyTime() + "");
            holder.txt5.setText(studentList.get(position).getCategory());
            //Date birDate = studentList.get(position).getBirthDate();
            holder.txt6.setText(new Date(studentList.get(position).getBirthDate()).toString());
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StudentInfoActivity) context).clickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt1;
        TextView txt2;
        TextView txt3;
        TextView txt4;
        TextView txt5;
        TextView txt6;
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView;
            txt1 = (TextView) itemView.findViewById(R.id.txt1); //ID 年级
            txt2 = (TextView) itemView.findViewById(R.id.txt2); //姓名 班级
            txt3 = (TextView) itemView.findViewById(R.id.txt3); //性别 入学日期
            txt4 = (TextView) itemView.findViewById(R.id.txt4); //学号 学制
            txt5 = (TextView) itemView.findViewById(R.id.txt5); //学院 类别
            txt6 = (TextView) itemView.findViewById(R.id.txt6); //专业 出生日期
        }
    }

}
