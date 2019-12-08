package com.example.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.seeker.R;
import com.example.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ${WLX} on 2019/10/3.
 */

public class MyView extends View {
    private Bitmap mBitmap;//画图用的图片，相当于一个缓冲区
    private int view_width;//屏幕宽度
    private int view_height;//屏幕高度
    private Paint mPaint;
    private Path path;
    private float startX;
    private float startY;
    private static final String TAG="MyView";
    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view_width=context.getResources().getDisplayMetrics().widthPixels;
        view_height=context.getResources().getDisplayMetrics().heightPixels;
        mBitmap = Bitmap.createBitmap(view_width, view_height, Bitmap.Config.ARGB_8888);
        init();
    }

    private  void init() {
        mPaint = new Paint();//初始化画笔
        path = new Path();//路径
        mPaint.setStrokeWidth(5);//笔宽
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(Color.RED);
        mPaint.setDither(true);//抗抖动
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
    }

    @Override
    public void draw(Canvas canvas) {//ondraw看不到东西的
        canvas.drawBitmap(mBitmap,0,0,mPaint);
        canvas.drawPath(path,mPaint);
        canvas.save();
        canvas.restore();//保存之后防止对后面的绘图产生影响
        super.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(event.getX(),event.getY());
                startX=event.getX();
                startY=event.getY();
                return true;
                case MotionEvent.ACTION_MOVE:
                    path.quadTo(startX,startY,event.getX(),event.getY());
                    startX=event.getX();
                    startY=event.getY();
                    invalidate();
                    break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void clear() {
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(40);//橡皮宽度
        L.e(TAG,"清除");
    }

    public void save(String name) {
        try {
            String path= Environment.getExternalStorageDirectory().getPath();
            File file = new File(path + "/pictures/" + name + ".png");
            L.e(TAG,""+file);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
