package cn.tz.cj.service.intf;

import java.io.File;

public interface ISystemService {

    public boolean checkDBAndInit();

    public void impData(File file);

    public void expData(File file);

    public void deleteUser();

}
