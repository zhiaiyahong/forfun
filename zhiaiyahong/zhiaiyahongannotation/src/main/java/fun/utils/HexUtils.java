package fun.utils;

/**
 * @author pengweichao
 * @date 2019/7/30
 */
public class HexUtils {

    private HexUtils(){
        throw new IllegalAccessError("utils");
    }

    /**
     * 将byte数组转换为表示16进制值的字符串
     *
     * @param arrB
     *            需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception
     */
    public static  String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍..一个byte转成16进制最多不会超过两位。FF
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16)); // 16代表进制
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组
     *
     * @param strIn 需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception
     */
    public static  byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }
}
