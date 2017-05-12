package com.example.swjtu.databaseexpriment.exercise6;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.Student;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/5/11.
 */

public class FirstFragment extends Fragment {
    private List<Student> studentList;
    private FirstStudentAdapter studentAdapter;
    public RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private boolean isFirstPage = true;

    private static final String TAG = "FirstFragment";

    public static String KEY_STUDENT = "key_student";
    public static String KEY_FIRST_PAGE = "key_first_page";

    public static FirstFragment getInstance(List<Student> studentList, boolean isFirstPage) {
        FirstFragment firstFragment = new FirstFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_STUDENT, (ArrayList<Student>) studentList);
        bundle.putBoolean(KEY_FIRST_PAGE, isFirstPage);
        firstFragment.setArguments(bundle);
        return firstFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        studentList = (List<Student>) bundle.getSerializable(KEY_STUDENT);
        isFirstPage = bundle.getBoolean(KEY_FIRST_PAGE);
        View root = null;
        if (isFirstPage) {
            root = inflater.inflate(R.layout.fragment_first_student, container, false);
            ((TextView) root.findViewById(R.id.first_txt1)).setText("ID");
            ((TextView) root.findViewById(R.id.first_txt2)).setText("姓名");
            ((TextView) root.findViewById(R.id.first_txt3)).setText("性别");
            ((TextView) root.findViewById(R.id.first_txt4)).setText("学号");
            ((TextView) root.findViewById(R.id.first_txt5)).setText("学院");
            ((TextView) root.findViewById(R.id.first_txt6)).setText("专业");
        } else {
            root = inflater.inflate(R.layout.fragment_second_student, container, false);
            ((TextView) root.findViewById(R.id.second_txt1)).setText("年级");
            ((TextView) root.findViewById(R.id.second_txt2)).setText("班级");
            ((TextView) root.findViewById(R.id.second_txt3)).setText("入学日期");
            ((TextView) root.findViewById(R.id.second_txt4)).setText("学制");
            ((TextView) root.findViewById(R.id.second_txt5)).setText("类别");
            ((TextView) root.findViewById(R.id.second_txt6)).setText("出生日期");
        }
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerFirst);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ((StudentInfoActivity) getActivity()).firstVisibleIndex = linearLayoutManager.findLastVisibleItemPosition();

                Log.i(TAG, "firstVisibleIndex: " + linearLayoutManager.findFirstVisibleItemPosition());
                Log.i(TAG, "LastVisibleItemPosition: " + linearLayoutManager.findLastVisibleItemPosition());
            }
        });
        linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        studentAdapter = new FirstStudentAdapter(studentList, isFirstPage);
        recyclerView.setAdapter(studentAdapter);
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "setUserVisibleHint: isVisibleToUser " + isVisibleToUser);
        if (isVisibleToUser) {
            if (recyclerView != null)
                recyclerView.scrollToPosition(((StudentInfoActivity) getActivity()).firstVisibleIndex);
        }
    }

    public void insertItem() {
        if (recyclerView != null) {
            studentList = DataSupport.findAll(Student.class);
            studentAdapter = new FirstStudentAdapter(studentList, isFirstPage);
            recyclerView.setAdapter(studentAdapter);
            recyclerView.scrollToPosition(studentList.size() - 1);
        }
    }

    public void deleteItem(List<Student> newStudentList) {
        this.studentList = newStudentList;
        studentAdapter = new FirstStudentAdapter(studentList, isFirstPage);
        recyclerView.setAdapter(studentAdapter);
        studentAdapter.notifyDataSetChanged();
    }

}
