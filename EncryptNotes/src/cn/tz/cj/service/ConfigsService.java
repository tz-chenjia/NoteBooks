package cn.tz.cj.service;

import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.tools.EncryptUtils;
import cn.tz.cj.tools.ExceptionHandleUtils;
import cn.tz.cj.tools.FileRWUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class ConfigsService implements IConfigsService {

    private static final String USER_PROPERTIES = "my.properties";

    @Override
    public void saveUserConfigs(UserConfigs configs) {
        try {
            File dbPropFile = getSingleConf(USER_PROPERTIES);
            Properties p = new Properties();
            OutputStream outputFile = new FileOutputStream(dbPropFile);
            p.setProperty("db_type", configs.getDbType());
            p.setProperty("db_name", configs.getDbName());
            p.setProperty("db_host", configs.getDbHost());
            p.setProperty("db_port", configs.getDbPort());
            p.setProperty("db_driverClass", configs.getDbDriverClass());
            p.setProperty("db_userName", configs.getDbUserName());
            p.setProperty("db_password", EncryptUtils.e(configs.getDbPassword(), ConfigsService.class.getName()));
            p.store(outputFile, null);
            outputFile.close();
        } catch (Exception ex) {
            ExceptionHandleUtils.handling(ex);
        }
    }

    @Override
    public UserConfigs getUserConfigs() {
        File propFile = getSingleConf(USER_PROPERTIES);
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(propFile));
            properties.load(bufferedReader);
        } catch (FileNotFoundException e) {
            ExceptionHandleUtils.handling(e);
        } catch (IOException e) {
            ExceptionHandleUtils.handling(e);
        }
        if(properties.isEmpty()){
            return null;
        }else{
            UserConfigs userConfigs = new UserConfigs();
            userConfigs.setDbType(properties.getProperty("db_type"));
            userConfigs.setDbName(properties.getProperty("db_name"));
            userConfigs.setDbDriverClass(properties.getProperty("db_driverClass"));
            userConfigs.setDbHost(properties.getProperty("db_host"));
            userConfigs.setDbPort(properties.getProperty("db_port"));
            userConfigs.setDbUserName(properties.getProperty("db_userName"));
            userConfigs.setDbPassword(EncryptUtils.d(properties.getProperty("db_password"), ConfigsService.class.getName()));
            return userConfigs;
        }
    }

    public static Image getLogo() {
        Image logo = null;
        try {
            logo = ImageIO.read(ConfigsService.class.getResource("../resource/images/logo.png"));
        } catch (IOException e) {
            ExceptionHandleUtils.handling(e);
        }
        return logo;
    }

    private static File getSingleConf(String conf) {
        FileRWUtils.existsAndCreate(getConfPath() + conf);
        return new File(getConfPath() + conf);
    }

    public static String getConfPath() {
        return getRootPath() + "conf" + File.separator;
    }

    public static String getRootPath() {
        return System.getProperty("user.dir") + File.separator;
    }
}
