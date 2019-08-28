package yadi.dlms.classes.clock;

import java.time.LocalTime;

public class CosemTime {
	private final int hour;
	private final int minute;
	private final int seconds;
	private final int hundreths;
	
	public static CosemTime now() {
		return new CosemTime(LocalTime.now());
	}
	
	public CosemTime(int hour, int minute, int seconds, int hundreths) {
		super();
		this.hour = hour;
		this.minute = minute;
		this.seconds = seconds;
		this.hundreths = hundreths;
	}
	
	public CosemTime(LocalTime time) {
		this.hour = time.getHour();
		this.minute = time.getMinute();
		this.seconds = time.getSecond();
		this.hundreths = 0;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSeconds() {
		return seconds;
	}
	
	public int getHundreths() {
		return hundreths;
	}

	public LocalTime toLocalTime() {
		return LocalTime.of(hour, minute, seconds);
	}
	
	@Override public String toString() {
		int hour = this.hour == 0xFF ? 99 : this.hour;
		int minute = this.minute == 0xFF ? 99 : this.minute;
		int seconds = this.seconds == 0xFF ? 99 : this.seconds;
		return String.format("%02d:%02d:%02d", hour, minute, seconds);
	}
	
}
