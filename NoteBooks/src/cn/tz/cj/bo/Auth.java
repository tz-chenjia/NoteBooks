package cn.tz.cj.bo;

public class Auth {
    private static Auth auth;
    private String name;
    private String pwd;
    private String searchKey;
    private String selectedNoteBookName;
    private String selectedNoteName;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSelectedNoteBookName() {
        return selectedNoteBookName;
    }

    public void setSelectedNoteBookName(String selectedNoteBookName) {
        this.selectedNoteBookName = selectedNoteBookName;
    }

    public String getSelectedNoteName() {
        return selectedNoteName;
    }

    public void setSelectedNoteName(String selectedNoteName) {
        this.selectedNoteName = selectedNoteName;
    }

    private Auth() {

    }

    public static Auth getInstance() {
        if (auth == null) {
            synchronized (Auth.class) {
                if (auth == null) {
                    auth = new Auth();
                }
            }
        }
        return auth;
    }

}
