package cn.tz.cj.service.intf;

import cn.tz.cj.entity.Note;

import java.util.Set;

public interface INoteService {

    int addNote(String noteBookName, String title, String content);

    int removeNote(String noteBookName, String title);

    int updateNote(String oldNoteBookName, String oldTitle, String noteBookName, String title, String content);

    Note getNote(String noteBookName, String title);

    int getNotesNumWithNoteBook(String noteBookName);

    int updateNoteBookByNote(String noteBookName, String newName);

    Set<String> getNotesTitlesByNoteBook(String noteBookName);

    boolean checkTitleExists(String noteBookName, String title);

}
