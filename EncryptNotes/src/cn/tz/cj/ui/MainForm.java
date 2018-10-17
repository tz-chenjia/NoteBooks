package cn.tz.cj.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

public class MainForm extends JFrame{
    private JTextField searchTextField;
    private JTree tree;
    private JEditorPane contentEditorPane;
    private JPanel mainJPanel;
    private JPanel leftJPanel;
    private JScrollPane treeJScrollPane;
    private JScrollPane contentJScrollPane;

    public MainForm(){
        setContentPane(mainJPanel);
        setSize(500, 500);
        setLocationRelativeTo(mainJPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                initTree();
            }
        });
    }

    private void initTree(){
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Notes");
        DefaultMutableTreeNode secondNode = new DefaultMutableTreeNode("笔记一");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("AAA");
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("BBB");
        secondNode.add(node1);
        secondNode.add(node2);
        rootNode.add(secondNode);
        TreeModel treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                TreePath selPath   =   tree.getPathForLocation(e.getX(),   e.getY());
                if(selPath != null){
                    int level = selPath.getPathCount();
                    //Object[] path = selPath.getPath();
                    if(e.isPopupTrigger()){
                        if(level == 1){
                            treeMenu().show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                        }else {
                            treeNodeMenu().show(e.getComponent(), e.getX(), e.getY());//弹出右键菜单
                        }

                    }
                }
            }
        });
    }

    private JPopupMenu treeMenu(){
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_addTitle = new JMenuItem("新建标题组");
        jMenuItem_addTitle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String typeName = (String)JOptionPane.showInputDialog(null,"","输入分类名称",
                        JOptionPane.QUESTION_MESSAGE);
                System.out.println(typeName);
            }
        });
        JMenuItem jMenuItem_addContent = new JMenuItem("新建内容");
        jMenuItem_addContent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditDialog.runEditDialog();
            }
        });
        treeJPopupMenu.add(jMenuItem_addContent);
        treeJPopupMenu.add(jMenuItem_addTitle);
        return treeJPopupMenu;
    }

    private JPopupMenu treeNodeMenu(){
        JPopupMenu treeJPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem_add = new JMenuItem("新增分类");
        jMenuItem_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("新增分类");
            }
        });
        JMenuItem jMenuItem_pwd = new JMenuItem("删除分类");
        jMenuItem_pwd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("删除分类");
            }
        });
        JMenuItem jMenuItem_fmt = new JMenuItem("重命名");
        jMenuItem_fmt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("重命名");
            }
        });
        treeJPopupMenu.add(jMenuItem_add);
        treeJPopupMenu.add(jMenuItem_pwd);
        treeJPopupMenu.add(jMenuItem_fmt);
        return treeJPopupMenu;
    }
}
