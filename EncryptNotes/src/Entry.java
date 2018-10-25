import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import cn.tz.cj.ui.LoginDialog;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;

public class Entry {

    public static void main(String[] args) {

        UIManager.put("RootPane.setupButtonVisible", false);
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
        BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        UIUtils.setPreferredLookAndFeel();
        if (!NativeInterface.isOpen()) {
            NativeInterface.initialize();
            NativeInterface.open();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LoginDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
