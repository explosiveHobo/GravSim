import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

class Body implements Serializable {

    private int leftBound, rightBound, upperBound, lowerBound;

    private int xPos, yPos, mass, angularV, angle;
    private int imageID = 0, age, massID = 0;
    private Color color, imageColor;
    private boolean selected = false;
    private ArrayList<Body> drawingTo = new ArrayList<>();
    private Random rand = new Random();

    private static double G = 50;

    private Point velocity;

    Body(int m, int x, int y, int vx, int vy, Color c) {
        mass = m;
        xPos = x;
        yPos = y;
        angularV = rand.nextInt(17) + 4;
        angle = 0;
        age = 0;
        velocity = new Point(vx, vy);
        color = c;
    }

    Body(int m, int x, int y, int vx, int vy, int imageID) {
        mass = m;
        xPos = x;
        yPos = y;
        angularV = rand.nextInt(17) + 4;
        angle = 0;
        age = 0;
        velocity = new Point(vx, vy);
        this.imageID = imageID;
    }

    void update() {
        ArrayList<Body> allBodies = BodyManager.getAllBodies();

        if (this.xPos < -700 || this.xPos > Simulation.getResolution().width + 700 || this.yPos > Simulation.getResolution().getHeight() + 700 || this.yPos < -700) {
            BodyManager.removeBody(this);
        }

        double differenceInX, differenceInY;
        double forceMag, forceX, forceY, ang, distance;

        int k = 0;
        while (k < allBodies.size()) {

            Body b = allBodies.get(k);

            if (b != this) {

                differenceInX = (xPos - b.getxPos());
                differenceInY = (yPos - b.getyPos());
                distance = (Math.sqrt(Math.pow(differenceInX, 2) + Math.pow(differenceInY, 2)));

                if (distance > 8) {

                    ang = Math.asin((differenceInY / distance));

                    forceMag = (mass * b.getMass() * G) / (Math.pow(distance, 2));

                    forceX = Math.abs(Math.cos(ang) * forceMag);
                    forceY = Math.abs(Math.sin(ang) * forceMag);

                    if (differenceInY > 0) {
                        forceY *= -1;
                    }
                    if (differenceInX > 0) {
                        forceX *= -1;
                    }

                    velocity.setLocation(new Point((int) ((velocity.x + (forceX / mass))),
                            (int) ((velocity.y + (forceY / mass)))));
                }
                if (this.mass < 5000 && b.getMass() < 5000 && b.age > 66 && age > 66 && this.mass >= BodyManager.PLANET_MASS && b.getMass() >= BodyManager.PLANET_MASS
                        && distance <= (b.getMass() / 30) + (this.mass / 30)) {

                    Body tempBody = this;

                    BodyManager.removeBody(this);
                    BodyManager.removeBody(b);

                    int initMass1 = tempBody.mass, initMass2 = b.mass;
                    int totalMass = initMass1 + initMass2;

                    for (int t = 0; t < 6; t++) {

                        rand.setSeed(System.nanoTime() + t);

                        int size = rand.nextInt(11) + 23;

                        if (t > 8 * (initMass1 / totalMass)) {
                            for (int s = 0; s <= (initMass1 / 2) / size; s++) {
                                rand.setSeed(System.nanoTime() + s);

                                BodyManager.spawnBody(size, tempBody.getxPos() + (rand.nextInt(15) - 7),
                                        tempBody.getyPos() + (rand.nextInt(15) - 7), (int) (tempBody.getVelocity().x * (.45 + rand.nextDouble())), (int) (tempBody.getVelocity().y * (.45 + rand.nextDouble())), ImageManipulator.randomColor(tempBody.getImageColor(), tempBody.getMass() > BodyManager.GASGIANT_MASS ? 50 : 24));
                            }
                        }
                        if (t > 8 * (initMass2 / totalMass)) {
                            for (int s = 0; s <= (initMass2 / 2) / size; s++) {
                                rand.setSeed(System.nanoTime() + s);

                                BodyManager.spawnBody(size, b.getxPos() + (rand.nextInt(15) - 7),
                                        b.getyPos() + (rand.nextInt(15) - 7), (int) (b.getVelocity().x * (.45 + rand.nextDouble())), (int) (b.getVelocity().y * (.45 + rand.nextDouble())), ImageManipulator.randomColor(b.getImageColor(), b.getMass() > BodyManager.GASGIANT_MASS ? 50 : 24));
                            }
                        }
                    }
                }
                //If this is the sun, and a non-star body is colliding
                else if (mass >= BodyManager.STAR_MASS && b.getMass() < BodyManager.STAR_MASS
                        && distance + 5 <= (b.getMass() / 30) + (mass / 3000)) {

                    mass += (b.getMass());

                    //uncomment for messages on sun mass gain
                    //Simulation.addString(new PositionedString("+" + b.getMass() + "", b.xPos - 5, b.yPos));
                    BodyManager.removeBody(b);

                } else if (mass >= BodyManager.PLANET_MASS && mass < BodyManager.STAR_MASS && b.getMass() < BodyManager.PLANET_MASS
                        && distance + 6 <= (b.getMass() / 15) + (mass / 30)) {

                    mass += (b.mass / 2.5);

                    Simulation.addString(new PositionedString("+" + b.getMass() / 2.5 + "", this.xPos, this.yPos - 7));
                    BodyManager.removeBody(b);

                } else if (mass < BodyManager.PLANET_MASS && b.getMass() < BodyManager.PLANET_MASS
                        && distance < (b.getMass() / 15) + (mass / 12) && b.age > 66 && age > 66) {

                    if (b.getMass() > mass) {
                        b.setMass(b.getMass() + (mass / 2));
                        //ImageManipulator.blendColor(b, this);
                        BodyManager.removeBody(this);
                        Simulation.addString(new PositionedString("+" + mass / 2 + "", this.xPos, this.yPos - 7));
                        if (b.getMass() > BodyManager.PLANET_MASS) {
                            b.imageID = rand.nextInt(8) + 1;
                        }
                    } else {
                        mass += (b.getMass() / 2);
                        // ImageManipulator.blendColor(this, b);
                        BodyManager.removeBody(b);
                        Simulation.addString(new PositionedString("+" + b.getMass() / 2 + "", this.xPos, this.yPos - 7));
                        if (mass > BodyManager.PLANET_MASS) {
                            imageID = rand.nextInt(8) + 1;
                        }
                    }
                }
            }
            k++;
        }

        xPos += velocity.x / 300;
        yPos += velocity.y / 300;

        if (mass >= BodyManager.STAR_MASS) {
            leftBound = this.getxPos() - (this.getMass() / 2000);
            rightBound = this.getxPos() + (this.getMass() / 2000);
            upperBound = this.getyPos() - (this.getMass() / 2000);
            lowerBound = this.getyPos() + (this.getMass() / 2000);
        } else if (mass < BodyManager.PLANET_MASS) {
            massID = 1;
            leftBound = this.getxPos() - (this.getMass() / 10);
            rightBound = this.getxPos() + (this.getMass() / 10);
            upperBound = this.getyPos() - (this.getMass() / 10);
            lowerBound = this.getyPos() + (this.getMass() / 10);
        } else {
            int oldID = massID;
            massID = (mass > BodyManager.GASGIANT_MASS ? 3 : 2);
            if (oldID != massID) {
                color = Color.black;
            }
            leftBound = this.getxPos() - (this.getMass() / 30);
            rightBound = this.getxPos() + (this.getMass() / 30);
            upperBound = this.getyPos() - (this.getMass() / 30);
            lowerBound = this.getyPos() + (this.getMass() / 30);
        }

        this.angle += angularV;

        if (angle >= 360) {
            angle -= 360;
        }

        age++;
    }

