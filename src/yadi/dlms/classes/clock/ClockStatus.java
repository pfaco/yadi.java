package yadi.dlms.classes.clock;

public class ClockStatus {

	private final int statusBits;
	
	public ClockStatus(int statusBits) {
		this.statusBits = statusBits;
	}
	
	public int getStatusValue() {
		return statusBits;
	}
	
	public boolean isInvalidValue() {
		return (statusBits & 0x01) == 0x01;
	}
	
	public boolean isDoubtValue() {
		return (statusBits & 0x02) == 0x02;
	}
	
	public boolean isDifferentClockBase() {
		return (statusBits & 0x04) == 0x04;
	}
	
	public boolean isInvalidClockStatus() {
		return (statusBits & 0x08) == 0x08;
	}
	
	public boolean isDaylightSavingActive() {
		return (statusBits & 0x80) == 0x80;
	}
	
	public boolean isNotSpecified() {
		return statusBits == 0xFF;
	}
	
}
