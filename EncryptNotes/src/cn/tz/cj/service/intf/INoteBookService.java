package cn.tz.cj.service.intf;

import cn.tz.cj.entity.NoteBook;

import java.util.List;

public interface INoteBookService {

    void addNoteBook(String noteBookName);

    void rename(String noteBookName, String newName);

    void removeNoteBook(String noteBookName);

    List<NoteBook> getNoteBooks();

}
