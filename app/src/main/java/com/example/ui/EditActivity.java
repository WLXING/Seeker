package com.example.ui;


import com.example.service.AlarmService;
import com.example.bean.Note;

import android.Manifest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.seeker.R;
import com.example.utils.ContentToSpannableString;
import com.example.utils.DateUtil;
import com.example.utils.L;
import com.example.utils.PictureUtil;
import com.example.utils.TimeUtil;
import com.example.utils.UriToPathUtil;
import com.umeng.message.PushAgent;
import com.zhihu.matisse.Matisse;

import org.litepal.LitePal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.mthli.knife.KnifeText;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by ${WLX} on 2019.
 */

public class EditActivity extends AppCompatActivity {
    //启动EditActivity需要你传值data1,data2
//    public static void actionStart(Context context, Note data1, String data2, int CODE) {
//        Intent intent = new Intent(context, EditActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("note", data1);
//        bundle.putString("type", data2);//type=新建，还是编辑
//        intent.putExtras(bundle);
//        context.startActivityForResult(intent);
//    }
    private static final String TAG = "EditActivity";
    private Toolbar toolbar;
    private String content; //笔记内容
    private String title;   //笔记标题，用于在RecyclerView中展示
    private String previewContent;  //笔记预览显示的内容，也是用于Recyclerview
    private String createTime;  //笔记日期
    private Note note = null;
    private String type;//标志note是新建的还是编辑的
    private int oldNote_id;
    private static int REQUEST_CODE = 23;
    private static int COMPLETE = 1;
    private static final String GROUP_ALL = "全部笔记";//默认分组
    private static final String GROUP_WORK = "工   作";//
    private static final String GROUP_STUDY = "学   习";//这些和EditActivity中的一样，直接拷贝过来
    private static final String GROUP_LIFE = "生   活";//
    private static final String GROUP_NO = "未分组";//
    private static final String NO_DO = "待   办";
    private String currentGroupName = GROUP_NO;//当前所在分组，用于监听分组菜单的点击事件，点击则改变，默认是未分组"
    private static int ALARM_SET_DATA_QUESTCODE = 1;
    private static int ALARM_SET_DATA_RESULTCODE = 2;
    private int isAlarm = 0;//这个标志是有没有设立闹钟，注意和isCancelAlarm的区别
    private int m_year;
    private int m_month;
    private int m_day;
    private int m_hour;
    private int m_minute;
    private MenuItem item_alarmSet;//用于在onActivityResult()中,设置好闹钟后改变菜单上面的闹钟图标
    //这个是取消闹钟标志;没设立闹钟,或者设立了又取消了都为true,只有设立闹钟了，也就是isAlarm为1,它才为false,设立闹钟状态下点取消闹钟，它才变回true
    private int isRepeat = 0;//是否每天重复,1是，0否
    private String username;
    private String password;
    private boolean home_staue = true;//返回键的状态，刚进来显示是打钩，点一次保存，变成一个返回箭头，再按就返回了
    private ActionBar actionBar;
    private String city;
    private String weather;
    int start = 0;//edittext被选中的光标起始点
    int end = 0;//edittext被选中的光标结束点
    private RelativeLayout relativeLayout;
    private KnifeText knifeText;//这个类继承自EditText,就当做EditText用就行，它有个特点就是可以将文字以html的形式保存，但是那样的话就不能用spannablestring了，很

    //文字有特效就得舍弃插入图片，能插入图片文字就不能有特效，现在保存插入图片功能
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        PushAgent.getInstance(this).onAppStart(); //友盟消息推送的
        //Toast.makeText(EditActivity.this,"NOteid"+note.getId(),LENGTH_SHORT).show();//加这句崩溃，难道新note不允许这样操作？估计是没在数据库里
        Bundle bundle = getIntent().getExtras();
        note = (Note) bundle.getSerializable("note");
        type = bundle.getString("type");
        username = bundle.getString("username");
        password = bundle.getString("password");
        city = bundle.getString("city");
        weather = bundle.getString("weather");
        //oldNote_id = note.getId();不能放在这里，会报空指针异常
        if ("新建".equals(type)) {
            //  Toast.makeText(EditActivity.this,"新建"+note.getId()+note.getGroupName(),Toast.LENGTH_SHORT).show();
            // Toast.makeText(EditActivity.this, "新建"+isAlarm, LENGTH_SHORT).show();
        } else if ("编辑".equals(type)) {
            oldNote_id = note.getId();
            currentGroupName = note.getGroupName();
            m_year = note.getYear();
            m_month = note.getMonth();
            m_day = note.getDay();
            m_hour = note.getHour();
            m_minute = note.getMinute();
            isAlarm = note.getIsAlarm();//因为闹铃图标默认是未设置状态，先拿到一个日记的闹铃标志
            isRepeat = note.getIsRepeat();
            if (TimeUtil.CountTime(m_year, m_month, m_day, m_hour, m_minute) < 0 && !(isRepeat == 1 ? true : false)) {
                isAlarm = 0;
            }
            SpannableString spannableString = ContentToSpannableString.Content2SpanStr(EditActivity.this, note.getContent());
            knifeText.append(spannableString);
        }
        knifeText.setFocusable(true);//获取焦点
        knifeText.setFocusableInTouchMode(true);
        knifeText.requestFocus();
        //显示软键盘
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
        //InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInput(editText, 0);

        knifeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    L.i("editText", "已经获得焦点");
                } else {
                    L.i("editText", "未获得焦点");
                }
            }
        });
        knifeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                actionBar.setHomeAsUpIndicator(R.drawable.icon_done);
                home_staue = true;
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.editActivity_toolbar);
        knifeText = (KnifeText) findViewById(R.id.editActivity_knifeText);
        relativeLayout = (RelativeLayout) findViewById(R.id.editActivity_relativeLayout);
        knifeText.setSelection(knifeText.getEditableText().length());
        setSupportActionBar(toolbar);//如不需用到ActionBar的某些功能，则省略这句
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_done);
        }
    }

    /**
     * 可以在这里拿到菜单的Item,下次编辑一个note的时候可以根据是否设有闹钟，改变闹钟图片
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item_alarmSet = menu.findItem(R.id.editactivity_reminder).setChecked(true);
        if (isAlarm == 1) {
            item_alarmSet.setIcon(getResources().getDrawable(R.drawable.ic_new_task_reminder_time_setted));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editactivity_toolbar_menu, menu);
        return true;
    }

    /**
     * 标题栏各项监听
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //用户新建了但没编辑就不保存  这里如果用  !"".equals(editText.getText())
                //替代!TextUtils.isEmpty(editText.getText())的话不行
                // if (!TextUtils.isEmpty(editText.getText()) || type.equals("编辑")) {}
                if (home_staue == true) {
                    SaveNote();
                    if (type.equals("新建")) {
                        type = "编辑";//如果新建的保存了没退出去，那就变成编辑
                    }
                    actionBar.setHomeAsUpIndicator(R.drawable.ab_ink_undo);//变成返回箭头
                    home_staue = false;
                } else {
                    finish();//要给edittext设立一个监听器，内容一变化就改变home_statue
                }
                break;
            case R.id.editactivity_reminder:
                if (isAlarm == 1 ? true : false) {
                    item.setIcon(R.drawable.ic_new_task_reminder_time_unset);//取消闹钟显示这个图标
                    isAlarm = 0;
                    isRepeat = 0;//取消了闹钟则每天重复也取消了
                    Toast.makeText(EditActivity.this, " 闹钟已取消", LENGTH_SHORT).show();
                } else {
                    //闹钟设置在onActivityResult（）中进行
                    Intent intent = new Intent(EditActivity.this, AlarmSetActivity.class);//跳转到闹钟配置页面
                    intent.putExtra("isRepeat", isRepeat);
                    startActivityForResult(intent, ALARM_SET_DATA_QUESTCODE);//怎么知道有没有设闹钟呢，通过下面的onActivityResult判断
                }
                break;
            case R.id.editactivity_pictures:
                PictureUtil.SelectPictures(EditActivity.this, REQUEST_CODE);
                break;
            case R.id.editactivity_group_all:
                currentGroupName = GROUP_ALL;
                break;
            case R.id.editactivity_group_study:
                currentGroupName = GROUP_STUDY;
                break;
            case R.id.editactivity_group_work:
                currentGroupName = GROUP_WORK;
                break;
            case R.id.editactivity_group_life:
                currentGroupName = GROUP_LIFE;
                break;
            case R.id.editactivity_group_no:
                currentGroupName = GROUP_NO;
                break;
            case R.id.editactivity_fontStrikethrough:
                knifeText.strikethrough(!knifeText.contains(KnifeText.FORMAT_STRIKETHROUGH));
                break;
            case R.id.editactivity_fontUnderline:
                knifeText.underline(!knifeText.contains(KnifeText.FORMAT_UNDERLINED));
                break;
            case R.id.editactivity_fontBold:
                knifeText.bold(!knifeText.contains(KnifeText.FORMAT_BOLD));
                break;
            case R.id.editactivity_fontItalic:
                knifeText.italic(!knifeText.contains(KnifeText.FORMAT_ITALIC));
                break;
        }
        return true;
    }

    /**
     * 打开闹钟
     */
    private void StartAlarm(int noteId) {
        //用服务来做
        long triggerTime = SystemClock.elapsedRealtime() + TimeUtil.CountTime(m_year, m_month, m_day, m_hour, m_minute);//System.currentTimeMillis()对应AlarmManager.RTC_WAKEUP
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", noteId);//这样子写系统会崩溃,估计是note还没存进数据库，所以只好把StartAlarm()放在onSave()里面来做
        bundle.putString("title", title);
        bundle.putString("previewContent", previewContent);
        bundle.putString("username", username);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getService(this, noteId, intent, 0);//requestcode设立为ID用于区分不同信息
        if (isRepeat == 1 ? true : false) {
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent);//重复隔天执行
        } else {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);//只执行一次
        }
        //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,2*1000,pendingIntent);//间隔重复执行

        //用广播来做，不开启服务，如果程序被清后台了，则不会执行闹钟
