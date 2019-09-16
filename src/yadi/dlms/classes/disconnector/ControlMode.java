package yadi.dlms.classes.disconnector;

public enum ControlMode {
	MODE_0(0),
	MODE_1(1),
	MODE_2(2),
	MODE_3(3),
	MODE_4(4),
	MODE_5(5),
	MODE_6(6);
	
	int value;
	
	ControlMode(int value) {
		this.value = value;
	}
	
	public static ControlMode fromValue(int value) {
		for (ControlMode mode : ControlMode.values()) {
			if (mode.value == value) {
				return mode;
			}
		}
		throw new IllegalArgumentException();
	}
}
