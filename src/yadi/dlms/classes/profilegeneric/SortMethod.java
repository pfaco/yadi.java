package yadi.dlms.classes.profilegeneric;

public enum SortMethod {
	FIFO(1),
	LIFO(2),
	LARGEST(3),
	SMALLEST(4),
	NEAREST_TO_ZERO(5),
	FAREST_FROM_ZERO(6),
	UNKNOWN(0);
	
	int value;
	
	SortMethod(int value) {
		this.value = value;
	}
	
	public static SortMethod fromValue(int value) {
		for (SortMethod s : SortMethod.values()) {
			if (s.value == value) {
				return s;
			}
		}
		throw new IllegalArgumentException();
	}
}
