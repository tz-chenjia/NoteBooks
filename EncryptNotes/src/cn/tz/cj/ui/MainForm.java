package cn.tz.cj.ui;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import cn.tz.cj.entity.Note;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class MainForm extends JFrame{

    private final String URL = EditDialog.class.getResource("../resource/html/summer/index.html").getPath().substring(1);

    private JTextField searchTextField;
    private JPanel mainJPanel;
    private JPanel leftJPanel;
    private JScrollPane treeJScrollPane;
    private JPanel rightJPanel;
    private NoteBookTree tree;
    private JWebBrowser jWebBrowser;	//浏览器模型

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    public JWebBrowser getjWebBrowser() {
        return jWebBrowser;
    }

    public MainForm(){
        setContentPane(mainJPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1000, 500);
        FormSetting.setFrameLocation(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        initJWebBrowser();
        initTree();
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }

            @Override
            public void windowOpened(WindowEvent e) {
            }
        });
        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    //initTree(searchTextField.getText(), null,null);
                    String text = searchTextField.getText();
                    if(text != null && text.trim() != ""){
                        tree.refresh(false, text.trim(),null,null);
                    }
                }
            }
        });
    }

    private void initTree(){
        tree = NoteBookTree.getInstance(jWebBrowser);
        treeJScrollPane.setViewportView(tree);
        tree.refresh(true, null,null,null);
    }

    private void initJWebBrowser(){
        jWebBrowser = new JWebBrowser();
        jWebBrowser.setBarsVisible(false);
        jWebBrowser.setMenuBarVisible(false);
        jWebBrowser.setButtonBarVisible(false);
        jWebBrowser.setStatusBarVisible(false);
        rightJPanel.add(jWebBrowser);
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

    class NodeRenderer extends DefaultTreeCellRenderer {

        private String parentKey;
        private String key;

        public NodeRenderer(String parentKey,String key){
            this.parentKey = parentKey;
            this.key = key;
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
            super.getTreeCellRendererComponent(tree, value, selected, expanded,
                    leaf, row, hasFocus);
            if (node.getUserObject().toString().trim().equals(key) && parent.getUserObject().toString().trim().equals(parentKey)) {

                setForeground(Color.BLUE);
                setTextSelectionColor(Color.WHITE);
                setBackgroundSelectionColor(Color.WHITE);
                setBackgroundNonSelectionColor(Color.WHITE);

            }
            return this;
        }
    }
}
