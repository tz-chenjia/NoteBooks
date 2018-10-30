package cn.tz.cj.service.intf;

import cn.tz.cj.entity.UserConfigs;

import java.util.Properties;

public interface IConfigsService {

    void saveUserConfigs(UserConfigs configs);

    UserConfigs getUserConfigs();

    UserConfigs setUserEmail(String userEmail);

    Properties getDBProperties();

}
