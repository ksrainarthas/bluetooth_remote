package com.xiyoutech.bluetoothremote.lib;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.util.UUID;

/**
 * 蓝牙发送端工具类
 * Created by Syusuke on 2017/9/19.
 */

public class BluetoothSenderUtils
{
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket mSocket = null;

    private static BluetoothSenderUtils mBluetoothSenderUtils = null;

    private Context mContext = null;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothSenderUtils(Context context)
    {
        mContext = context;
    }

    public static BluetoothSenderUtils getInstance(Context context)
    {

        if(mBluetoothSenderUtils == null)
        {
            synchronized(BluetoothSenderUtils.class)
            {
                if(mBluetoothSenderUtils == null)
                {
                    mBluetoothSenderUtils = new BluetoothSenderUtils(context);
                }
            }
        }

        return mBluetoothSenderUtils;
    }

    public boolean connect(final BluetoothDevice device)
    {
        try
        {
            mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public boolean send(int cmd)
    {
        byte byteValue = DataConvertUtils.int2Byte(cmd);
        byte[] bytes = {byteValue};
        try
        {
            if(mSocket != null)
            {
                mSocket.getOutputStream().write(bytes);
                return true;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public boolean send(byte[] buffer)
    {
        try
        {
            if(mSocket == null)
            {
                Utils.showToast(mContext, mContext.getResources().getString(R.string.conn_device_first));
                return false;
            }
            mSocket.getOutputStream().write(buffer);
            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public void release()
    {
        try
        {
            if(mSocket != null)
            {
                mSocket.close();
                mSocket = null;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        if(mBluetoothSenderUtils != null)
        {
            mBluetoothSenderUtils = null;
        }
    }
}
