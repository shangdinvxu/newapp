package com.linkloving.rtring_new.logic.UI.launch;

/**
 * Created by 123 on 2016/3/2.
 */
public class LoginInfo {
    private String useName;
    private String pwd;

    public LoginInfo(String userName ,String pwd){
        this.useName=userName;
        this.pwd=pwd;
    }

    public LoginInfo(){

    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public String getUseName(){
        return useName;
    }
}
