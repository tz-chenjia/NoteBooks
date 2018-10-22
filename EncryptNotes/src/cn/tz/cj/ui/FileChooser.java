package cn.tz.cj.ui;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileChooser {

    private static final Logger log = Logger.getLogger(FileChooser.class);

    public static File impFileChooser() {
        File file = null;
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        //是否可多选
        fc.setMultiSelectionEnabled(false);
        //选择模式，可选择文件和文件夹
        // fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // 设置是否显示隐藏文件
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        //设置文件筛选器
        fc.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".sql") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "*.sql";
            }
        });

        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        return file;
    }

    public static File expFileChooser() {
        JFileChooser chooser = new JFileChooser();
        //设置文件筛选器
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".sql") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "*.sql";
            }
        });
        chooser.setAcceptAllFileFilterUsed(false);
        int option = chooser.showSaveDialog(null);
        if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
            File file = chooser.getSelectedFile();

            String fname = chooser.getName(file);	//从文件名输入框中获取文件名

            //假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
            if(fname.indexOf(".sql")==-1){
                file=new File(chooser.getCurrentDirectory(),fname+".sql");
                System.out.println("renamed");
                System.out.println(file.getName());
            }
            return file;
        }
        return null;
    }

}
