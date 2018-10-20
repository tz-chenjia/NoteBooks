package cn.tz.cj.entity;

import java.text.Collator;
import java.util.Comparator;

public class NoteBook implements Comparable<NoteBook>{

    private String email;

    private String notebook;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotebook() {
        return notebook;
    }

    public void setNotebook(String notebook) {
        this.notebook = notebook;
    }

    @Override
    public String toString() {
        return "{email:"+email+",notebook:"+notebook+"}";
    }

    @Override
    public int compareTo(NoteBook o) {
        Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
        return com.compare(notebook, o.getNotebook());
    }
}
