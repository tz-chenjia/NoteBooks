package cn.tz.cj.ui;

import javax.swing.*;
import java.awt.*;

public class FormSetting {

    public static int getWindowWidth(){
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        return (int) screensize.getWidth();
    }

    public static int getWindowHeight(){
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        return (int) screensize.getHeight();
    }

    public static void setFrameLocation(JFrame frame){
        double w = (getWindowWidth() - frame.getWidth()) / 2;
        double h = (getWindowHeight() - frame.getHeight()) / 2;
        frame.setLocation((int) w, (int)h);
    }

}
