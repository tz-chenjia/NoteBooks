package cn.tz.cj.ui;

import cn.tz.cj.service.AuthService;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.intf.IAuthService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton configBtn;
    private JTextField emailTextField;
    private JPasswordField pwdPasswordField;
    private JLabel emailLabel;
    private JLabel pwdLabel;

    private IAuthService authService = new AuthService();

    public LoginDialog() {
        setTitle("NoteBooks - 登录");
        setIconImage(ConfigsService.getImage("notebook.png"));
        setSize(400, 400);
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
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

    private void onOK() {
        if (authService.login(emailTextField.getText(), String.valueOf(pwdPasswordField.getPassword()))) {
            new MainForm(emailTextField.getText());
            dispose();
        }
    }

    public static void main(String[] args) {
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
