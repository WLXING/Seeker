package com.example.seeker;

import android.view.View;

import com.example.bean.Note;

/**
 * Created by ${WLX} on 2019/4/22.
 */

public interface RecycylerViewListener {
    void onItemClick(View view, Note note);

    void onItemLongClick(View view, Note note);
}
