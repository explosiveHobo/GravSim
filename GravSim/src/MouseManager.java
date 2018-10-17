import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public class MouseManager implements MouseListener {


    private static boolean mouseDown = false;

    private static int sizeIterator;
    private static Point initialMousePos;
    private static int xDiff, yDiff;

    static boolean isMouseDown() {
        return mouseDown;
    }

    static Point getInitMousePos() {
        return initialMousePos;
    }

    static int getSizeIterator() {
        return sizeIterator;
    }

    static void setSizeIterator(int sizeIterator) {
        MouseManager.sizeIterator = sizeIterator;
    }

    static void setXDiff(int xD) {
        xDiff = xD;
    }

    static void setYDiff(int yD) {
        yDiff = yD;
    }

    //handles mousePressed events, including:
    //  The selection of existing Bodies
    //  The creation of new Bodies with initial Velocity components
    //  The manipulation of existing Bodies
    @Override
    public void mousePressed(MouseEvent arg0) {
        Point point = arg0.getPoint();

        //If the left mouse button was pressed
        if (arg0.getButton() == MouseEvent.BUTTON1) {

            Body currentBody;

            if ((currentBody = BodyManager.inWhichBody(point)) != null) {
                currentBody.setSelected(!currentBody.isSelected());
                currentBody.invert();
            }
        }
        //else if the Right mouse button was pressed
        else if (arg0.getButton() == MouseEvent.BUTTON3) {

            initialMousePos = point;

            //if the mouse click was NOT on a body
            if (!BodyManager.isInBody(point)) {
                sizeIterator = 25;
                mouseDown = true;
            } else { //the mouse click WAS in a body, take that body and select it as the selectedBody for manipulation
                BodyManager.setSelectedBody(Objects.requireNonNull(BodyManager.inWhichBody(point)).getMass() < 5000 ? BodyManager.inWhichBody(point) : null);
                BodyManager.setAffectingBody(BodyManager.getSelectedBody() != null);
                mouseDown = BodyManager.isAffectingBody();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

        if (mouseDown && !BodyManager.isAffectingBody()) {
            BodyManager.spawnBody(sizeIterator, initialMousePos.x, initialMousePos.y,(int) (xDiff * 6.5), (int)(yDiff * 6.5),Color.gray.brighter());
        } else if (mouseDown) {
            BodyManager.getSelectedBody().addVelocityComponents(xDiff * 6.5, yDiff * 6.5);
            BodyManager.setAffectingBody(false);
        }
        mouseDown = false;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    static int getXDiff() {
        return xDiff;
    }

    static int getYDiff() {
        return yDiff;
    }
}
