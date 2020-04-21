package com.xinsite.common.uitls.lang;

import com.xinsite.common.enums.UnitEnum;

/**
 * 字节转换工具
 *
 * @author www.xinsite.vip
 * @version 2018-07-15
 */
public class ByteUtils {

    private static final int UNIT = 1024;

    /**
     * 将unit_val换成给定的单位，保留整数
     */
    public static int getUnitByType(String unit_val, UnitEnum unitEnum) {
        long byteSize = ByteUtils.getByteSize(unit_val);
        return ByteUtils.getUnitByType(byteSize, unitEnum);
    }

    /**
     * 将unit_val转换成字节
     */
    public static long getByteSize(String unit_val) {
        long size = 0;
        int len = unit_val.length();
        if (len > 2) {
            String type = unit_val.substring(len - 2, len).toUpperCase();
            int val = ValueUtils.tryParse(unit_val.substring(0, len - 2), 0);
            if (UnitEnum.B.toString().equals(type)) {
                size = val * UNIT;
            } else if (UnitEnum.KB.toString().equals(type)) {
                size = val * UNIT;
            } else if (UnitEnum.MB.toString().equals(type)) {
                size = val * UNIT * UNIT;
            } else if (UnitEnum.GB.toString().equals(type)) {
                size = val * UNIT * UNIT * UNIT;
            } else if (UnitEnum.TB.toString().equals(type)) {
                size = val * UNIT * UNIT * UNIT * UNIT;
            } else if (UnitEnum.PB.toString().equals(type)) {
                size = val * UNIT * UNIT * UNIT * UNIT * UNIT;
            }
        } else {
            size = ValueUtils.tryParse(unit_val, 0L);
        }
        return size;
    }

    /**
     * 将字节转换成给定的单位，保留整数
     */
    public static int getUnitByType(long byteSize, UnitEnum type) {
        int unit = 0;
        if (UnitEnum.KB == type) {
            unit = (int) Math.floor(byteSize / UNIT);
        } else if (UnitEnum.MB == type) {
            byteSize = byteSize / UNIT;
            unit = (int) Math.floor(byteSize / UNIT);
        } else if (UnitEnum.GB == type) {
            byteSize = byteSize / UNIT / UNIT;
            unit = (int) Math.floor(byteSize / UNIT);
        } else if (UnitEnum.TB == type) {
            byteSize = byteSize / UNIT / UNIT / UNIT;
            unit = (int) Math.floor(byteSize / UNIT);
        } else if (UnitEnum.PB == type) {
            byteSize = byteSize / UNIT / UNIT / UNIT / UNIT;
            unit = (int) Math.floor(byteSize / UNIT);
        }
        return unit;
    }


    /**
     * @param byteSize 字节
     * @return
     */
    public static String formatByteSize(long byteSize) {

        double size = 1.0 * byteSize;

        String type = "B";
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1KB
            type = "B";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1MB
            type = "KB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1GB
            type = "MB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1TB
            type = "GB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1PB
            type = "TB";
            return format(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) {
            type = "PB";
            return format(size, type);
        }
        return ">PB";
    }

    private static String format(double size, String type) {
        int precision = 0;

        if (size * 1000 % 10 > 0) {
            precision = 3;
        } else if (size * 100 % 10 > 0) {
            precision = 2;
        } else if (size * 10 % 10 > 0) {
            precision = 1;
        } else {
            precision = 0;
        }

        String formatStr = "%." + precision + "f";

        if ("KB".equals(type)) {
            return String.format(formatStr, (size)) + "KB";
        } else if ("MB".equals(type)) {
            return String.format(formatStr, (size)) + "MB";
        } else if ("GB".equals(type)) {
            return String.format(formatStr, (size)) + "GB";
        } else if ("TB".equals(type)) {
            return String.format(formatStr, (size)) + "TB";
        } else if ("PB".equals(type)) {
            return String.format(formatStr, (size)) + "PB";
        }
        return String.format(formatStr, (size)) + "B";
    }

//    public static void main(String[] args) {
//        System.out.println(ByteUtils.formatByteSize(1023));
//        System.out.println(ByteUtils.formatByteSize(1L * UNIT));
//        System.out.println(ByteUtils.formatByteSize(1L * UNIT * UNIT));
//        System.out.println(ByteUtils.formatByteSize(1L * UNIT * 1023));
//        System.out.println(ByteUtils.formatByteSize(1L * 1023 * 1023 * 1023));
//        System.out.println(ByteUtils.formatByteSize(1L * UNIT * UNIT * UNIT));
//        System.out.println(ByteUtils.formatByteSize(1L * UNIT * UNIT * UNIT * UNIT));
//        System.out.println(ByteUtils.formatByteSize(1L * UNIT * UNIT * UNIT * UNIT * UNIT));
//        System.out.println(ByteUtils.formatByteSize(1L * UNIT * UNIT * UNIT * UNIT * UNIT * UNIT));
//    }

}
