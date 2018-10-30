package cn.tz.cj.ui;

import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultPopupHandler;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.List;

public class Editor {

    private static final String URL = ConfigsService.getConfPath() + "summer" + File.separator + "index.html";

    private Browser browser = new Browser();

    private BrowserView browserView = new BrowserView(browser);

    private NoteBookTree nbTree;

    private JComboBox noteBook;

    private JTextField note;

    private INoteBookService noteBookService = new NoteBookService();
    private INoteService noteService = new NoteService();

    private boolean isNewAdd;
    private String oldNoteBookName;
    private String oldNoteName;

    public Editor(NoteBookTree nbTree, JComboBox noteBook, JTextField note) {
        this.nbTree = nbTree;
        this.noteBook = noteBook;
        this.note = note;
    }

    public Browser getBrowser() {
        return browser;
    }

    public BrowserView getBrowserView() {
        return browserView;
    }

    public void initEditor(boolean isNewAdd) {
        this.isNewAdd = isNewAdd;
        this.browserView.setDragAndDropEnabled(false);
        this.browser.setPopupHandler(new DefaultPopupHandler() {
            @Override
            public PopupContainer handlePopup(PopupParams params) {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(new URI(params.getURL()));
                        } catch (Exception ex) {
                            //return super.handlePopup(params);
                        }
                        return null;
                    }
                } else {
                    return super.handlePopup(params);
                }
                return super.handlePopup(params);
            }
        });
        this.browser.loadURL(URL);
    }

    public void refresh(String noteBookName, String noteName, String htmlContent) {
        noteBookName = noteBookName != null ? noteBookName : "";
        noteName = noteName != null ? noteName : "";
        htmlContent = htmlContent != null ? htmlContent.trim() : "";
        this.oldNoteBookName = noteBookName;
        this.oldNoteName = noteName;
        this.noteBook.removeAllItems();
        List<NoteBook> noteBooks = noteBookService.getNoteBooks();
        for (NoteBook nb : noteBooks) {
            this.noteBook.addItem(nb.getNotebook());
        }
        this.noteBook.setSelectedItem(noteBookName);
        this.note.setText(noteName);
        setHTMLContent(htmlContent);
    }

    private void emptyHTMLContent() {
        this.browser.executeJavaScript("$(\"div#summernote\").summernote(\"code\",\"\")");
    }

    private void setHTMLContent(String htmlContent) {
        //this.browser.executeJavaScript("$(\".note-editable\").html(\"" + htmlContent + "\")");
        this.browser.executeJavaScript("$(\"div#summernote\").summernote(\"code\",\"" + htmlContent + "\")");
    }

    private String getHTMLContent() {
        //JSValue jsValue = this.browser.executeJavaScriptAndReturnValue("$(\".note-editable\").html()");
        JSValue jsValue = this.browser.executeJavaScriptAndReturnValue("$(\"div#summernote\").summernote(\"code\")");
        //String stringValue = jsValue.getStringValue().replace("\\","\\\\").replace("\"","\\\"").replace("\r","\\\n").replace("\n","\\\n");
        String stringValue = StringEscapeUtils.escapeJava(jsValue.getStringValue());
        return stringValue;
    }

    public void save() {
        String newNoteName = note.getText();
        String newNoteBookName = noteBook.getSelectedItem() == null ? "" : noteBook.getSelectedItem().toString();
        String htmlContent = getHTMLContent().trim();
        if (!newNoteBookName.trim().equals("") && !newNoteName.trim().equals("")) {
            if (isNewAdd) {
                noteService.addNote(newNoteBookName, newNoteName, htmlContent);
            } else {
                noteService.updateNote(oldNoteBookName, oldNoteName, newNoteBookName, newNoteName, htmlContent);
                JOptionPane.showMessageDialog(null, "保存成功");
            }
            nbTree.refresh(null, newNoteBookName, newNoteName);
        } else if (newNoteName.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "保存失败，笔记名称不能为空");
        }
    }

    public void close() {
        if (browser != null) {
            browser.dispose();
        }
    }

}
