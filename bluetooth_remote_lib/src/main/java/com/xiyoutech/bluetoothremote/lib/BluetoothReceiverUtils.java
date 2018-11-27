package com.xiyoutech.bluetoothremote.lib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.UUID;

/**
 * 蓝牙接收端工具类
 * Created by Syusuke on 2017/9/19.
 */

public class BluetoothReceiverUtils
{
    private static final String CHAR_ENCODE = "utf-8";

    private static final String NAME = "BluetoothChat";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static BluetoothReceiverUtils mBluetoothReceiverUtils = null;

    private BluetoothSocket mSocket = null;

    private BluetoothServerSocket mServerSocket = null;

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothReceiverUtils() throws IOException
    {
        mServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(NAME, MY_UUID);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothReceiverUtils getInstance() throws IOException
    {
        if(mBluetoothReceiverUtils == null)
        {
            synchronized(BluetoothReceiverUtils.class)
            {
                if(mBluetoothReceiverUtils == null)
                {
                    mBluetoothReceiverUtils = new BluetoothReceiverUtils();
                }
            }
        }

        return mBluetoothReceiverUtils;
    }

    public boolean listen()
    {
        try
        {
            mSocket = mServerSocket.accept();

            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public void setDiscoverable(Context context)
    {
        if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            context.startActivity(discoverableIntent);
        }
    }

    public byte[] recv()
    {
        byte[] buffer = new byte[1];

        try
        {
            mSocket.getInputStream().read(buffer);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return buffer;
    }

    public int recv_int()
    {
        byte[] buffer = new byte[1];
        try
        {
            int num = mSocket.getInputStream().read(buffer);
            if(num > 0)
            {
                return DataConvertUtils.byte2Int(buffer[0]);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return -1;
    }

    public void release()
    {
        try
        {
            if(mServerSocket != null)
            {
                mServerSocket.close();
                mServerSocket = null;
            }
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

        if(mBluetoothReceiverUtils != null)
        {
            mBluetoothReceiverUtils = null;
        }
    }
}
