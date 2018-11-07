
import cn.tz.cj.ui.ImageIconMananger;
import cn.tz.cj.ui.LoginForm;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;

public class Entry {

    public static void main(String[] args) {

        UIManager.put("RootPane.setupButtonVisible", false);
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("Tree.expandedIcon", ImageIconMananger.EXPANDED.getImageIcon15_15());
            UIManager.put("Tree.collapsedIcon", ImageIconMananger.COLLAPSED.getImageIcon15_15());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
