package com.example.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.seeker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${WLX} on 2019/9/1.
 */

public class NewsItemsAdatper extends RecyclerView.Adapter<NewsItemsAdatper.ViewHolder> {
    List<String> itemNames;
    //1、定义一个集合，用来记录选中
    private List<Boolean> isClicks;//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色
    //2、定义监听并设set方法
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView newsItems_name;

        public ViewHolder(@NonNull View view) {
            super(view);
            newsItems_name = (TextView) view.findViewById(R.id.newsActivity_items_name);
        }
    }

    public NewsItemsAdatper(List<String> itemNames) {
        this.itemNames = itemNames;
        //3、为集合添加值
        isClicks = new ArrayList<>();
        isClicks.add(true);
        for (int i = 1; i < itemNames.size(); i++) {//设置第一个默认选中，刚进来的时候显示第一个的新闻
            isClicks.add(false);
        }
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item_name_item, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        String itemName = itemNames.get(position);
        viewHolder.newsItems_name.setText(itemName);
        //4：设置点击事件
        if (mOnItemClickListener != null) {
            viewHolder.newsItems_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getLayoutPosition(); // 1
                    for (int i = 0; i < isClicks.size(); i++) {
                        isClicks.set(i, false);
                    }
                    isClicks.set(position, true);
                    notifyDataSetChanged();
                    mOnItemClickListener.onItemClick(viewHolder.itemView, position); // 2
                }
            });
            //5、记录要更改属性的控件
            viewHolder.itemView.setTag(viewHolder.newsItems_name);
            //6、判断改变属性
            if (isClicks.get(position)) {
                viewHolder.newsItems_name.setTextColor(Color.parseColor("#ff0000"));
                viewHolder.newsItems_name.setTextSize(20);
            } else {
                viewHolder.newsItems_name.setTextColor(Color.parseColor("#000000"));
                viewHolder.newsItems_name.setTextSize(15);
            }
        }

    }
    //7、定义点击事件回调接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    @Override
    public int getItemCount() {
        return itemNames.size();
    }
}
