package com.example.adapter;


import android.content.Context;
import android.graphics.Color;
import com.example.bean.Note;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seeker.MyApplication;
import com.example.seeker.R;
import com.example.seeker.RecycylerViewListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>  {
    RecycylerViewListener recycylerViewListener;
    private List<Note> notes;
    private Context context= MyApplication.getContext();

    public NoteAdapter( RecycylerViewListener recycylerViewListener, List<Note> notes) {
        this.recycylerViewListener = recycylerViewListener;
        this.notes = notes;

    }

    private static final int[] colors = new int[]{R.color.color_0,R.color.color_1,
            R.color.color_2,R.color.color_3,R.color.color_4,
            R.color.color_5,R.color.color_6,R.color.color_7,
            R.color.color_8,R.color.color_9,R.color.color_10,};

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
         CardView cardView;
         ImageView previewImg;
         TextView title;
         TextView previewContent;
         TextView createTime;
        public ViewHolder(@NonNull View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.mainActivity_cardView);
            previewImg = (ImageView) view.findViewById(R.id.cardImg);
            title = (TextView) view.findViewById(R.id.cardTitle);
            previewContent = (TextView) view.findViewById(R.id.cardPreviewContent);
            createTime = (TextView) view.findViewById(R.id.cardCreateTime);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup  parent, int i) {
        final View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.note_item, parent, false);
        //注册点击事件
        final ViewHolder viewHolder = new ViewHolder(view);
     //   int positon=viewHolder.getAdapterPosition();这两句放这里会出现ArrayList,outofbound,length=10.index=-1,所以要放在下面两个
       // final Note note = notes.get(positon);//点击事件里面，具体原因不详
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positon=viewHolder.getAdapterPosition();
                Note note = notes.get(positon);
                recycylerViewListener.onItemClick( viewHolder.cardView,  note);
            }
        });
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int positon=viewHolder.getAdapterPosition();
                Note note = notes.get(positon);
                recycylerViewListener.onItemLongClick( viewHolder.cardView,  note);
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);
        int id=note.getId();
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(colors[id%11]));
        holder.title.setTextColor(Color.rgb(0, 0, 0));
        holder.title.setText(note.getTitle());

        String time = note.getCreateTime();
        //如果是今年新建的便签的话，没必要显示年份
     //  if(time.contains("2019年")){
//            time = time.replace("2019年","");
      //  }
        holder.createTime.setText(time);

        String str=note.getPreviewContent();
        String pattern = "<pic uri='(.*?)'>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(note.getContent());
        if(m.find()){
            try{
                Uri uri = Uri.parse(m.group(1));
                Drawable drawable = Drawable.createFromStream(context.getContentResolver().openInputStream(uri),null);
                drawable.setBounds(0,0,2 * drawable.getIntrinsicWidth(),2 * drawable.getIntrinsicHeight());
                holder.previewImg.setImageDrawable(drawable);
                str = str.replace("<pic uri='" + uri + "'>", "");
                Log.d("子内容------------>"," "+str);
            }catch (Exception FileNotFindException){
                Log.d("找到图片","不能根据当前Uri找到图片");
            }
        }else{
            holder.previewImg.setImageDrawable(null);
            Log.d("匹配","没有完成匹配");
        }
        holder.previewContent.setText(str);

    }

    @Override
    public int getItemCount() {
        if (notes==null) {
            return 0;
        }
        else return notes.size();
    }

    public void refreshShow(List<Note> notes) {
        this.notes=notes;
        notifyDataSetChanged();
    }


}
