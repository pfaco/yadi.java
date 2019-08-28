package yadi.dlms.classes.activitycalendar;

import yadi.dlms.classes.clock.CosemDateTime;

public class SeasonProfile {
	private final String profileName;
	private final CosemDateTime start;
	private final String weekName;
	
	public SeasonProfile(String profileName, CosemDateTime start, String weekName) {
		this.profileName = profileName;
		this.start = start;
		this.weekName = weekName;
	}

	public String getProfileName() {
		return profileName;
	}

	public CosemDateTime getStart() {
		return start;
	}

	public String getWeekName() {
		return weekName;
	}
}
