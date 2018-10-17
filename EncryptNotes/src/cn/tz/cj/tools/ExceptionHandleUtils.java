package cn.tz.cj.tools;


import org.apache.log4j.Logger;

import javax.swing.*;

public class ExceptionHandleUtils {

    private static final Logger log = Logger.getLogger(ExceptionHandleUtils.class);

    public static void handling(Throwable e){
        log.error(e);
        StackTraceElement[] stackTrace = e.getStackTrace();
        for(StackTraceElement ste : stackTrace){
            log.error(ste);
        }
        JOptionPane.showMessageDialog(null, "Secret 系统错误！！看日志！！", "系统错误", JOptionPane.ERROR_MESSAGE);
        //System.exit(1);
    }

}
