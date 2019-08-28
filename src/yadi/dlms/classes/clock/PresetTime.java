package yadi.dlms.classes.clock;

public class PresetTime {
	private final CosemDateTime presetTime;
	private final CosemDateTime validityStart;
	private final CosemDateTime validityEnd;
	
	public PresetTime(CosemDateTime presetTime, CosemDateTime validityStart, CosemDateTime validityEnd) {
		this.presetTime = presetTime;
		this.validityStart = validityStart;
		this.validityEnd = validityEnd;
	}

	public CosemDateTime getPresetTime() {
		return presetTime;
	}

	public CosemDateTime getValidityStart() {
		return validityStart;
	}

	public CosemDateTime getValidityEnd() {
		return validityEnd;
	}
	
	
}
