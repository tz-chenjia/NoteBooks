package cn.tz.cj.service;

import cn.tz.cj.dao.NoteDao;
import cn.tz.cj.entity.Note;
import cn.tz.cj.service.intf.INoteService;

import javax.swing.*;
import java.util.Set;

public class NoteService implements INoteService {

    private NoteDao noteDao = new NoteDao();

    @Override
    public int addNote(String noteBookName, String title, String content) {
        int i = 0;
        if (title == null || title.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "笔记标题不能为空！", "添加笔记失败", JOptionPane.WARNING_MESSAGE);
            return i;
        }
        title = title.trim();
        // 需要判断标题是否已存在
        if(!checkTitleExists(noteBookName, title)){
            i = noteDao.insertNoteDao(buildNote(noteBookName, title, content));
        }else{
            JOptionPane.showMessageDialog(null, "笔记["+title+"]已存在！", "添加笔记失败", JOptionPane.WARNING_MESSAGE);
        }
        return i;
    }

    @Override
    public int removeNote(String noteBookName, String title) {
        return noteDao.deleteNoteDao(noteBookName, title);
    }

    @Override
    public int updateNote(String noteBookName, String title, String content) {
        return noteDao.updateNoteDao(buildNote(noteBookName,title,content));
    }

    @Override
    public Note getNote(String noteBookName, String title) {
        return noteDao.getNote(noteBookName,title);
    }

    @Override
    public int getNotesNumWithNoteBook(String noteBookName) {
        return noteDao.getNotesNumWithNoteBook(noteBookName);
    }

    @Override
    public int updateNoteBookByNote(String noteBookName, String newName) {
        return noteDao.updateNoteBookByNote(noteBookName,newName);
    }

    @Override
    public Set<String> getNotesTitlesByNoteBook(String noteBookName) {
        return noteDao.getNotesTitlesByNoteBook(noteBookName);
    }

    private Note buildNote(String noteBookName, String title, String content){
        Note note = new Note();
        note.setNotebook(noteBookName);
        note.setTitle(title);
        note.setContent(content);
        return note;
    }

    private boolean checkTitleExists(String noteBookName, String title){
        Note note = noteDao.getNote(noteBookName, title);
        return note != null;
    }

}
