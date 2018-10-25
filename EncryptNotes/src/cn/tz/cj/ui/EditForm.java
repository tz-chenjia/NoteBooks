package cn.tz.cj.ui;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import cn.tz.cj.entity.Note;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;
import cn.tz.cj.tools.FileRWUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class EditForm extends JFrame {
    private final String URL = ConfigsService.getConfPath() + "summer" + File.separator + "index.html";

    private JPanel mainJPanel;
    private JComboBox notebookComboBox;
    private JTextField titleTextField;
    private JButton buttonCancel;
    private JButton buttonOK;
    private JPanel editJPanel;
    private JLabel errorLabel;

    private JWebBrowser jWebBrowser;    //浏览器模型
    private NoteBookTree nbTree;
    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    public EditForm(MainForm mainForm, String notebookName, String noteName) {
        this.nbTree = mainForm.getTree();
        setTitle("NoteBooks - 编辑");
        setIconImage(ImageIconMananger.LOGO.getImage());
        initNotebooks(notebookName);
        initJWebBrowser(notebookName, noteName);
        titleTextField.requestFocusInWindow();
        setContentPane(mainJPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension size = new Dimension();
        size.setSize(1200, 700);
        setPreferredSize(size);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(notebookName, noteName);
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

        pack();
        setLocationRelativeTo(mainForm);
        setVisible(true);
    }

    private void onOK(String oldNotebookName, String oldNoteName) {
        Object notebookObj = notebookComboBox.getSelectedItem();
        if (notebookObj == null || notebookObj.toString().trim().equals("")) {
            errorLabel.setText("请选择笔记本！");
            return;
        }
        String notebookName = notebookObj.toString().trim();
        String text = titleTextField.getText();
        if (text == null || text.trim().equals("")) {
            errorLabel.setText("笔记标题不能为空！");
            return;
        }
        if (oldNoteName == null && noteService.checkTitleExists(notebookName, text)) {
            errorLabel.setText("笔记[" + text + "]已存在！");
            return;
        }
        String htmlContent = jWebBrowser.getHTMLContent();
        Document doc = Jsoup.parse(htmlContent);
        htmlContent = doc.select("div.note-editable").html();
        if (oldNoteName != null) {
            noteService.updateNote(oldNotebookName, oldNoteName, notebookName, text, htmlContent);
        } else {
            noteService.addNote(notebookName, text, htmlContent);
        }
        nbTree.refresh(null, notebookName, text);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void initNotebooks(String notebookName) {
        List<NoteBook> noteBooks = noteBookService.getNoteBooks();
        for (NoteBook nb : noteBooks) {
            notebookComboBox.addItem(nb.getNotebook());
        }
        if (notebookName != null) {
            notebookComboBox.setSelectedItem(notebookName);
        }
    }

    private void initLocalHTMLModel(String notebookName, String noteName) {
        String htmlContent = FileRWUtils.read(new File(URL)).trim();
        htmlContent = htmlContent.substring(htmlContent.indexOf("doctype") - 2);
        Document doc = Jsoup.parse(htmlContent, "utf-8");
        Elements select = doc.select("div#summernote");
        if (noteName != null) {
            Note note = noteService.getNote(notebookName, noteName);
            if (note != null) {
                titleTextField.setText(noteName);
                select.empty();
                select.append(note.getContent());
            } else {
                select.empty();
            }
        } else {
            select.empty();
        }
        FileRWUtils.write(new File(URL), doc.html());
    }

    private void initJWebBrowser(String notebookName, String noteName) {
        initLocalHTMLModel(notebookName, noteName);
        jWebBrowser = new JWebBrowser();
        jWebBrowser.navigate(URL);
        //jWebBrowser.setPreferredSize(new Dimension(800, 400));
        jWebBrowser.setBarsVisible(false);
        jWebBrowser.setMenuBarVisible(false);
        jWebBrowser.setButtonBarVisible(false);
        jWebBrowser.setStatusBarVisible(false);
        jWebBrowser.setDefaultPopupMenuRegistered(false);
        jWebBrowser.setFocusable(true);
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

        editJPanel.add(jWebBrowser);
    }
}
