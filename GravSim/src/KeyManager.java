import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener {

    //Handle key presses, which can:
    //  Pause game (Space)
    //  Toggle visibility and creation of Trails (T) helps speed up on older machines
    //  Change speed settings/tick rate (Q/E)
    //  Deselect all Bodies (Esc)
    //  Select all Bodies (A)
    //  Save current bodies as a loadout file (S)
    //  Load a loadout file (L)
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:

                Simulation.pause();
                break;
            case KeyEvent.VK_T:

                if (Simulation.trails()) {

                    Simulation.setTrails(false);

                    for (Trail trail : Simulation.getAllTrails()) {
                        trail.invalidate();
                    }
                } else {
                    Simulation.setTrails(true);
                }
                break;
            case KeyEvent.VK_E:
            case KeyEvent.VK_Q:

                Simulation.cycleSpeed(e.getKeyCode() == KeyEvent.VK_E);
                break;
            case KeyEvent.VK_D:

                for (Body body : BodyManager.getAllBodies()) {
                    if (body.isSelected()) {
                        body.setSelected(false);
                        body.invert();
                    }
                }
                break;
            case KeyEvent.VK_A:
                for (Body body : BodyManager.getAllBodies()) {
                    if (!body.isSelected()) {
                        body.setSelected(true);
                        body.invert();
                    }
                }
                break;
            case KeyEvent.VK_X:
                System.exit(0);
                break;
            case KeyEvent.VK_S:
                Simulation.savePreset();
                break;
            case KeyEvent.VK_L:
                Simulation.loadPreset();
                break;
            case KeyEvent.VK_F:
                GUI.toggleFullscreen();
                break;
            case KeyEvent.VK_PLUS:
                //     Simulation.zoomIn();
                break;
            case KeyEvent.VK_MINUS:
                //    Simulation.zoomOut();
                break;
            case KeyEvent.VK_I:
                Simulation.setStringsOn(!Simulation.isStringsOn());
                Simulation.getAllStrings().clear();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
