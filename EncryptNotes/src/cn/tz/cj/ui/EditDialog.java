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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EditDialog extends JDialog {

    private final String URL = EditDialog.class.getResource("../resource/summer/index.html").getPath().substring(1);

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox notebookComboBox;
    private JPanel editJPanel;
    private JTextField titleTextField;
    private JWebBrowser jWebBrowser;	//浏览器模型

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    public EditDialog(String notebook, String note) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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

    private void onOK() {
        String notebook = notebookComboBox.getSelectedItem().toString();
        String text = titleTextField.getText();

        String htmlContent = jWebBrowser.getHTMLContent();
        Document doc = Jsoup.parse(htmlContent);
        htmlContent = doc.select("div.note-editable").html();
        int i = noteService.addNote(notebook, text, htmlContent);
        if(i > 0){
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

    private void initJWebBrowser(String notebook, String note){
        jWebBrowser = new JWebBrowser();
        jWebBrowser.navigate(URL);
        jWebBrowser.setPreferredSize(new Dimension(800,400));
        jWebBrowser.setBarsVisible(false);
        jWebBrowser.setMenuBarVisible(false);
        jWebBrowser.setButtonBarVisible(false);
        jWebBrowser.setStatusBarVisible(false);
        editJPanel.add(jWebBrowser);

        if(note != null){
            titleTextField.setText(note);
            Note n = noteService.getNote(notebook, note);
            String htmlContent = jWebBrowser.getHTMLContent();
            Document doc = Jsoup.parse(htmlContent);
            htmlContent = doc.select("div.note-editable").append(n.getContent()).html();
            jWebBrowser.setHTMLContent(htmlContent);
        }
    }

    public static void runEditDialog(String notebook, String note){
        UIUtils.setPreferredLookAndFeel();
        if(!NativeInterface.isOpen()){
            NativeInterface.initialize();
            NativeInterface.open();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    EditDialog editDialog = new EditDialog(notebook, note);
                    editDialog.pack();
                    editDialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
