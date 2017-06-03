package com.example.swjtu.databaseexpriment.exercise8;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.swjtu.databaseexpriment.R;
import com.example.swjtu.databaseexpriment.entity.BookIn;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.litepal.crud.DataSupport;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangpeng on 2017/6/3.
 */

public class BookInPicActivity extends AppCompatActivity {

    private BarChart barChart;
    private String[] xAxisName = new String[]{"一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_in_pic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("图书入库统计图");
        setSupportActionBar(toolbar);

        barChart = (BarChart) findViewById(R.id.barChart);
        setXAxis();
        initData();
    }

    private void setXAxis() {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //设置x坐标的位置
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisName[(int) value];
            }
        });
        barChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) (value) + "";
            }
        });
        barChart.getAxisLeft().setAxisMinimum(0f); // start at zero
        barChart.getAxisRight().setEnabled(false);
    }

    private void initData() {
        List<Double> sum = getSumPerMonth();
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < xAxisName.length; i++) {
            barEntries.add(new BarEntry(i, (float) sum.get(i).doubleValue()));
        }
        BarDataSet dataSet = new BarDataSet(barEntries, "每月入库数");
        dataSet.setColors(new int[]{R.color.springgreen, R.color.yellow, R.color.red, R.color.deepskyblue}, this);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f); //由x坐标值已经决定了两个bar之间的间隙为1，所以这里设置bar的宽度为0.9，则bar之间的宽度就为0.1了
        barChart.clear();
        barChart.animateY(2000);    //设置动画效果
        barChart.setData(data);
        barChart.setFitBars(true);  //使所有的bar都能完整显示出来
        barChart.invalidate();
    }

    private List<Double> getSumPerMonth() {
        List<Double> sum = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Date initDate = Date.valueOf("2017-1-1");
            initDate.setMonth(i);
            long startTime = initDate.getTime();
            if (i != 11)
                initDate.setMonth(i + 1);
            else initDate.setYear(initDate.getYear() + 1);
            long endTime = initDate.getTime();
            double sumMonth = DataSupport.where("rkrq >= ? and rkrq <= ?", startTime + "", endTime + "").sum(BookIn.class, "rkcs", double.class);
            sum.add(sumMonth);
        }
        return sum;
    }
}
