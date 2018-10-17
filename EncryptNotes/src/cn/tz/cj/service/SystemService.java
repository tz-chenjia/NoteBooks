package cn.tz.cj.service;

import cn.tz.cj.dao.SystemDao;
import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.rule.EDBType;
import cn.tz.cj.service.intf.IConfigsService;
import cn.tz.cj.service.intf.ISystemService;
import cn.tz.cj.tools.JDBCUtils;

import javax.swing.*;

public class SystemService implements ISystemService {
    SystemDao systemDao = new SystemDao();

    @Override
    public boolean checkDBAndInit() {
        boolean isOk = true;
        IConfigsService configsService = new ConfigsService();
        UserConfigs configs = configsService.getUserConfigs();
        if(configs != null){
            // 验证连接是否可用
            boolean useDB = JDBCUtils.isUseDB(configs.getDbHost(), configs.getDbPort(), configs.getDbName(), configs.getDbUserName(), configs.getDbPassword(), EDBType.toEDBType(configs.getDbType()));
            if(useDB){
                if(!systemDao.tablesExists()){
                    systemDao.initDBTable();
                }
            }else{
                // 提示数据库不能连
                JOptionPane.showMessageDialog(null, "请检查您的配置及网络！", "配置连接失败", JOptionPane.WARNING_MESSAGE);
                isOk = false;
            }
        }else{
            // 提示需要配置
            JOptionPane.showMessageDialog(null, "请设置您的配置！", "配置未设置", JOptionPane.WARNING_MESSAGE);
            isOk = false;
        }
        return isOk;
    }
}
