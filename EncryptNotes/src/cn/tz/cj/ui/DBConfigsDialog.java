package cn.tz.cj.ui;

import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.rule.EDBType;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.tools.EncryptUtils;

import javax.swing.*;
import java.awt.event.*;

public class DBConfigsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox dbTypeComboBox;
    private JTextField IPTextField;
    private JTextField portTextField;
    private JTextField dbNameTextField;
    private JTextField dbUserNameTextField;
    private JPasswordField dbPwdPasswordField;
    private JLabel dbTypeLabel;
    private JLabel IPLabel;
    private JLabel portLabel;
    private JLabel dbName;
    private JLabel dbUserNameLabel;
    private JLabel dbPwdLabel;

    private IConfigsService configsService = new ConfigsService();

    public DBConfigsDialog() {
        setTitle("NoteBooks - 配置");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(500, 500);
        setLocationRelativeTo(contentPane);
        setIconImage(ConfigsService.getImage("notebook.png"));

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
            @Override
            public void windowOpened(WindowEvent e) {
                UserConfigs userConfigs = configsService.getUserConfigs();
                if(userConfigs != null){
                    dbTypeComboBox.setSelectedItem(userConfigs.getDbType());
                    IPTextField.setText(userConfigs.getDbHost());
                    portTextField.setText(userConfigs.getDbPort());
                    dbNameTextField.setText(userConfigs.getDbName());
                    dbUserNameTextField.setText(userConfigs.getDbUserName());
                    dbPwdPasswordField.setText(userConfigs.getDbPassword());
                }
            }

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
        UserConfigs userConfigs = checkInputAndBuild();
        if(userConfigs != null){
            configsService.saveUserConfigs(userConfigs);
            dispose();
        }
    }

    private UserConfigs checkInputAndBuild(){
        String dbType = dbTypeComboBox.getSelectedItem().toString();
        String dbHost = IPTextField.getText().trim();
        if(dbHost == null || dbHost.equals("")){
            JOptionPane.showMessageDialog(null, "主机名或IP地址不能为空！", "配置设置失败", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String dbPort = portTextField.getText().trim();
        if(dbPort == null || dbPort.equals("")){
            JOptionPane.showMessageDialog(null, "端口不能为空！", "配置设置失败", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String dbName = dbNameTextField.getText().trim();
        if(dbName == null || dbName.equals("")){
            JOptionPane.showMessageDialog(null, "库名不能为空！", "配置设置失败", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String dbUserName = dbUserNameTextField.getText().trim();
        if(dbUserName == null || dbUserName.equals("")){
            JOptionPane.showMessageDialog(null, "用户名不能为空！", "配置设置失败", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String dbPassword = String.valueOf(dbPwdPasswordField.getPassword());
        UserConfigs userConfigs = new UserConfigs();
        userConfigs.setDbType(dbType);
        userConfigs.setDbName(dbName);
        userConfigs.setDbDriverClass(EDBType.toEDBType(dbType).getDriverClass());
        userConfigs.setDbHost(dbHost);
        userConfigs.setDbPort(dbPort);
        userConfigs.setDbUserName(dbUserName);
        userConfigs.setDbPassword(dbPassword);
        return userConfigs;
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
