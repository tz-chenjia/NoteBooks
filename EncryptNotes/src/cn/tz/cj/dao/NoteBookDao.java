package cn.tz.cj.dao;

import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.tools.EncryptUtils;

import java.util.List;

public class NoteBookDao extends BaseDao {

    public int insertNoteBook(NoteBook noteBook){
        int update = 0;
        if(noteBook != null){
            String sql = "insert into nb_notebook  (email,notebook) values (?,?)";
            update = update(sql, new Object[]{noteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(noteBook.getNotebook())});

        }
        return update;
    }

    public int updateNoteBook(NoteBook oldNoteBook, NoteBook newNoteBook){
        int update = 0;
        if(oldNoteBook != null){
            String sql = "update nb_notebook set email = ?,notebook = ? where email = ? and notebook = ?";
            System.out.println(oldNoteBook);
            System.out.println(newNoteBook);
            update = update(sql, new Object[]{newNoteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(newNoteBook.getNotebook()),oldNoteBook.getEmail(),EncryptUtils.toEncryptWithUserPwd(oldNoteBook.getNotebook())});
        }
        return update;
    }

    public int updateNoteBookWithNewPwd(NoteBook noteBook, String newPwd){
        int update = 0;
        if(noteBook != null){
            String sql = "update nb_notebook set email = ?,notebook = ? where email = ? and notebook = ?";
            update = update(sql, new Object[]{noteBook.getEmail(), EncryptUtils.e(noteBook.getNotebook(), newPwd),noteBook.getEmail(),EncryptUtils.toEncryptWithUserPwd(noteBook.getNotebook())});
        }
        return update;
    }

    public int deleteNoteBook(NoteBook noteBook){
        int update = 0;
        if(noteBook != null){
            String sql = "delete from nb_notebook where email = ? and notebook = ?";
            update = update(sql, new Object[]{noteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(noteBook.getNotebook())});
        }
        return update;
    }

    public List<NoteBook> getNoteBooks(String email){
        String sql = "select * from nb_notebook where email = ?";
        List<NoteBook> noteBooks = queryToBean(sql, new Object[]{email}, NoteBook.class);
        for(NoteBook nb : noteBooks){
            nb.setNotebook(EncryptUtils.toDencryptWithUserPwd(nb.getNotebook()));
        }
        return noteBooks;
    }

    public List<NoteBook> getNoteBooksToExport(String email){
        String sql = "select * from nb_notebook where email = ?";
        List<NoteBook> noteBooks = queryToBean(sql, new Object[]{email}, NoteBook.class);
        return noteBooks;
    }

}
