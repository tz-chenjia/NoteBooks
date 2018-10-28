package cn.tz.cj.ui;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.intf.INoteService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class EditForm extends JFrame {
    private final String URL = ConfigsService.getConfPath() + "summer" + File.separator + "index.html";

    private JPanel mainJPanel;
    private JComboBox notebookComboBox;
    private JTextField titleTextField;
    private JButton buttonCancel;
    private JButton buttonOK;
    private JPanel editJPanel;
    private JLabel errorLabel;

    private MouseLoading mouseLoading;
    private Editor editor;
    private NoteBookTree nbTree;
    private INoteService noteService = new NoteService();

    public EditForm(MainForm mainForm) {
        this.nbTree = mainForm.getTree();
        setTitle("NoteBooks - 编辑");
        setIconImage(ImageIconMananger.LOGO.getImage());
        initEditor(notebookComboBox, titleTextField);
        titleTextField.requestFocusInWindow();
        setContentPane(mainJPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension size = new Dimension();
        size.setSize(1200, 700);
        setPreferredSize(size);

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
            }
        });

        // call onCancel() on ESCAPE
        mainJPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        mainJPanel.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        mouseLoading = new MouseLoading(this);

        pack();
        setLocationRelativeTo(mainForm);
        setVisible(true);
    }

    private void onOK() {
        Object notebookObj = notebookComboBox.getSelectedItem();
        if (notebookObj == null || notebookObj.toString().trim().equals("")) {
            errorLabel.setText("请选择笔记本！");
            return;
        }
        String notebookName = notebookObj.toString().trim();
        String noteName = titleTextField.getText();
        if (noteName == null || noteName.trim().equals("")) {
            errorLabel.setText("笔记标题不能为空！");
            return;
        }
        if (noteService.checkTitleExists(notebookName, noteName)) {
            errorLabel.setText("笔记[" + noteName + "]已存在！");
            return;
        }
        mouseLoading.startLoading();
        editor.save();
        dispose();
        mouseLoading.stopLoading();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void initEditor(JComboBox notebook, JTextField note){
        editor = new Editor(nbTree, notebook, note);
        editJPanel.add(editor.getBrowserView());
        editor.initEditor(true);
        editor.refresh(Auth.getInstance().getSelectedNoteBookName(), "", "");
    }
}
