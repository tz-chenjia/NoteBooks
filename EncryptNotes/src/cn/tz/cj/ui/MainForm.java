package cn.tz.cj.ui;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

public class MainForm extends JFrame{
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
    private NoteBookTree tree;
    private JWebBrowser jWebBrowser;	//浏览器模型

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    public JWebBrowser getjWebBrowser() {
        return jWebBrowser;
    }

    public JLabel getNoteLabel() {
        return noteLabel;
    }

    public MainForm(){
        setTitle("NoteBooks");
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
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    //initTree(searchTextField.getText(), null,null);
                    String text = searchTextField.getText();
                    if(text != null && !text.trim().equals("")){
                        tree.refresh(text.trim(),null,null);
                    }else {
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
    }

    private void setBtnIcon(){
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

    private void initTree(){
        tree = NoteBookTree.getInstance(this);
        treeJScrollPane.setViewportView(tree);
        NoteBookTree.initTree(this);
    }

    private void initJWebBrowser(){
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
                                } catch (Exception ex) {}
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

    public static void runMainForm(){
        UIUtils.setPreferredLookAndFeel();
        if(!NativeInterface.isOpen()){
            NativeInterface.initialize();
            NativeInterface.open();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
