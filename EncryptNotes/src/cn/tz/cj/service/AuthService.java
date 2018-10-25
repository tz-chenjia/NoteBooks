package cn.tz.cj.service;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.dao.NoteBookDao;
import cn.tz.cj.dao.NoteDao;
import cn.tz.cj.dao.UserDao;
import cn.tz.cj.entity.Note;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.entity.User;
import cn.tz.cj.service.intf.IAuthService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.EncryptUtils;
import cn.tz.cj.ui.LoginDialog;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public class AuthService implements IAuthService {

    public static final String EMAIL_REG = "^[A-Za-z0-9_.-\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    private UserDao userDao = new UserDao();

    private NoteBookDao noteBookDao = new NoteBookDao();

    private NoteDao noteDao = new NoteDao();

    private ISystemService systemService = new SystemService();

    @Override
    public boolean login(String email, String pwd) {
        if (email == null || email.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "邮箱不能为空！", "登录失败", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (!email.matches(EMAIL_REG)) {
            JOptionPane.showMessageDialog(null, "邮箱格式不正确！", "登录失败", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (systemService.checkDBAndInit()) {
            email = EncryptUtils.e(email, AuthService.class.getName());
            pwd = EncryptUtils.e(pwd, pwd);
            Auth.getInstance().setName(email);
            Auth.getInstance().setPwd(pwd);
            User user = userDao.getUser(email);
            if (user != null) {
                if (pwd.equals(user.getPwd())) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "邮箱或密码不正确！", "登录失败", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } else {
                userDao.createUser(email, pwd);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean loginOut(boolean isSaveTempData) {
        if(isSaveTempData){
            systemService.tempSaveDataToLocal();
        }
        Auth.getInstance().setAuth(null);
        new LoginDialog();
        return false;
    }

    @Override
    public boolean editUserInfo(String email, String pwd) {
        //systemService.tempSaveDataToLocal();
        String oldPwd = Auth.getInstance().getPwd();
        String oldEmail = Auth.getInstance().getName();
        String newEmail = EncryptUtils.e(email, AuthService.class.getName());
        String newPwd = EncryptUtils.e(pwd, pwd);
        if (!newEmail.equals(oldEmail)) {
            List<NoteBook> noteBooks = noteBookDao.getNoteBooks(oldEmail);
            for (NoteBook nb : noteBooks) {
                NoteBook newNb = new NoteBook();
                newNb.setNotebook(nb.getNotebook());
                newNb.setEmail(newEmail);
                noteBookDao.updateNoteBook(nb, newNb);
            }
        }
        if (!oldPwd.equals(newPwd)) {
            List<NoteBook> noteBooks = noteBookDao.getNoteBooks(newEmail);
            for (NoteBook nb : noteBooks) {
                noteBookDao.updateNoteBookWithNewPwd(nb, newPwd);
                Set<String> notesTitlesByNoteBook = noteDao.getNotesTitlesByNoteBook(nb.getNotebook());
                for (String title : notesTitlesByNoteBook) {
                    Note note = new Note();
                    note.setContent("");
                    note.setTitle(title);
                    note.setNotebook(nb.getNotebook());
                    note = noteDao.getNote(note);
                    noteDao.updateNoteDaoWithNewPwd(note, newPwd);
                }

            }
        }
        userDao.updateUser(oldEmail, oldPwd, newEmail, newPwd);
        return true;
    }
}
