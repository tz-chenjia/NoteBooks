package cn.tz.cj.ui;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.entity.Note;
import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.service.AuthService;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.SystemService;
import cn.tz.cj.service.intf.IAuthService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.email.SimpleMailSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainForm extends JFrame {

    private JTextField searchTextField;
    private JPanel mainJPanel;
    private JPanel leftJPanel;
    private JScrollPane treeJScrollPane;
    private JButton addNotebookBtn;
    private JButton addNoteBtn;
    private JButton loginOutBtn;

    private JButton impDataBtn;
    private JButton expDataBtn;
    private JButton emailBackupBtn;
    private JButton delUserBtn;
    private JButton editUserBtn;
    private JButton aboutBtn;
    private NoteBookTree tree;
    private JButton recoverBtn;
    private JToolBar.Separator recoverSeparator;
    private JPanel rightMainJPanel;
    private JButton delNoteBtn;

    //公共组件
    private JPanel rightJPanel;
    private JPanel rightHeaderJPanel;
    private JComboBox noteBookComboBox;
    private JTextField noteTextField;
    private JButton saveBtn;
    private javax.swing.JToolBar JToolBar;

    private MouseLoading mouseLoading;
    private Editor editor;
    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();
    private ISystemService systemService = new SystemService();
    private IAuthService authService = new AuthService();

    public MainForm(UserConfigs userConfigs) {
        if(userConfigs != null){
            setTitle("NoteBooks - " + userConfigs.getUserEmail() + " - " + userConfigs.getDbType() + " - " + userConfigs.getDbHost() + " : " + userConfigs.getDbPort());
        }else {
            setTitle("NoteBooks");
        }
        setIconImage(ImageIconMananger.LOGO.getImage());
        setContentPane(mainJPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);//最大化
        Dimension size = new Dimension();
        size.setSize(1300, 800);
        setPreferredSize(size);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        initTree();
        initEditor(noteBookComboBox,noteTextField);
        setBtnIcon();
        refreshNoteTools();
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "感谢您的使用，确定退出 NoteBooks？", "退出确认", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    System.exit(0);
                }
            }

            @Override
            public void windowOpened(WindowEvent e) {
                searchTextField.requestFocusInWindow();
            }
        });
        JToolBar.setFloatable( false );//不能拖动
        searchTextField.setDocument(new InputLengthLimit(100));
        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    mouseLoading.startLoading();
                    String text = searchTextField.getText();
                    tree.refresh(text.trim(), null, null);
                    mouseLoading.stopLoading();
                }
            }
        });
        addNotebookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                tree.onAddNotebook();
                mouseLoading.stopLoading();
            }
        });
        addNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.onAddNote(Auth.getInstance().getSelectedNoteBookName());
            }
        });
        delNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                String notebookName = noteBookComboBox.getSelectedItem().toString();
                String noteName = noteTextField.getText();
                if (notebookName != null && noteName != null && !notebookName.equals("") && !noteName.equals("")) {
                    int i = JOptionPane.showConfirmDialog(null, "确定删除[" + noteName + "]？", "删除笔记", JOptionPane.YES_NO_OPTION);
                    if (i == 0) {
                        noteService.removeNote(notebookName, noteName);
                        tree.refresh(null, null, null);
                    }
                }
                mouseLoading.stopLoading();
            }
        });
        expDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                File file = FileChooser.expFileChooser();
                if (file != null) {
                    systemService.expData(file);
                    JOptionPane.showMessageDialog(null, "导出成功\n" + file.getPath());
                }
                mouseLoading.stopLoading();
            }
        });
        impDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                int i = JOptionPane.showConfirmDialog(null, "导入数据是覆盖该用户的所有数据，确认导入？", "导入数据", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    File file = FileChooser.impFileChooser();
                    if (file != null) {
                        systemService.impData(file);
                        JOptionPane.showMessageDialog(null, "导入完成，请重新登录系统！");
                        authService.loginOut(false);
                        dispose();
                    }
                }
                mouseLoading.stopLoading();
            }
        });
        delUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                int i = JOptionPane.showConfirmDialog(null, "删除帐号是删除该用户的所有数据，确认删除？", "删除帐号", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    systemService.deleteUser();
                    authService.loginOut(false);
                    dispose();
                }
                mouseLoading.stopLoading();
            }
        });
        loginOutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                systemService.tempSaveDataToLocal(); //关闭之前自动备份到本地
                authService.loginOut(true);
                dispose();
                mouseLoading.stopLoading();
            }
        });
        emailBackupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                Object o = JOptionPane.showInputDialog(null, "请输入您的邮箱", "邮箱备份",
                        JOptionPane.QUESTION_MESSAGE, null, null, userConfigs.getUserEmail());
                if (o != null) {
                    String email = o.toString();
                    if (email.matches(AuthService.EMAIL_REG)) {
                        File file = systemService.tempSaveDataToLocal();
                        Map<String, File> files = new HashMap<String, File>();
                        files.put("notebooks.sql", file);
                        boolean b = SimpleMailSender.sendMail(email, "【NoteBooks】", "数据备份文件已存放在附件中，请注意查收，祝您生活愉快！", files);
                        if (b) {
                            JOptionPane.showMessageDialog(null, "邮件已发送，请注意查收！");
                        } else {
                            JOptionPane.showMessageDialog(null, "邮件发送失败，请检查网络问题！");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "发送失败，邮箱格式不正确！");
                    }
                }
                mouseLoading.stopLoading();
            }
        });
        editUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditUserDialog(MainForm.this);
            }
        });
        aboutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "作者：CJ\nQQ：1014376159\nEmail：1014376159@qq.com");
            }
        });
        recoverBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                int i = JOptionPane.showConfirmDialog(null, "紧急恢复数据，一般发生系统错误后再使用，该操作存在风险！\n数据将恢复至上一次手动关闭之前的数据，确认恢复？", "紧急恢复", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    systemService.impData(SystemService.getTempDataFile());
                    JOptionPane.showMessageDialog(null, "数据已恢复，请重新登录系统！");
                    authService.loginOut(false);
                    dispose();
                }
                mouseLoading.stopLoading();
            }
        });
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                editor.save();
                mouseLoading.stopLoading();
            }
        });
        rightJPanel.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mouseLoading.startLoading();
                editor.save();
                mouseLoading.stopLoading();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        mouseLoading = new MouseLoading(this);

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public NoteBookTree getTree() {
        return tree;
    }

    public Editor getEditor() {
        return editor;
    }

    public MouseLoading getMouseLoading() {
        return mouseLoading;
    }

    /**
     * 刷新右边内容区域以及操作按钮
     */
    public void refreshNoteTools() {
        if (Auth.getInstance().getSelectedNoteBookName() != null && Auth.getInstance().getSelectedNoteName() != null) {
            rightHeaderJPanel.setVisible(true);
            rightMainJPanel.setVisible(true);
            Note note = noteService.getNote(Auth.getInstance().getSelectedNoteBookName(), Auth.getInstance().getSelectedNoteName());
            if (note != null) {
                editor.refresh(note.getNotebook(), note.getTitle(), note.getContent());
            }
        } else {
            rightHeaderJPanel.setVisible(false);
            rightMainJPanel.setVisible(false);
//            editor.refresh("", "", "");
        }
    }

    private void setBtnIcon() {
        loginOutBtn.setIcon(ImageIconMananger.LOGINOUT.getImageIcon20_20());
        editUserBtn.setIcon(ImageIconMananger.EDITUSER.getImageIcon20_20());
        delUserBtn.setIcon(ImageIconMananger.DELETE.getImageIcon20_20());
        emailBackupBtn.setIcon(ImageIconMananger.BACKUP.getImageIcon20_20());
        expDataBtn.setIcon(ImageIconMananger.EXP.getImageIcon20_20());
        impDataBtn.setIcon(ImageIconMananger.IMP.getImageIcon20_20());
        addNotebookBtn.setIcon(ImageIconMananger.NOTEBOOK.getImageIcon20_20());
        addNoteBtn.setIcon(ImageIconMananger.NOTE.getImageIcon20_20());
        aboutBtn.setIcon(ImageIconMananger.ABOUT.getImageIcon20_20());
        recoverBtn.setIcon(ImageIconMananger.RECOVER.getImageIcon20_20());
        //delNoteBtn.setIcon(ImageIconMananger.DELETE.getImageIcon20_20());
    }

    private void initTree() {
        tree = new NoteBookTree(this);
        treeJScrollPane.setViewportView(tree);
    }

    private void initEditor(JComboBox noteBook, JTextField note){
        editor = new Editor(tree, noteBook, note);
        rightMainJPanel.add(editor.getBrowserView());
        editor.initEditor(false);
    }
}
