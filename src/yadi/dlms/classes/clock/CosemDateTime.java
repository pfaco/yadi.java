package yadi.dlms.classes.clock;

import java.time.LocalDateTime;

public class CosemDateTime {
	private final CosemTime time;
	private final CosemDate date;
	private final ClockStatus status;
	private final int deviation;
	
	public static CosemDateTime now() {
		return new CosemDateTime(LocalDateTime.now());
	}
	
	public CosemDateTime(CosemDate date, CosemTime time, ClockStatus status) {
		this(date, time, 0, status);
	}
	
	public CosemDateTime(CosemDate date, CosemTime time, int deviation, ClockStatus status) {
		this.time = time;
		this.date = date;
		this.status = status;
		this.deviation = deviation;
	}
	
	public CosemDateTime(LocalDateTime dt) {
		this(new CosemDate(dt.toLocalDate()), new CosemTime(dt.toLocalTime()), 0, new ClockStatus(0));
	}

	public CosemTime getTime() {
		return time;
	}

	public CosemDate getDate() {
		return date;
	}
	
	public ClockStatus getStatus() {
		return status;
	}
	
	public int getDeviation() {
		return deviation;
	}
	
	public LocalDateTime toLocalDateTime() {
		return LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
	}
	
	@Override public String toString() {
		return time.toString() + " " + date.toString();
	}
	
}
