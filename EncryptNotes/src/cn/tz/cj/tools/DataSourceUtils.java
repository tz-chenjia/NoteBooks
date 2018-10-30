package cn.tz.cj.tools;

import cn.tz.cj.service.ConfigsService;
import cn.tz.cj.service.intf.IConfigsService;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DataSourceUtils {

    private static Logger logger = Logger.getLogger(DataSourceUtils.class);

    private static DataSource dataSource;

    private static IConfigsService configsService = new ConfigsService();

    public static boolean init() {
        Properties info = configsService.getDBProperties();
        try {
            dataSource = DruidDataSourceFactory.createDataSource(info);
            if (getConnection() == null) {
                return false;
            }//测试连接
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return conn;
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

}
