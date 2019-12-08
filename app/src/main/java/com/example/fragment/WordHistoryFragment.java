package com.example.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bean.Process;
import com.example.bean.WordList;
import com.example.seeker.R;
import com.example.ui.WordActivity;
import com.example.utils.L;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

/**
 * Created by ${WLX} on 2019/7/23.
 */

public class WordHistoryFragment extends Fragment implements OnChartValueSelectedListener, View.OnClickListener {
    private static final String TAG = "WordHistoryFragment";
    private BarChart mBarChart;
    private Button btn_show_values;//显示顶点值
    private Button btn_amin_xy;//xy轴动画
    private View view;
    private int currentResite;
    private int currentTest;
    private int total;
    private String currentUsername;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.word_history_fragment, container, false);
        initView();
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WordActivity wordActivity= (WordActivity) getActivity();
        currentUsername = wordActivity.getUsername();
    }

    private float CaculateProcess(String guid,int type) {
        //type=0说明是算背诵进度，=1说明是计算测试进度
        float  result=0;
        List<Process> processes = LitePal.where("guid=? ", guid).find(Process.class);
        Process process = processes.get(0);
        currentResite=process.getCurrentReciteId();
        currentTest=process.getCurrentTestId();
        total=process.getTotal();
        if (total != 0) {
            if (type == 0) {
                result = (float) currentResite / total;
            } else {
                result = (float)currentTest / total;
            }
        } else {
            return 0;
        }
        //这里得到的是百分比，也就是小数，所以要乘以100，不然显示永远是0
        if (result==0) {
            return 0;
        }else
        return result*100;
    }
    //初始化
    private void initView() {
        //基本控件
        btn_show_values = (Button) view.findViewById(R.id.studyHistory_btn_show_values);
        btn_show_values.setOnClickListener(this);
        btn_amin_xy = (Button) view.findViewById(R.id.studyHistory_btn_anim_xy);
        btn_amin_xy.setOnClickListener(this);

        //第一步，配置好条形图
        mBarChart = (BarChart) view.findViewById(R.id.studyHistory_BarChart);
        //设置表格上的点，被点击的时候，的回调函数
        mBarChart.setOnChartValueSelectedListener(this);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.getDescription().setEnabled(false);
        // 如果60多个条目显示在图表,drawn没有值
        mBarChart.setMaxVisibleValueCount(60);
        // 禁止x轴y轴同时进行缩放
        mBarChart.setPinchZoom(false);
        //是否显示表格颜色
        mBarChart.setDrawGridBackground(false);

        //第二步，准备数据配置好X轴
        final ArrayList<String> data = new ArrayList<>();
        data.add("");
        data.add("大学英语四级");
        data.add("");
        data.add("");
        data.add("大学英语六级");
        data.add("");
        data.add("");
        data.add("考研词汇");
        data.add("");
        data.add("");
        data.add("出国考试(G)");
        data.add("");
        data.add("");
        data.add("出国考试(GM)");
        data.add("");
        data.add("");
        data.add("国外生活词汇");
        data.add("");
        data.add("");
        data.add("高考词汇");
        data.add("");
        data.add("");
        data.add("出国考试(T)");
        data.add("");
        data.add("");
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);    //是不是显示轴上的刻度
        xAxis.setLabelCount(data.size());    //强制有多少个刻度,不然的话只会隔几个显示
        xAxis.setAxisMaximum(24f);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelRotationAngle(75);//设置字体角度为90度
        //下面把X轴的刻度改为文字
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(data.get((int) value));
            }
        });

        //第三步，配置好左侧和右侧的Y轴
        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        //这个替换setStartAtZero(true)
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        //设置Y轴的显示格式
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String str = value + "";
                if (str.length()==0) {
                    return str;
                }
                return str.substring(0,str.indexOf("."));//设置自己的返回位数
            }
        });

        YAxis rightAxis = mBarChart.getAxisRight();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setDrawGridLines(false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(100f);
        //设置Y轴的显示格式
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String str = value + "";
                if (str.length()==0) {
                    return str;
                }
                return str.substring(0,str.indexOf("."));//设置自己的返回位数
            }
        });
        // 设置标示，就是那个一组y的value的
        Legend l = mBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        //样式
        l.setForm(Legend.LegendForm.SQUARE);
        //字体
        l.setFormSize(8f);
        //大小
        l.setTextSize(8f);
        l.setXEntrySpace(4f);
        ArrayList<BarEntry> yVals1 = getData();
        setData(yVals1);
    }

    @NonNull
    private ArrayList<BarEntry> getData() {
        //获取数据
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        //第一条是Process表里面的当前背诵进度除以总词数得到背诵进度,type=0
        //第二条是Process表里面的当前测试进度除以总词数得到测试进度,type=1
        yVals1.add(new BarEntry(1,CaculateProcess("1001",0) ));
        yVals1.add(new BarEntry(2, CaculateProcess("1001",1)));

        yVals1.add(new BarEntry(4, CaculateProcess("1002",0)));
        yVals1.add(new BarEntry(5, CaculateProcess("1002",1)));

        yVals1.add(new BarEntry(7, CaculateProcess("1003",0)));
        yVals1.add(new BarEntry(8, CaculateProcess("1003",1)));

        yVals1.add(new BarEntry(10, CaculateProcess("1004",0)));
        yVals1.add(new BarEntry(11, CaculateProcess("1004",1)));

        yVals1.add(new BarEntry(13, CaculateProcess("1005",0)));
        yVals1.add(new BarEntry(14, CaculateProcess("1005",1)));

        yVals1.add(new BarEntry(16, CaculateProcess("1006",0)));
        yVals1.add(new BarEntry(17, CaculateProcess("1006",1)));

        yVals1.add(new BarEntry(19, CaculateProcess("1007",0)));
        yVals1.add(new BarEntry(20, CaculateProcess("1007",1)));

        yVals1.add(new BarEntry(22, CaculateProcess("1008",0)));
        yVals1.add(new BarEntry(23, CaculateProcess("1008",1)));
        return yVals1;
    }

    //设置数据
    private void setData(ArrayList yVals1) {
        float start = 1f;
        BarDataSet set1;
        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "绿色：背诵进度；黄色：测试进度");
            //设置有两种颜色
            int[] barChartColor = {rgb("#2ecc71"), rgb("#f1c40f")};
            set1.setColors(barChartColor);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(1f);
            //设置数据
            mBarChart.setData(data);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //显示进度值
            case R.id.studyHistory_btn_show_values:
                for (IDataSet set : mBarChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());
                mBarChart.invalidate();
                break;
            //xy轴动画
            case R.id.studyHistory_btn_anim_xy:
                setData(getData());
                mBarChart.animateXY(3000, 3000);
                break;
        }
    }
}

