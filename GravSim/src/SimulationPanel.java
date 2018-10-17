import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.awt.Color.white;
import static java.awt.MouseInfo.getPointerInfo;

public class SimulationPanel extends JPanel {

    private static final Font NORMAL_FONT = new Font("IBM Plex Serif", Font.PLAIN, 17), HEADER_FONT = new Font("Roboto Slab", Font.BOLD, 35);

    private static ArrayList<BufferedImage> planetImages = new ArrayList<>(), gasGiantImages = new ArrayList<>();
    private static ArrayList<BufferedImage> invertedPlanetImages = new ArrayList<>(), invertedGiantImages = new ArrayList<>();

    private static BufferedImage spaceBG, sunImage, invertedSunImage;

    //The colors our lines can be
    private static Color[] colors = {Color.red, Color.blue, Color.green, Color.ORANGE, Color.CYAN, Color.WHITE, Color.MAGENTA,
            Color.PINK, new Color(124, 100, 57), Color.DARK_GRAY, new Color(0, 79, 62), new Color(90, 89, 30),
            new Color(67, 75, 65), new Color(30, 0, 111), new Color(9, 118, 0), Color.orange.darker()};

    private static Timer timer;

    private static final String resourcesFolderPath = "GravSim/resources/";

    //Constructor for main SimulationPanel
    SimulationPanel() {

        try {
            sunImage = ImageIO.read(new File(resourcesFolderPath + "sol.png"));
            spaceBG = ImageIO.read(new File(resourcesFolderPath + "space.jpg"));
            invertedSunImage = ImageIO.read(new File(resourcesFolderPath + "invertsol.png"));
            for (int k = 1; k <= 4; k++) {
                gasGiantImages.add(ImageIO.read(new File(resourcesFolderPath + "gas" + +(k) + ".png")));
                invertedGiantImages.add(ImageIO.read(new File(resourcesFolderPath + "invertgas" + k + ".png")));
            }
            for (int k = 1; k <= 8; k++) {
                planetImages.add(ImageIO.read(new File(resourcesFolderPath + "planet" + (k) + ".png")));
                invertedPlanetImages.add(ImageIO.read(new File(resourcesFolderPath + "invertplanet" + k + ".png")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Simulation.initialize(new Dimension(1440, 800));
        setSize(Simulation.getResolution());

        setDoubleBuffered(true);
        setBackground(Color.BLACK);

        timer = new Timer(90, e -> {
            Simulation.update();
            repaint();
            try {
                Thread.sleep(3);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            timer.setDelay(Simulation.getTimerDelay());
        });

        timer.setInitialDelay(250);
        timer.start();
    }

    public void update(Graphics g) {
    }

    public void paintComponent(Graphics g) {

        DecimalFormat dataFormat = Simulation.getDataFmt();
        Dimension dim = Simulation.getResolution();

        Image offscreen = createImage(dim.width, dim.height);
        Graphics2D bufferGraphics = (Graphics2D) offscreen.getGraphics();

        bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bufferGraphics.setColor(white);
        bufferGraphics.drawImage(spaceBG, 0, 0, (int) Simulation.getResolution().getWidth(), (int) Simulation.getResolution().getWidth(), this);
        drawControls(bufferGraphics);

        int xDiff, yDiff;
        Point initialMousePos;

        //if the mouse is down, we are likely creating a new body.
        if (MouseManager.isMouseDown()) {

            initialMousePos = MouseManager.getInitMousePos();

            //get the delta X and delta Y of mouse position from when we started holding down the mouse button
            MouseManager.setXDiff(initialMousePos.x - getPointerInfo().getLocation().x);
            MouseManager.setYDiff(initialMousePos.y - getPointerInfo().getLocation().y);

            xDiff = MouseManager.getXDiff();
            yDiff = MouseManager.getYDiff();

            if (!BodyManager.isAffectingBody()) {

                int sizeIterator = MouseManager.getSizeIterator();

                MouseManager.setSizeIterator(sizeIterator + 1);

                //Depending on how long we've held down the mouse, our sizeIterator will increase with time
                if (sizeIterator < BodyManager.PLANET_MASS) {
                    bufferGraphics.draw(new Ellipse2D.Double(initialMousePos.x, initialMousePos.y, (sizeIterator / 10), (sizeIterator / 10)));
                    bufferGraphics.drawLine((initialMousePos.x + (sizeIterator / 20)), (initialMousePos.y + (sizeIterator / 20)),
                            (initialMousePos.x + xDiff), (initialMousePos.y + yDiff));
                } else {
                    bufferGraphics.draw(new Ellipse2D.Double(initialMousePos.x, initialMousePos.y, (sizeIterator / 15), (sizeIterator / 15)));
                    bufferGraphics.drawLine((initialMousePos.x + (sizeIterator / 30)), (initialMousePos.y + (sizeIterator / 30)),
                            (initialMousePos.x + xDiff), (initialMousePos.y + yDiff));
                }
            }
            //else if we are affecting a pre existing body
            else {
                //get the delta X and delta Y of mouse position from when we started holding down the mouse button
                xDiff = (initialMousePos.x - getPointerInfo().getLocation().x);
                yDiff = (initialMousePos.y - getPointerInfo().getLocation().y);

                Body selectedBody = BodyManager.getSelectedBody();

                bufferGraphics.setColor(white);

                //allow users to move bodies with rmb
                if (selectedBody.getMass() < BodyManager.PLANET_MASS) {

                    bufferGraphics.drawLine((selectedBody.getLeftBound() + ((selectedBody.getRightBound() - selectedBody.getLeftBound()) / 4)),
                            selectedBody.getUpperBound() + ((selectedBody.getLowerBound() - selectedBody.getUpperBound()) / 4), selectedBody.getxPos() + xDiff,
                            selectedBody.getyPos() + yDiff);
                } else if (selectedBody.getMass() >= BodyManager.PLANET_MASS && selectedBody.getMass() < BodyManager.STAR_MASS) {

                    bufferGraphics.drawLine((selectedBody.getLeftBound() + ((selectedBody.getRightBound() - selectedBody.getLeftBound()) / 4)),
                            selectedBody.getUpperBound() + ((selectedBody.getLowerBound() - selectedBody.getUpperBound()) / 4), selectedBody.getxPos() + xDiff,
                            selectedBody.getyPos() + yDiff);
                }
            }
        }

        int iterator = 0;
        int selectionCounter = 0;

        //foreach body that is in our Bodies ArrayList
        for (Body body : BodyManager.getAllBodies()) {
            //If it's selected, increment the selectionCounter
            if (body.isSelected()) {
                selectionCounter++;
            }
        }

        bufferGraphics.setColor(154 - Simulation.getTimerDelay() > 109 ? (154 - Simulation.getTimerDelay() > 129 ? Color.red : Color.orange) : Color.white);

        //format and draw time and speed setting
        bufferGraphics.drawString("Speed = " + (154 - Simulation.getTimerDelay()), ImageObserver.WIDTH + 8, 15);

        bufferGraphics.setColor(Color.white);

        bufferGraphics.drawString("Number of bodies = " + BodyManager.getAllBodies().size(), ImageObserver.WIDTH + 8, 35);
        bufferGraphics.drawString("Trails " + (Simulation.trails() ? "on" : "off"), ImageObserver.WIDTH + 8, 55);
        bufferGraphics.drawString("Messages " + (Simulation.isStringsOn() ? "on" : "off"), ImageObserver.WIDTH + 8, 75);

        bufferGraphics.setFont(NORMAL_FONT);

        //if Trails are on, draw all the trails
        if (Simulation.trails()) {
            for (Trail trail : Simulation.getAllTrails()) {
                if (trail.isValid()) {
                    bufferGraphics.setColor(trail.getColor());
                    Shape circle = new Ellipse2D.Double(trail.getX(), trail.getY(), trail.getSize(), trail.getSize());
                    bufferGraphics.draw(circle);
                }
            }
        }

        ArrayList<Body> allBodies = BodyManager.getAllBodies();

        //loop through each Body, storing current Body from allBodies in Body b
        for (Body b : allBodies) {

            //if more than 1 Body is selected, we must draw some relation lines
            if (selectionCounter > 1) {

                //while still keeping track of Body b, lets loop again to check and specify which bodies should draw to which bodies
                for (Body b2 : allBodies) {

                    if (!b2.isSelected() || !b.isSelected()) {
                        b.dontDrawTo(b2);
                        b2.dontDrawTo(b);
                    }

                    if (!b2.isSelected()) {
                        continue;
                    }

                    //if we have a draw order from b to b2
                    if (b.isDrawingTo(b2) && iterator < (selectionCounter * (selectionCounter - 1)) / 2) {

                        //increment the line count
                        iterator++;

                        try {
                            //pick a color from our colors for the line
                            bufferGraphics.setColor(colors[iterator - 1]);
                        } catch (Exception e) {
                            continue;
                        }

                        bufferGraphics.setFont(NORMAL_FONT);

                        //finally, draw the line and appropriate data
                        bufferGraphics.drawLine(b2.getxPos(), b2.getyPos(), b.getxPos(), b.getyPos());
                        bufferGraphics.drawString("Distance = " + dataFormat.format(Point.distance(b.getxPos(), b.getyPos(), b2.getxPos(), b2.getyPos())),
                                15, (getHeight() - (iterator * 16)) - 5);
                        bufferGraphics.drawString("Force = " + dataFormat.format(b.getForceWith(b2)), 165,
                                (getHeight() - (iterator * 16)) - 5);
                        bufferGraphics.drawString("Potential = " + dataFormat.format(b.getPotentialWith(b2)), 320,
                                (getHeight() - (iterator * 16)) - 5);
                    }

                    //specify draw orders
                    if (b != b2 && !b2.isDrawingTo(b)) {
                        b.drawTo(b2);
                    }
                }
            }

            BufferedImage image = null;

            //if the body has a valid imageID
            if (b.getImageID() < 9 && b.getImageID() != 0) {

                boolean invert = false;

                //this body is selected, and therefore inverted
                if (b.getImageID() < 0) {
                    invert = true;
                }

                int id = b.getImageID();

                if (b.getMass() >= BodyManager.STAR_MASS) {
                    image = invert ? invertedSunImage : sunImage;
                } else if (b.getMass() < BodyManager.GASGIANT_MASS) {
                    image = invert ? invertedPlanetImages.get(Math.abs(id) - 1) : planetImages.get(id - 1);
                } else {
                    image = invert ? invertedGiantImages.get(((Math.abs(id) + 1) / 2) - 1) : gasGiantImages.get(((id + 1) / 2) - 1);
                }
                //Set average image color for trails
                if (b.getImageColor() == null || b.getImageColor() != b.getColor()) {
                    b.setImageColor(ImageManipulator.averageColor(image, image.getWidth(), image.getHeight()).brighter());
                    b.setColor(b.getImageColor());
                }
            }
            if (b.getColor() != null) {
                bufferGraphics.setColor(b.getColor());
            }

            int diameter = b.getRightBound() - b.getLeftBound(), radius = diameter / 2;
            int toSubtract = (diameter / (b.getMass() > BodyManager.STAR_MASS ? 2000 : 10));

            //differently sized Bodies must scale with their mass, but not linearly.  This would result in Stars
            //taking up the whole screen.  The larger a Body gets, the smaller it is ratio-wise
            if (b.getMass() > BodyManager.PLANET_MASS) {
                bufferGraphics.rotate(Math.toRadians(b.getAngle()), b.getxPos() - toSubtract, b.getyPos() - toSubtract);
                bufferGraphics.drawImage(image, (b.getxPos() - radius), b.getyPos() - radius, diameter, diameter, this);

                bufferGraphics = (Graphics2D) offscreen.getGraphics();
            } else {
                Shape circle = new Ellipse2D.Double((b.getxPos() - radius), (b.getyPos() - radius), radius, radius);

                if (b.isSelected()) {
                    bufferGraphics.setColor(b.getColor().brighter().brighter());
                }
                bufferGraphics.fill(circle);
            }
        }

        Point relativeMousePosition = new Point((MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x), (MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y));
        Body body;

        if ((body = BodyManager.inWhichBody(relativeMousePosition)) != null || selectionCounter == 1) {
            if (selectionCounter == 1) {
                for (Body b : allBodies) {
                    if (b.isSelected()) {
                        body = b;
                    }
                }
            }
            assert body != null;
            drawBodyData(bufferGraphics, body);
        }

        if (Simulation.isStringsOn()) {
            //draw all PositionedStrings and update them if the game isn't paused
            for (int k = 0; k < Simulation.getAllStrings().size(); k++) {
                PositionedString currentString = Simulation.getAllStrings().get(k);

                bufferGraphics.setColor(currentString.getMyColor());

                if (currentString.getMyString().toCharArray()[0] == 'S' || currentString.getMyString().toCharArray()[0] == 'L') {
                    bufferGraphics.setFont(HEADER_FONT);
                    bufferGraphics.drawString(currentString.getMyString(), currentString.getX(), currentString.getY());
                } else {
                    bufferGraphics.setFont(NORMAL_FONT);
                    bufferGraphics.drawString("+" + dataFormat.format(Double.parseDouble(currentString.getMyString().substring(1))), currentString.getX(), currentString.getY());
                }

                //make PositionedStrings fade by decreasing Alpha
                try {
                    Simulation.getAllStrings().get(k).setMyColor(new Color(currentString.getMyColor().getRed(),
                            currentString.getMyColor().getGreen(), currentString.getMyColor().getBlue(),
                            currentString.getMyColor().getAlpha() - 4));
                } catch (Exception e) {
                    Simulation.getAllStrings().remove(k);
                }
            }
        }

        bufferGraphics.dispose();
        g.drawImage(offscreen, 0, 0, this);
    }

    private static void drawBodyData(Graphics2D g, Body body) {
        g.setColor(white);
        g.setFont(NORMAL_FONT);

        g.drawString("Mass: " + body.getMass(), Simulation.getResolution().width - 240, 620);
        g.drawString("Vx: " + body.getVelocity().x, Simulation.getResolution().width - 240, 645);
        g.drawString("Vy: " + body.getVelocity().y, Simulation.getResolution().width - 240, 670);
        g.drawString("Potential: " + Simulation.getDataFmt().format(body.getPotential()), Simulation.getResolution().width - 240, 695);
    }

    private static void drawControls(Graphics2D g) {
        g.setColor(white);
        g.setFont(HEADER_FONT);
        g.drawString("CONTROLS", Simulation.getResolution().width - 370, 40);

        g.setFont(NORMAL_FONT);
        g.drawString("Q/E: increase/decrease simulation speed", Simulation.getResolution().width - 380, 65);
        g.drawString("T: toggle trails off and on", Simulation.getResolution().width - 380, 85);
        g.drawString("I: toggle info messages off and on", Simulation.getResolution().width - 380, 105);
        g.drawString("D: deselect all bodies", Simulation.getResolution().width - 380, 125);
        g.drawString("A: select all bodies", Simulation.getResolution().width - 380, 145);
        g.drawString("Space: pause and unpause", Simulation.getResolution().width - 380, 165);
        g.drawString("Mouse: select/create bodies (left / right click)", Simulation.getResolution().width - 380, 185);
        g.drawString("S: save current bodies as a loadout", Simulation.getResolution().width - 380, 205);
        g.drawString("L: load an existing .grav file", Simulation.getResolution().width - 380, 225);

        g.setColor(Color.red);
        g.drawString("X: quit the program", Simulation.getResolution().width - 380, 245);

        g.setColor(white);
        g.drawString("Programmed by Camilo Conde :)", Simulation.getResolution().width - 300, Simulation.getResolution().height - 20);
    }
}