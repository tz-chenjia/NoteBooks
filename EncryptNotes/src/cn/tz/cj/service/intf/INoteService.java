package cn.tz.cj.service.intf;

import cn.tz.cj.entity.Note;

import java.util.List;
import java.util.Set;

public interface INoteService {

    public int addNote(String noteBookName, String title, String content);
    public int removeNote(String noteBookName, String title);
    public int updateNote(String oldNoteBookName, String oldTitle, String noteBookName, String title, String content);
    public Note getNote(String noteBookName, String title);
    public int getNotesNumWithNoteBook(String noteBookName);
    public int updateNoteBookByNote(String noteBookName, String newName);
    public Set<String> getNotesTitlesByNoteBook(String noteBookName);
    public boolean checkTitleExists(String noteBookName, String title);

}
