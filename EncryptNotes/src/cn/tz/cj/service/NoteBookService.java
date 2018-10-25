package cn.tz.cj.service;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.dao.NoteBookDao;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;
import cn.tz.cj.service.intf.ISystemService;

import javax.swing.*;
import java.util.List;

public class NoteBookService implements INoteBookService {

    private NoteBookDao noteBookDao = new NoteBookDao();
    private INoteService noteService = new NoteService();
    private ISystemService systemService = new SystemService();

    @Override
    public void addNoteBook(String noteBookName) {
        //systemService.tempSaveDataToLocal();
        if (noteBookName == null || noteBookName.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "笔记本名称不能为空！", "添加笔记本失败", JOptionPane.WARNING_MESSAGE);
            return;
        }
        noteBookName = noteBookName.trim();
        // 需要判断是否已存在同名的
        if (!checkNoteBookExists(noteBookName)) {
            noteBookDao.insertNoteBook(buildNoteBook(noteBookName));
        } else {
            JOptionPane.showMessageDialog(null, "笔记本[" + noteBookName + "]已存在！", "添加笔记本失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void rename(String noteBookName, String newName) {
        //systemService.tempSaveDataToLocal();
        if (newName == null || newName.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "笔记本名称不能为空！", "重命名失败", JOptionPane.WARNING_MESSAGE);
            return;
        }
        newName = newName.trim();
        // 需要判断是否已存在同名的
        if (!checkNoteBookExists(newName)) {
            // 需要修改note中的Notebook
            noteService.updateNoteBookByNote(noteBookName, newName);
            noteBookDao.updateNoteBook(buildNoteBook(noteBookName), buildNoteBook(newName));
        } else {
            JOptionPane.showMessageDialog(null, "笔记本[" + newName + "]已存在！", "重命名失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void removeNoteBook(String noteBookName) {
        //systemService.tempSaveDataToLocal();
        // 需要判断note中的是否有这个Notebook，有的话不能删除
        if (noteService.getNotesNumWithNoteBook(noteBookName) == 0) {
            noteBookDao.deleteNoteBook(buildNoteBook(noteBookName));
        } else {
            JOptionPane.showMessageDialog(null, "笔记本[" + noteBookName + "]中有笔记，请先删除笔记！", "删除笔记本失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public List<NoteBook> getNoteBooks() {
        List<NoteBook> noteBooks = noteBookDao.getNoteBooks(Auth.getInstance().getName());
        return noteBooks;
    }

    private NoteBook buildNoteBook(String noteBookName) {
        NoteBook noteBook = new NoteBook();
        noteBook.setEmail(Auth.getInstance().getName());
        noteBook.setNotebook(noteBookName);
        return noteBook;
    }

    private boolean checkNoteBookExists(String noteBookName) {
        boolean exists = false;
        List<NoteBook> noteBooks = getNoteBooks();
        for (NoteBook noteBook : noteBooks) {
            if (noteBook.getNotebook().equalsIgnoreCase(noteBookName)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

}
