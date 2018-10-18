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
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class EditDialog extends JDialog {

    private final String INTERNAL_HTMLMODEL_URL = EditDialog.class.getResource("../resource/html/summer").getPath().substring(1);
    private final String CONFIGS_HTMLMODEL_URL = ConfigsService.getConfPath() + "summer";
    private final String URL = ConfigsService.getConfPath() + "summer"+ File.separator + "index.html";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox notebookComboBox;
    private JPanel editJPanel;
    private JTextField titleTextField;
    private JWebBrowser jWebBrowser;	//浏览器模型

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();
    private MainForm mainForm;

    public EditDialog(JFrame parentFrame, String notebook, String note) {
        mainForm = (MainForm)parentFrame;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize((int) (FormSetting.getWindowWidth() * 0.8), (int) (FormSetting.getWindowHeight() * 0.8));
        setLocationRelativeTo(contentPane);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(note);
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
                initNotebooks(notebook);
                initJWebBrowser(notebook, note);
            }


        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(String noteName) {
        int i = 0;
        String notebook = notebookComboBox.getSelectedItem().toString();
        String text = titleTextField.getText();
        String htmlContent = jWebBrowser.getHTMLContent();
        Document doc = Jsoup.parse(htmlContent);
        htmlContent = doc.select("div.note-editable").html();
        if(noteName != null){
            i = noteService.updateNote(notebook, noteName, htmlContent);
        }else{
            i = noteService.addNote(notebook, text, htmlContent);
        }
        if(i > 0){
            mainForm.initTree(null, notebook, text);
            mainForm.getjWebBrowser().setHTMLContent(htmlContent);
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void initNotebooks(String notebook){
        List<NoteBook> noteBooks = noteBookService.getNoteBooks();
        for(NoteBook nb : noteBooks){
            notebookComboBox.addItem(nb.getNotebook());
        }
        notebookComboBox.setSelectedItem(notebook);
    }

    private void initLocalHTMLModel(String notebookName, String noteName){
        if(!FileRWUtils.exists(URL)){
            FileRWUtils.copyFolder(INTERNAL_HTMLMODEL_URL, CONFIGS_HTMLMODEL_URL);
        }
        String htmlContent = FileRWUtils.read(new File(URL)).trim();
        htmlContent = htmlContent.substring(htmlContent.indexOf("doctype") - 2);
        Document doc = Jsoup.parse(htmlContent, "utf-8");
        Elements select = doc.select("div#summernote");
        if(noteName != null){
            Note note = noteService.getNote(notebookName, noteName);
            if(note != null){
                titleTextField.setText(noteName);
                titleTextField.setEditable(false);
                select.empty();
                select.append(note.getContent());
            }else{
                select.empty();
            }
        }else {
            select.empty();
        }
        FileRWUtils.write(new File(URL), doc.html());
    }

    private void initJWebBrowser(String notebookName, String noteName){
        initLocalHTMLModel(notebookName, noteName);
        jWebBrowser = new JWebBrowser();
        jWebBrowser.navigate(URL);
        jWebBrowser.setPreferredSize(new Dimension(800,400));
        jWebBrowser.setBarsVisible(false);
        jWebBrowser.setMenuBarVisible(false);
        jWebBrowser.setButtonBarVisible(false);
        jWebBrowser.setStatusBarVisible(false);
        editJPanel.add(jWebBrowser);
    }

    public static void runEditDialog(JFrame parentFrame, String notebook, String note){
        UIUtils.setPreferredLookAndFeel();
        if(!NativeInterface.isOpen()){
            NativeInterface.initialize();
            NativeInterface.open();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EditDialog editDialog = new EditDialog(parentFrame, notebook, note);
                    editDialog.pack();
                    editDialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
