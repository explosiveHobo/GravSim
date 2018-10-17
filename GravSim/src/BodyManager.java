import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

class BodyManager {

    static final int STAR_MASS = 10000, GASGIANT_MASS = 350, PLANET_MASS = 100;
    private static boolean affectingBody = false;
    private static Body selectedBody;
    private static ArrayList<Body> allBodies = new ArrayList<>();

    //creates a new Body with respective mass, x and y position, and velocity components
    static void spawnBody(int mass, int x, int y, int vx, int vy, Color color) {

        Body b;

        if (mass > PLANET_MASS) {
            Random random = new Random();
            b = new Body(mass, x, y, vx, vy, random.nextInt(8) + 1);
            allBodies.add(b);
        } else {
            b = new Body(mass, x, y, vx, vy, color);
            allBodies.add(b);
        }
        b.update();
        b.setPosition(new Point(x, y));
    }

    //removes a specified Body, requires direct Alias
    static void removeBody(Body b) {
        allBodies.remove(b);
    }

    //returns which Body is at Point p
    static Body inWhichBody(Point p) {
        for (Body b : allBodies) {
            if (p.x > b.getLeftBound() && p.x < b.getRightBound() && p.y < b.getLowerBound()
                    && p.y > b.getUpperBound()) {
                return b;
            }
        }
        return null;
    }

    //returns true if there is a Body at Point p
    static boolean isInBody(Point p) {
        return inWhichBody(p) != null;
    }

    static boolean isAffectingBody() {
        return affectingBody;
    }

    static Body getSelectedBody() {
        return selectedBody;
    }

    static void setSelectedBody(Body body) {
        selectedBody = body;
    }

    static void setAffectingBody(boolean affectingBody) {
        BodyManager.affectingBody = affectingBody;
    }

    static ArrayList<Body> getAllBodies() {
        return allBodies;
    }

    static void setAllBodies(ArrayList<Body> allBodies) {
        BodyManager.allBodies = allBodies;
    }
}
