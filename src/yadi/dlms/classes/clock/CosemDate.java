package yadi.dlms.classes.clock;

import java.time.LocalDate;

public class CosemDate {
	private final int year;
	private final int month;
	private final int monthDay;
	private final int weekDay;
	
	public static CosemDate now() {
		return new CosemDate(LocalDate.now());
	}
	
	public CosemDate(int year, int month, int monthDay, int weekDay) {
		this.year = year;
		this.month = month;
		this.monthDay = monthDay;
		this.weekDay = weekDay;
	}
	
	public CosemDate(LocalDate date) {
		this.year = date.getYear();
		this.month = date.getMonthValue();
		this.monthDay = date.getDayOfMonth();
		this.weekDay = date.getDayOfWeek().getValue();
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getMonthDay() {
		return monthDay;
	}

	public int getWeekDay() {
		return weekDay;
	}

	public LocalDate toLocalDate() {
		return LocalDate.of(year, month, monthDay);
	}
	
	@Override public String toString() {
		int monthDay = this.monthDay == 0xFF ? 99 : this.monthDay;
		int month = this.month == 0xFF ? 99 : this.month;
		int year = this.year == 0xFFFF ? 9999 : this.year;
		return String.format("%02d/%02d/%04d", monthDay, month, year);
	}
	
}
