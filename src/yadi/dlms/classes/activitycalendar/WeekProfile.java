package yadi.dlms.classes.activitycalendar;

public class WeekProfile {
	private final String profileName;
	private final int mondayDayId;
	private final int tuesdayDayId;
	private final int wednesdayDayId;
	private final int thursdayDayId;
	private final int fridayDayId;
	private final int saturdayDayId;
	private final int sundayDayId;
	
	public WeekProfile(String profileName, int monday, int tuesday, int wednesday, int thursday, int friday, int saturday, int sunday) {
		this.profileName = profileName;
		this.mondayDayId = monday;
		this.tuesdayDayId = tuesday;
		this.wednesdayDayId = wednesday;
		this.thursdayDayId = thursday;
		this.fridayDayId = friday;
		this.saturdayDayId = saturday;
		this.sundayDayId = sunday;
	}

	public String getProfileName() {
		return profileName;
	}

	public int getMondayDayId() {
		return mondayDayId;
	}

	public int getTuesdayDayId() {
		return tuesdayDayId;
	}

	public int getWednesdayDayId() {
		return wednesdayDayId;
	}

	public int getThursdayDayId() {
		return thursdayDayId;
	}

	public int getFridayDayId() {
		return fridayDayId;
	}

	public int getSaturdayDayId() {
		return saturdayDayId;
	}

	public int getSundayDayId() {
		return sundayDayId;
	}
}
