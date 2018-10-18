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
    private JTree tree;
    private JPanel mainJPanel;
    private JPanel leftJPanel;
    private JScrollPane treeJScrollPane;
    private JPanel rightJPanel;
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
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                initTree(null,null,null);
            }
        });
        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    initTree(searchTextField.getText(), null,null);
                }
            }
        });
    }

    private void initJWebBrowser(){
        jWebBrowser = new JWebBrowser();
        jWebBrowser.setBarsVisible(false);
        jWebBrowser.setMenuBarVisible(false);
        jWebBrowser.setButtonBarVisible(false);
        jWebBrowser.setStatusBarVisible(false);
        rightJPanel.add(jWebBrowser);
    }

    public void initTree(String key, String selectedParentNodeName ,String selectedNodeName){
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Notes");
        List<NoteBook> noteBooks = noteBookService.getNoteBooks();
        if(key == null || key.trim().equals("")){
            for(NoteBook nb : noteBooks){
                String notebook = nb.getNotebook();
                DefaultMutableTreeNode notebookNode = new DefaultMutableTreeNode(notebook);
                Set<String> notesTitlesByNoteBook = noteService.getNotesTitlesByNoteBook(notebook);
                for(String title : notesTitlesByNoteBook){
                    DefaultMutableTreeNode noteNode = new DefaultMutableTreeNode(title);
                    notebookNode.add(noteNode);
                }
                rootNode.add(notebookNode);
            }
        }else {
            for(NoteBook nb : noteBooks){
                String notebook = nb.getNotebook();
                DefaultMutableTreeNode notebookNode = new DefaultMutableTreeNode(notebook);
                Set<String> notesTitlesByNoteBook = noteService.getNotesTitlesByNoteBook(notebook);
                boolean match = false;
                for(String title : notesTitlesByNoteBook){
                    String a = title.toLowerCase();
                    String b = key.toLowerCase();
                    if(a.contains(b)){
                        DefaultMutableTreeNode noteNode = new DefaultMutableTreeNode(title);
                        notebookNode.add(noteNode);
                        match = true;
                    }
                }
                if(match || notebook.toLowerCase().contains(key.toLowerCase())){
                    rootNode.add(notebookNode);
                }
            }
        }
        TreeModel treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger()){
                    TreePath selPath   =   tree.getPathForLocation(e.getX(),   e.getY());
                    if(selPath != null){
                        int level = selPath.getPathCount();
                        String sel = selPath.getLastPathComponent().toString();
                        if(level == 1){
                            treeMenu().show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                        }else if(level == 2){
                            noteBookMenu(sel).show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                        }else if(level == 3){
                            Object[] path = selPath.getPath();
                            noteMenu(path[1].toString(),sel).show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                        }
                    }else {
                        treeMenu().show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                    }
                }else{
                    TreePath selPath   =   tree.getPathForLocation(e.getX(),   e.getY());
                    if(selPath != null){
                        int level = selPath.getPathCount();
                        Object[] path = selPath.getPath();
                        if(level == 3){
                            tree.setCellRenderer(new NodeRenderer(path[1].toString(),path[2].toString()));
                            Note note = noteService.getNote(path[1].toString(), path[2].toString());
                            jWebBrowser.setHTMLContent(note.getContent());
                        }
                    }
                }
            }
        });
        expandAll(tree, new TreePath(rootNode), true);
        if(selectedNodeName != null && !selectedNodeName.trim().equals("")){
            DefaultMutableTreeNode selectedNode = new DefaultMutableTreeNode(selectedNodeName);
            //DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            tree.setSelectionPath(new TreePath(selectedNode.getPath())); //选中该节点
            //tree.expandPath(selectedNode.getPath()); //展开该节点，对叶子节点无效
            tree.scrollPathToVisible(new TreePath(selectedNode.getPath())); //滚动Tree使该节点可见。
            tree.setCellRenderer(new NodeRenderer(selectedParentNodeName,selectedNodeName));
        }

    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    private JPopupMenu treeMenu(){
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_addNotebook = new JMenuItem("新建笔记本");
        jMenuItem_addNotebook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String typeName = (String)JOptionPane.showInputDialog(null,"","请输入笔记本的名称",
                        JOptionPane.QUESTION_MESSAGE);
                if(noteBookService.addNoteBook(typeName) >  0){
                    initTree(null,null,null);
                }
            }
        });
        treeJPopupMenu.add(jMenuItem_addNotebook);
        return treeJPopupMenu;
    }

    private JPopupMenu noteBookMenu(String sel){
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_addNote = new JMenuItem("新建笔记");
        jMenuItem_addNote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditDialog.runEditDialog(MainForm.this, sel, null);
            }
        });
        JMenuItem jMenuItem_rename = new JMenuItem("重命名");
        jMenuItem_rename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = (String)JOptionPane.showInputDialog(null,"","请输入笔记本的名称",
                        JOptionPane.QUESTION_MESSAGE);
                if(noteBookService.rename(sel, newName) >  0){
                    initTree(null,null,null);
                }
            }
        });
        JMenuItem jMenuItem_remove = new JMenuItem("删除笔记本");
        jMenuItem_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "确定删除["+sel+"]？", "删除笔记本", JOptionPane.YES_NO_OPTION);
                if(i == 0){
                    if(noteBookService.removeNoteBook(sel) > 0){
                        initTree(null,null,null);
                    }
                }
            }
        });
        treeJPopupMenu.add(jMenuItem_addNote);
        treeJPopupMenu.add(jMenuItem_rename);
        treeJPopupMenu.add(jMenuItem_remove);
        return treeJPopupMenu;
    }

    private JPopupMenu noteMenu(String notebook, String sel){
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_updateNote = new JMenuItem("修改笔记");
        jMenuItem_updateNote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditDialog.runEditDialog(MainForm.this,notebook, sel);
            }
        });
        JMenuItem jMenuItem_remove = new JMenuItem("删除笔记");
        jMenuItem_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("删除笔记");
            }
        });
        treeJPopupMenu.add(jMenuItem_updateNote);
        treeJPopupMenu.add(jMenuItem_remove);
        return treeJPopupMenu;
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
