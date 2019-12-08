package com.example.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentToSpannableString {


    public static SpannableString   Content2SpanStr(Context context, String noteContent) {
        String fakeNoteContent = noteContent;

        Pattern img = Pattern.compile("<pic uri='(.*?)'>");
        Matcher mImg = img.matcher(fakeNoteContent);

        SpannableString spanStr = new SpannableString(fakeNoteContent);

        while (mImg.find()) {
            String str = mImg.group(0);
            int start = mImg.start();
            int end = mImg.end();
            Uri imgUri = Uri.parse(mImg.group(1));
            Drawable drawable = null;
            try {
                drawable = Drawable.createFromStream(context.getContentResolver().openInputStream(imgUri), null);
                drawable.setBounds(0, 0, 2 * drawable.getIntrinsicWidth(), 2 * drawable.getIntrinsicHeight());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ImageSpan imageSpan = new ImageSpan(drawable);
            spanStr.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spanStr;
    }
}
