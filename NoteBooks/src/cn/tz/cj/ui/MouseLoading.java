package cn.tz.cj.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

public class MouseLoading {

    private Component component;

    private Component glassPane;

    public MouseLoading(Component component){
        this.component = component;
        if(component instanceof JFrame){
            glassPane = ((JFrame) component).getGlassPane();
        }else if(component instanceof JDialog){
            glassPane = ((JDialog) component).getGlassPane();
        }
    }

    public void register(){
        glassPane.addMouseListener(new MouseAdapter() {
        });
        glassPane.addMouseMotionListener(new MouseMotionAdapter() {
        });
        glassPane.setVisible(false);
    }

    public void startLoading(){
        component.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        glassPane.setVisible(true);
    }

    public void stopLoading(){
        component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        glassPane.setVisible(false);
    }

}
