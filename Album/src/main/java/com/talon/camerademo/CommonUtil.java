package com.talon.camerademo;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;


import java.text.MessageFormat;


/**
 * Created by 003 on 2016-12-08.
 */
public class CommonUtil {

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        float px = dpValue * scale;
        int pxInt = (int) px;
        return px == pxInt ? pxInt : pxInt + 1;
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        float dp = pxValue / scale;
        int dpInt = (int) dp;
        return dp == dpInt ? dpInt : dpInt + 1;
    }

    public static CharSequence fillString(Context context, int strResId, Object... args) {
        String str = ResourceUtil.getString(context, strResId);
        return fillString(str, args);
    }

    public static CharSequence fillHtmlString(Context context, int strResId, Object... args) {
        String str = ResourceUtil.getString(context, strResId);
        return fillHtmlString(str, args);
    }

    public static CharSequence fillString(String baseStr, Object... args) {
        return MessageFormat.format(baseStr, args);
    }

    @SuppressWarnings("deprecation")
    public static CharSequence fillHtmlString(String baseStr, Object... args) {
        CharSequence str = fillString(baseStr, args);
        str = Html.fromHtml(str.toString());
        return str;
    }

}