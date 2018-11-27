package com.xiyoutech.bluetoothremote.receiver;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.xiyoutech.bluetoothremote.lib.BluetoothReceiverUtils;
import com.xiyoutech.bluetoothremote.lib.Utils;

import java.io.IOException;

/**
 * 蓝牙接收端页面
 * Created by Syusuke on 2017/9/19.
 */

public class ReceiverActivity extends AppCompatActivity
{
    private static final int READ_CTRL = 1;

    private Button mBtnAccept;
    private ListView mLvAcceptCtrl;
    private BluetoothReceiverUtils mBluetoothServerUtils;
    private boolean isConn = false;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case READ_CTRL:
                    if(Utils.sendIntCtrl)
                    {
                        int info = (int) msg.obj;
                        mCtrlAdapter.add(new String(String.valueOf(info)));
                    }
                    else
                    {
                        byte[] readBuf = (byte[]) msg.obj;
                        String readMessage = new String(readBuf);
                        mCtrlAdapter.add(readMessage);
                    }

                    // 每次发送完命令后, 接收端总是显示到接收到的命令列表的最后
                    mLvAcceptCtrl.setSelection(mCtrlAdapter.getCount() - 1);

                    break;
            }
            super.handleMessage(msg);
        }
    };
    private ArrayAdapter<String> mCtrlAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        try
        {
            mBluetoothServerUtils = BluetoothReceiverUtils.getInstance();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        mBtnAccept = (Button) findViewById(R.id.btn_accept);
        mLvAcceptCtrl = (ListView) findViewById(R.id.lv_accept_ctrl);

        mBtnAccept.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // 点击开始接收
                        if(mBluetoothServerUtils != null)
                        {
                            mBluetoothServerUtils.setDiscoverable(ReceiverActivity.this);
                            isConn = mBluetoothServerUtils.listen();

                            while(isConn)
                            {
                                if(Utils.sendIntCtrl)
                                {
                                    int recv_int = mBluetoothServerUtils.recv_int();
                                    if(recv_int > 0)
                                    {
                                        handler.obtainMessage(READ_CTRL, -1, -1, recv_int).sendToTarget();
                                    }
                                }
                                else
                                {
                                    byte[] buffer = mBluetoothServerUtils.recv();
                                    if(buffer[0] > 0)
                                    {
                                        handler.obtainMessage(READ_CTRL, -1, -1, buffer).sendToTarget();
                                    }
                                }
                            }
                        }
                    }
                }).start();
            }
        });

        mCtrlAdapter = new ArrayAdapter<String>(ReceiverActivity.this, android.R.layout.simple_list_item_1);
        mLvAcceptCtrl.setAdapter(mCtrlAdapter);
    }

    @Override
    protected void onDestroy()
    {
        isConn = false;
        if(mBluetoothServerUtils != null)
        {
            mBluetoothServerUtils.release();
            mBluetoothServerUtils = null;
        }
        super.onDestroy();
    }
}
