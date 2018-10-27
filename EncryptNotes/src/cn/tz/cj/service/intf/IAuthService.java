package cn.tz.cj.service.intf;

public interface IAuthService {

    boolean login(String email, String pwd);

    boolean loginOut(boolean isSaveTempData);

    boolean editUserInfo(String email, String pwd);

}
