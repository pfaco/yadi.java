package yadi.dlms.classes.specialday;

import yadi.dlms.classes.clock.CosemDate;

public class SpecialDayEntry {
	private final int index;
	private final CosemDate date;
	private final int dayId;
	
	public SpecialDayEntry(int index, CosemDate date, int dayId) {
		this.index = index;
		this.date = date;
		this.dayId = dayId;
	}
	
	public int getIndex() {
		return index;
	}
	
	public CosemDate getDate() {
		return date;
	}
	
	public int getDayId() {
		return dayId;
	}
}
