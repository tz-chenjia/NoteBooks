package cn.tz.cj.service.intf;

import cn.tz.cj.entity.UserConfigs;

public interface IConfigsService {

    void saveUserConfigs(UserConfigs configs);

    UserConfigs getUserConfigs();

}
