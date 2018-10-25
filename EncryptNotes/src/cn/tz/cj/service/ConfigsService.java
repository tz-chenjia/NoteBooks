package cn.tz.cj.service;

import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.tools.EncryptUtils;
import cn.tz.cj.tools.FileRWUtils;
import cn.tz.cj.tools.GlobalExceptionHandling;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Properties;

public class ConfigsService implements IConfigsService {

    private static final String USER_PROPERTIES = "my.properties";

    @Override
    public void saveUserConfigs(UserConfigs configs) {
        OutputStream outputFile = null;
        try {
            File dbPropFile = getSingleConf(USER_PROPERTIES);
            Properties p = new Properties();
            outputFile = new FileOutputStream(dbPropFile);
            p.setProperty("db_type", configs.getDbType());
            p.setProperty("db_name", configs.getDbName());
            p.setProperty("db_host", configs.getDbHost());
            p.setProperty("db_port", configs.getDbPort());
            p.setProperty("db_driverClass", configs.getDbDriverClass());
            p.setProperty("db_userName", configs.getDbUserName());
            p.setProperty("db_password", EncryptUtils.e(configs.getDbPassword(), ConfigsService.class.getName()));
            p.setProperty("db_email", configs.getUserEmail());
            p.store(outputFile, null);
        } catch (Throwable e) {
            GlobalExceptionHandling.exceptionHanding(e);
        } finally {
            if (outputFile != null) {
                try {
                    outputFile.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
        }
    }

    @Override
    public UserConfigs getUserConfigs(){
        BufferedReader bufferedReader = null;
        try {
            File propFile = getSingleConf(USER_PROPERTIES);
            Properties properties = new Properties();
            bufferedReader = new BufferedReader(new FileReader(propFile));
            properties.load(bufferedReader);
            if (properties.isEmpty()) {
                return null;
            } else {
                UserConfigs userConfigs = new UserConfigs();
                userConfigs.setDbType(properties.getProperty("db_type"));
                userConfigs.setDbName(properties.getProperty("db_name"));
                userConfigs.setDbDriverClass(properties.getProperty("db_driverClass"));
                userConfigs.setDbHost(properties.getProperty("db_host"));
                userConfigs.setDbPort(properties.getProperty("db_port"));
                userConfigs.setDbUserName(properties.getProperty("db_userName"));
                userConfigs.setDbPassword(EncryptUtils.d(properties.getProperty("db_password"), ConfigsService.class.getName()));
                userConfigs.setUserEmail(properties.getProperty("db_email"));
                return userConfigs;
            }
        }catch (Throwable e) {
            GlobalExceptionHandling.exceptionHanding(e);
        }finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
        }
       return null;
    }

    @Override
    public UserConfigs setUserEmail(String userEmail){
        UserConfigs userConfigs = getUserConfigs();
        if(userConfigs != null){
            userConfigs.setUserEmail(userEmail);
            saveUserConfigs(userConfigs);
        }
        return userConfigs;
    }

    public static Image getImage(String name){
        Image img = null;
        try {
            img = ImageIO.read(ConfigsService.class.getResource("../resource/images/" + name));
        } catch (IOException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        }
        return img;
    }

    private static File getSingleConf(String conf){
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
