package cn.tz.cj.service.intf;

import cn.tz.cj.entity.NoteBook;

import java.util.List;

public interface INoteBookService {

    public int addNoteBook(String noteBookName);

    public int rename(String noteBookName, String newName);

    public int removeNoteBook(String noteBookName);

    public List<NoteBook> getNoteBooks();

}
