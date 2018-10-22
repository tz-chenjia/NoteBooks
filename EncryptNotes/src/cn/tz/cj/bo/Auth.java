package cn.tz.cj.bo;

import cn.tz.cj.entity.User;

public class Auth {

    private static Auth auth;
    private String name;
    private String pwd;

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
