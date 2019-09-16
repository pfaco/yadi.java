package yadi.dlms.classes.register;

public enum CosemUnit {
	YEAR(1),
	MONTH(2),
	WEEK(3),
	DAY(4),
	HOUR(5),
	MINUTE(6),
	SECOND(7),
	ANGLE_DEGREE(8),
	CELSIUS(9),
	WATT(27),
	VA(28),
	VAR(29),
	WH(30),
	VAH(31),
	VARH(32),
	AMPERE(33),
	CHARGE_Q(34),
	VOLT(35),
	HERTZ(44),
	NONE(255);
	
	private final int value;
	
	CosemUnit(int value) {
		this.value = value;
	}
	
	public static CosemUnit fromValue(int value) {
		for (CosemUnit u : CosemUnit.values()) {
			if (u.value == value) {
				return u;
			}
		}
		throw new IllegalArgumentException();
	}
}
