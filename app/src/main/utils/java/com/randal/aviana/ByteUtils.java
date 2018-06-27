package com.randal.aviana;

public class ByteUtils {
    private ByteUtils(){
        throw new UnsupportedOperationException("DO NOT INSTANTIATE THIS CLASS");
    }

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte[] int2byte_8bit(final int value) {
        byte[] src = new byte[1];
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    // BIG-END
    public static byte[] short2byte(final short value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    // BIG-END
    public static byte[] int2byte_16bit(final int value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    // BIG-END
    public static byte[] int2byte_32bit(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static short byte2short(final byte[] bytes) {
        int a = (bytes[0] & 0xff) << 8;
        int b = (bytes[1] & 0xff);
        return (short)(a | b);
    }

    public static int byte2int_16bit(final byte[] bytes) {
        int a = (bytes[0] & 0xff) << 8;
        int b = (bytes[1] & 0xff);
        return a | b;
    }

    public static int byte2int_32bit(final byte[] bytes) {
        int a = (bytes[0] & 0xff) << 24;
        int b = (bytes[1] & 0xff) << 16;
        int c = (bytes[2] & 0xff) << 8;
        int d = (bytes[3] & 0xff);
        return a | b | c | d;
    }

    public static byte[] mergeBytes(byte[]... bytes){
        int len = 0;
        for (byte[] bt : bytes) {
            len += bt.length;
        }
        byte[] src = new byte[len];

        int offset = 0;
        for (byte[] bt : bytes) {
            System.arraycopy(bt, 0, src, offset, bt.length);
            offset += bt.length;
        }
        return src;
    }

    public static byte[] subBytes(final byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; ++i) {
            bs[i-begin] = src[i];
        }
        return bs;
    }

    public static short HexString2Short(String str) {
        short retVal = -1;
        try {
            str = str.replaceAll("[-\\s.:]", "");
            retVal = Short.parseShort(str, 16);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static int HexString2Int(String str) {
        int retVal = -1;
        try {
            str = str.replaceAll("[-\\s.:]", "");
            retVal = Integer.parseInt(str, 16);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static long HexString2Long(String str) {
        long retVal = -1;
        try {
            str = str.replaceAll("[-\\s.:]", "");
            retVal = Long.parseLong(str, 16);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static String int2HexString(int n) {
        String retVal = Integer.toHexString(n);
        for (int len = retVal.length(); len < 2; ++len) {
            retVal = "0".concat(retVal);
        }
        return retVal;
    }

    public static String long2HexString(long n) {
        String retVal = Long.toHexString(n);
        for (int len = retVal.length(); len < 2; ++len) {
            retVal = "0".concat(retVal);
        }
        return retVal;
    }

    public static void printBytes(byte[] bytes) {
        String printArray = "[";
        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hex += " ";
            printArray += hex;
        }
        printArray = printArray.toUpperCase() + "]";
        LogUtils.d(printArray);
    }

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len * 3];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
            ret[j++] = ' ';
        }
        return new String(ret);
    }
}
