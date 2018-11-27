package com.xiyoutech.bluetoothremote.lib;

/**
 * 数据类型转换工具类
 * Created by Syusuke on 2017/9/19.
 */
public class DataConvertUtils
{
    /**
     * int 转 byte
     * 
     * @param intValue 要转换的int值,int值介于0~255
     * 
     * @return 转换后的byte值
     */
    public static byte int2Byte(int intValue)
    {
        return (byte) intValue;
    }

    /**
     * byte 转 Int
     * 
     * @param byteValue 要转换的byte值
     * 
     * @return 转换后的int值(无符号)
     */
    public static int byte2Int(byte byteValue)
    {
        return byteValue & 0xFF;
    }
}
