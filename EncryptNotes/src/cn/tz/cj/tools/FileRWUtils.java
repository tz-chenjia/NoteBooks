package cn.tz.cj.tools;

import java.io.*;

public class FileRWUtils {

    public static boolean exists(String filePath) {
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if(!fileParent.exists()){
            fileParent.mkdirs();
        }
        if (file.exists()) {
            return true;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                ExceptionHandleUtils.handling(e);
            }
            return false;
        }
    }

    public static String read(File file) {
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
            ExceptionHandleUtils.handling(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    ExceptionHandleUtils.handling(e);
                }
            }
        }
        return str.toString();
    }

    public static String read(InputStreamReader isr) {
        BufferedReader br = null;
        StringBuffer str = new StringBuffer();
        try {
            br = new BufferedReader(isr);
            String lineStr;
            while ((lineStr = br.readLine()) != null) {
                str.append(lineStr);
            }
        } catch (IOException e) {
            ExceptionHandleUtils.handling(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    ExceptionHandleUtils.handling(e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    ExceptionHandleUtils.handling(e);
                }
            }
        }
        return str.toString();
    }

    public static void write(File file, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            out.write(content);
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        } catch (IOException e) {
            ExceptionHandleUtils.handling(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ExceptionHandleUtils.handling(e);
                }
            }
        }

    }

}
