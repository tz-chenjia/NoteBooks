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

    public int updateNoteBook(NoteBook noteBook, String noteBookName){
        int update = 0;
        if(noteBook != null){
            String sql = "update nb_notebook set notebook = ? where email = ? and notebook = ?";
            update = update(sql, new Object[]{EncryptUtils.toEncryptWithUserPwd(noteBookName), noteBook.getEmail(), EncryptUtils.toEncryptWithUserPwd(noteBook.getNotebook())});
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
        String sql = "select notebook from nb_notebook where email = ?";
        List<NoteBook> noteBooks = queryToBean(sql, new Object[]{email}, NoteBook.class);
        for(NoteBook nb : noteBooks){
            nb.setNotebook(EncryptUtils.toDencryptWithUserPwd(nb.getNotebook()));
        }
        return noteBooks;
    }

}