//        IntentFilter intentFilter = new IntentFilter();//动态注册广播
//        intentFilter.addAction("alarm");
//        AlarmBroadcastReceiver alarmBroadcastReceiver = new AlarmBroadcastReceiver();
//        registerReceiver(alarmBroadcastReceiver, intentFilter);
//        Intent intent = new Intent();
//        intent.setAction("alarm");
//
//        Calendar currentCalendar=Calendar.getInstance();
//        long nowTime=currentCalendar.getTimeInMillis();//当前时间
//        Calendar myCalendar=Calendar.getInstance();
//        myCalendar.set(m_year,m_month,m_day,m_hour,m_minute);
//        long alarmTime=myCalendar.getTimeInMillis();
//        long triggerTime= SystemClock.elapsedRealtime()+alarmTime-nowTime;//System.currentTimeMillis()对应AlarmManager.RTC_WAKEUP
//        Toast.makeText(EditActivity.this,triggerTime+"",Toast.LENGTH_SHORT).show();
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        PendingIntent  pendingIntent = PendingIntent.getBroadcast(this, 0, intent,0);
//        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent);//只执行一次
        //manager.setRepeating(AlarmManager.RTC_WAKEUP,triggerTime,2*1000,pendingIntent);//间隔重复执行
    }

    //    private void SelectPictures() {
