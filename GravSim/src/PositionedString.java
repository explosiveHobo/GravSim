import java.awt.*;

class PositionedString {

	private int x, y, myIterator = 0;
	private String myString;
	private Color myColor = Color.white;

	int getX() {
		return x;
	}

	int getY() {
		return y;
	}

	void setY(int y) {
		this.y = y;
	}

	String getMyString() {
		return myString;
	}

	PositionedString(String s, int x, int y) {
		myString = s;
		this.x = x;
		this.y = y;
	}

	void setMyColor(Color myColor) {
		this.myColor = myColor;
	}

	Color getMyColor() {
		return myColor;
	}

	void setMyIterator(int myIterator) {
		this.myIterator = myIterator;
	}

	int getMyIterator() {
		return myIterator;
	}

}
