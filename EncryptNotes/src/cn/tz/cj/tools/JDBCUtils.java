package cn.tz.cj.tools;

import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.rule.EDBType;
import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.intf.IConfigsService;
import org.apache.log4j.Logger;

import java.sql.*;

public class JDBCUtils {

    private static final Logger log = Logger.getLogger(JDBCUtils.class);

    // 驱动包名和数据库url
    private static String url = null;
    private static String driverClass = null;
    // 数据库用户名和密码
    private static String userName = null;
    private static String password = null;

    public static boolean isUseDB(String host, String port, String dbName, String userName, String pwd, EDBType dbType) {
        String url = buildDBUrl(dbType.getType(), host, port, dbName);
        if (JDBCUtils.testConnection(url, dbType.getDriverClass(), userName, pwd) == null) {
            //log.warn("数据库不能用,配置：{" + EDBType.toString(dbType) + "," + url + "," + userName + "}");
            return false;
        } else {
            //log.info("数据库能用,配置：{" + EDBType.toString(dbType) + "," + url + "," + userName + "}");
            return true;
        }
    }

    private static String buildDBUrl(String dbType, String host, String port, String dbName) {
        String url;
        switch (dbType) {
            case "db2":
                url = "jdbc:db2://" + host + "[:" + port + "]/" + dbName;
                break;
            case "sqlserver":
                url = "jdbc:sqlserver://" + host + ":" + port + ";DatabaseName=" + dbName;
                break;
            case "oracle":
                url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;
                break;
            default:
                //mysql
                url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
                break;
        }
        return url;
    }

    public static Connection testConnection(String url, String driverClass, String userName, String password) {
        Connection conn = null;
        try {
            //注册驱动程序
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException e) {
            log.warn(e.getMessage());
        } catch (SQLException e) {
            log.warn(e.getMessage());
        }
        return conn;
    }

    /**
     * 打开数据库驱动连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            IConfigsService configsService = new ConfigsService();
            UserConfigs userConfigs = configsService.getUserConfigs();
            if (userConfigs != null) {
                driverClass = userConfigs.getDbDriverClass();
                userName = userConfigs.getDbUserName();
                password = userConfigs.getDbPassword();
                String dbType = userConfigs.getDbType();
                String host = userConfigs.getDbHost();
                String port = userConfigs.getDbPort();
                String dbName = userConfigs.getDbName();
                url = buildDBUrl(dbType, host, port, dbName);
            }

            //注册驱动程序
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException e) {
            ExceptionHandleUtils.handling(e);
        } catch (SQLException e) {
            ExceptionHandleUtils.handling(e);
        }
        return conn;
    }

    /**
     * 清理环境，关闭连接(顺序:后打开的先关闭)
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e1) {
                ExceptionHandleUtils.handling(e1);
            }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                ExceptionHandleUtils.handling(e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                ExceptionHandleUtils.handling(e);
            }
        }
    }
}
