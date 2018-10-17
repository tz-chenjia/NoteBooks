package cn.tz.cj.bo;

import cn.tz.cj.entity.User;

public class Auth {

    private static Auth auth;
    private String name;
    private String pwd;
    private boolean online;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    private Auth(){

    }

    public static Auth getInstance(){
        if (auth == null) {
            synchronized (Auth.class) {
                if (auth == null) {
                    auth = new Auth();
                }
            }
        }
        return auth;
    }

}
