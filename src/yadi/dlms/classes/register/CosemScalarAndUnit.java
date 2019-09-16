package yadi.dlms.classes.register;

public class CosemScalarAndUnit {
	private final int scalar;
	private final CosemUnit unit;
	
	CosemScalarAndUnit(int scalar, CosemUnit unit) {
		this.scalar = scalar;
		this.unit = unit;
	}
	
	public int getScalar() {
		return scalar;
	}
	
	public CosemUnit getUnit() {
		return unit;
	}
	
	@Override public String toString() {
		return "("+scalar+")"+unit;
	}
}
