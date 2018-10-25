package cn.tz.cj.dao;

import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.tools.EncryptUtils;

import java.util.List;

public class NoteBookDao extends BaseDao {

    public void insertNoteBook(NoteBook noteBook) {
        if (noteBook != null) {
            String sql = "insert into nb_notebook  (email,notebook) values (?,?)";
            update(sql, new Object[]{noteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(noteBook.getNotebook())});
        }
    }

    public void updateNoteBook(NoteBook oldNoteBook, NoteBook newNoteBook) {
        if (oldNoteBook != null) {
            String sql = "update nb_notebook set email = ?,notebook = ? where email = ? and notebook = ?";
            update(sql, new Object[]{newNoteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(newNoteBook.getNotebook()), oldNoteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(oldNoteBook.getNotebook())});
        }
    }

    public void updateNoteBookWithNewPwd(NoteBook noteBook, String newPwd) {
        if (noteBook != null) {
            String sql = "update nb_notebook set email = ?,notebook = ? where email = ? and notebook = ?";
            update(sql, new Object[]{noteBook.getEmail(), EncryptUtils.e(noteBook.getNotebook(), newPwd), noteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(noteBook.getNotebook())});
        }
    }

    public void deleteNoteBook(NoteBook noteBook) {
        if (noteBook != null) {
            String sql = "delete from nb_notebook where email = ? and notebook = ?";
            update(sql, new Object[]{noteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(noteBook.getNotebook())});
        }
    }

    public List<NoteBook> getNoteBooks(String email) {
        String sql = "select * from nb_notebook where email = ?";
        List<NoteBook> noteBooks = queryToBean(sql, new Object[]{email}, NoteBook.class);
        for (NoteBook nb : noteBooks) {
            nb.setNotebook(EncryptUtils.toDencryptWithUserPwd(nb.getNotebook()));
        }
        return noteBooks;
    }

    public List<NoteBook> getNoteBooksToExport(String email) {
        String sql = "select * from nb_notebook where email = ?";
        List<NoteBook> noteBooks = queryToBean(sql, new Object[]{email}, NoteBook.class);
        return noteBooks;
    }

}
