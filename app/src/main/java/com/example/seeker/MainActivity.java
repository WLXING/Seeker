package com.example.seeker;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.adapter.NoteAdapter;
import com.example.bean.Note;
import com.example.bean.User;
import com.example.service.Day_Night_Service;
import com.example.ui.EditActivity;
import com.example.ui.NewsActivity;
import com.example.ui.ModifyActivity;
import com.example.ui.WordActivity;
import com.example.utils.L;
import com.example.utils.PictureUtil;
import com.example.utils.TimeUtil;
import com.google.gson.Gson;
import com.simple.spiderman.CrashModel;
import com.simple.spiderman.SpiderMan;
import com.umeng.message.PushAgent;
import com.zhihu.matisse.Matisse;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements RecycylerViewListener, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationLeft;
    private NavigationView navigationRight;
    private RecyclerView recyclerView;
    private FloatingActionButton floating_noteBtn;
    private FloatingActionButton floating_topBtn;
    private FloatingActionButton floating_wordBtn;
    private FloatingActionButton floating_jokeBtn;
    private List<Note> notes = null;
    private NoteAdapter noteAdapter;
    private TextView toolbarText;
    private static int COMPLETE = 1;
    private static final String GROUP_ALL = "全部笔记";//默认分组
    private static final String GROUP_WORK = "工   作";//
    private static final String GROUP_STUDY = "学   习";//这些和EditActivity中的一样，直接拷贝过来
    private static final String GROUP_LIFE = "生   活";//
    private static final String GROUP_NO = "未分组";//
    private static final String NO_DO = "待   办";
    private String currentGroupName = GROUP_ALL;//当前所在分组，用于监听分组菜单的点击事件，点击则改变，默认是"都在这了"
    private static final String TIME_ASC = "createTime asc";//按时间新->旧
    private static final String TIME_DESC = "createTime desc";//按时间旧->新-----------按时间排序，你重新编辑日记则日记时间会改变，排序也会发生变化；
    private static final String ID_ASC = "id asc";//按建立的顺序，最近->以前
    private static final String ID_DESC = "id desc";//按建立的顺序，以前->最近--------------如果按ID排序，你重新编辑日记，ID不会发生改变，排序还是按照之前的
    private static final int LIST_MODE = 4;//单列模式
    private static final int STAGGER_MODE = 5;//瀑布流模式
    private String currentSort = TIME_DESC;//当前排序方式，默认是按时间降序，用户编辑完后来刷新页面时还是按照之前的排序方式展示
    private int currentDisplayMode = STAGGER_MODE;//当前预览模式，默认瀑布流模式，用户可以根据爱好进行改变
    private boolean isSearching = false;//是否正在搜索，因为搜索出某个笔记，点进去修改后回来发现多了几个其它笔记，用这个标志通知mainactivity要不要刷新页面
    private static int IS_ALARM = 1;//表明设置了闹钟
    private String username;//用于数据存储
    private String password;//这里可以修改密码
    private static int PETNAME_REQUEST = 101;
    private static int PERSONALITY_REQUEST = 102;
    private static int PASSWORD_REQUEST = 103;
    private static int WEATHER_REQUEST = 104;
    private static int MODIFY_OK = 100;//修改完毕
    private static final String MODIFY_TYPE_PUBLIC = "noSecret";//修改的内容不是密码
    private static final String MODIFY_TYPE_SECRET = "Secret";//修改密码
    private View nav_left_head;
    private View nav_left_head_img;
    private TextView nav_left_head_petName;
    private TextView nav_left_head_personality;
    private int login_isCheck = 0;//登录如果记住密码，需要在这里修改sharepreference存储的数据，不然用户修改签名或者密码后退出去还是原来的账号密码
    private static int REFRESH_LOGIN = 12;
    private String personality;
    private static int REQUEST_HEADIMG = 150;
    private int currentUserID;
    private TextView nav_left_head_city;
    private TextView nav_left_head_temperature;
    private TextView nav_left_head_weather;
    private int displayWeather = 0;//是否显示天气，为0不显示
    private int changeNightMode = 0;//切换夜间模式，0就切到日间，1就切到夜间
    private String currentCity = "城市";
    private String currentWeather = "天气";
    private String currentTemperature = "温度";
    private String searchContent = null;//搜索内容，当用户搜索某个东西，点进去编辑返回后按搜索内容刷新页面
    private int[] floatingActionButtonRes = {R.id.mainActivity_floatingActionTop, R.id.mainActivity_floatingActionNote,
            R.id.mainActivity_floatingActionWord, R.id.mainActivity_floatingActionJoke};
    private List<FloatingActionButton> floatingActionButtonList = new ArrayList<FloatingActionButton>();//放置FloatingActionButton，用于动画
    private boolean DisplayMenu = true;//点击FloatingActionButton展开菜单
    public static int currentNightMode = 0;//当前默认为日间模式
    private SharedPreferences sharedPreferences;//用于保存日间模式还是夜间模式
    private SharedPreferences.Editor sharepre_editor;
    private String petName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.mainActivity_toolbar); //将toolbar设为标题，记得将application的label去掉，并在activity里设置label=""
        init();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharepre_editor = sharedPreferences.edit();
        setSupportActionBar(toolbar);//如不需用到ActionBar的某些功能，则省略这句
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.seeker_icon_main_navigation);
        }
        for (int i = 0; i < floatingActionButtonRes.length; i++) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(floatingActionButtonRes[i]);
            floatingActionButton.setOnClickListener(this);
            floatingActionButtonList.add(floatingActionButton);
        }
        applyThePermission();//申请权限
        PushAgent.getInstance(this).onAppStart(); //友盟消息推送的
        HeConfig.init("这个自己去查看和风SDK说明", "这个自己去查看和风SDK说明");//和风天气账户初始化
        HeConfig.switchToFreeServerNode();//切换到免费服务域名
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;//当前是日间还是夜间模式
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        login_isCheck = getIntent().getIntExtra("loginCheckFlag", 0);
        // L.e("单词", "----------->" + LitePal.find(WordList.class,1).getQuickdefinition());

        //每次登录进来都要根据用户名刷新昵称和个性签名还有用户头像
        List<User> users = null;
        users = LitePal.where("username=? and password=?", username, password).find(User.class);
        User user = users.get(0);
        currentUserID = user.getId();
        petName = user.getPetname();
        L.e(TAG, "------------->用户名是 " + username);
        if (!TextUtils.isEmpty(petName)) {//不要用！"".isEmpty（petName）
            nav_left_head_petName.setText("      " + petName);
        } else {
            nav_left_head_petName.setText("  您好，  " + username);
        }
        if (!TextUtils.isEmpty(user.getPersonality())) {
            nav_left_head_personality.setText(user.getPersonality());//加载签名
        } else {
            nav_left_head_personality.setText("您当前还没有个性签名哦~~");
        }
        if (!TextUtils.isEmpty(user.getHeadimguri())) {
            Uri uri = Uri.parse(user.getHeadimguri());//String string=uri.toString();Uri uri=Uri.parse(string)
            Glide.with(this).load(uri).into((ImageView) nav_left_head_img);//加载头像,
        } else {
        }
        Connector.getDatabase();
        setDisplayMode();
        notes = Sort(currentGroupName, currentSort);
        noteAdapter = new NoteAdapter(MainActivity.this, notes);//实现了接口则传上下文过去，也可以采用匿名内部类来做
        recyclerView.setAdapter(noteAdapter);

        /**
         *  左边navigation点击事件
         */
        navigationLeft.setCheckedItem(R.id.nav_left_headImg);
        navigationLeft.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_left_headImg://结果在onActivityResult()中
                        PictureUtil.SelectPictures(MainActivity.this, REQUEST_HEADIMG);
                        break;
                    case R.id.nav_left_petname:
                        Intent petNameIntent = new Intent(MainActivity.this, ModifyActivity.class);
                        petNameIntent.putExtra("itemTitle", (String) item.getTitle());
                        petNameIntent.putExtra("content", petName);
                        petNameIntent.putExtra("modifyType", MODIFY_TYPE_PUBLIC);
                        startActivityForResult(petNameIntent, PETNAME_REQUEST);
                        break;
                    case R.id.nav_left_personality:
                        String personality = nav_left_head_personality.getText().toString();
                        Intent personalityIntent = new Intent(MainActivity.this, ModifyActivity.class);
                        personalityIntent.putExtra("itemTitle", item.getTitle());
                        personalityIntent.putExtra("content", personality);
                        personalityIntent.putExtra("modifyType", MODIFY_TYPE_PUBLIC);
                        startActivityForResult(personalityIntent, PERSONALITY_REQUEST);
                        break;
                    case R.id.nav_left_password:
                        Intent passwordIntent = new Intent(MainActivity.this, ModifyActivity.class);
                        passwordIntent.putExtra("itemTitle", (String) item.getTitle());
                        passwordIntent.putExtra("content", password);
                        passwordIntent.putExtra("modifyType", MODIFY_TYPE_SECRET);
                        startActivityForResult(passwordIntent, PASSWORD_REQUEST);
                        break;
                    case R.id.nav_left_dayMode:
                        if (displayWeather % 2 == 0) {//显示天气
                            Intent whetherdIntent = new Intent(MainActivity.this, ModifyActivity.class);
                            whetherdIntent.putExtra("itemTitle", "请输入当前城市以获取天气");
                            whetherdIntent.putExtra("content", nav_left_head_city.getText());
                            whetherdIntent.putExtra("modifyType", MODIFY_TYPE_PUBLIC);
                            startActivityForResult(whetherdIntent, WEATHER_REQUEST);
                            item.setTitle("关闭天气");
                            displayWeather++;
                        } else {//关闭天气
                            currentCity = "城市";
                            currentWeather = "天气";
                            currentTemperature = "温度";
                            nav_left_head_city.setText(currentCity);
                            nav_left_head_weather.setText(currentWeather);
                            nav_left_head_temperature.setText(currentTemperature);
                            item.setTitle("打开天气");
                            displayWeather++;
                        }
                        break;
                    case R.id.nav_left_nightMode:
                        if (changeNightMode % 2 == 1) {
                            setNightMode();
                            item.setTitle("日间模式");
                            changeNightMode++;
                        } else {
                            setNightMode();
                            item.setTitle("夜间模式");
                            changeNightMode++;
                        }
                        break;
                    case R.id.nav_left_exit:
                        Intent intent = new Intent();
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        setResult(REFRESH_LOGIN, intent);
                        finish();
                        break;
                }
                return true;
            }
        });

        /**
         * 右边navigation点击事件
         */
        navigationRight.setCheckedItem(R.id.nav_right_all);
        navigationRight.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                //这部分和EditActivity是一样的，只不过把EditActivity的菜单itemId换成了navId
                switch (menuItem.getItemId()) {
                    case R.id.nav_right_all:
                        noteAdapter.refreshShow(Sort(GROUP_ALL, currentSort));
                        currentGroupName = GROUP_ALL;//切换到一个分组，标题就显示分组名
                        toolbarText.setText(GROUP_ALL);

                        break;
                    case R.id.nav_right_study:
                        noteAdapter.refreshShow(Sort(GROUP_STUDY, currentSort));
                        currentGroupName = GROUP_STUDY;
                        toolbarText.setText(GROUP_STUDY);

                        break;
                    case R.id.nav_right_work:
                        noteAdapter.refreshShow(Sort(GROUP_WORK, currentSort));
                        currentGroupName = GROUP_WORK;
                        toolbarText.setText(GROUP_WORK);

                        break;
                    case R.id.nav_right_life:
                        noteAdapter.refreshShow(Sort(GROUP_LIFE, currentSort));
                        currentGroupName = GROUP_LIFE;
                        toolbarText.setText(GROUP_LIFE);

                        break;
                    case R.id.nav_right_nogroup:
                        noteAdapter.refreshShow(Sort(GROUP_NO, currentSort));
                        currentGroupName = GROUP_NO;
                        toolbarText.setText(GROUP_NO);

                        break;
                    case R.id.nav_right_nodo:
                        noteAdapter.refreshShow(overdueCheck(SortByAlarm()));
                        currentGroupName = NO_DO;
                        toolbarText.setText(NO_DO);

                }
                return true;
            }
        });

        /**
         * 弹出崩溃信息展示界面
         */
        SpiderMan.getInstance()
                .init(this)
                //设置是否捕获异常，不弹出崩溃框
                .setEnable(true)
                //设置是否显示崩溃信息展示页面
                .showCrashMessage(true)
                //是否回调异常信息，友盟等第三方崩溃信息收集平台会用到,
                .setOnCrashListener(new SpiderMan.OnCrashListener() {
                    public void onCrash(Thread t, Throwable ex, CrashModel model) {
                        //CrashModel 崩溃信息记录，包含设备信息
                    }
                });
        // L.e(TAG, "---------------->" + LitePal.findLast(Notebook.class).getDisplayname());

        //动态注册广播，用于更换日夜模式
        IntentFilter intentFilter = new IntentFilter("com.example.seeker.DAY_NIGHT_CHANGE");
        intentFilter.addCategory("com.example.seeker.DAY_NIGHT_CHANGE");
        Day_Night_Receiver broadReceiver = new Day_Night_Receiver();
        registerReceiver(broadReceiver, intentFilter);
        //启动服务
        L.e(TAG, "启动服务前" + currentNightMode);
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Day_Night_Service.class);
        startService(intent);
    }

    private void init() {
        toolbarText = (TextView) findViewById(R.id.mainActivity_toolbar_text);
        toolbarText.setTextColor(Color.BLACK);
        drawerLayout = (DrawerLayout) findViewById(R.id.mainActivity_drawerlayout);
        navigationLeft = (NavigationView) findViewById(R.id.mainActivity_navigation_left);
        nav_left_head = navigationLeft.getHeaderView(0);
        nav_left_head_img = (CircleImageView) nav_left_head.findViewById(R.id.nav_left_head_image);
        nav_left_head_petName = (TextView) nav_left_head.findViewById(R.id.nav_left_head_petname);
        nav_left_head_personality = (TextView) nav_left_head.findViewById(R.id.nav_left_head_personality);//这里要转成Textview，不然用不了
        nav_left_head_city = (TextView) nav_left_head.findViewById(R.id.nav_left_head_city);
        nav_left_head_temperature = (TextView) nav_left_head.findViewById(R.id.nav_left_head_temperature);
        nav_left_head_weather = (TextView) nav_left_head.findViewById(R.id.nav_left_head_weather);
        navigationRight = (NavigationView) findViewById(R.id.mainActivity_navigation_right);
        recyclerView = (RecyclerView) findViewById(R.id.mainActivity_receclerView);
    }

    private void WhetherHandle(final String city) {
        /**
         * 实况天气
         * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
         * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
         *
         * @param context  上下文
         * @param location 地址详解
         * @param lang     多语言，默认为简体中文，海外城市默认为英文
         * @param unit     单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问回调接口
         */
        HeWeather.getWeatherNow(MainActivity.this, city, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Weather Now onError: ", e);
            }

            @Override
            public void onSuccess(Now dataObject) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(dataObject));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(dataObject.getStatus())) {
                    //此时返回数据
                    NowBase now = dataObject.getNow();
                    currentTemperature = now.getTmp();
                    nav_left_head_temperature.setText(currentTemperature + "℃");
                    String wind_dir = now.getWind_dir();
                    String cond_txt = now.getCond_txt();
                    String hum = now.getHum();
                    currentCity = city;
                    currentWeather = cond_txt + "," + wind_dir + "," + "H:" + hum;
                    nav_left_head_city.setText(currentCity);
                    nav_left_head_weather.setText(currentWeather);
                } else {
                    //在此查看返回数据失败的原因
                    String status = dataObject.getStatus();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                    Toast.makeText(MainActivity.this, "未知错误，获取天气失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 检查笔记的闹钟是否已经过期
     */
    private List<Note> overdueCheck(List<Note> notes) {
        for (Note note : notes) {
            Calendar calendar = Calendar.getInstance();
            int m_year = note.getYear();
            int m_month = note.getMonth();
            int m_day = note.getDay();
            int m_hour = note.getHour();
            int m_minute = note.getMinute();
            int isAlarm = note.getIsAlarm();
            int isRepeat = note.getIsRepeat();
            //获得每个笔记的闹钟时间，如果过期的就将其移除，并且不是每天重复的话就将其移除
            if (TimeUtil.CountTime(m_year, m_month, m_day, m_hour, m_minute) < 0 && !(isRepeat == 1 ? true : false)) {
                notes.remove(note);
            }
        }
        return notes;
    }

    /**
     * 根据当前的排序规则对待办事件进行升序或者降序排序
     */
    private List<Note> SortByAlarm() {
        List<Note> notes = null;
        if (ID_ASC.equals(currentSort)) {
            notes = LitePal.where("username=? and isAlarm=? ", username, "" + IS_ALARM).order(ID_ASC).find(Note.class);
        } else if (ID_DESC.equals(currentSort)) {
            notes = LitePal.where("username=? and isAlarm=?", username, "" + IS_ALARM).order(ID_DESC).find(Note.class);
        } else if (TIME_ASC.equals(currentSort)) {
            notes = LitePal.where("username=? and isAlarm=?", username, "" + IS_ALARM).order(TIME_ASC).find(Note.class);
        } else if (TIME_DESC.equals(currentSort)) {
            notes = LitePal.where("username=? and isAlarm=?", username, "" + IS_ALARM).order(TIME_DESC).find(Note.class);
        }
        for (Note note : notes
                ) {
            if (!((note.getPassword()).equals(password))) {
                notes.remove(note);
            }
        }

        return notes;
    }

    /**
     * 设置展示模式，单列模式还是瀑布流模式
     */
    private void setDisplayMode() {
        if (currentDisplayMode == LIST_MODE) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(layoutManager);
        } else if (currentDisplayMode == STAGGER_MODE) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    /**
     * 搜索显示有两个条件，根据当前分组名和排序规则进行排序
     * 能改变分组名的地方在NavigationRight的navigationRight.setNavigationItemSelectedListener中
     * 能改变排序方式的在菜单的监听事件中onOptionsItemSelected(MenuItem item)
     * 我们就拿这两个条件进行搜索
     * <p>
     * if (groupName == GROUP_ALL && sortMode == ID_ASC) {
     * notes = LitePal.where("groupName=?","都在这了").order("id asc").find(Note.class);
     * 注意： 上面这句，其实"都在这了"可以用GROUP_ALL代替，下次我们改成其它组名，直接在前面修改 GROUP_ALL就行了。ID_ASC类似
     * 我之前在上面定义ID_ASC为private static final int ID_ASC = 2;，定义成int类型，那么下次我想按其他字段（比如分数，姓氏首字母）
     * 升序查询的时候，需要改很多处 "id asc"改成什么什么"我想查的字段 asc" ,很麻烦，所以private static final String ID_ASC = "id asc"
     * 以后只需要修改开头的"id asc"就行了，，切记！！！！！！！！
     *
     * @param groupName 当前分组名
     * @param sortMode  排序方式
     * @return
     */
    private List<Note> Sort(String groupName, String sortMode) {
        List<Note> notes = null;
        //本来想把待办作为一组，但是，全部，工作，生活，学习，未分组都有可能是待办，这样不可能有两个组名，所以把待办另外查询
        if ((GROUP_ALL.equals(groupName) && ID_ASC.equals(sortMode))) {
            //这里不能用select,select查询某些列的数据，where查符合条件的
            //一定不要 if (groupName == GROUP_ALL && sortMode == TIME_ASC)
            //字符串用equal啊啊啊啊啊啊啊啊 啊啊啊啊啊 啊
            //不能groupName.equals(GROUP_ALL)
            //要GROUP_ALL.equals(groupName) 切记切记切记切记切记vv
            notes = LitePal.where("username=? ", username).order(ID_ASC).find(Note.class);
        } else if (GROUP_ALL.equals(groupName) && ID_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? ", username).order(ID_DESC).find(Note.class);
        } else if (GROUP_ALL.equals(groupName) && TIME_ASC.equals(sortMode)) {
            notes = LitePal.where("username=?", username).order(TIME_ASC).find(Note.class);
        } else if (GROUP_ALL.equals(groupName) && TIME_DESC.equals(sortMode)) {
            notes = LitePal.where("username=?", username).order(TIME_DESC).find(Note.class);

        } else if (GROUP_WORK.equals(groupName) && ID_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_WORK).order(ID_ASC).find(Note.class);
        } else if (GROUP_WORK.equals(groupName) && ID_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_WORK).order(ID_DESC).find(Note.class);
        } else if (GROUP_WORK.equals(groupName) && TIME_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_WORK).order(TIME_ASC).find(Note.class);
        } else if (GROUP_WORK.equals(groupName) && TIME_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=?", username, GROUP_WORK).order(TIME_DESC).find(Note.class);

        } else if (GROUP_STUDY.equals(groupName) && ID_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_STUDY).order(ID_ASC).find(Note.class);
        } else if (GROUP_STUDY.equals(groupName) && ID_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_STUDY).order(ID_DESC).find(Note.class);
        } else if (GROUP_STUDY.equals(groupName) && TIME_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_STUDY).order(TIME_ASC).find(Note.class);
        } else if (GROUP_STUDY.equals(groupName) && TIME_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_STUDY).order(TIME_DESC).find(Note.class);

        } else if (GROUP_LIFE.equals(groupName) && ID_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_LIFE).order(ID_ASC).find(Note.class);
        } else if (GROUP_LIFE.equals(groupName) && ID_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_LIFE).order(ID_DESC).find(Note.class);
        } else if (GROUP_LIFE.equals(groupName) && TIME_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_LIFE).order(TIME_ASC).find(Note.class);
        } else if (GROUP_LIFE.equals(groupName) && TIME_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_LIFE).order(TIME_DESC).find(Note.class);
        } else if (GROUP_NO.equals(groupName) && ID_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_NO).order(ID_ASC).find(Note.class);
        } else if (GROUP_NO.equals(groupName) && ID_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_NO).order(ID_DESC).find(Note.class);
        } else if (GROUP_NO.equals(groupName) && TIME_ASC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_NO).order(TIME_ASC).find(Note.class);
        } else if (GROUP_NO.equals(groupName) && TIME_DESC.equals(sortMode)) {
            notes = LitePal.where("username=? and groupName=? ", username, GROUP_NO).order(TIME_DESC).find(Note.class);
        }
        for (Note note : notes
                ) {
            if (!((note.getPassword()).equals(password))) {
                notes.remove(note);
            }
        }
        return notes;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == COMPLETE) {
            //如果结果ok，通知MainActivity刷新页面
            Bundle bundle = data.getExtras();
            Note note = new Note();
            note = (Note) bundle.getSerializable("last");
            Log.i("mainactivity-----------", "" + note.getId() + "  " + note.getContent() + note.getYear() + "年" + note.getMonth() + "月"
                    + note.getDay() + note.getHour() + note.getMinute());
            if (isSearching) {
                //如果正在搜索则返回后不用刷新全部页面，因为用户搜到后就直接点进去看了，如果你返回来后刷新页面则会显示一些不在搜索范围内的日记
                List<Note> notes = LitePal.where("content like ? and username=?", "%" + searchContent + "%", username).find(Note.class);
                for (Note searchNote : notes
                        ) {
                    if (!((searchNote.getPassword()).equals(password))) {
                        notes.remove(note);
                    }
                }
                noteAdapter.refreshShow(notes);
            } else if (currentGroupName.equals(NO_DO)) {//正在查看待办的，就刷新待办的界面，否则就按组来刷新
                noteAdapter.refreshShow(SortByAlarm());
            } else {
                noteAdapter.refreshShow(Sort(currentGroupName, currentSort));//只要返回主界面则要把数据库里的笔记取出传递给Adapter刷新界面重新刷新界面
            }
        } else if (requestCode == PETNAME_REQUEST && resultCode == MODIFY_OK) {
            petName = data.getStringExtra("content");
            ContentValues values = new ContentValues();
            values.put("petname", petName);
            LitePal.updateAll(User.class, values, "username=?", username);//改用户表
            nav_left_head_petName.setText("     " + petName);
        } else if (requestCode == PERSONALITY_REQUEST && resultCode == MODIFY_OK) {
            personality = data.getStringExtra("content");
            nav_left_head_personality.setText(personality);
            User user = new User();
            user.setPersonality(personality);
            user.update(currentUserID);
        } else if (requestCode == PASSWORD_REQUEST && resultCode == MODIFY_OK) {
            String old_password = password;
            password = data.getStringExtra("content");
            ContentValues values = new ContentValues();
            values.put("password", password);
            LitePal.updateAll(User.class, values, "username=? and password=?", username, old_password);//因为笔记也会根据密码来进行查询，所以也要更新笔记的密码
            LitePal.updateAll(Note.class, values, "username=? and password=?", username, old_password);
        } else if (requestCode == REQUEST_HEADIMG && resultCode == RESULT_OK) {
            List<Uri> mSelected = null;//接收返回的地址
            mSelected = Matisse.obtainResult(data);
            Uri pictureUri = mSelected.get(0);//获得图片uri
            Glide.with(this).load(pictureUri).into((ImageView) nav_left_head_img);
            User user = new User();
            user.setHeadimguri(pictureUri.toString());
            user.update(currentUserID);
        } else if (requestCode == WEATHER_REQUEST && resultCode == MODIFY_OK) {
            String city = data.getStringExtra("content");
            WhetherHandle(city);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_toobal_memu, menu);
        //找到SearchView并配置相关参数
        MenuItem searchItem = menu.findItem(R.id.mainactivity_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint("搜索本地日记");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                isSearching = true;
                searchContent = s;
                //这里要加上username的限制条件，不然一搜索就把其他用户的搜出来了，用litepal的模糊搜索，like+%
                List<Note> notes = LitePal.where("content like ? and username=?", "%" + s + "%", username).find(Note.class);
                for (Note note : notes) {
                    if (!((note.getPassword()).equals(password))) {
                        notes.remove(note);
                    }
                }
                noteAdapter.refreshShow(notes);
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearching = false;
                noteAdapter.refreshShow(Sort(currentGroupName, currentSort));//停止搜索之后还是根据搜索前的排序样式刷新界面
                return false;
            }
        });
        return true;
    }

    /**
     * 监听标题栏各项的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.mainactivity_search:
                break;
            case R.id.mainactivity_id_asc:
                noteAdapter.refreshShow(Sort(currentGroupName, ID_ASC));
                currentSort = ID_ASC;
                break;
            case R.id.mainactivity_id_desc:
                noteAdapter.refreshShow(Sort(currentGroupName, ID_DESC));
                currentSort = ID_DESC;
                break;
            case R.id.mainactivity_createTime_asc:
                noteAdapter.refreshShow(Sort(currentGroupName, TIME_ASC));
                currentSort = TIME_ASC;
                break;
            case R.id.mainactivity_cretetTime_desc:
                noteAdapter.refreshShow(Sort(currentGroupName, TIME_DESC));
                currentSort = TIME_DESC;
                break;
            case R.id.mainactivity_listMode:
                currentDisplayMode = LIST_MODE;
                setDisplayMode();
                break;
            case R.id.mainactivity_staggeredMode:
                currentDisplayMode = STAGGER_MODE;
                setDisplayMode();
                break;
            case R.id.mainactivity_clearAll:
                if (notes != null && !currentGroupName.equals(NO_DO))
                    ClearAll();
                else if (notes != null && currentGroupName.equals(NO_DO))
                    Clear_NoDo();
                break;
            case R.id.mainactivity_openGroup:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
        }
        return true;
    }


    private void Clear_NoDo() {
        LitePal.deleteAll(Note.class, "isAlarm=?", 1 + "");
        notes = SortByAlarm();
        noteAdapter.refreshShow(notes);
    }

    private void ClearAll() {
        notes = Sort(currentGroupName, currentSort);//查询组的所有日记，获取组名，全部删除
        Note note = notes.get(0);
        String mgroupName = note.getGroupName();
        LitePal.deleteAll(Note.class, "groupName=?", mgroupName);
        notes = Sort(currentGroupName, currentSort);
        noteAdapter.refreshShow(notes);//只要返回主界面则要把数据库里的笔记取出传递给Adapter刷新界面重新刷新界面
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainActivity_drawerlayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * recyclerview点击事件
     */
    @Override
    public void onItemClick(View view, Note note) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", "编辑");//type=新建，还是编辑
        bundle.putSerializable("note", note);
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("city", currentCity);
        bundle.putString("weather", currentWeather);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
        L.e("mainactivity--->", note.getId() + "      " + note.getGroupName());
    }

    @Override
    public void onItemLongClick(View view, final Note note) {
        //弹出一个dialog，用用户选择是否删除
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog alertDialog = builder.setTitle("系统提示：")
                .setMessage("确定要删除该便签吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LitePal.delete(Note.class, note.getId());//删除笔记
                        noteAdapter.refreshShow(Sort(currentGroupName, currentSort));//只要返回主界面则要把数据库里的笔记取出传递给Adapter刷新界面重新刷新界面
                    }
                }).create();
        alertDialog.show();
    }

    /**
     * 按返回键退到桌面,而不是回到登录界面
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainActivity_floatingActionTop:
//                //给EditActivity发送一条广播,收不到
//                Intent intent2 = new Intent("com.example.seeker.nightBroadcast");
//                sendBroadcast(intent2);
                if (DisplayMenu) {
                    StartAnim();
                } else {
                    CloseAnim();
                }
                break;
            case R.id.mainActivity_floatingActionNote:
                CloseAnim();
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "新建");//type=新建，还是编辑
                bundle.putSerializable("note", null);
                bundle.putString("username", username);
                bundle.putString("password", password);
                bundle.putString("city", currentCity);
                bundle.putString("weather", currentWeather);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.mainActivity_floatingActionWord:
                CloseAnim();
                Intent intent_wordActivity = new Intent(MainActivity.this, WordActivity.class);
                intent_wordActivity.putExtra("username", username);
                startActivity(intent_wordActivity);
                break;
            case R.id.mainActivity_floatingActionJoke:
                Intent intent_entertainmentActivity = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(intent_entertainmentActivity);
                break;
        }
    }

    private void CloseAnim() {
        for (int i = 1; i < floatingActionButtonRes.length; i++) {//因为点击最上面那个弹出其它的，所以i从1开始
            ObjectAnimator animator = ObjectAnimator.ofFloat(floatingActionButtonList.get(i), "translationY",
                    -i * 250, 0f);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setStartDelay(i * 200);
            animator.start();
            DisplayMenu = true;
        }
        //旋转回原来的样子
        ObjectAnimator ratation = ObjectAnimator.ofFloat(floatingActionButtonList.get(0), "rotation",
                -90f, 0f);
        ratation.setDuration(400);
        ratation.start();
    }

    //rotation旋转，scaleX缩放，alpha透明，translationX X轴平移,translationY Y轴平移
    private void StartAnim() {
        //先转90度再弹出其它的
        ObjectAnimator ratation = ObjectAnimator.ofFloat(floatingActionButtonList.get(0), "rotation",
                0f, -90f);
        ratation.setDuration(400);
        ratation.start();
        for (int i = 1; i < floatingActionButtonRes.length; i++) {//因为点击最上面那个弹出其它的，所以i从1开始
            ObjectAnimator animator = ObjectAnimator.ofFloat(floatingActionButtonList.get(i), "translationY",
                    0f, -i * 250);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setStartDelay(i * 200);
            animator.start();
            DisplayMenu = false;
        }
    }

    public class Day_Night_Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String nightMode = intent.getStringExtra("day_or_night");
            //这里本来想用setNightMode()的，但是广播出异常，可能是广播里不能进行耗时操作的原因
            if ("day".equals(nightMode)) {
                Toast.makeText(MainActivity.this, "当前是白天，日间模式保护眼睛~", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "当前是夜晚，夜间模式保护眼睛~", Toast.LENGTH_SHORT).show();
            }
//            if (nightMode.equals("night")) {
//
//            } else {
//
//            }

        }
    }

    /**
     * 设置日夜模式
     */
    private void setNightMode() {
        //  获取当前模式
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        L.e(TAG, "保存前" + currentNightMode);
        //  切换模式
        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            currentNightMode = Configuration.UI_MODE_NIGHT_YES;//注意不是AppCompatDelegate.MODE_NIGHT_YES
        } else {
            currentNightMode = Configuration.UI_MODE_NIGHT_NO;
        }
        sharepre_editor.putInt("currentNightMode", currentNightMode);//将模式保存起来
        sharepre_editor.apply();
        L.e(TAG, "保存后" + currentNightMode);
        // recreate();
        startActivity(new Intent(this, MainActivity.class));
        // finish();
    }

    /**
     * 运行时权限申请
     */
    private void applyThePermission() {

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CALENDAR);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_CALENDAR);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.VIBRATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CHANGE_WIFI_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECEIVE_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_TASKS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.GET_TASKS);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "已授权", LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "未知错误", LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

}
