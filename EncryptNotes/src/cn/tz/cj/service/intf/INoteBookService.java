package cn.tz.cj.service.intf;

import cn.tz.cj.entity.NoteBook;

import java.util.List;

public interface INoteBookService {

    int addNoteBook(String noteBookName);

    int rename(String noteBookName, String newName);

    int removeNoteBook(String noteBookName);

    List<NoteBook> getNoteBooks();

}
