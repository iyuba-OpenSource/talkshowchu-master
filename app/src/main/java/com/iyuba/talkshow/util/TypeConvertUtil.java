package com.iyuba.talkshow.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TypeConvertUtil {
    public static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    public static byte[] shortToByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }
}
