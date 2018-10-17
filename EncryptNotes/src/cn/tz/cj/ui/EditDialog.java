package cn.tz.cj.ui;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditDialog extends JDialog {

    private final String URL = EditDialog.class.getResource("../resource/summer/index.html").getPath().substring(1);

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox titleComboBox;
    private JLabel titleLabel;
    private JPanel editJPanel;
    private JWebBrowser jWebBrowser;	//浏览器模型

    public EditDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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

            @Override
            public void windowOpened(WindowEvent e) {
                initJWebBrowser();
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
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void initJWebBrowser(){
        jWebBrowser = new JWebBrowser();
        jWebBrowser.navigate(URL);
        jWebBrowser.setPreferredSize(new Dimension(800,400));
        jWebBrowser.setBarsVisible(false);
        jWebBrowser.setMenuBarVisible(false);
        jWebBrowser.setButtonBarVisible(false);
        jWebBrowser.setStatusBarVisible(false);

        editJPanel.add(jWebBrowser);
    }

    public static void runEditDialog(){
        UIUtils.setPreferredLookAndFeel();
        if(!NativeInterface.isOpen()){
            NativeInterface.initialize();
            NativeInterface.open();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EditDialog editDialog = new EditDialog();
                    editDialog.pack();
                    editDialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
