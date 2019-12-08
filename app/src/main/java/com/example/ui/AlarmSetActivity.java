package com.example.ui;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adapter.ViewPagerAdapter;
import com.example.seeker.R;
import com.example.utils.TimeUtil;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmSetActivity extends AppCompatActivity {
    private static final String TAG = "AlarmSetActivity";
    private ViewPager viewPager;
    private ImageView topImg_left;
    private ImageView topImg_right;
    private ImageView buttomImg_left;
    private ImageView buttomImg_right;
    private List<View> views=null;
    private TextView time_show;
    private int m_year;
    private int m_month;
    private int m_day;
    private int m_hour;
    private int m_minute;
    private TextView showTime;
    private Button alarmSetOkBtn;
    private Button alarmSetCancelBtn;
    private CheckBox repeatChe;
    private static int ALARM_SET_DATA_RESULTCODE=2;
    private  int  isRepeat=0;//每天重复标志
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        init();
        PushAgent.getInstance(this).onAppStart();//友盟消息推送的
       // datePicker = (DatePicker) findViewById(R.id.alarmSetActivity_datePicker);
        //timePicker = (TimePicker) findViewById(R.id.alarmSetActivity_timePicker);
        isRepeat = getIntent().getIntExtra("isRepeat", 0);//上一个活动传过来的isRepeat,修改checkbox的状态
        if (isRepeat == 1 ? true : false) {
            repeatChe.setChecked(true);
        } else {
            repeatChe.setChecked(false);//默认每天不重复
        }
        initViewPager();

        alarmSetOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("year",m_year);
                bundle.putInt("month",m_month);
                bundle.putInt("night",m_day);
                bundle.putInt("hour",m_hour);
                bundle.putInt("minute",m_minute);
                bundle.putInt("isRepeat",isRepeat);
                intent.putExtras(bundle);
                if (TimeUtil.CountTime(m_year, m_month, m_day, m_hour, m_minute) < 0) {
                    Toast.makeText(AlarmSetActivity.this, "无效日期", Toast.LENGTH_SHORT).show();
                } else {
                    setResult(ALARM_SET_DATA_RESULTCODE,intent);
                    Toast.makeText(AlarmSetActivity.this,"已设立闹钟",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        alarmSetCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        repeatChe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isRepeat = 1;
                } else {
                    isRepeat=0;
                }
            }
        });
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.alarmSetActivity_viewpager);
        topImg_left = (ImageView) findViewById(R.id.alarmSetActivity_topImg_left);
        topImg_right = (ImageView) findViewById((R.id.alarmSetActivity_topImg_right));
        buttomImg_left = (ImageView) findViewById(R.id.alarmSetActivity_buttomImg_left);
        buttomImg_right = (ImageView) findViewById(R.id.alarmSetActivity_buttomImg_right);
        showTime = (TextView) findViewById(R.id.alarmSetActivity_time_show);
        alarmSetOkBtn = (Button) findViewById(R.id.alarmSet_Ok);
        alarmSetCancelBtn = (Button) findViewById(R.id.alarmSet_Cancel);
        repeatChe = (CheckBox) findViewById(R.id.alarmSet_repeat);
    }


    private void initViewPager() {
        views = new ArrayList<View>();
        //这里不可以TimePicker timePicker = (TimePicker) viewPager.findViewById(R.id.alarmSetActivity_timePicker);
        //要分别加载ViewPager的子布局
        // views.add(LayoutInflater.from(this).inflate(R.layout.alarm_set_date,null));
        //views.add(LayoutInflater.from(this).inflate(R.layout.alarm_set_time,null));
        View view_data = LayoutInflater.from(this).inflate(R.layout.alarm_set_date, null);
        DatePicker datePicker = (DatePicker) view_data.findViewById(R.id.alarmSetActivity_datePicker);
        View view_time=LayoutInflater.from(this).inflate(R.layout.alarm_set_time, null);
        TimePicker timePicker = (TimePicker) view_time.findViewById(R.id.alarmSetActivity_timePicker);
        timePicker.setIs24HourView(true);
        views.add(view_data);
        views.add(view_time);
        viewPager.setAdapter(new ViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        Calendar calendar = Calendar.getInstance();
        m_year = calendar.get(Calendar.YEAR);
        m_month = calendar.get(Calendar.MONTH );
        m_day = calendar.get(Calendar.DAY_OF_MONTH);
        m_hour = calendar.get(Calendar.HOUR_OF_DAY);
        m_minute = calendar.get(Calendar.MINUTE);
        showTime.setText(""+m_year+"年"+(m_month+1)+"月"+m_day+"日"+m_hour+"时"+m_minute+"分");
        datePicker.init(m_year, m_month, m_day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                m_year =year;
                m_month =monthOfYear;
                m_day =dayOfMonth;
                showTime.setText(""+m_year+"年"+(m_month+1)+"月"+m_day+"日"+m_hour+"时"+m_minute+"分");
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                m_hour=hourOfDay;
                m_minute=minute;
                showTime.setText(""+m_year+"年"+(m_month+1)+"月"+m_day+"日"+m_hour+"时"+m_minute+"分");
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i==0) {
                    topImg_left.setVisibility(View.VISIBLE);
                    topImg_right.setVisibility(View.INVISIBLE);
                    buttomImg_left.setVisibility(View.VISIBLE);
                    buttomImg_right.setVisibility(View.INVISIBLE);
                    Toast.makeText(AlarmSetActivity.this,"选择日期",Toast.LENGTH_SHORT).show();
                } else if (i==1) {
                    topImg_left.setVisibility(View.INVISIBLE);
                    topImg_right.setVisibility(View.VISIBLE);
                    buttomImg_left.setVisibility(View.INVISIBLE);
                    buttomImg_right.setVisibility(View.VISIBLE);
                    Toast.makeText(AlarmSetActivity.this,"选择时间",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

}