    void addVelocityComponents(double x, double y) {
        this.velocity.x += (x);
        this.velocity.y += (y);
    }

    double getForceWith(Body other) {
        int differenceInX = xPos - other.getxPos();
        int differenceInY = yPos - other.getyPos();
        double distance = Math.sqrt(Math.pow(differenceInX, 2) + Math.pow(differenceInY, 2));
        return (mass * other.getMass() * G) / Math.pow(distance, 2);
    }

    double getPotentialWith(Body t) {
        return (G * this.mass * t.mass) / Point.distance(this.xPos, this.yPos, t.xPos, t.yPos);
    }

    void invert() {
        if (color != null) {
            try {
                color = (new Color(255 - color.getRed(), 255 - color.getGreen(),
                        255 - color.getBlue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (imageID != 0) {
            imageID *= -1;
        }
    }

    double getPotential() {
        ArrayList<Body> allBodies = BodyManager.getAllBodies();
        double potential = 0;
        for (Body allBody : allBodies) {
            if (!allBody.equals(this)) {
                potential += (G * this.mass * allBody.getMass()) / Point.distance(this.xPos,
                        this.yPos, allBody.getxPos(), allBody.getyPos());
            }
        }
        return potential;
    }

    void setSelected(boolean isSelected) {
        this.selected = isSelected;
        if (!isSelected) {
            this.drawingTo.clear();
        }
    }

    boolean isSelected() {
        return selected;
    }

    void drawTo(Body b) {
        drawingTo.add(b);
    }

    boolean isDrawingTo(Body b) {
        for (Body aDrawingTo : drawingTo) {
            if (aDrawingTo.equals(b)) {
                return true;
            }
        }
        return false;
    }

    void dontDrawTo(Body t) {
        drawingTo.remove(t);
    }

    int getxPos() {
        return xPos;
    }

    int getyPos() {
        return yPos;
    }

    int getMass() {
        return mass;
    }

    private void setMass(int mass) {
        this.mass = mass;
    }

    Color getColor() {
        return color;
    }

    void setColor(Color color) {
        this.color = color;
    }

    Point getVelocity() {
        return velocity;
    }

    int getLeftBound() {
        return leftBound;
    }

    int getRightBound() {
        return rightBound;
    }

    int getUpperBound() {
        return upperBound;
    }

    int getLowerBound() {
        return lowerBound;
    }

    int getImageID() {
        return imageID;
    }

    int getAngle() {
        return angle;
    }

    void setAngularVelocity() {
        angularV = 1;
    }

    void setPosition(Point point) {
        this.xPos = point.x;
        this.yPos = point.y;
    }

    Color getImageColor() {
        return imageColor;
    }

    void setImageColor(Color imageColor) {
        this.imageColor = imageColor;
    }
}
