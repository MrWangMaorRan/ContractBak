package com.peep.contractbak.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


/**
 * 屏幕工具类
 */
public class ScreenUtils {
    public static int SCREEN_WIDTH; //屏幕宽度(px)
    public static int SCREEN_HEIGHT; //屏幕高度(px)
    public static float SCREEN_DENSITY; //屏幕密度


    /**
     * 初始化屏幕款高
     */
    public static void initScreenUtils(Context context) {
        if (SCREEN_WIDTH > 0) {
            return;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH = dm.widthPixels;         // 屏幕宽度（像素）
        SCREEN_HEIGHT = dm.heightPixels;       // 屏幕高度（像素）
        SCREEN_DENSITY = dm.density;         // 屏幕密度
    }

    /**
     * dp 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return (int) (dpValue * SCREEN_DENSITY + 0.5f);
    }


    /**
     * px(像素) 转成为 dp
     */
    public static int px2dp(float pxValue) {
        return (int) (pxValue / SCREEN_DENSITY + 0.5f);
    }

    /**
     * 关闭软键盘
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 获取TextView里面的内容
     * */
    public static String getTextContent(TextView tv){
        Layout layout = tv.getLayout();
        int line = tv.getLayout().getLineCount();
        String result = " ";
        String text = layout.getText().toString();
        for (int i = 0; i < line - 1; i ++) {
            int start = layout.getLineStart(i);
            int end = layout.getLineEnd(i);
            result += text.substring(start, end) + "\\n";
        }
        int start = layout.getLineStart(line - 1);
        int end = layout.getLineEnd(line - 1);
        result += text.substring(start, end);
        return result;
    }

}
