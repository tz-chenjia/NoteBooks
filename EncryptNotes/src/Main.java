import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import cn.tz.cj.ui.LoginForm;
import cn.tz.cj.ui.MainForm;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        UIManager.put("RootPane.setupButtonVisible", false);
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        UIUtils.setPreferredLookAndFeel();
        if(!NativeInterface.isOpen()){
            NativeInterface.initialize();
            NativeInterface.open();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LoginForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
