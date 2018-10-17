package cn.tz.cj.service.intf;

import cn.tz.cj.entity.UserConfigs;

import java.awt.*;

public interface IConfigsService {

    public void saveUserConfigs(UserConfigs configs);

    public UserConfigs getUserConfigs();

}
