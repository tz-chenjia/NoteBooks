package cn.tz.cj.ui;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import cn.tz.cj.bo.Auth;
import cn.tz.cj.entity.Note;
import cn.tz.cj.service.*;
import cn.tz.cj.service.intf.IAuthService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.FileRWUtils;
import cn.tz.cj.tools.email.SimpleMailSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MainForm extends JFrame {
    private static final String URL = EditDialog.class.getResource("../resource/html/summer/index.html").getPath().substring(1);
    private static final String TEMPDATAFILE = System.getProperty("user.dir") + File.separator + "data" + File.separator + "notebooks.sql";

    private JTextField searchTextField;
    private JPanel mainJPanel;
    private JPanel leftJPanel;
    private JScrollPane treeJScrollPane;
    private JButton addNotebookBtn;
    private JButton addNoteBtn;
    private JButton loginOutBtn;
    private JPanel rightJPanel;
    private JPanel contentJPanel;
    private JButton impDataBtn;
    private JButton expDataBtn;
    private JButton emailBackupBtn;
    private JButton delUserBtn;
    private JButton editUserBtn;
    private JButton aboutBtn;
    private NoteBookTree tree;

    //公共组件
    public JButton editNoteBtn;
    public JButton delNoteBtn;
    public JLabel noteLabel;
    public JLabel notebookLabel;
    public JWebBrowser jWebBrowser;    //浏览器模型

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();
    private ISystemService systemService = new SystemService();
    private IAuthService authService = new AuthService();
    private Auth auth = Auth.getInstance();

    public JWebBrowser getjWebBrowser() {
        return jWebBrowser;
    }

    public JLabel getNoteLabel() {
        return noteLabel;
    }

    public MainForm(String userName) {
        setTitle("NoteBooks - " + userName);
        setIconImage(ConfigsService.getImage("notebook.png"));
        setContentPane(mainJPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);//最大化
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        initJWebBrowser();
        initTree();
        setBtnIcon();
        refreshNoteTools();
        notebookLabel.setOpaque(true);
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
                    String text = searchTextField.getText();
                    if (text != null && !text.trim().equals("")) {
                        tree.refresh(text.trim(), null, null);
                    } else {
                        tree.refresh(null, null, null);
                    }
                }
            }
        });
        addNotebookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.onAddNotebook();
            }
        });
        addNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.onAddNote(null);
            }
        });
        loginOutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        editNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String notebookName = notebookLabel.getText();
                String noteName = noteLabel.getText();
                EditDialog.runEditDialog(tree, notebookName, noteName);
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
                        tree.refresh(null, null, null);
                    }
                }
            }
        });
        expDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = FileChooser.expFileChooser();
                if (file != null) {
                    systemService.expData(file);
                    JOptionPane.showMessageDialog(null, "导出成功\n" + file.getPath());
                }
            }
        });
        impDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "导入数据是覆盖该用户的所有数据，确认导入？", "导入数据", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    File file = FileChooser.impFileChooser();
                    if (file != null) {
                        systemService.impData(file);
                        JOptionPane.showMessageDialog(null, "导入完成，请重新登录系统！");
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
                Object o = JOptionPane.showInputDialog(null, "请输入您的邮箱", "邮箱备份",
                        JOptionPane.QUESTION_MESSAGE, null, null, userName);
                if (o != null) {
                    String email = o.toString();
                    if (email.matches(AuthService.EMAIL_REG)) {
                        FileRWUtils.existsAndCreate(TEMPDATAFILE);
                        Map<String, File> files = new HashMap<String, File>();
                        File file = new File(TEMPDATAFILE);
                        files.put("notebooks.sql",file);
                        systemService.expData(file);
                        boolean b = SimpleMailSender.sendMail(email, "【NoteBooks】", "数据备份文件已存放在附件中，请注意查收，祝您生活愉快！", files);
                        if(b){
                            JOptionPane.showMessageDialog(null, "邮件已发送，请注意查收！");
                        }else{
                            JOptionPane.showMessageDialog(null, "邮件发送失败，请检查网络问题！");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "发送失败，邮箱格式不正确！");
                    }
                }

            }
        });
        editUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditUserDialog editUserForm = new EditUserDialog(MainForm.this);
                editUserForm.setVisible(true);
                editUserForm.pack();
            }
        });
    }

    /**
     * 刷新右边内容区域以及操作按钮
     */
    public void refreshNoteTools() {
        if (auth.getSelectedNoteBookName() != null && auth.getSelectedNoteName() != null) {
            editNoteBtn.setVisible(true);
            delNoteBtn.setVisible(true);
            noteLabel.setVisible(true);
            Note note = noteService.getNote(auth.getSelectedNoteBookName(), auth.getSelectedNoteName());
            if (note != null) {
                notebookLabel.setText(note.getNotebook());
                noteLabel.setText(note.getTitle());
                jWebBrowser.setHTMLContent(note.getContent());
            }
        } else {
            editNoteBtn.setVisible(false);
            delNoteBtn.setVisible(false);
            noteLabel.setVisible(false);
            notebookLabel.setText("");
            noteLabel.setText("");
            jWebBrowser.setHTMLContent("");
        }
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
        tree = new NoteBookTree(this);
        treeJScrollPane.setViewportView(tree);
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
