package cn.tz.cj.entity;

public class Note {

    private String notebook;
    private String title;
    private String content;
    private int sectionno;

    public String getNotebook() {
        return notebook;
    }

    public void setNotebook(String notebook) {
        this.notebook = notebook;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSectionno() {
        return sectionno;
    }

    public void setSectionno(int sectionno) {
        this.sectionno = sectionno;
    }

    @Override
    public String toString() {
        return "{notebook:" + notebook + ",title:" + title + ",content:" + content + ",sectionno:" + sectionno + "}";
    }
}
