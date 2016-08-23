package com.linkloving.rtring_new.basic;

/**
 * Created by zkx on 2016/2/24.
 */
public class RHolder {
    private static RHolder instance = null;
    private HelloR eva$android$R = null;

    public static RHolder getInstance()
    {
        if (instance == null)
            instance = new RHolder();
        return instance;
    }

    public HelloR getEva$android$R()
    {
        return this.eva$android$R;
    }

    public void setEva$android$R(HelloR eva$android$R) {
        this.eva$android$R = eva$android$R;
    }
}
