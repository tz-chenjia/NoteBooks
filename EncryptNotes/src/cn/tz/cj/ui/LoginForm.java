package cn.tz.cj.ui;

import cn.tz.cj.service.AuthService;
import cn.tz.cj.service.intf.IAuthService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame{
    public static void main(String[] args) {
        new LoginForm();
    }

    public LoginForm(){
        setTitle("Secret");
        setContentPane(mainJPanel);
        setSize(500, 500);
        setLocationRelativeTo(mainJPanel);
        FormSetting.setFrameLocation(this);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        configBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBConfigsDialog dbConfigsDialog = new DBConfigsDialog();
                dbConfigsDialog.pack();
                dbConfigsDialog.setVisible(true);
            }
        });
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IAuthService authService = new AuthService();
                if(authService.login(mailTextField.getText(), String.valueOf(pwdPasswordField.getPassword()))){
                    MainForm.runMainForm();
                    dispose();
                }
            }
        });
    }

    private JPanel mainJPanel;
    private JTextField mailTextField;
    private JPasswordField pwdPasswordField;
    private JButton configBtn;
    private JButton loginBtn;
    private JLabel mailLabel;
    private JLabel pwdLabel;
}
