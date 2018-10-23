package cn.tz.cj.dao;

import cn.tz.cj.entity.Note;
import cn.tz.cj.tools.EncryptUtils;

import java.util.*;

public class NoteDao extends BaseDao {

    private void encrypt(Note note) {
        note.setNotebook(EncryptUtils.toEncryptWithUserPwd(note.getNotebook()));
        note.setContent(EncryptUtils.toEncryptWithUserPwd(note.getContent()));
        note.setTitle(EncryptUtils.toEncryptWithUserPwd(note.getTitle()));
    }

    private void dencrypt(Note note) {
        note.setNotebook(EncryptUtils.toDencryptWithUserPwd(note.getNotebook()));
        note.setContent(EncryptUtils.toDencryptWithUserPwd(note.getContent()));
        note.setTitle(EncryptUtils.toDencryptWithUserPwd(note.getTitle()));
    }

    public int insertNoteDao(Note note) {
        encrypt(note);
        int i = 0;
        if (note != null) {
            List<String> section = section(note.getContent(), 1000);
            for (int j = 0; j < section.size(); j++) {
                String sql = "insert into nb_note  (notebook,title,content,sectionno) values (?,?,?,?)";
                i = update(sql, new Object[]{note.getNotebook(), note.getTitle(), section.get(j), j});
            }
        }
        return i;
    }

    public int updateNoteDao(Note oldNote, Note note) {
        int i = 0;
        if (oldNote != null && note != null) {
            i = deleteNoteDao(oldNote);
            i = insertNoteDao(note);
        }
        return i;
    }

    public int updateNoteDaoWithNewPwd(Note note, String newPwd) {
        int i = 0;
        if (note != null) {
            String sql = "delete from nb_note where notebook = ? and title = ?";
            i = update(sql, new Object[]{EncryptUtils.toEncryptWithUserPwd(note.getNotebook()), EncryptUtils.toEncryptWithUserPwd(note.getTitle())});
            String newNotebook = EncryptUtils.e(note.getNotebook(), newPwd);
            String newTitle = EncryptUtils.e(note.getTitle(), newPwd);
            String newContent = EncryptUtils.e(note.getContent(), newPwd);
            List<String> section = section(newContent, 1000);
            for (int j = 0; j < section.size(); j++) {
                sql = "insert into nb_note  (notebook,title,content,sectionno) values (?,?,?,?)";
                i = update(sql, new Object[]{newNotebook, newTitle, section.get(j), j});
            }
        }
        return i;
    }

    public int deleteNoteDao(Note note) {
        encrypt(note);
        int i = 0;
        String sql = "delete from nb_note where notebook = ? and title = ?";
        i = update(sql, new Object[]{note.getNotebook(), note.getTitle()});
        return i;
    }

    public Note getNote(Note note) {
        encrypt(note);
        Note n = null;
        String sql = "select * from nb_note where notebook = ? and title = ?";
        List<Note> notes = queryToBean(sql, new Object[]{note.getNotebook(), note.getTitle()}, Note.class);
        if (notes.size() > 0) {
            Map<Integer, String> sections = new HashMap<Integer, String>();
            for (Note nt : notes) {
                String content = nt.getContent();
                int sectionno = nt.getSectionno();
                sections.put(sectionno, content);
            }
            String merge = merge(sections);
            n = notes.get(0);
            n.setContent(merge);
            dencrypt(n);
        }
        return n;
    }

    public List<Note> getNotesToExport(String notebookName) {
        String sql = "select * from nb_note where notebook = ?";
        return queryToBean(sql, new Object[]{notebookName}, Note.class);
    }

    public int getNotesNumWithNoteBook(String noteBookName) {
        int n = 0;
        String sql = "select count(title) n from nb_note where notebook = ?";
        List<Map<String, Object>> data = query(sql, new Object[]{EncryptUtils.toEncryptWithUserPwd(noteBookName)});
        if (data.size() > 0) {
            n = Integer.valueOf(data.get(0).get("n").toString());
        }
        return n;
    }

    public int updateNoteBookByNote(String noteBookName, String newName) {
        int n = 0;
        String sql = "update nb_note set notebook= ? where notebook = ?";
        n = update(sql, new Object[]{EncryptUtils.toEncryptWithUserPwd(newName), EncryptUtils.toEncryptWithUserPwd(noteBookName)});
        return n;
    }

    public Set<String> getNotesTitlesByNoteBook(String noteBookName) {
        Set<String> titles = new HashSet<String>();
        String sql = "select title from nb_note where notebook = ?";
        List<Map<String, Object>> data = query(sql, new Object[]{EncryptUtils.toEncryptWithUserPwd(noteBookName)});
        for (Map<String, Object> m : data) {
            titles.add(EncryptUtils.toDencryptWithUserPwd(m.get("title").toString()));
        }
        return titles;
    }

    private static List<String> section(String content, int len) {
        List<String> strList = new ArrayList<String>();
        if (content != null && !content.equals("")) {
            do {
                len = len > content.length() ? content.length() : len;
                String section = content.substring(0, len);
                strList.add(section);
            } while ((content = content.substring(len)).length() > 0);
        }
        return strList;
    }

    private static String merge(Map<Integer, String> sections) {
        StringBuffer sb = new StringBuffer();
        if (sections != null || !sections.isEmpty()) {
            TreeMap<Integer, String> treeMap = new TreeMap<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    if (o1 > 02) {
                        return -1;
                    }
                    return 1;
                }
            });
            treeMap.putAll(sections);
            for (Map.Entry<Integer, String> e : sections.entrySet()) {
                sb.append(e.getValue());
            }
        }
        return sb.toString();
    }

}
