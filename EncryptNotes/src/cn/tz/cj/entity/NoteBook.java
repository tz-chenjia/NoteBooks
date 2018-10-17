package cn.tz.cj.entity;

public class NoteBook {

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
}
