import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

class Simulation {

    private static int timerDelay = 40;

    private static Dimension resolution;

    private static boolean trailsOn = false, stringsOn = false, paused = false;

    private static ArrayList<PositionedString> allStrings = new ArrayList<>();
    private static ArrayList<Trail> allTrails = new ArrayList<>();

    private static DecimalFormat dataFormat = new DecimalFormat(".##");

    private static Body sol = new Body(100000, 675, 380, 0, 0, 1);

    static void initialize(Dimension size) {

        sol.setAngularVelocity();
        BodyManager.getAllBodies().add(sol);

        resolution = size;

       // Body planet = new Body(290, 350, 450, 0, -2000, 8);
       // BodyManager.getAllBodies().add(planet);

        //Body fleeboTheMoon = new Body(50, 350, 461, 365, -1750, Color.gray.brighter());
        //BodyManager.getAllBodies().add(fleeboTheMoon);
    }

    //update the simulation
    static void update() {

        ArrayList<Body> allBodies = BodyManager.getAllBodies();

        Body b;

        if (!paused) {

            if (trailsOn) {
                updateTrails();
            }
            for (int k = 0; k < allBodies.size(); k++) {
                b = allBodies.get(k);
                if (trailsOn) {
                    addTrail(b);
                }
                b.update();
            }
        }
        if (stringsOn) {
            updateStrings();
        }

        System.gc();
    }

    private static void updateStrings() {

        for (PositionedString currentString : allStrings) {
            currentString.setMyIterator(currentString.getMyIterator() + 1);

            //All PositionedStrings must slowly fade and rise upwards at a predetermined rate
            if (currentString.getMyIterator() > 6) {
                currentString.setMyIterator(0);
                currentString.setY(currentString.getY() - 1);
            }
        }
    }

    private static void updateTrails() {
        for (int k = 0; k < allTrails.size(); k++) {
            allTrails.get(k).update();

            if (!allTrails.get(k).isValid()) {
                allTrails.remove(k);
            }
        }
    }

    static DecimalFormat getDataFmt() {
        return dataFormat;
    }

    static ArrayList<PositionedString> getAllStrings() {
        return allStrings;
    }

    static Dimension getResolution() {
        return resolution;
    }

    static void pause() {
        paused = !paused;
    }

    static void cycleSpeed(boolean forwards) {
        if (forwards) {
            if (timerDelay > 4) {
                timerDelay -= 2;
            }
        } else {
            if (timerDelay < 150) {
                timerDelay += 2;
            }
        }
    }

    static void setTrails(boolean trails) {
        trailsOn = trails;
    }

    static ArrayList<Trail> getAllTrails() {
        return allTrails;
    }

    static boolean trails() {
        return trailsOn;
    }

    static int getTimerDelay() {
        return timerDelay;
    }

    //saves existing bodies as a preset to be loaded later
    static void savePreset() {

        boolean wasPaused = paused;

        if (!paused) {
            pause();
        }

        if (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getFullScreenWindow() != null) {
            GUI.toggleFullscreen();
        }

        FileOutputStream fileOut;
        String fileName;
        JFileChooser fileChooser = new JFileChooser("GravSim/presets/");
        fileChooser.setDragEnabled(true);
        fileChooser.setMultiSelectionEnabled(true);
        ContentManager.registerDeleteFileAction(fileChooser);

        if (fileChooser.showSaveDialog(GUI.getFrame()) == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();
            fileName = file.getName() + ".grav";

            try {
                fileOut = new FileOutputStream("GravSim/presets/" + fileName, true);
                ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
                outStream.writeObject(BodyManager.getAllBodies());
                PositionedString saveMessage = new PositionedString("Saved as " + fileName, (int) (resolution.getWidth() / 2) - 200, (int) (resolution.getHeight() / 2) - 80);
                saveMessage.setMyIterator(300);
                allStrings.add(saveMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (paused && !wasPaused) {
            pause();
        }
    }

    //loads a preset of bodies
    static void loadPreset() {

        boolean wasPaused = paused;

        if (!paused) {
            pause();
        }

        if (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getFullScreenWindow() != null) {
            GUI.toggleFullscreen();
        }

        boolean selected = false;

        FileInputStream inputStream;
        String fileName;

        JFileChooser fileChooser = new JFileChooser("GravSim/presets/");
        fileChooser.setDragEnabled(true);
        fileChooser.setMultiSelectionEnabled(true);
        ContentManager.registerDeleteFileAction(fileChooser);

        if (fileChooser.showOpenDialog(GUI.getFrame()) == JFileChooser.APPROVE_OPTION) {

            fileName = fileChooser.getSelectedFile().getName();

            try {
                inputStream = new FileInputStream("GravSim/presets/" + fileName);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                BodyManager.setAllBodies((ArrayList<Body>) objectInputStream.readObject());
                allTrails.clear();

                PositionedString saveMessage = new PositionedString("Loaded " + fileName, (int) (resolution.getWidth() / 2) - 200, (int) (resolution.getHeight() / 2) - 80);
                saveMessage.setMyIterator(300);
                allStrings.add(saveMessage);
                selected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!selected && paused && !wasPaused) {
            pause();
        }
    }

    static void setResolution(Dimension r) {
        resolution = r;
    }

    static boolean isStringsOn() {
        return stringsOn;
    }

    static void setStringsOn(boolean stringsOn) {
        Simulation.stringsOn = stringsOn;
    }

    private static void addTrail(Body b) {
        Trail trail;
        if (b.getMass() < BodyManager.STAR_MASS) {
            trail = new Trail((b.getLeftBound() + ((b.getRightBound() - b.getLeftBound() - 4) / 2)),
                    b.getUpperBound() + ((b.getLowerBound() - b.getUpperBound() - 4) / 2),
                    b.getMass() >= BodyManager.PLANET_MASS ? (b.getMass() < BodyManager.GASGIANT_MASS ? 3 : 4) : 1);
            allTrails.add(trail);
            if (b.getMass() < BodyManager.PLANET_MASS) {
                trail.setMaxCounter(12);
            } else {
                trail.setColor(b.getImageColor() != null ? b.getColor() : Color.white);
            }
            allTrails.add(trail);
        }
    }

    //add a PositionedString to be displayed and eventually disappear
    static void addString(PositionedString s) {
        allStrings.add(s);
    }
}
