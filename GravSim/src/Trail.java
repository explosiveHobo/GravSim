import java.awt.*;

class Trail {

    private int x;
    private int y;
    private int size;

    private int counter = 0,maxCounter = 40;
    private Color myColor;
    private boolean isValid = true;

    Trail(int x, int y, int s) {
        this.setX(x);
        this.setY(y);
        size = s;
        myColor = new Color(myColor.getRed(),myColor.getGreen(),myColor.getBlue(),  (255-(maxCounter*2)));
    }

    void update() {
        myColor = new Color(myColor.getRed(),myColor.getGreen(),myColor.getBlue(),myColor.getAlpha()-((maxCounter/6)+ (maxCounter== 12?10:0)));
        counter++;
            if (counter > maxCounter) {
                invalidate();
            }
        }


    void invalidate() {
        isValid = false;
    }

    boolean isValid() {
        return isValid;
    }

    private void setX(int x) {
        this.x = x;
    }

    int getX() {
        return x;
    }

    private void setY(int y) {
        this.y = y;
    }

    int getY() {
        return y;
    }

    Color getColor() {
        return myColor;
    }

    void setColor(Color color) {
        this.myColor = color;
    }

    int getSize() {
        return size;
    }

    void setMaxCounter(int max) {
        this.maxCounter = max;
    }

}
