package cn.tz.cj.ui;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.*;
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
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class EditDialog extends JDialog {

    private final String INTERNAL_HTMLMODEL_URL = EditDialog.class.getResource("../resource/html/summer").getPath().substring(1);
    private final String CONFIGS_HTMLMODEL_URL = ConfigsService.getConfPath() + "summer";
    private final String URL = ConfigsService.getConfPath() + "summer" + File.separator + "index.html";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox notebookComboBox;
    private JPanel editJPanel;
    private JTextField titleTextField;
    private JLabel errorLabel;
    private JWebBrowser jWebBrowser;    //浏览器模型
    private NoteBookTree nbTree;

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    public EditDialog(NoteBookTree nbTree, String notebookName, String noteName) {
        this.nbTree = nbTree;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize((int) (FormSetting.getWindowWidth() * 0.8), (int) (FormSetting.getWindowHeight() * 0.8));
        setLocationRelativeTo(contentPane);

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
                initNotebooks(notebookName);
                initJWebBrowser(notebookName, noteName);
                titleTextField.requestFocusInWindow();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(String oldNotebookName, String oldNoteName) {
        int i = 0;
        String notebookName = notebookComboBox.getSelectedItem().toString();
        if (notebookName == null || notebookName.trim().equals("")) {
            errorLabel.setText("请选择笔记本！");
            return;
        }
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
            i = noteService.updateNote(oldNotebookName, oldNoteName, notebookName, text, htmlContent);
        } else {
            i = noteService.addNote(notebookName, text, htmlContent);
        }
        nbTree.refresh(true, null, notebookName, text);
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
        if (!FileRWUtils.exists(URL)) {
            FileRWUtils.copyFolder(INTERNAL_HTMLMODEL_URL, CONFIGS_HTMLMODEL_URL);
        }
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
        jWebBrowser.setPreferredSize(new Dimension(800, 400));
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

    public static void runEditDialog(NoteBookTree nbTree, String notebook, String note) {
        UIUtils.setPreferredLookAndFeel();
        if (!NativeInterface.isOpen()) {
            NativeInterface.initialize();
            NativeInterface.open();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EditDialog editDialog = new EditDialog(nbTree, notebook, note);
                    editDialog.pack();
                    editDialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
