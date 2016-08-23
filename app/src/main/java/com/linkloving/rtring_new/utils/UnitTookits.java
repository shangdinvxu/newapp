package com.linkloving.rtring_new.utils;

import java.math.BigDecimal;

/**
 * Created by zkx on 2016/4/9.
 */
public class UnitTookits {

    /**
     * 主界面单位转换
     * 厘米转英寸 已经验证
     * @param cm
     * @return
     */
    public static double CMChangetoINRate(int cm)
    {
        BigDecimal   b   =   new   BigDecimal(cm * 0.3937008);
        return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 和上面方法相反
     * 英寸转厘米 已经验证
     * @param cm
     * @return
     */
    public static Integer INRateChangetoCM(int cm)
    {
        return (int) (cm / 0.3937008);
    }

    /**
     * 主界面单位转换 已经验证
     * @param m 米
     * @return 英里
     */
    public static double MChangetoMIRate (int m)
    {
        BigDecimal b   =   new   BigDecimal(m * 0.0006214);
        return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 磅转成KG 已经验证
     * @param lb 磅
     * @return KG
     */
    public static Integer LBRateChangetoKG(int lb)
    {
        return (int) (lb / 2.2046226);
    }

    /**
     * KG转成磅 已经验证
     * @param kg
     * @return 磅
     */
    public static Integer KGChange2LBRate(int kg)
    {
        return (int) (kg * 2.2046226);
    }
}
