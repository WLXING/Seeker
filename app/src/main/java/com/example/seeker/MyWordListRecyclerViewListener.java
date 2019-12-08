package com.example.seeker;

import android.view.View;

import com.example.bean.MyWordList;
import com.example.bean.WordList;

/**
 * Created by ${WLX} on 2019/4/22.
 */

public interface MyWordListRecyclerViewListener {
    void onItemClick(View view, MyWordList myWordList);

    void onItemLongClick(View view, MyWordList myWordList);
}