//        Matisse
//                .from(EditActivity.this)
//                .choose(MimeType.ofAll())//照片视频全部显示
//                .countable(true)//有序选择图片
//                .maxSelectable(9)//最大选择数量为9
//                .gridExpectedSize(120)//图片显示表格的大小getResources()
//                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向。
//                .thumbnailScale(0.85f)//缩放比例
//                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
//                .imageEngine(new MyGlideEngine())//加载方式
//                .forResult(REQUEST_CODE);//请求码
//
//    }

    List<Uri> mSelected;//用于放置所选图片的Uri

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            L.d("Matisse--------->", "mSelected: " + mSelected);
            Uri pictureUri = mSelected.get(0);//获得图片uri
            SpannableString spanStr = new SpannableString("<pic uri='" + pictureUri.toString() + "'>");
            L.d("spanStr-------->", spanStr.toString());
            String path = UriToPathUtil.getRealFilePath(this, pictureUri);
            L.d("picture's path---------->", path);
            //根据Uri 获得 drawable资源
            try {
//                String s=CopyFileToNewPath.copyFileToNewPathAfterAndroidN(EditActivity.this,pictureUri);
//                pictureUri=Uri.parse(s);
                //  L.d("CopyFileToNewPath---------->", s);

                Drawable drawable = Drawable.createFromStream(this.getContentResolver().openInputStream(pictureUri), null);
                drawable.setBounds(0, 0, 2 * drawable.getIntrinsicWidth(), 2 * drawable.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                spanStr.setSpan(span, 0, spanStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                int cursor = knifeText.getSelectionStart();
                knifeText.getText().insert(cursor, spanStr);
            } catch (FileNotFoundException e) {
                L.d("spannableString", "根据Uri找不到图片");
                e.printStackTrace();
            }
        } else if (requestCode == ALARM_SET_DATA_QUESTCODE && resultCode == ALARM_SET_DATA_RESULTCODE) {
            isAlarm = 1;
            item_alarmSet.setIcon(R.drawable.ic_new_task_reminder_time_setted);//设置闹钟后显示这个图标
            Bundle bundle = data.getExtras();
            m_year = bundle.getInt("year");
            m_month = bundle.getInt("month");
            m_day = bundle.getInt("night");
            m_hour = bundle.getInt("hour");
            m_minute = bundle.getInt("minute");
            isRepeat = bundle.getInt("isRepeat");
        }
    }

    /**
     * 保存笔记
     */
    private void SaveNote() {
        getTitConPre();
        if (!TextUtils.isEmpty(knifeText.getText()) && "新建".equals(type)) {
            Note note = new Note();
            note.setUsername(username);//新建的话为每个note加上username,password,编辑因为没有修改这两个参数所以不用重新保存
            note.setPassword(password);
            note.setGroupName(currentGroupName);
            note.setTitle(title);
            note.setContent(content);
            note.setPreviewContent(previewContent);
            if (!"城市".equals(city) && !"天气".equals(weather)) {//用户更改了城市、天气
                note.setCreateTime(DateUtil.Date2String(new Date()) + " 于" + city + "," + weather);
            } else {
                note.setCreateTime(DateUtil.Date2String(new Date()));
            }
            if (isAlarm == 1 ? true : false) {//新建的日记，如果设立了闹钟就保存，否则保持默认状态
                note.setYear(m_year);
                note.setMonth(m_month);
                note.setDay(m_day);
                note.setHour(m_hour);
                note.setMinute(m_minute);
                note.setIsAlarm(isAlarm);
                note.setIsRepeat(isRepeat);
            }
            note.save();//数据库增加note
            //设立闹钟和取消闹钟只有保存了才会生效
            if (isAlarm == 1 ? true : false) {
                StartAlarm(note.getId());
            }
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("last", note);
            intent.putExtras(bundle);
            setResult(COMPLETE, intent);
            //finish();
        } else if ("编辑".equals(type)) {
            ContentValues values = new ContentValues();
            values.put("groupName", currentGroupName);
            values.put("title", title);
            values.put("content", content);
            values.put("previewContent", previewContent);
            if (!"城市".equals(city) && !"天气".equals(weather)) {
                createTime = DateUtil.Date2String(new Date()) + " 于" + city + "," + weather;
            } else {
                createTime = DateUtil.Date2String(new Date());
            }
            values.put("createTime", createTime);
            values.put("year", m_year);
            values.put("month", m_month);
            values.put("day", m_day);
            values.put("hour", m_hour);
            values.put("minute", m_minute);
            values.put("isAlarm", isAlarm);
            values.put("isRepeat", isRepeat);
            if (isAlarm == 1) {//说明新设立了闹钟
                StartAlarm(note.getId());
            } else if (isAlarm == 0 && note.getIsAlarm() == 1) {//说明要取消闹钟
                CancelAlarm(note.getId());
            }
            LitePal.update(Note.class, values, oldNote_id);//在原来的基础上更新数据
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("last", LitePal.find(Note.class, oldNote_id));
            intent.putExtras(bundle);
            setResult(COMPLETE, intent);
            //finish();
        } else {
            //finish();
        }
    }

    private void CancelAlarm(int noteId) {
        Intent intent = new Intent(this, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, noteId, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 获取笔记的主标题，子标题，图片用于MainActivity的显示
     */
    private void getTitConPre() {
        content = knifeText.getText().toString();
        L.d("用户输入的内容是", content);
        int i;
        for (i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n' || content.charAt(i) == '<')//如果内容只有一行，那么肯会取到路径，显示就不好看了
                break;
        }
        title = content.substring(0, i);
        if (i < content.length()) {
            int j = 0;
            for (j = i + 1; j < content.length(); j++) {
                if (content.charAt(i) == '\n' || content.charAt(i) == '<') {
                    break;
                }
            }
            previewContent = content.substring(i + 1, j);
        } else {
            previewContent = "";
        }
    }


    //关闭软键盘
    private void closeSoftKeyInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //弹出一个dialog，用用户选择是否保存修改，作用和按后退图标一样，只不过这里弹出对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        AlertDialog alertDialog = builder.setTitle("系统提示：")
                .setMessage("保存刚才的修改吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveNote();
                        finish();
                    }
                }).create();
        alertDialog.show();
    }
}