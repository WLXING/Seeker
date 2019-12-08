package com.example.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bean.NewsTitle;
import com.example.seeker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${WLX} on 2019/9/1.
 */

public class NewsTitleAdapter extends ArrayAdapter<NewsTitle> {
    private int listviewLayoutId;

    public NewsTitleAdapter(@NonNull Context context, int resource, @NonNull List<NewsTitle> objects) {
        super(context, resource, objects);
        this.listviewLayoutId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NewsTitle newsTitle = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(listviewLayoutId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleImg = (ImageView) convertView.findViewById(R.id.newsTitle_img);
            viewHolder.titleSrc = (TextView) convertView.findViewById(R.id.newsTitle_src);
            viewHolder.titleText = (TextView) convertView.findViewById(R.id.newsTitle_text);
            viewHolder.titleTime = (TextView) convertView.findViewById(R.id.newsTitle_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.titleSrc.setText(newsTitle.getSrc());
        viewHolder.titleText.setText(newsTitle.getText());
        viewHolder.titleTime.setText(newsTitle.getTime());
        Glide.with(getContext()).load(newsTitle.getImageurl()).into(viewHolder.titleImg);//Glide加载新闻图片到标题栏中
        return convertView;
    }

    class ViewHolder {
        ImageView titleImg;
        TextView titleText;
        TextView titleSrc;
        TextView titleTime;
    }

}
