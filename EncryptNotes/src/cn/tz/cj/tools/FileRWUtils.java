package cn.tz.cj.tools;

import cn.tz.cj.entity.UserConfigs;
import cn.tz.cj.service.ConfigsService;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class FileRWUtils {

    private static Logger log = Logger.getLogger(FileRWUtils.class);

    public static boolean existsAndCreate(String filePath){
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        if (file.exists()) {
            return true;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                GlobalExceptionHandling.exceptionHanding(e);
            }
            return false;
        }
    }

    public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath){
        log.info(oldPath);
        (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
        File a = new File(oldPath);
        String[] file = a.list();
        File temp = null;
        for (int i = 0; i < file.length; i++) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file[i]);
            } else {
                temp = new File(oldPath + File.separator + file[i]);
            }
            if (temp.isFile()) {
                FileInputStream input = null;
                FileOutputStream output = null;
                try {
                    input = new FileInputStream(temp);
                    output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                            output.close();
                        } catch (IOException e) {
                            GlobalExceptionHandling.exceptionHanding(e);
                        }
                    }
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            GlobalExceptionHandling.exceptionHanding(e);
                        }
                    }
                }
            }
            if (temp.isDirectory()) {// 如果是子文件夹
                copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
            }
        }

    }

    public static void inputstreamtofile(InputStream ins,File file){
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        } catch (IOException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        }finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
            if(ins != null){
                try {
                    ins.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
        }
    }

    public static String read(File file){
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer str = new StringBuffer();
        try {
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            br = new BufferedReader(isr);
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                str.append(lineStr);
            }
        } catch (IOException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
        }
        return str.toString();
    }

    public static String read(InputStreamReader isr){
        BufferedReader br = null;
        StringBuffer str = new StringBuffer();
        try {
            br = new BufferedReader(isr);
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                str.append(lineStr);
            }
        } catch (IOException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
        }
        return str.toString();
    }

    public static Properties readProperties(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            Properties properties = new Properties();
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                properties.load(bufferedReader);
            } catch (FileNotFoundException e) {
                GlobalExceptionHandling.exceptionHanding(e);
            } catch (IOException e) {
                GlobalExceptionHandling.exceptionHanding(e);
            }
            return properties;
        }else {
            return null;
        }
    }

    public static void writeProperties(File file, Properties prop){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            prop.store(out, null);
        } catch (IOException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
        }
    }

    public static void write(File file, String content){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            out.write(content);
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        } catch (IOException e) {
            GlobalExceptionHandling.exceptionHanding(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    GlobalExceptionHandling.exceptionHanding(e);
                }
            }
        }

    }

}
