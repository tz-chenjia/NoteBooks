package cn.tz.cj.service;

import cn.tz.cj.bo.Auth;
import cn.tz.cj.dao.NoteBookDao;
import cn.tz.cj.dao.NoteDao;
import cn.tz.cj.dao.SystemDao;
import cn.tz.cj.entity.Note;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.rule.EDBType;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.FileRWUtils;
import cn.tz.cj.tools.JDBCUtils;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class SystemService implements ISystemService {
    private static final String TEMPDATAFILE = System.getProperty("user.dir") + File.separator + "data" + File.separator + "notebooks.sql";

    SystemDao systemDao = new SystemDao();
    NoteBookDao noteBookDao = new NoteBookDao();
    NoteDao noteDao = new NoteDao();

    @Override
    public boolean checkDBAndInit() {
        boolean isOk = true;
        IConfigsService configsService = new ConfigsService();
        UserConfigs configs = configsService.getUserConfigs();
        if (configs != null) {
            // 验证连接是否可用
            boolean useDB = JDBCUtils.isUseDB(configs.getDbHost(), configs.getDbPort(), configs.getDbName(), configs.getDbUserName(), configs.getDbPassword(), EDBType.toEDBType(configs.getDbType()));
            if (useDB) {
                if (!systemDao.tablesExists()) {
                    systemDao.initDBTable();
                }
            } else {
                // 提示数据库不能连
                JOptionPane.showMessageDialog(null, "请检查您的配置及网络！", "配置连接失败", JOptionPane.WARNING_MESSAGE);
                isOk = false;
            }
        } else {
            // 提示需要配置
            JOptionPane.showMessageDialog(null, "请设置您的配置！", "配置未设置", JOptionPane.WARNING_MESSAGE);
            isOk = false;
        }
        return isOk;
    }

    @Override
    public void impData(File file) {
        String sqls = FileRWUtils.read(file);
        String[] split = sqls.split(";");
        for (String sql : split) {
            String s = sql.toLowerCase();
            if (s.contains("nb_note") || s.contains("nb_notebook") || s.contains("nb_user")) {
                systemDao.update(sql, new Object[]{});
            }
        }
    }

    @Override
    public void expData(File file) {
        FileRWUtils.write(file, getAllDataWithUser());
    }

    @Override
    public void deleteUser() {
        String userName = Auth.getInstance().getName();
        systemDao.update("delete from nb_note where notebook in (select notebook from nb_notebook where email='" + userName + "')", new Object[]{});
        systemDao.update("delete from nb_notebook where email='" + userName + "'", new Object[]{});
        systemDao.update("delete from nb_user where email='" + userName + "'", new Object[]{});
    }

    @Override
    public File tempSaveDataToLocal() {
        File file = getTempDataFile();
        expData(file);
        return file;
    }

    public static File getTempDataFile() {
        FileRWUtils.existsAndCreate(TEMPDATAFILE);
        File file = new File(TEMPDATAFILE);
        return file;
    }

    private String getAllDataWithUser() {
        String userName = Auth.getInstance().getName();
        String userPwd = Auth.getInstance().getPwd();
        StringBuffer sb = new StringBuffer();
        sb.append("delete from nb_note where notebook in (select notebook from nb_notebook where email='" + userName + "');\n");
        sb.append("delete from nb_notebook where email='" + userName + "';\n");
        sb.append("delete from nb_user where email='" + userName + "';\n");
        sb.append("insert into NB_USER (email, pwd) values ('" + userName + "','" + userPwd + "');\n");
        List<NoteBook> noteBooks = noteBookDao.getNoteBooksToExport(userName);
        for (NoteBook nb : noteBooks) {
            sb.append("insert into NB_NOTEBOOK (email, notebook) values ('" + nb.getEmail() + "','" + nb.getNotebook() + "');\n");
            List<Note> notes = noteDao.getNotesToExport(nb.getNotebook());
            for (Note n : notes) {
                sb.append("insert into NB_NOTE (notebook, title,content,sectionno) values ('" + n.getNotebook() + "','" + n.getTitle() + "','" + n.getContent() + "','" + n.getSectionno() + "');\n");
            }
        }
        return sb.toString();
    }
}
