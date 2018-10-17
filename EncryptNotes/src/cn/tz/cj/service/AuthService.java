package cn.tz.cj.service;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.dao.UserDao;
import cn.tz.cj.entity.User;
import cn.tz.cj.service.intf.IAuthService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.EncryptUtils;

import javax.swing.*;

public class AuthService implements IAuthService {

    private static final String EMAIL_REG = "^[A-Za-z0-9_.-\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    private UserDao userDao = new UserDao();

    private ISystemService systemService = new SystemService();

    @Override
    public boolean login(String email, String pwd) {
        boolean success = false;
        if(email == null || email.trim().equals("")){
            JOptionPane.showMessageDialog(null, "邮箱不能为空！", "登录失败", JOptionPane.WARNING_MESSAGE);
        }else if(!email.matches(EMAIL_REG)){
            JOptionPane.showMessageDialog(null, "邮箱格式不正确！", "登录失败", JOptionPane.WARNING_MESSAGE);
        }
        if(systemService.checkDBAndInit()){
            Auth auth = Auth.getInstance();
            email = EncryptUtils.e(email, AuthService.class.getName());
            pwd = EncryptUtils.e(pwd, pwd);
            auth.setName(email);
            auth.setPwd(pwd);
            User user = userDao.getUser(email);
            if(user != null){
                if(pwd.equals(user.getPwd())){
                    success = true;
                }else {
                    JOptionPane.showMessageDialog(null, "邮箱或密码不正确！", "登录失败", JOptionPane.WARNING_MESSAGE);
                }
            }else {
                userDao.createUser(email, pwd);
                success = true;
            }
        }
        return success;
    }
}
