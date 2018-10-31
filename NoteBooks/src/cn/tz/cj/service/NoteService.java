package cn.tz.cj.service;

import cn.tz.cj.dao.NoteDao;
import cn.tz.cj.entity.Note;
import cn.tz.cj.service.intf.INoteService;
import cn.tz.cj.service.intf.ISystemService;

import java.util.Set;

public class NoteService implements INoteService {

    private NoteDao noteDao = new NoteDao();

    private ISystemService systemService = new SystemService();

    @Override
    public void addNote(String noteBookName, String title, String content) {
        //systemService.tempSaveDataToLocal();
        noteDao.insertNoteDao(buildNote(noteBookName, title, content));
    }

    @Override
    public void removeNote(String noteBookName, String title) {
        //systemService.tempSaveDataToLocal();
        noteDao.deleteNoteDao(buildNote(noteBookName, title, ""));
    }

    @Override
    public void updateNote(String oldNoteBookName, String oldTitle, String noteBookName, String title, String content) {
        //systemService.tempSaveDataToLocal();
        noteDao.updateNoteDao(buildNote(oldNoteBookName, oldTitle, ""), buildNote(noteBookName, title, content));
    }

    @Override
    public Note getNote(String noteBookName, String title) {
        Note note = noteDao.getNote(buildNote(noteBookName, title, ""));
        return note;
    }

    @Override
    public int getNotesNumWithNoteBook(String noteBookName) {
        int notesNumWithNoteBook = noteDao.getNotesNumWithNoteBook(noteBookName);
        return notesNumWithNoteBook;
    }

    @Override
    public void updateNoteBookByNote(String noteBookName, String newName) {
        //systemService.tempSaveDataToLocal();
        noteDao.updateNoteBookByNote(noteBookName, newName);
    }

    @Override
    public Set<String> getNotesTitlesByNoteBook(String noteBookName) {
        Set<String> notesTitlesByNoteBook = noteDao.getNotesTitlesByNoteBook(noteBookName);
        return notesTitlesByNoteBook;
    }

    private Note buildNote(String noteBookName, String title, String content) {
        Note note = new Note();
        note.setNotebook(noteBookName);
        note.setTitle(title);
        note.setContent(content);
        return note;
    }

    @Override
    public boolean checkTitleExists(String noteBookName, String title) {
        Note note = noteDao.getNote(buildNote(noteBookName, title, ""));
        return note != null;
    }

}
