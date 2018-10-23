package cn.tz.cj.service.intf;

import java.io.File;

public interface ISystemService {

    boolean checkDBAndInit();

    void impData(File file);

    void expData(File file);

    void deleteUser();

    /**
     * 对数据做任何修改之前，做临时备份，防数据错误导致系统崩溃
     * */
    File tempSaveDataToLocal();

}
