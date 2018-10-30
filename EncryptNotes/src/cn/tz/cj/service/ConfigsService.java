package cn.tz.cj.service;

import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.rule.EDBType;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.tools.EncryptUtils;
import cn.tz.cj.tools.FileRWUtils;
import cn.tz.cj.tools.GlobalExceptionHandling;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ConfigsService implements IConfigsService {

    private static final String USER_PROPERTIES = "my.properties";

    private static Logger log = Logger.getLogger(ConfigsService.class);

    @Override
    public void saveUserConfigs(UserConfigs configs) {
        FileRWUtils.existsAndCreate(getTempDataPath(USER_PROPERTIES));
        Properties p = new Properties();
        p.setProperty("db_type", configs.getDbType());
        p.setProperty("db_name", configs.getDbName());
        p.setProperty("db_host", configs.getDbHost());
        p.setProperty("db_port", configs.getDbPort());
        p.setProperty("db_email", configs.getUserEmail());
        // 数据库配置
        p.setProperty("driverClassName", configs.getDbDriverClass());
        p.setProperty("url", EDBType.buildDBUrl(configs.getDbType(), configs.getDbHost(), configs.getDbPort(), configs.getDbName()));
        p.setProperty("username", configs.getDbUserName());
        p.setProperty("password", EncryptUtils.e(configs.getDbPassword(), ConfigsService.class.getName()));
        p.setProperty("initialSize", "5");
        p.setProperty("maxActive", "10");
        p.setProperty("maxWait", "3000");
        p.setProperty("minIdle", "3");
        p.setProperty("connectionErrorRetryAttempts", "0");
        p.setProperty("breakAfterAcquireFailure", "true");
        FileRWUtils.writeProperties(new File(getTempDataPath(USER_PROPERTIES)), p);
    }

    @Override
    public UserConfigs getUserConfigs() {
        Properties properties = FileRWUtils.readProperties(getTempDataPath(USER_PROPERTIES));
        if (properties != null && !properties.isEmpty()) {
            UserConfigs userConfigs = new UserConfigs();
            userConfigs.setDbType(properties.getProperty("db_type"));
            userConfigs.setDbName(properties.getProperty("db_name"));
            userConfigs.setDbDriverClass(properties.getProperty("driverClassName"));
            userConfigs.setDbHost(properties.getProperty("db_host"));
            userConfigs.setDbPort(properties.getProperty("db_port"));
            userConfigs.setDbUserName(properties.getProperty("username"));
            userConfigs.setDbPassword(EncryptUtils.d(properties.getProperty("password"), ConfigsService.class.getName()));
            userConfigs.setUserEmail(properties.getProperty("db_email"));
            return userConfigs;
        } else {
            return null;
        }
    }

    @Override
    public UserConfigs setUserEmail(String userEmail) {
        UserConfigs userConfigs = getUserConfigs();
        if (userConfigs != null) {
            userConfigs.setUserEmail(userEmail);
            saveUserConfigs(userConfigs);
        }
        return userConfigs;
    }

    @Override
    public Properties getDBProperties() {
        Properties info = FileRWUtils.readProperties(getTempDataPath(USER_PROPERTIES));
        if (!info.isEmpty()) {
            info.setProperty("password", EncryptUtils.d(info.getProperty("password"), ConfigsService.class.getName()));
        }
        return info;
    }

    public static Image getImage(String name) {
        Image img = null;
        try {
            URL resource = getSingleConf("images/" + name).toURI().toURL();
            img = ImageIO.read(resource);
        } catch (IOException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        }
        return img;
    }

    public static File getSingleConf(String conf) {
        FileRWUtils.existsAndCreate(getConfPath() + conf);
        return new File(getConfPath() + conf);
    }

    public static String getConfPath() {
        return getRootPath() + "configs" + File.separator;
    }

    public static String getRootPath() {
        return System.getProperty("user.dir") + File.separator;
    }

    public static String getTempDataPath(String fileName) {
        return getRootPath() + "data" + File.separator + fileName;
    }
}
