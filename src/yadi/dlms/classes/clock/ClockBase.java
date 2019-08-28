package yadi.dlms.classes.clock;

public enum ClockBase {
	NOT_DEFINED(0),
	INTERNAL_CRYSTAL(1),
	MAINS_FREQUENCY_50HZ(2),
	MAINS_FREQUENCY_60HZ(3),
	GPS(4),
	RADIO_CONTROLLED(5);
	
	int value;
	
	ClockBase(int value) {
		this.value = value;
	}
	
	public static ClockBase fromValue(int value) {
		for (ClockBase c : ClockBase.values()) {
			if (c.value == value) {
				return c;
			}
		}
		throw new IllegalArgumentException();
	}
}
