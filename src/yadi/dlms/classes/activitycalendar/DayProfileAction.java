package yadi.dlms.classes.activitycalendar;

import yadi.dlms.classes.clock.CosemTime;

public class DayProfileAction {
	private final CosemTime startTime;
	private final byte[] scriptLogicalName;
	private final int scriptSelector;
	
	public DayProfileAction(CosemTime startTime, byte[]  scriptLogicalName, int scriptSelector) {
		this.startTime = startTime;
		this.scriptLogicalName = scriptLogicalName;
		this.scriptSelector = scriptSelector;
	}

	public CosemTime getStartTime() {
		return startTime;
	}

	public byte[] getScriptLogicalName() {
		return scriptLogicalName;
	}

	public int getScriptSelector() {
		return scriptSelector;
	}
}
