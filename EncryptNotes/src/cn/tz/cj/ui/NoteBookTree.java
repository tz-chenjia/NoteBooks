package cn.tz.cj.ui;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class NoteBookTree extends JTree {
    private static final String ROOTNODE_NAME = "NoteBooks";
    private static final Color SELECTED_BACKGROUD = new Color(115, 115, 115);
    private static final Color MOUSE_BACKGROUD = new Color(222, 222, 222);
    private static final int TREENODE_HIGH_LINE = 30;

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    private MainForm mainForm;
    private int mouseRow;

    public NoteBookTree(MainForm mainForm) {
        this.mainForm = mainForm;
        init();
    }

    private void init() {
        // 数据加载
        refresh(null, null, null);
        // 事件绑定
        bindEvent();
        // 树样式渲染
        this.setCellRenderer(new MyTreeCellRenderer());
        this.addFocusListener(fl);
        this.setOpaque(false);
        this.setRowHeight(TREENODE_HIGH_LINE);
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
    }

    public void refresh(String key, String lastSelectedNotebook, String lastSelectedNote) {
        loadTree(key, lastSelectedNotebook, lastSelectedNote);
        findInTree();// 设置自动选中
        mainForm.refreshNoteTools(); // 主页右边内容匹配显示
    }

    private void loadTree(String key, String lastSelectedNotebook, String lastSelectedNote) {
        Auth.getInstance().setSearchKey(key);
        Auth.getInstance().setSelectedNoteBookName(lastSelectedNotebook);
        Auth.getInstance().setSelectedNoteName(lastSelectedNote);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(ROOTNODE_NAME);
        List<NoteBook> noteBooks = noteBookService.getNoteBooks();
        Collections.sort(noteBooks);
        if (key == null || key.trim().equals("")) {
            for (NoteBook nb : noteBooks) {
                String notebook = nb.getNotebook();
                DefaultMutableTreeNode notebookNode = new DefaultMutableTreeNode(notebook);
                Set<String> notesTitlesByNoteBook = noteService.getNotesTitlesByNoteBook(notebook);
                for (String title : notesTitlesByNoteBook) {
                    DefaultMutableTreeNode noteNode = new DefaultMutableTreeNode(title);
                    notebookNode.add(noteNode);
                }
                rootNode.add(notebookNode);
            }
        } else {
            boolean isRecordFirstSelected = false;
            for (NoteBook nb : noteBooks) {
                String notebook = nb.getNotebook();
                DefaultMutableTreeNode notebookNode = new DefaultMutableTreeNode(notebook);
                Set<String> notesTitlesByNoteBook = noteService.getNotesTitlesByNoteBook(notebook);
                boolean match = false;
                for (String title : notesTitlesByNoteBook) {
                    String a = title.toLowerCase();
                    String b = key.toLowerCase();
                    if (a.contains(b)) {
                        DefaultMutableTreeNode noteNode = new DefaultMutableTreeNode(title);
                        if (!isRecordFirstSelected) {
                            if (lastSelectedNote == null && lastSelectedNotebook == null) {
                                Auth.getInstance().setSelectedNoteBookName(notebook);
                                Auth.getInstance().setSelectedNoteName(title);
                            }
                            isRecordFirstSelected = true;
                        }
                        notebookNode.add(noteNode);
                        match = true;
                    }
                }
                if (match || notebook.toLowerCase().contains(key.toLowerCase())) {
                    rootNode.add(notebookNode);
                }
            }
        }
        TreeModel treeModel = new DefaultTreeModel(rootNode);
        this.setModel(treeModel);
        //if (key != null && !key.trim().equals("")) {
        expandAll(this, new TreePath(rootNode), true);
        //}
    }

    private void bindEvent() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int y = (int) e.getPoint().getY();
                TreePath selPath = getPathForRow(matchRow(y));
                setSelected(selPath);
                if (e.getButton() == e.BUTTON3) {
                    if (selPath != null) {
                        int level = selPath.getPathCount();
                        String sel = selPath.getLastPathComponent().toString();
                        if (level == 2) {
                            noteBookMenu(sel).show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                        } else if (level == 3) {
                            Object[] path = selPath.getPath();
                            noteMenu(path[1].toString(), sel).show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                        }
                    }
                } else if (e.getButton() == e.BUTTON1) {
                    mouseClickSelected(selPath);
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent arg0) {
                int y = (int) arg0.getPoint().getY();
                mouseRow = matchRow(y);
                NoteBookTree.this.repaint();
            }
        });
    }

    private void mouseClickSelected(TreePath selPath) {
        if (selPath != null) {
           // mainForm.getEditor().autoSave();
            int level = selPath.getPathCount();
            Object[] path = selPath.getPath();
            switch (level) {
                case 2:
                    Auth.getInstance().setSelectedNoteBookName(path[1].toString());
                    Auth.getInstance().setSelectedNoteName(null);
                    break;
                case 3:
                    Auth.getInstance().setSelectedNoteBookName(path[1].toString());
                    Auth.getInstance().setSelectedNoteName(path[2].toString());
                    break;
            }
            if (this.isExpanded(selPath)) {
                this.collapsePath(selPath);
            } else {
                this.expandPath(selPath);
            }
            mainForm.refreshNoteTools();
        }
    }

    private int matchRow(int y) {
        int r = -1;
        for (int i = 0; i < this.getRowCount(); i++) {
            Rectangle rowBounds = this.getRowBounds(i);
            int y1 = (int) rowBounds.getY();
            int y2 = TREENODE_HIGH_LINE + (int) rowBounds.getY();
            if (y >= y1 && y <= y2) {
                r = i;
                break;
            }
        }
        return r;
    }

    private JPopupMenu treeMenu() {
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_addNotebook = new JMenuItem("新建笔记本");
        jMenuItem_addNotebook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddNotebook();
            }
        });
        treeJPopupMenu.add(jMenuItem_addNotebook);
        return treeJPopupMenu;
    }

    private JPopupMenu noteBookMenu(String notebookName) {
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_addNote = new JMenuItem("新建笔记");
        jMenuItem_addNote.setIcon(ImageIconMananger.NOTE.getImageIcon20_20());
        jMenuItem_addNote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddNote(notebookName);
            }
        });
        JMenuItem jMenuItem_rename = new JMenuItem("重命名");
        jMenuItem_rename.setIcon(ImageIconMananger.RENAME.getImageIcon20_20());
        jMenuItem_rename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = (String) JOptionPane.showInputDialog(null, "", "请输入笔记本的名称",
                        JOptionPane.QUESTION_MESSAGE, null, null, notebookName);
                noteBookService.rename(notebookName, newName);
                refresh(null, null, null);
            }
        });
        JMenuItem jMenuItem_remove = new JMenuItem("删除笔记本");
        jMenuItem_remove.setIcon(ImageIconMananger.DELETE.getImageIcon20_20());
        jMenuItem_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "确定删除[" + notebookName + "]？", "删除笔记本", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    noteBookService.removeNoteBook(notebookName);
                    refresh(null, null, null);
                }
            }
        });
        treeJPopupMenu.add(jMenuItem_addNote);
        treeJPopupMenu.add(jMenuItem_rename);
        treeJPopupMenu.add(jMenuItem_remove);
        return treeJPopupMenu;
    }

    private JPopupMenu noteMenu(String notebookName, String noteName) {
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_remove = new JMenuItem("删除笔记");
        jMenuItem_remove.setIcon(ImageIconMananger.DELETE.getImageIcon20_20());
        jMenuItem_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "确定删除[" + noteName + "]？", "删除笔记", JOptionPane.YES_NO_OPTION);
                if (i == 0) {
                    noteService.removeNote(notebookName, noteName);
                    refresh(null, null, null);
                }
            }
        });
        treeJPopupMenu.add(jMenuItem_remove);
        return treeJPopupMenu;
    }

    /**
     * 定位选中的笔记
     */
    private void findInTree() {
        if (Auth.getInstance().getSelectedNoteBookName() == null) {
            return;
        }
        Object root = this.getModel().getRoot();
        TreePath treePath = new TreePath(root);
        treePath = findInPath(treePath, Auth.getInstance().getSelectedNoteBookName(), Auth.getInstance().getSelectedNoteName());
        setSelected(treePath);
    }

    private void setSelected(TreePath treePath) {
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
        if (noteName != null && noteName.equals(value) && treePath.getParentPath().getLastPathComponent().toString().equals(notebookName)) {
            return treePath;
        } else if (noteName == null && notebookName.equals(value)) {
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
     *
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

    public void onAddNotebook() {
        String typeName = (String) JOptionPane.showInputDialog(null, "", "请输入笔记本的名称",
                JOptionPane.QUESTION_MESSAGE);
        if (typeName != null) {
            if (!typeName.equals("")) {
                if (typeName.length() <= 100) {
                    noteBookService.addNoteBook(typeName);
                    refresh(null, null, null);
                } else {
                    JOptionPane.showMessageDialog(null, "笔记本名称限制100字符！", "创建笔记本失败", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "笔记本名称不能为空！", "创建笔记本失败", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public void onAddNote(String notebookName) {
        new EditForm(mainForm);
    }

    FocusListener fl = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            e.getComponent().repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            e.getComponent().repaint();
        }
    };

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (mouseRow > 0 && mouseRow < this.getRowCount()) {
            g.setColor(MOUSE_BACKGROUD);
            System.out.println(mouseRow);
            Rectangle r = getRowBounds(mouseRow);
            g.fillRect(0, r.y, getWidth(), r.height);
            g.drawRect(0, r.y, getWidth() - 1, r.height - 1);
            MyTreeCellRenderer.mouseEnter = true;
            MyTreeCellRenderer.mouseRow = mouseRow;
        } else {
            MyTreeCellRenderer.mouseEnter = false;
        }

        if (getSelectionCount() > 0) {
            g.setColor(SELECTED_BACKGROUD);
            for (int i : getSelectionRows()) {
                Rectangle r = getRowBounds(i);
                g.fillRect(0, r.y, getWidth(), r.height);
            }
        }
        super.paintComponent(g);
        if (getLeadSelectionPath() != null) {
            Rectangle r = getRowBounds(getRowForPath(getLeadSelectionPath()));
            g.setColor(SELECTED_BACKGROUD);
            g.drawRect(0, r.y, getWidth() - 1, r.height - 1);
        }
    }
}

class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final Color SELECTED_BACKGROUD = new Color(115, 115, 115);
    private static final Color MOUSE_BACKGROUD = new Color(222, 222, 222);
    public static boolean mouseEnter;
    public static int mouseRow;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        //执行父类原型操作
        JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);
        if (mouseEnter && !selected && mouseRow == row) {
            l.setBackground(mouseEnter ? MOUSE_BACKGROUD : tree.getBackground());
        } else {
            l.setBackground(selected ? SELECTED_BACKGROUD : tree.getBackground());
        }

        l.setOpaque(true);
        setText(value.toString());
        setIcon(null);

        //得到每个节点的TreeNode
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        int level = node.getLevel();
        switch (level) {
            case 1:
                setFont(new Font("SimSun", Font.BOLD, 16));
                break;
            case 2:
                setFont(new Font("SimSun", Font.PLAIN, 14));
                break;
            default:
                break;
        }
        return this;
    }
}
