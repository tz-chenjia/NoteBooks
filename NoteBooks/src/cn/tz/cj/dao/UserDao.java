package cn.tz.cj.dao;

import cn.tz.cj.entity.User;

import java.util.List;

public class UserDao extends BaseDao {

    public User getUser(String email) {
        User user = null;
        String sql = "select * from nb_user where email=?";
        List<User> users = queryToBean(sql, new Object[]{email}, User.class);
        if (users.size() > 0) {
            user = users.get(0);
        }
        return user;
    }

    public void createUser(String email, String pwd) {
        String sql = "insert into nb_user (email,pwd) values(?,?)";
        update(sql, new Object[]{email, pwd});
    }

    public void updateUser(String oldEmail, String oldPwd, String newEmail, String newPwd) {
        String sql = "update nb_user set email=?,pwd=? where email=? and pwd=?";
        update(sql, new Object[]{newEmail, newPwd, oldEmail, oldPwd});
    }

}
