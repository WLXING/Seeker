package com.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bean.MyWordList;
import com.example.bean.WordList;
import com.example.seeker.MyApplication;
import com.example.seeker.MyWordListRecyclerViewListener;
import com.example.seeker.R;
import com.example.seeker.WordListRecyclerViewListener;

import java.util.List;

/**
 * Created by ${WLX} on 2019/8/26.
 */

public class MyWordListAdapter extends RecyclerView.Adapter<MyWordListAdapter.ViewHolder>{
    MyWordListRecyclerViewListener recycylerViewListener;
    private List<MyWordList> myWordLists;
    private Context context= MyApplication.getContext();

    public MyWordListAdapter( MyWordListRecyclerViewListener recycylerViewListener, List<MyWordList> myWordLists) {
        this.recycylerViewListener = recycylerViewListener;
        this.myWordLists = myWordLists;
    }
    private static final int[] colors = new int[]{R.color.color_0,R.color.color_1,
            R.color.color_2,R.color.color_3,R.color.color_4,
            R.color.color_5,R.color.color_6,R.color.color_7,
            R.color.color_8,R.color.color_9,R.color.color_10,};

    public MyWordListAdapter( List<MyWordList> myWordLists) {
        this.myWordLists = myWordLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        final View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.word_search_recyclerview_item, parent, false);
        //注册点击事件
        final ViewHolder viewHolder = new ViewHolder(view);
        //   int positon=viewHolder.getAdapterPosition();这两句放这里会出现ArrayList,outofbound,length=10.index=-1,所以要放在下面两个
        // final Note note = notes.get(positon);//点击事件里面，具体原因不详
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positon=viewHolder.getAdapterPosition();
                MyWordList myWordList = myWordLists.get(positon);
                recycylerViewListener.onItemClick( viewHolder.cardView,  myWordList);
            }
        });
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int positon=viewHolder.getAdapterPosition();
                MyWordList myWordList = myWordLists.get(positon);
                recycylerViewListener.onItemLongClick(viewHolder.cardView,myWordList);
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyWordList myWordList=myWordLists.get(position);
        int id=myWordList.getId();
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(colors[id%11]));
        holder.headWord.setTextColor(Color.rgb(0, 0, 0));
        holder.headWord.setText(myWordList.getHeadword());
        holder.phonetic.setText(myWordList.getPhonetic());
        holder.quickDefinition.setText(myWordList.getQuickdefinition());
    }

    @Override
    public int getItemCount() {
        if (myWordLists==null) {
            return 0;
        }
        else return myWordLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView headWord;
        TextView phonetic;
        TextView quickDefinition;
        public ViewHolder(@NonNull View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.wordActivity_cardView);
            headWord = (TextView) view.findViewById(R.id.wordActivity_search_text_headWord);
            phonetic = (TextView) view.findViewById(R.id.wordActivity_search_text_phonetic);
            quickDefinition = (TextView) view.findViewById(R.id.wordActivity_search_text_quickDefinition);
        }
    }
    public void refreshShow(List<MyWordList> myWordLists) {
        this.myWordLists=myWordLists;
        notifyDataSetChanged();
    }
}
