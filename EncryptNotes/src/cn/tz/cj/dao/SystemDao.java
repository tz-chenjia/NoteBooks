package cn.tz.cj.dao;

import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.rule.EDBType;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.tools.GlobalExceptionHandling;
import cn.tz.cj.tools.JDBCUtils;
import org.apache.log4j.Logger;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SystemDao extends BaseDao {

    public boolean tablesExists() {
        return tableExists("NB_NOTE") && tableExists("NB_NOTEBOOK") && tableExists("NB_USER");
    }

    private boolean tableExists(String tableName) {
        boolean flag = false;
        con = JDBCUtils.getConnection();
        DatabaseMetaData meta = null;
        try {
            meta = con.getMetaData();
            //String type[] = {"TABLE"};
            rs = meta.getTables(null, null, tableName.toUpperCase(), null);
            flag = rs.next();
        } catch (SQLException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        }
        return flag;
    }

    public void initDBTable() {
        IConfigsService configsService = new ConfigsService();
        UserConfigs userConfigs = configsService.getUserConfigs();
        if (userConfigs != null) {
            String noteSQL = "CREATE TABLE NB_NOTE (notebook varchar(200) NOT NULL,title varchar(1000) NOT NULL,content varchar(2000) NOT NULL,sectionno int NOT NULL)";
            String noteBookSQL = "CREATE TABLE NB_NOTEBOOK (email varchar(200) NOT NULL,notebook varchar(200) NOT NULL)";
            String userSQL = "CREATE TABLE NB_USER (email varchar(200) NOT NULL,pwd varchar(200) NOT NULL)";
            EDBType dbType = EDBType.toEDBType(userConfigs.getDbType());
            switch (dbType) {
                case DB2:
                    break;
                case SQLSERVER:
                    break;
                case ORACLE:
                    //noteSQL.replace("int","number");
                    //noteBookSQL.replace("int","number");
                    //userSQL.replace("int","number");
                    //break;
                default:
                    //mysql
                    break;
            }
            update(noteSQL, new Object[]{});
            update(noteBookSQL, new Object[]{});
            update(userSQL, new Object[]{});
        }
    }

}
