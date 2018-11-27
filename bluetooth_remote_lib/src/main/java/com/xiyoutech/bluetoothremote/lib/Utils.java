package com.xiyoutech.bluetoothremote.lib;

import android.content.Context;
import android.widget.Toast;

/**
 * 工具类
 * Created by Syusuke on 2017/9/21.
 */

public class Utils
{
    public static boolean sendIntCtrl = false;

    public static void showToast(Context context, CharSequence charSequence)
    {
        Toast.makeText(context, charSequence, Toast.LENGTH_SHORT).show();
    }
}
