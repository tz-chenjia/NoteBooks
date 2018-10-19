package cn.tz.cj.ui;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import cn.tz.cj.bo.Auth;
import cn.tz.cj.entity.Note;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class NoteBookTree extends JTree {
    private static final String ROOTNODE_NAME = "Notes";
    private static NoteBookTree nbTree;
    private JWebBrowser jWebBrowser;	//浏览器模型
    private NoteBookTree(){
    }
    public static NoteBookTree getInstance(JWebBrowser jWebBrowser){
        if (nbTree == null) {
            synchronized (Auth.class) {
                if (nbTree == null) {
                    nbTree = new NoteBookTree();
                    nbTree.bindEvent(); //监听事件是在初始化时绑定一次
                    nbTree.jWebBrowser = jWebBrowser;
                }
            }
        }
        return nbTree;
    }

    private String key;
    private String lastSelectedNotebook;
    private String lastSelectedNote;

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    public void refresh(boolean isAll, String key, String lastSelectedNotebook, String lastSelectedNote){
        this.key = key;
        setLastSelected(lastSelectedNotebook, lastSelectedNote);
        if(isAll){
            loadTree(null);
        }else {
            loadTree(key);
        }
        if(this.lastSelectedNotebook != null && this.lastSelectedNote != null){
            // 自动显示对应内容
            Note note = noteService.getNote(this.lastSelectedNotebook, this.lastSelectedNote);
            jWebBrowser.setHTMLContent(note.getContent());
        }
    }

    private void setLastSelected(String lastSelectedNotebook, String lastSelectedNote){
        if(lastSelectedNotebook != null && lastSelectedNote != null){
            this.lastSelectedNotebook = lastSelectedNotebook;
            this.lastSelectedNote = lastSelectedNote;
        }
    }

    private void loadTree(String key){
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(ROOTNODE_NAME);
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
            boolean isRecordFirstSelected = false;
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
                        if(!isRecordFirstSelected){
                            lastSelectedNote = title;
                            lastSelectedNotebook = notebook;
                            isRecordFirstSelected = true;
                        }
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
        this.setModel(treeModel);
        treeRender(rootNode);
    }
    int i;
    private void bindEvent(){
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath selPath   =   NoteBookTree.this.getPathForLocation(e.getX(),   e.getY());
                if(e.getButton() == e.BUTTON3){
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
                }else if(e.getButton() == e.BUTTON1){
                    if(selPath != null){
                        int level = selPath.getPathCount();
                    Object[] path = selPath.getPath();
                    if(level == 3){
                        refresh(true, key, path[1].toString(), path[2].toString());
                    }
                }
                }
            }
        });
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
                   refresh(true,null,null,null);
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
                EditDialog.runEditDialog(NoteBookTree.this, sel, null);
            }
        });
        JMenuItem jMenuItem_rename = new JMenuItem("重命名");
        jMenuItem_rename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = (String)JOptionPane.showInputDialog(null,"","请输入笔记本的名称",
                        JOptionPane.QUESTION_MESSAGE);
                if(noteBookService.rename(sel, newName) >  0){
                    //initTree(null,null,null);
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
                        //initTree(null,null,null);
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
                //EditDialog.runEditDialog(MainForm.this,notebook, sel);
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

    private void treeRender(DefaultMutableTreeNode rootNode){
        //expandAll(this, new TreePath(rootNode), true);
        // 设置自动选中
        if(this.lastSelectedNotebook != null && this.lastSelectedNote != null){
            findInTree(this.lastSelectedNotebook, this.lastSelectedNote);
        }
        /*if(selectedNodeName != null && !selectedNodeName.trim().equals("")){
            DefaultMutableTreeNode selectedNode = new DefaultMutableTreeNode(selectedNodeName);
            //DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            tree.setSelectionPath(new TreePath(selectedNode.getPath())); //选中该节点
            //tree.expandPath(selectedNode.getPath()); //展开该节点，对叶子节点无效
            tree.scrollPathToVisible(new TreePath(selectedNode.getPath())); //滚动Tree使该节点可见。
            tree.setCellRenderer(new NodeRenderer(selectedParentNodeName,selectedNodeName));
        }*/
    }

    private void findInTree(String notebookName, String noteName) {
        Object root = this.getModel().getRoot();
        TreePath treePath = new TreePath(root);
        treePath = findInPath(treePath, notebookName, noteName);
        if (treePath != null) {
            this.setSelectionPath(treePath);
            this.scrollPathToVisible(treePath);
        }
    }

    private TreePath findInPath(TreePath treePath, String notebookName, String noteName) {
        Object object = treePath.getLastPathComponent();
        if (object == null) {
            return null;
        }
        String value = object.toString();
        if (noteName.equals(value) && treePath.getParentPath().getLastPathComponent().toString().equals(notebookName)) {
            return treePath;
        } else {
            TreeModel model = this.getModel();
            int n = model.getChildCount(object);
            for (int i = 0; i < n; i++) {
                Object child = model.getChild(object, i);
                TreePath path = treePath.pathByAddingChild(child);

                path = findInPath(path, notebookName, noteName);
                if (path != null) {
                    return path;
                }
            }
            return null;
        }
    }

    /**
     * 全部展开
     * @param tree
     * @param parent
     * @param expand
     */
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

}