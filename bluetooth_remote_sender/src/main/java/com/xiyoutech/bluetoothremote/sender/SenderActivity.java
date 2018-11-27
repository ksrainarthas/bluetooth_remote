package com.xiyoutech.bluetoothremote.sender;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xiyoutech.bluetoothremote.lib.BluetoothSenderUtils;
import com.xiyoutech.bluetoothremote.lib.Utils;

import java.util.Set;

/**
 * 蓝牙发送端页面
 * Created by Syusuke on 2017/9/19.
 */

public class SenderActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final int START_CONN_SUCCEED = 1;
    private static final int START_CONN_FAIL = 2;

    private static final int FORWARD = 8;
    private static final int BACKWARD = 9;
    private static final int TURN_LEFT = 10;
    private static final int TURN_RIGHT = 11;

    TextView mTvCurrConnDevice;

    Button mBtnScan;
    Button mBtnForward;
    Button mBtnBackward;
    Button mBtnTurnLeft;
    Button mBtnTurnRight;

    /**
     * 配对设备列表
     */
    private ListView mLvPairedDevices;

    /**
     * 配对列表适配器
     */
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private BluetoothSenderUtils mBluetoothSenderUtils = null;

    private BluetoothAdapter mBtAdapter;

    /**
     * 需要连接的蓝牙MAC地址
     */
    private String mConnAddress;

    private String mConnName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        mTvCurrConnDevice = (TextView) findViewById(R.id.tv_curr_conn_device);
        mBtnScan = (Button) findViewById(R.id.btn_scan);
        mBtnForward = (Button) findViewById(R.id.btn_forward);
        mBtnBackward = (Button) findViewById(R.id.btn_backward);
        mBtnTurnLeft = (Button) findViewById(R.id.btn_turn_left);
        mBtnTurnRight = (Button) findViewById(R.id.btn_turn_right);
        mLvPairedDevices = (ListView) findViewById(R.id.lv_paireddevices);

        mBtnScan.setOnClickListener(this);
        mBtnForward.setOnClickListener(this);
        mBtnBackward.setOnClickListener(this);
        mBtnTurnLeft.setOnClickListener(this);
        mBtnTurnRight.setOnClickListener(this);

        mBluetoothSenderUtils = BluetoothSenderUtils.getInstance(this);
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            // 查找已配对设备
            case R.id.btn_scan:

                // 查找之前先断开当前连接
                mBluetoothSenderUtils.release();
                mTvCurrConnDevice.setText(getString(R.string.curr_conn_null));

                // 显示配对列表
                mLvPairedDevices.setVisibility(View.VISIBLE);
                mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.item_device);
                mLvPairedDevices.setAdapter(mPairedDevicesArrayAdapter);

                // 设置配对列表条目点击事件
                mLvPairedDevices.setOnItemClickListener(mDeviceClickListener);

                mBtAdapter = BluetoothAdapter.getDefaultAdapter();

                // 获取所有配对设备
                Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
                if(pairedDevices.size() > 0)
                {
                    for(BluetoothDevice device : pairedDevices)
                    {
                        mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
                else
                {
                    mPairedDevicesArrayAdapter.add(getString(R.string.no_paired_device));
                }

                break;
            case R.id.btn_forward:
                if(Utils.sendIntCtrl)
                {
                    mBluetoothSenderUtils.send(FORWARD);
                }
                else
                {
                    mBluetoothSenderUtils.send(new byte[]{'w'});
                }
                break;
            case R.id.btn_backward:
                if(Utils.sendIntCtrl)
                {
                    mBluetoothSenderUtils.send(BACKWARD);
                }
                else
                {
                    mBluetoothSenderUtils.send(new byte[]{'s'});
                }

                break;
            case R.id.btn_turn_left:
                if(Utils.sendIntCtrl)
                {
                    mBluetoothSenderUtils.send(TURN_LEFT);
                }
                else
                {
                    mBluetoothSenderUtils.send(new byte[]{'a'});
                }

                break;
            case R.id.btn_turn_right:
                if(Utils.sendIntCtrl)
                {
                    mBluetoothSenderUtils.send(TURN_RIGHT);
                }
                else
                {
                    mBluetoothSenderUtils.send(new byte[]{'d'});
                }

                break;
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case START_CONN_SUCCEED:
                    mTvCurrConnDevice.setText(getString(R.string.curr_conn_device) + "：" + mConnName);
                    Utils.showToast(SenderActivity.this, getString(R.string.conn_succeed));
                    break;
                case START_CONN_FAIL:
                    Utils.showToast(SenderActivity.this, getString(R.string.conn_fail));
                    break;
            }

            super.handleMessage(msg);

        }
    };

    /**
     * 配对设备列表条目点击
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            String info = ((TextView) view).getText().toString();
            mConnAddress = info.substring(info.length() - 17);
            mConnName = info.substring(0, info.length() - 18);

            startConn();
            mLvPairedDevices.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * 开始连接
     */
    private void startConn()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                BluetoothDevice device = mBtAdapter.getRemoteDevice(mConnAddress);
                boolean isConnect = mBluetoothSenderUtils.connect(device);

                if(isConnect)
                {
                    handler.sendEmptyMessage(START_CONN_SUCCEED);
                }
                else
                {
                    handler.sendEmptyMessage(START_CONN_FAIL);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy()
    {

        if(mBluetoothSenderUtils != null)
        {
            mBluetoothSenderUtils.release();
            mBluetoothSenderUtils = null;
        }

        super.onDestroy();
    }
}
