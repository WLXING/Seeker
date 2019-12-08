package com.example.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;

import com.example.seeker.MyGlideEngine;
import com.example.seeker.R;
import com.example.ui.EditActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

/**
 * Created by ${WLX} on 2019/5/19.
 */

public class PictureUtil {
    public static void SelectPictures(Context context,int REQUEST_CODE) {
        Matisse
                .from((Activity) context)
                .choose(MimeType.ofAll())//照片视频全部显示
                .countable(true)//有序选择图片
                .maxSelectable(9)//最大选择数量为9
                .gridExpectedSize(120)//图片显示表格的大小getResources()
                //.captureStrategy(new CaptureStrategy(true, "com.example.seeker.fileprovider"))
                //.capture(true)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向。
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//加载方式
                .forResult(REQUEST_CODE);//请求码
    }
}
