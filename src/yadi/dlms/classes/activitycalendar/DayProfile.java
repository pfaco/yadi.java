package yadi.dlms.classes.activitycalendar;

public class DayProfile {
	private final int dayId;
	private final DayProfileAction[] daySchedule;
	
	DayProfile(int dayId, DayProfileAction[] daySchedule) {
		this.dayId = dayId;
		this.daySchedule = daySchedule;
	}

	public int getDayId() {
		return dayId;
	}

	public DayProfileAction[] getDaySchedule() {
		return daySchedule;
	}
	
}
