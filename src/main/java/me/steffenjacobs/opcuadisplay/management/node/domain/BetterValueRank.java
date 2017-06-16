package me.steffenjacobs.opcuadisplay.management.node.domain;
/** @author Steffen Jacobs */
public enum BetterValueRank {
	OneDimension(1),
	OneOrMoreDimensions(0),
	Scalar(-1),
	Any(-2),
	ScalarOrOneDimension(-3),
	TwoDimensions(2),
	ThreeDimensions(3),
	FourDimensions(4),
	Unknown(Integer.MIN_VALUE);

	private final int value;

	BetterValueRank(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static BetterValueRank valueOf(int value) {
		for (BetterValueRank bvr : BetterValueRank.values()) {
			if (bvr.value == value) {
				return bvr;
			}
		}
		return Unknown;
	}
}
