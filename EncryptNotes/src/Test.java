import cn.tz.cj.bo.Auth;
import cn.tz.cj.entity.Note;
import cn.tz.cj.entity.NoteBook;
import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.rule.EDBType;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.NoteBookService;
import cn.tz.cj.service.NoteService;
import cn.tz.cj.service.SystemService;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.service.intf.INoteBookService;
import cn.tz.cj.service.intf.INoteService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.EncryptUtils;
import cn.tz.cj.tools.JDBCUtils;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        // 配置设置－－－－－－－－－－－－－－－－－－－－－－－－－
        /*IConfigsService configsService = new ConfigsService();
        // 写入
        UserConfigs userConfigs = new UserConfigs();
        userConfigs.setDbType("mysql");
        userConfigs.setDbName("notebook");
        userConfigs.setDbDriverClass("com.mysql.jdbc.Driver");
        userConfigs.setDbHost("localhost");
        userConfigs.setDbPort("3306");
        userConfigs.setDbUserName("root");
        userConfigs.setDbPassword("123456");
        userConfigs.setUserEmail("tz_chenjia@qq.com");
        configsService.saveUserConfigs(userConfigs);
        // 读取
        UserConfigs userConfigs1 = configsService.getUserConfigs();
        System.out.println(userConfigs1.getDbPassword());*/

        // 数据环境检查及初始化
        /*ISystemService systemService = new SystemService();
        systemService.checkDBAndInit();*/

        Auth instance = Auth.getInstance();
        instance.setName("chenjia@qq.com");
        INoteBookService noteBookService = new NoteBookService();
        INoteService noteService = new NoteService();
        //noteBookService.addNoteBook("笔记本2");
        //添加笔记
        //noteService.addNote("笔记本2", "笔记A", "内容A");
        //修改笔记
        //noteService.updateNote("笔记本A", "笔记b", "内容AAABBAA");
        //查看笔记
        //Note note = noteService.getNote("笔记本2", "笔记A");
        //删除笔记
        //noteService.removeNote("笔记本2", "笔记A");
        //根据笔记本查找所有笔记
        //Set<String> notesTitles = noteService.getNotesTitlesByNoteBook("笔记本A");
        //System.out.println(notesTitles);

        //笔记本重命名
        //noteBookService.rename("笔记本1","笔记本A");
        //删除笔记本
        //noteBookService.removeNoteBook("笔记本3");
        //查找所有笔记本
        //List<NoteBook> noteBooks = noteBookService.getNoteBooks();
        //System.out.println(noteBooks);

       Object[] a = {};
       Object[] b = {};
        System.out.println(a.equals(b));
    }

}
