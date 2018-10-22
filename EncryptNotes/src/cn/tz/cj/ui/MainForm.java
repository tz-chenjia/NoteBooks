package cn.tz.cj.ui;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import cn.tz.cj.bo.Auth;
import cn.tz.cj.entity.User;
import cn.tz.cj.service.*;
import cn.tz.cj.service.intf.IAuthService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.EncryptUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;

public class MainForm extends JFrame {
    private final String URL = EditDialog.class.getResource("../resource/html/summer/index.html").getPath().substring(1);

    private JTextField searchTextField;
    private JPanel mainJPanel;
    private JPanel leftJPanel;
    private JScrollPane treeJScrollPane;
    private JButton addNotebookBtn;
    private JButton addNoteBtn;
    private JButton loginOutBtn;
    private JPanel rightJPanel;
    private JPanel contentJPanel;
    private JLabel noteLabel;
    private JButton impDataBtn;
    private JButton expDataBtn;
    private JButton emailBackupBtn;
    private JButton delUserBtn;
    private JButton editUserBtn;
    private JButton aboutBtn;
    private JButton editNoteBtn;
    private JButton delNoteBtn;
    private JLabel notebookLabel;
    private NoteBookTree tree;
    private JWebBrowser jWebBrowser;    //浏览器模型

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();
    private ISystemService systemService = new SystemService();
    private IAuthService authService = new AuthService();

    public JWebBrowser getjWebBrowser() {
        return jWebBrowser;
    }

    public JLabel getNoteLabel() {
        return noteLabel;
    }

    public MainForm(String userName) {
        setTitle("NoteBooks - " + userName);
        setContentPane(mainJPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1000, 500);
        FormSetting.setFrameLocation(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ConfigsService.getImage("notebook.png"));
        pack();
        setVisible(true);
        initJWebBrowser();
        initTree();
        setBtnIcon();
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (NativeInterface.isOpen()) {
                    NativeInterface.close();
                }
                System.exit(1);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                searchTextField.requestFocusInWindow();
            }
        });
        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //initTree(searchTextField.getText(), null,null);
                    String text = searchTextField.getText();
                    if (text != null && !text.trim().equals("")) {
                        tree.refresh(text.trim(), null, null);
                    } else {
                        NoteBookTree.initTree(MainForm.this);
                    }
                }
            }
        });
        addNotebookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteBookTree.getInstance(MainForm.this).onAddNotebook();
            }
        });
        addNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NoteBookTree.getInstance(MainForm.this).onAddNote(null);
            }
        });
        loginOutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setNoteToolsVisible(false, "", "");
        notebookLabel.setOpaque(true);
        editNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String notebookName = notebookLabel.getText();
                String noteName = noteLabel.getText();
                EditDialog.runEditDialog(NoteBookTree.getInstance(MainForm.this), notebookName, noteName);
            }
        });
        delNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String notebookName = notebookLabel.getText();
                String noteName = noteLabel.getText();
                if (notebookName != null && noteName != null && !notebookName.equals("") && !noteName.equals("")) {
                    int i = JOptionPane.showConfirmDialog(null, "确定删除[" + noteName + "]？", "删除笔记", JOptionPane.YES_NO_OPTION);
                    if (i == 0) {
                        noteService.removeNote(notebookName, noteName);
                        NoteBookTree.initTree(MainForm.this);
                    }
                }
            }
        });
        expDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileChooser.expFileChooser();
                if(file != null){
                    systemService.expData(file);
                    JOptionPane.showMessageDialog(null,"导出成功\n"+file.getPath());
                }
            }
        });
        impDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "导入数据是覆盖该用户的所有数据，确认导入？", "导入数据", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    File file = FileChooser.impFileChooser();
                    if(file != null){
                        systemService.impData(file);
                        JOptionPane.showMessageDialog(null,"导入完成，请重新登录系统！");
                        authService.loginOut(MainForm.this);
                    }
                }
            }
        });
        delUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "删除帐号是删除该用户的所有数据，确认删除？", "删除帐号", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    systemService.deleteUser();
                    authService.loginOut(MainForm.this);
                }
            }
        });
        loginOutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authService.loginOut(MainForm.this);
            }
        });
        emailBackupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
    }

    public void setNoteToolsVisible(boolean isShow, String notebookName, String noteName) {
        editNoteBtn.setVisible(isShow);
        delNoteBtn.setVisible(isShow);
        noteLabel.setVisible(isShow);
        notebookLabel.setText(notebookName);
        noteLabel.setText(noteName);
    }

    private void setBtnIcon() {
        loginOutBtn.setIcon(new ImageIcon(ConfigsService.getImage("loginout.png")));
        editUserBtn.setIcon(new ImageIcon(ConfigsService.getImage("edituser.png")));
        delUserBtn.setIcon(new ImageIcon(ConfigsService.getImage("deluser.png")));
        emailBackupBtn.setIcon(new ImageIcon(ConfigsService.getImage("sendemail.png")));
        expDataBtn.setIcon(new ImageIcon(ConfigsService.getImage("expdata.png")));
        impDataBtn.setIcon(new ImageIcon(ConfigsService.getImage("impdata.png")));
        addNotebookBtn.setIcon(new ImageIcon(ConfigsService.getImage("addnotebook.png")));
        addNoteBtn.setIcon(new ImageIcon(ConfigsService.getImage("addnote.png")));
        aboutBtn.setIcon(new ImageIcon(ConfigsService.getImage("about.png")));
    }

    private void initTree() {
        tree = NoteBookTree.getInstance(this);
        treeJScrollPane.setViewportView(tree);
        NoteBookTree.initTree(this);
    }

    private void initJWebBrowser() {
        jWebBrowser = new JWebBrowser();
        jWebBrowser.setBarsVisible(false);
        jWebBrowser.setMenuBarVisible(false);
        jWebBrowser.setButtonBarVisible(false);
        jWebBrowser.setStatusBarVisible(false);
        jWebBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            @Override
            public void windowWillOpen(WebBrowserWindowWillOpenEvent e) {
                JWebBrowser newWebBrowser = e.getNewWebBrowser();
                newWebBrowser.addWebBrowserListener(new WebBrowserAdapter() {
                    @Override
                    public void locationChanging(WebBrowserNavigationEvent newEvent) {
                        // launch default OS browser
                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();

                            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                                try {
                                    desktop.browse(new URI(newEvent.getNewResourceLocation()));
                                } catch (Exception ex) {
                                }
                            }
                        }
                        newEvent.consume();

                        // immediately close the new swing window
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                newWebBrowser.getWebBrowserWindow().dispose();
                            }
                        });
                    }
                });
            }
        });
        contentJPanel.add(jWebBrowser);
    }
}
