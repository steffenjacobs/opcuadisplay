package me.steffenjacobs.opcuadisplay.shared.util;
/** @author Steffen Jacobs */
public class Tuple2<X, Y> {

	private final X x;
	private final Y y;

	public Tuple2(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}
}
