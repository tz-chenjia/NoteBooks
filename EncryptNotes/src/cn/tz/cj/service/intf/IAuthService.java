package cn.tz.cj.service.intf;

import javax.swing.*;

public interface IAuthService {

    public boolean login(String email, String pwd);
    public boolean loginOut(JFrame frame);
    public boolean editUserInfo(String email, String pwd);

}
