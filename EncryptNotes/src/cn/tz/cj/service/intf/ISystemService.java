package cn.tz.cj.service.intf;

import java.io.File;

public interface ISystemService {

    boolean checkDBAndInit();

    void impData(File file);

    void expData(File file);

    void deleteUser();

}
