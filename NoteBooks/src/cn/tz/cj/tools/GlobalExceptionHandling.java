package cn.tz.cj.tools;

import org.apache.log4j.Logger;

import javax.swing.*;

public class GlobalExceptionHandling {

    private static Logger log = Logger.getLogger(GlobalExceptionHandling.class);

    public static void exceptionHanding(Throwable e){
        log.error(e);
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement ste : stackTrace) {
            log.error(ste);
        }
        JOptionPane.showMessageDialog(null, "系统错误，请查看日志，可以通过'紧急恢复'按钮进行数据恢复。", "系统错误", JOptionPane.ERROR_MESSAGE);
    }

}
