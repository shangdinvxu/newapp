package com.linkloving.rtring_new.utils;

import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zkx on 2016/2/25.
 */
public class CommonUtils {
    private static final String TAG = CommonUtils.class.getSimpleName();
    public static boolean isEmail(String email)
    {
        if (email == null) {
            return false;
        }
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static String getExceptionStackTraceForHTML(Exception e)
    {
        StringBuffer out = new StringBuffer();
        StackTraceElement[] stes = e.getStackTrace();

        out.append("<pre><strong>" + e.getClass().getName() + ":" + e.getMessage() + "</strong><br/>");
        for (StackTraceElement ste : stes)
            out.append("  (" + ste.getFileName() + ":<font color=\"#008000\">" + ste.getLineNumber() + "</font>) - " +
                    ste.getClassName() + ".<font color=\"#008000\">" + ste.getMethodName() + "()</font><br/>");
        out.append("</pre>");
        return out.toString();
    }

    public static String getExceptionStackTraceForPlainText(Exception e)
    {
        StringBuffer out = new StringBuffer();
        StackTraceElement[] stes = e.getStackTrace();

        out.append(e.getClass().getName()).append(":").append(e.getMessage()).append("\n");
        for (StackTraceElement ste : stes) {
            out.append("  (" + ste.getFileName()).append(":").append(ste.getLineNumber()).append(" - ")
                    .append(ste.getClassName()).append(".").append(ste.getMethodName()).append("()").append("\n");
        }
        return out.toString();
    }

    public static int getLikePageNum(int allCount, int countPerPage)
    {
        int pageNum = countPerPage == 0 ? 1 :
                allCount / countPerPage + (allCount % countPerPage > 0 ? 1 : 0);
        return pageNum;
    }

    public static String byteToHexStringForMD5(byte[] MD5Bytes)
    {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'a', 'b', 'c', 'd', 'e', 'f' };

        char[] str = new char[32];

        int k = 0;
        for (int i = 0; i < 16; i++)
        {
            byte byte0 = MD5Bytes[i];
            str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];

            str[(k++)] = hexDigits[(byte0 & 0xF)];
        }
        String s = new String(str);
        return s;
    }

    public static String stringNULL(String str, boolean needTrim)
    {
        return isStringEmpty(str, needTrim) ? "" : str;
    }

    public static String stringNULL(String str, boolean needTrim, String willPreAppend)
    {
        return willPreAppend + str;
    }

    public static String escapeXML(String beforeEscapeXML)
    {
        return beforeEscapeXML.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("'", "&apos;")
                .replaceAll("\"", "&quot;");
    }

    public static Object[][] to2DArray(Vector<Vector> towDVector)
    {
        Object[][] towDArray = new Object[towDVector.size()][];
        for (int i = 0; i < towDVector.size(); i++)
        {
            Vector row = (Vector)towDVector.elementAt(i);

            towDArray[i] = row.toArray();
        }
        return towDArray;
    }

    public static Vector to2DVector(Object[][] src) {
        return to2DVector(src, new Vector());
    }

    public static Vector to2DVector(Object[][] src, Vector dest) {
        if ((src != null) && (src.length > 0))
            for (int i = 0; i < src.length; i++)
                dest.addElement(toVector(src[i]));
        return dest;
    }

    public static Vector toVector(Object[] src) {
        return toVector(src, new Vector());
    }

    public static Vector toVector(Object[] src, Vector dest) {
        if ((src != null) && (src.length > 0))
            for (int i = 0; i < src.length; i++)
                dest.addElement(src[i]);
        return dest;
    }

    public static int[] getFlowingNums(int seed, int stepping, int count)
    {
        int[] nums = new int[count];
        for (int i = 0; i < count; i++)
            nums[i] = (seed + stepping * (i + 1));
        return nums;
    }

    public static String[] sequence(long currentValue, int count, int digit, String appendStr)
            throws Exception
    {
        if (count < 1)
        {
            String warn = "count不能小于1，currentValue=" + currentValue +
                    " =" + count + " digit=" + digit + " appendStr=" + appendStr;
            MyLog.v(TAG , warn);
            throw new Exception(warn);
        }

        String[] sns = new String[count];
        for (int i = 0; i < count; i++)
        {
            sns[i] = (appendStr + hiString(new StringBuilder().append(currentValue + i).toString(), digit, '0', true, false));
        }
        return sns;
    }

    public static final String encyptPassword(String originalPsw, String DEFAULT_PASSWORD)
    {
        if (originalPsw == null)
            return null;
        if (!DEFAULT_PASSWORD.equals(originalPsw)) {
            try
            {
                return byteToHexStringForMD5(
                        MessageDigest.getInstance("MD5").digest(originalPsw.getBytes()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return originalPsw;
    }

    @Deprecated
    public static final byte[] encyptPasswordOld(char[] raw, String DEFAULT_PASSWORD)
    {
        if (raw == null)
            return null;
        byte[] byteRaw = new String(raw).getBytes();
        if (!Arrays.equals(raw, DEFAULT_PASSWORD.toCharArray()))
        {
            try
            {
                byteRaw = MessageDigest.getInstance("SHA").digest(byteRaw);
                long v = 0L;
                int len = byteRaw.length;

                int n = Math.min(len, 8);
                for (int i = 0; i < n; i++)
                    v += ((byteRaw[i] + n & 0xFF) << i * 8);
                byteRaw = Long.toString(v).getBytes();
            }
            catch (NoSuchAlgorithmException ex)
            {
                ex.printStackTrace();
            }
        }

        return byteRaw;
    }



    public static String getDateDepthed(String srcDate, int depth, int calendarField)
    {
        try
        {
            Date csrq = DateFormat.getDateInstance().parse(srcDate);
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(csrq);
            gc.add(calendarField, depth);

            return new SimpleDateFormat("yyyy-MM-dd")
                    .format(gc.getTime());
        }
        catch (Exception ee) {
        }
        return null;
    }

    public static ArrayList getSplitedPartByBrackets(String str)
    {
        return getSplitedPartByBrackets(str, '{', '}');
    }

    public static ArrayList getSplitedPartByBrackets(String str, char startBracket, char endBracket)
    {
        ArrayList res = new ArrayList();
        if (str != null)
        {
            int point = 0;
            int start = -1;
            while (point < str.length())
            {
                char c = str.charAt(point);
                if (c == startBracket)
                    start = point;
                else if (c == endBracket)
                {
                    if (start != -1)
                    {
                        res.add(str.substring(start, point + 1));
                        start = -1;
                    }
                }
                point++;
            }
        }
        return res;
    }

    public static String firstCharUpper(String s)
    {
        if ((s == null) || ((s = s.trim()).equals("")))
            return null;
        return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
    }

    public static double getScaledDoubleValue(double d, int precision)
    {
        return getScaledValue(d, precision).doubleValue();
    }

    public static BigDecimal getScaledValue(double d, int precision)
    {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.divide(new BigDecimal("1"), precision < 0 ? 0 : precision, 4);
        return bd;
    }

    public static String getScaledValue(Object d, int precision, boolean trimTail)
    {
        String ori_str = d.toString();
        try
        {
            Double doubleStr = new Double(ori_str);
            return getScaledValue(Double.parseDouble(ori_str), precision, trimTail);
        }
        catch (Exception ex) {
        }
        return "0";
    }

    public static String getScaledValue(double d, int precision, boolean trimTail)
    {
        String str = getScaledValue(d, precision).toString();
        if ((trimTail) && (precision > 0))
        {
            StringBuffer sb = new StringBuffer(str);

            while (sb.length() > 0)
            {
                char c = sb.charAt(sb.length() - 1);
                if ((c == '0') || (c == '.'))
                {
                    sb.deleteCharAt(sb.length() - 1);
                    if (c == '.')
                        return sb.toString();
                }
                else {
                    return sb.toString();
                }
            }
        }
        return str;
    }

    public static String[] breakStringByReturnChar(String str)
    {
        return splitStringByChar(str, "\n");
    }

    public static String[] splitStringByChar(String str, String c)
    {
        ArrayList resultAl = splitStringByCharC(str, c);
        String[] strs = new String[resultAl.size()];
        resultAl.toArray(strs);
        return strs;
    }

    public static ArrayList splitStringByCharC(String str, String c)
    {
        StringTokenizer en = new StringTokenizer(str, c);
        ArrayList vec = new ArrayList();
        while (en.hasMoreElements())
            vec.add(en.nextElement());
        return vec;
    }

    public static String hiString(String originalStr, int theLength)
    {
        return hiString(originalStr, theLength, '0');
    }

    public static String hiString(String originalStr, int theLength, char stuffChar)
    {
        return hiString(originalStr, theLength, stuffChar, true, true);
    }

    public static String hiString(String originalStr, int theLength, char stuffChar, boolean stuffToLeft, boolean trucateIfLong)
    {
        if (originalStr.getBytes().length > theLength)
        {
            if (trucateIfLong) {
                return new String(originalStr.getBytes(), 0, theLength);
            }

            return originalStr;
        }
        if (originalStr.getBytes().length < theLength)
        {
            StringBuffer dst = new StringBuffer();
            for (int i = 0; i < theLength - originalStr.getBytes().length; i++) {
                dst.append(stuffChar);
            }

            if (stuffToLeft) {
                dst.append(originalStr);
            }
            else
                dst = new StringBuffer(originalStr).append(dst.toString());
            return dst.toString();
        }
        return originalStr;
    }

    public static String truncString(String msg, int maxLen, String trailStr)
    {
        String ret = msg;
        if (msg != null)
        {
            int len = msg.length();
            int max = maxLen - (trailStr != null ? trailStr.length() : 0);
            if (len > max)
            {
                System.out.println("文本内容超长（长度=" + len + ",但允许的最大长度=" + max + "），内容其余部分将被截断！");
                ret = truncString(msg, max) + (trailStr != null ? trailStr : "");
            }
        }

        return ret;
    }

    public static String truncStringEx(String msg, int maxLen, String trailStr)
    {
        String ret = msg;
        if (msg != null)
        {
            int len = msg.getBytes().length;
            int max = maxLen - (trailStr != null ? trailStr.length() : 0);
            if (len > max)
            {
                ret = truncStringEx(msg, max) + (trailStr != null ? trailStr : "");
            }
        }

        return ret;
    }

    public static String truncString(String msg, int maxLen)
    {
        String ret = msg;
        if ((msg != null) &&
                (msg.length() > maxLen))
            ret = msg.substring(0, maxLen);
        return ret;
    }

    public static String truncStringEx(String msg, int maxLen)
    {
        String ret = msg;
        if ((msg != null) &&
                (msg.length() > maxLen))
        {
            byte[] bs = msg.getBytes();
            byte[] tmp = new byte[maxLen];
            System.arraycopy(bs, 0, tmp, 0, maxLen);
            ret = new String(tmp);
        }
        return ret;
    }

    public static boolean isStringEmpty(String s, boolean needTrim)
    {
        return (s == null) || (needTrim ? s.trim().equals("") : s.equals(""));
    }

    public static boolean isStringEmpty(String s)
    {
        return  CommonUtils.isStringEmptyPrefer(s) || ToolKits.isJSONNullObj(s);
    }

    public static boolean isStringEmptyPrefer(String s)
    {
        return (s == null || s.equals("{}") || s.equals("\"\"")|| s.isEmpty());
    }

    public static float getFloatValue(String s)
    {
        try
        {
            return getFloatValue(s, 0.0F);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }return 0.0F;
    }

    public static float getFloatValueUnsafe(String s)
    {
        return getFloatValue(s, 0.0F);
    }

    public static float getFloatValue(Object obj, float defaultVal) {
        String value = String.valueOf(obj);
        return (obj == null) || (isStringEmpty(value, true)) ? defaultVal : Float.parseFloat(value);
    }

    public static double getDoubleValue(String s)
    {
        try
        {
            return getDoubleValue(s, 0.0D);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }return 0.0D;
    }

    public static double getDoubleValueUnsafe(String s)
    {
        return getDoubleValue(s, 0.0D);
    }

    public static double getDoubleValue(Object obj, double defaultVal) {
        String value = String.valueOf(obj);
        return (obj == null) || (isStringEmpty(value, true)) ? defaultVal : Double.parseDouble(value);
    }

    public static int getIntValue(Object ob)
    {
        try
        {
            return getIntValue(ob, 0);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }return 0;
    }

    public static int getIntValueUnsafe(Object ob)
    {
        return getIntValue(ob, 0);
    }

    public static int getIntValue(Object obj, int defaultVal) {
        String value = String.valueOf(obj);
        return (obj == null) || (isStringEmpty(value, true)) ? defaultVal : Integer.parseInt(value);
    }

    public static String getJavaHomePath()
    {
        String javaHome = System.getProperty("java.home");

        return javaHome + "/bin";
    }

    public static void sleep(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (Exception localException)
        {
        }
    }

    public static String getRandomStr()
    {
        return getRandomStr(20);
    }

    public static String getRandomStr(int len)
    {
        Random rd = new Random();
        String retStr = "";
        do
        {
            int rdGet1 = Math.abs(rd.nextInt()) % 10;

            int rdGet2 = Math.abs(rd.nextInt()) % 26 + 65;
            retStr = retStr + rdGet1 + (char)rdGet2;
        }while (retStr.length() < len);

        return retStr;
    }

    public static String getRandomStr2(int len)
    {
        Random rd = new Random();
        String retStr = "";
        do
        {
            int rdGet1 = -1; int rdGet2 = -1;

            int decide = Math.abs(rd.nextInt()) % 2;
            if (decide == 0)
            {
                rdGet1 = Math.abs(rd.nextInt()) % 10;
            }
            else
                rdGet2 = Math.abs(rd.nextInt()) % 26 + 65;
            retStr = retStr + (decide == 0 ? Integer.valueOf(rdGet1) : new StringBuilder().append((char)rdGet2).toString());
        }while (retStr.length() < len);

        return retStr;
    }

    public static String getTimeWithStr()
    {
        return getTimeWithStr(":");
    }

    public static String getTimeWithStr(String separator)
    {
        Calendar calendar = Calendar.getInstance();

        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);
        String time = (h >= 10 ? h : new StringBuilder("0").append(h).toString()) + separator + (
                m >= 10 ? m : new StringBuilder("0").append(m).toString()) + separator + s + separator + ms;

        return time;
    }

    public static String getDateWithStr()
    {
        Calendar calendar = Calendar.getInstance();

        int y = calendar.get(Calendar.YEAR);
        int M = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DATE);
        String date = y + "-" + (M >= 10 ? M : new StringBuilder("0").append(M).toString()) + "-" + (
                d >= 10 ? d : new StringBuilder("0").append(d).toString());

        return date;
    }

    public static String getConvenientFileSize(double size, int scale)
    {
        String ret = size + "字节";
        double temp = size / 1024.0D;
        if (temp >= 1.0D)
        {
            ret = roundEx(temp, scale) + "KB";
            temp /= 1024.0D;
            if (temp >= 1.0D)
            {
                ret = roundEx(temp, scale) + "MB";
                temp /= 1024.0D;
                if (temp >= 1.0D)
                {
                    ret = roundEx(temp, scale) + "GB";
                    temp /= 1024.0D;
                    if (temp >= 1.0D) {
                        ret = new DecimalFormat("#0.00")
                                .format(roundEx(temp, scale)) + "TB";
                    }
                }
            }
        }
        return ret;
    }

    public static double roundEx(double v, int scale)
    {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero!");
        }
        BigDecimal data = new BigDecimal(Double.toString(v));
        BigDecimal divisor = new BigDecimal("1");
        return data.divide(divisor, scale, 5).doubleValue();
    }

    private static String concatOneMACAddr(byte[] mac)
    {
        String macStr = "";
        if ((mac == null) || (mac.length == 0)) {
            return null;
        }
        byte[] arrayOfByte = mac; int j = mac.length; for (int i = 0; i < j; i++) { byte b = arrayOfByte[i];

        String s = Integer.toHexString(b & 0xFF).toUpperCase();
        macStr = macStr + (s.length() == 1 ? "0" + s : s) + (
                i == mac.length - 1 ? "" : "-");
        i++;
    }
        return macStr;
    }

    public static String getMACAddresses()
            throws Exception
    {
        String macStr = "";

        Enumeration nis = NetworkInterface.getNetworkInterfaces();
        while (nis.hasMoreElements())
        {
            NetworkInterface ni = (NetworkInterface)nis.nextElement();

            Method m = NetworkInterface.class.getMethod("getHardwareAddress", new Class[0]);
            byte[] mac = (byte[])m.invoke(ni, new Object(){});

            String oneMACStr = concatOneMACAddr(mac);
            if (!isStringEmpty(oneMACStr, true)) {
                macStr = macStr + (isStringEmpty(macStr, true) ? "" : ",") +
                        oneMACStr;
            }

        }

        return macStr;
    }

    public static boolean isNumber(char c)
    {
        switch (c)
        {
            case '0':
                return true;
            case '1':
                return true;
            case '2':
                return true;
            case '3':
                return true;
            case '4':
                return true;
            case '5':
                return true;
            case '6':
                return true;
            case '7':
                return true;
            case '8':
                return true;
            case '9':
                return true;
        }
        return false;
    }

    public static boolean isNumeric(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
