package cn.tz.cj.service;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.dao.NoteBookDao;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;

import javax.swing.*;
import java.util.List;

public class NoteBookService implements INoteBookService {

    private static final String EMAIL = Auth.getInstance().getName();
    private NoteBookDao noteBookDao = new NoteBookDao();
    private INoteService noteService = new NoteService();

    @Override
    public int addNoteBook(String noteBookName) {
        noteBookName = noteBookName.trim();
        if(noteBookName == null || noteBookName.equals("")){
            JOptionPane.showMessageDialog(null, "笔记本名称不能为空！", "添加笔记本失败", JOptionPane.WARNING_MESSAGE);
        }
        int i = 0;
        // 需要判断是否已存在同名的
        if(!checkNoteBookExists(noteBookName)){
            i = noteBookDao.insertNoteBook(buildNoteBook(noteBookName));
        }else {
            JOptionPane.showMessageDialog(null, "笔记本["+noteBookName+"]已存在！", "添加笔记本失败", JOptionPane.WARNING_MESSAGE);
        }
        return i;
    }

    @Override
    public int rename(String noteBookName, String newName) {
        newName = newName.trim();
        if(newName == null || newName.equals("")){
            JOptionPane.showMessageDialog(null, "笔记本名称不能为空！", "重命名失败", JOptionPane.WARNING_MESSAGE);
        }
        // 需要判断是否已存在同名的
        int i = 0;
        if(!checkNoteBookExists(newName)){
            // 需要修改note中的Notebook
            noteService.updateNoteBookByNote(noteBookName, newName);
            i = noteBookDao.updateNoteBook(buildNoteBook(noteBookName), newName);
        }else {
            JOptionPane.showMessageDialog(null, "笔记本["+newName+"]已存在！", "重命名失败", JOptionPane.WARNING_MESSAGE);
        }
        return i;
    }

    @Override
    public int removeNoteBook(String noteBookName) {
        int i = 0;
        // 需要判断note中的是否有这个Notebook，有的话不能删除
        if(noteService.getNotesNumWithNoteBook(noteBookName) == 0){
            i = noteBookDao.deleteNoteBook(buildNoteBook(noteBookName));
        }else{
            JOptionPane.showMessageDialog(null, "笔记本["+noteBookName+"]中有笔记，请先删除笔记！", "删除笔记本失败", JOptionPane.WARNING_MESSAGE);
        }
        return i;
    }

    @Override
    public List<NoteBook> getNoteBooks() {
        return noteBookDao.getNoteBooks(EMAIL);
    }

    private NoteBook buildNoteBook(String noteBookName){
        NoteBook noteBook = new NoteBook();
        noteBook.setEmail(EMAIL);
        noteBook.setNotebook(noteBookName);
        return noteBook;
    }

    private boolean checkNoteBookExists(String noteBookName){
        boolean exists = false;
        List<NoteBook> noteBooks = getNoteBooks();
        for(NoteBook noteBook : noteBooks){
            if(noteBook.getNotebook().equalsIgnoreCase(noteBookName)){
                exists = true;
                break;
            }
        }
        return exists;
    }

}
