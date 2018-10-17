package cn.tz.cj.ui;

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
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainForm();
                dispose();
            }
        });
        configBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBConfigsDialog dbConfigsDialog = new DBConfigsDialog();
                dbConfigsDialog.pack();
                dbConfigsDialog.setVisible(true);
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
