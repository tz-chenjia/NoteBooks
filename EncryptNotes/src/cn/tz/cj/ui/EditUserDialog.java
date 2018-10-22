package cn.tz.cj.ui;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.service.AuthService;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.intf.IAuthService;
import cn.tz.cj.tools.EncryptUtils;

import javax.swing.*;
import java.awt.event.*;

public class EditUserDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField oldPwdPasswordField;
    private JTextField newEmailTextField;
    private JPasswordField newPwdPasswordField;
    private JLabel oldPwdLabel;
    private JLabel newEmailLabel;
    private JLabel newPwdLabel;
    private JPasswordField againNewPwdPasswordField;
    private JLabel againNewPwdLabel;

    private MainForm mainForm;
    private Auth auth = Auth.getInstance();
    private IAuthService authService = new AuthService();

    public EditUserDialog(MainForm mainForm) {
        this.mainForm = mainForm;
        setTitle("NoteBooks - 帐号修改");
        setIconImage(ConfigsService.getImage("notebook.png"));
        setSize(350, 250);
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        newEmailTextField.setText(EncryptUtils.d(auth.getName(), AuthService.class.getName()));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        String oldPwd = String.valueOf(oldPwdPasswordField.getPassword());
        oldPwd = EncryptUtils.e(oldPwd, oldPwd);
        if(oldPwd.equals(auth.getPwd())){
            String newEmail = newEmailTextField.getText();
            if(newEmail.matches(AuthService.EMAIL_REG)){
                String newPwd = String.valueOf(newPwdPasswordField.getPassword());
                String newPwd2 = String.valueOf(againNewPwdPasswordField.getPassword());
                if(newPwd.equals(newPwd2)){
                    if(authService.editUserInfo(newEmail, newPwd)){
                        authService.loginOut(mainForm);
                        dispose();
                    }else {
                        JOptionPane.showMessageDialog(null, "帐号修改失败！");
                    }

                }else{
                    JOptionPane.showMessageDialog(null, "新密码与确认密码不一至！");
                }
            }else{
                JOptionPane.showMessageDialog(null, "新邮箱格式不正确！");
            }
        }else {
            JOptionPane.showMessageDialog(null, "旧密码不正确！");
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
