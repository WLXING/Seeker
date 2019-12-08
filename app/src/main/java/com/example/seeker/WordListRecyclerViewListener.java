package com.example.seeker;

import android.view.View;

import com.example.bean.WordList;

/**
 * Created by ${WLX} on 2019/6/24.
 */

public interface WordListRecyclerViewListener {
    void onItemClick(View view, WordList wordList);

    void onItemLongClick(View view, WordList wordList);
}
