import javax.swing.*;
import java.awt.*;

/**
 * Created by Camilo on 8/20/2017.
 */
class GUI {

    private static JFrame frame;

    private static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    static void initialize(JFrame f) {
        frame = f;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void toggleFullscreen() {
        device.setFullScreenWindow(device.getFullScreenWindow() == null ? frame : null);
        if (device.getFullScreenWindow() != null) {
            frame.setExtendedState(frame.getExtendedState() == Frame.MAXIMIZED_BOTH ? Frame.NORMAL : Frame.MAXIMIZED_BOTH);
            Simulation.setResolution(Toolkit.getDefaultToolkit().getScreenSize());
        }else{
            frame.setExtendedState(Frame.NORMAL);
            Simulation.setResolution(new Dimension(1440,800));
        }
    }

    static JFrame getFrame() {
        return frame;
    }


}
