package com.example.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fragment.ReciteWordFragment;
import com.example.fragment.SearchWordFragment;
import com.example.fragment.WordHistoryFragment;
import com.example.fragment.wordWarehouseFragment;
import com.example.seeker.R;
import com.example.utils.L;
import com.umeng.message.PushAgent;

import org.litepal.LitePal;

public class WordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="WordActivity";
    private TextView searchWord;
    private TextView resiteWord;
    private TextView wordNote;
    private TextView wordHistory;
    private ImageView backToMain;
    private SearchWordFragment searchFragment;
    private ReciteWordFragment reciteFragment;
    private wordWarehouseFragment noteFragment;
    private WordHistoryFragment historyFragment;
    private String currentUsername;//当前用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        init();
        PushAgent.getInstance(this).onAppStart();//友盟消息推送的
        displaySearchFrag();
        searchWord.setOnClickListener(this);
        resiteWord.setOnClickListener(this);
        wordNote.setOnClickListener(this);
        wordHistory.setOnClickListener(this);
        backToMain.setOnClickListener(this);
        currentUsername = getIntent().getStringExtra("username");
        L.e(TAG, "currentusername---->"+currentUsername);
    }

    private void init() {
        searchWord = (TextView) findViewById(R.id.word_left_fragment_search);
        resiteWord = (TextView) findViewById(R.id.word_left_fragment_recite);
        wordNote = (TextView) findViewById(R.id.word_left_fragment_wordNote);
        wordHistory = (TextView) findViewById(R.id.word_left_fragment_wordHistory);
        backToMain = (ImageView) findViewById(R.id.wordActivity_backToMain);
    }

    public String getUsername() {
        return currentUsername;
    }

    /**
     * 这里的Fragment如果用replace的话，每次你从其他的fragment回来，你的页面都会被刷新，如果用户在搜索单词，去我的词库看了下再
     * 回来，然后发现之前的搜索页面不见了，就会带来不好的用户体验，所以用add+hide的模式
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.word_left_fragment_search:
                displaySearchFrag();
                searchWord.setTextColor(getResources().getColor(R.color.toolbar_color));
                resiteWord.setTextColor(getResources().getColor(R.color.black));
                wordNote.setTextColor(getResources().getColor(R.color.black));
                wordHistory.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.word_left_fragment_recite:
                displayReciteFrag();
                searchWord.setTextColor(getResources().getColor(R.color.black));
                resiteWord.setTextColor(getResources().getColor(R.color.toolbar_color));
                wordNote.setTextColor(getResources().getColor(R.color.black));
                wordHistory.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.word_left_fragment_wordNote:
                displayNoteFrag();
                searchWord.setTextColor(getResources().getColor(R.color.black));
                resiteWord.setTextColor(getResources().getColor(R.color.black));
                wordNote.setTextColor(getResources().getColor(R.color.toolbar_color));
                wordHistory.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.word_left_fragment_wordHistory:
                displayHistoryFrag();
                searchWord.setTextColor(getResources().getColor(R.color.black));
                resiteWord.setTextColor(getResources().getColor(R.color.black));
                wordNote.setTextColor(getResources().getColor(R.color.black));
                wordHistory.setTextColor(getResources().getColor(R.color.toolbar_color));
                break;
            case R.id.wordActivity_backToMain:
                finish();
                break;
        }
    }

    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (searchFragment != null) {
            transaction.hide(searchFragment);
        }
        if (reciteFragment != null) {
            transaction.hide(reciteFragment);
        }
        if (noteFragment != null) {
            transaction.hide(noteFragment);
        }
        if (historyFragment != null) {
            transaction.hide(historyFragment);
        }
    }

    private void displaySearchFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //这里注意fragment 的add,hide,show和replace的区别，用replace的话再返回的时候不会保存之前做的改变,用add方法并不会重建
        if (searchFragment == null) {
            searchFragment = new SearchWordFragment();
            fragmentTransaction.add(R.id.wordActivity_right_linearlayout, searchFragment);
        }
        hideFragment(fragmentTransaction);//隐藏所有fragment
        fragmentTransaction.show(searchFragment);
        fragmentTransaction.commit();
    }

    private void displayReciteFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //这里注意fragment 的add,hide,show和replace的区别，用replace的话再返回的时候不会保存之前做的改变,用add方法并不会重建
        if (reciteFragment == null) {
            reciteFragment = new ReciteWordFragment();
            fragmentTransaction.add(R.id.wordActivity_right_linearlayout, reciteFragment);
        }
        hideFragment(fragmentTransaction);//隐藏所有fragment
        fragmentTransaction.show(reciteFragment);
        fragmentTransaction.commit();
    }

    private void displayNoteFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //这里注意fragment 的add,hide,show和replace的区别，用replace的话再返回的时候不会保存之前做的改变,用add方法并不会重建
        if (noteFragment == null) {
            noteFragment = new wordWarehouseFragment();
            fragmentTransaction.add(R.id.wordActivity_right_linearlayout, noteFragment);
        }
        hideFragment(fragmentTransaction);//隐藏所有fragment
        fragmentTransaction.show(noteFragment);
        fragmentTransaction.commit();
    }

    private void displayHistoryFrag() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //这里注意fragment 的add,hide,show和replace的区别，用replace的话再返回的时候不会保存之前做的改变,用add方法并不会重建
        if (historyFragment == null) {
            historyFragment = new WordHistoryFragment();
            fragmentTransaction.add(R.id.wordActivity_right_linearlayout, historyFragment);
        }
        hideFragment(fragmentTransaction);//隐藏所有fragment
        fragmentTransaction.show(historyFragment);
        fragmentTransaction.commit();
    }

}
