import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {

    //This is the main method, every Java application starts with this method
    //when you set up your main method, the first line MUST be written as shown
    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setBackground(Color.BLACK);
        frame.setResizable(false);

        try {
            frame.setIconImage(ImageIO.read(new File("GravSim/resources/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("FAILED TO SET APP ICON");
        }

        Dimension monitorResolution = Toolkit.getDefaultToolkit().getScreenSize();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1440, 800);
        frame.setLocation((monitorResolution.width - 1440) / 2, (monitorResolution.height - 850) / 2);
        SimulationPanel simPanel = new SimulationPanel();
        frame.add(simPanel);
        frame.setUndecorated(true);
        frame.setVisible(true);

        ContentManager.initialize(frame);

        simPanel.addMouseListener(new MouseManager());
        frame.addKeyListener(new KeyManager());

        GUI.initialize(frame);


    }
}