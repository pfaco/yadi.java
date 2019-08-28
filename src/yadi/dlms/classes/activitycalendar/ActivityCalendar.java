package yadi.dlms.classes.activitycalendar;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.classes.clock.CosemDateTime;
import yadi.dlms.cosem.CosemParser;
import yadi.dlms.cosem.CosemSerializer;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class ActivityCalendar {

	private final LnDescriptor attCalendarNameActive;
	private final LnDescriptor attSeasonProfileActive;
	private final LnDescriptor attWeekProfileTableActive;
	private final LnDescriptor attDayProfileTableActive;
	private final LnDescriptor attCalendarNamePassive;
	private final LnDescriptor attSeasonProfilePassive;
	private final LnDescriptor attWeekProfileTablePassive;
	private final LnDescriptor attDayProfileTablePassive;
	private final LnDescriptor attActivatePassiveCalendarTime;
	private final LnDescriptor mtdActivatePassiveCalendar;
	
	public static ActivityCalendar fromObis(String obis) {
		return new ActivityCalendar(new Obis(obis));
	}
	
	public ActivityCalendar(Obis obis) {
		attCalendarNameActive = new LnDescriptor(20, obis, 2);
		attSeasonProfileActive = new LnDescriptor(20, obis, 3);
		attWeekProfileTableActive = new LnDescriptor(20, obis, 4);
		attDayProfileTableActive = new LnDescriptor(20, obis, 5);
		attCalendarNamePassive = new LnDescriptor(20, obis, 6);
		attSeasonProfilePassive = new LnDescriptor(20, obis, 7);
		attWeekProfileTablePassive = new LnDescriptor(20, obis, 8);
		attDayProfileTablePassive = new LnDescriptor(20, obis, 9);
		attActivatePassiveCalendarTime = new LnDescriptor(20, obis, 10);
		mtdActivatePassiveCalendar = new LnDescriptor(20, obis, 1);
	}
	
	public String readCalendarNameActive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		dlms.get(phy, attCalendarNameActive);
		return new String(new CosemParser(attCalendarNameActive.getResponseData()).octetString());
	}
	
	public String readCalendarNamePassive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		dlms.get(phy, attCalendarNamePassive);
		return new String(new CosemParser(attCalendarNamePassive.getResponseData()).octetString());
	}
	
	public void writeCalendarNamePassive(DlmsClient dlms, PhyLayer phy, String calendarName) throws DlmsException, PhyLayerException, LinkLayerException {
		attCalendarNamePassive.setRequestData(new CosemSerializer().octetString(calendarName.getBytes()).serialize());
		dlms.set(phy, attCalendarNamePassive);
	}
	
	private SeasonProfile[] readSeasonProfile(DlmsClient dlms, PhyLayer phy, LnDescriptor descriptor) throws DlmsException, PhyLayerException, LinkLayerException {
		dlms.get(phy, descriptor);
		CosemParser parser = new CosemParser(descriptor.getResponseData());
		int size = parser.parseArraySize();
		SeasonProfile[] retval = new SeasonProfile[size];
		for (int i = 0; i < size; ++i) {
			parser.verifyStructureSize(3);
			retval[i] = new SeasonProfile(new String(parser.octetString()), parser.datetime(), new String(parser.octetString()));
		}
		return retval;
	}
	
	public SeasonProfile[] readSeasonProfileActive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		return readSeasonProfile(dlms, phy, attSeasonProfileActive);
	}
	
	public SeasonProfile[] readSeasonProfilePassive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		return readSeasonProfile(dlms, phy, attSeasonProfilePassive);
	}
	
	public void writeSeasonProfilePassive(DlmsClient dlms, PhyLayer phy, SeasonProfile[] entries) throws DlmsException, PhyLayerException, LinkLayerException {
		CosemSerializer serializer = new CosemSerializer();
		serializer.array(entries.length);
		for (SeasonProfile entry : entries) {
			serializer.structure(3);
			serializer.octetString(entry.getProfileName().getBytes());
			serializer.datetime(entry.getStart());
			serializer.octetString(entry.getWeekName().getBytes());
		}
		attSeasonProfilePassive.setRequestData(serializer.serialize());
		dlms.set(phy, attSeasonProfilePassive);
	}
	
	private WeekProfile[] readWeekProfile(DlmsClient dlms, PhyLayer phy, LnDescriptor descriptor) throws DlmsException, PhyLayerException, LinkLayerException {
		dlms.get(phy, descriptor);
		CosemParser parser = new CosemParser(descriptor.getResponseData());
		int size = parser.parseArraySize();
		WeekProfile[] retval = new WeekProfile[size];
		for (int i = 0; i < size; ++i) {
			parser.verifyStructureSize(8);
			retval[i] = new WeekProfile(new String(parser.octetString()), 
										parser.uint8(),
										parser.uint8(),
										parser.uint8(),
										parser.uint8(),
										parser.uint8(),
										parser.uint8(),
										parser.uint8());
		}
		return retval;
	}
	
	public WeekProfile[] readWeekProfileActive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		return readWeekProfile(dlms, phy, attWeekProfileTableActive);
	}
	
	public WeekProfile[] readWeekProfilePassive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		return readWeekProfile(dlms, phy, attWeekProfileTablePassive);
	}
	
	public void writeWeekProfilePassive(DlmsClient dlms, PhyLayer phy, WeekProfile[] entries) throws DlmsException, PhyLayerException, LinkLayerException {
		CosemSerializer serializer = new CosemSerializer();
		serializer.array(entries.length);
		for (WeekProfile entry : entries) {
			serializer.structure(8);
			serializer.octetString(entry.getProfileName().getBytes());
			serializer.uint8(entry.getMondayDayId());
			serializer.uint8(entry.getTuesdayDayId());
			serializer.uint8(entry.getWednesdayDayId());
			serializer.uint8(entry.getThursdayDayId());
			serializer.uint8(entry.getFridayDayId());
			serializer.uint8(entry.getSaturdayDayId());
			serializer.uint8(entry.getSundayDayId());
		}
		attWeekProfileTablePassive.setRequestData(serializer.serialize());
		dlms.set(phy, attWeekProfileTablePassive);
	}
	
	private DayProfile[] readDayProfile(DlmsClient dlms, PhyLayer phy, LnDescriptor descriptor) throws DlmsException, PhyLayerException, LinkLayerException {
		dlms.get(phy, descriptor);
		CosemParser parser = new CosemParser(descriptor.getResponseData());
		int size = parser.parseArraySize();
		DayProfile[] retval = new DayProfile[size];
		for (int i = 0; i < size; ++i) {
			parser.verifyStructureSize(2);
			int dayId = parser.uint8();
			int dayScheduleSize = parser.parseArraySize();
			DayProfileAction[] profileAction = new DayProfileAction[dayScheduleSize];
			for (int j = 0; j < dayScheduleSize; ++j) {
				parser.verifyStructureSize(3);
				profileAction[j] = new DayProfileAction(parser.time(), parser.octetString(), parser.uint16());
			}
			retval[i] = new DayProfile(dayId, profileAction);
		}
		return retval;
	}
	
	public DayProfile[] readDayProfileActive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		return readDayProfile(dlms, phy, attDayProfileTableActive);
	}
	
	public DayProfile[] readDayProfilePassive(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		return readDayProfile(dlms, phy, attDayProfileTablePassive);
	}
	
	public void writeDayProfilePassive(DlmsClient dlms, PhyLayer phy, DayProfile[] entries) throws DlmsException, PhyLayerException, LinkLayerException {
		CosemSerializer serializer = new CosemSerializer();
		serializer.array(entries.length);
		for (DayProfile entry : entries) {
			serializer.structure(2);
			serializer.uint8(entry.getDayId());
			serializer.array(entry.getDaySchedule().length);
			for (DayProfileAction action : entry.getDaySchedule()) {
				serializer.structure(3);
				serializer.time(action.getStartTime());
				serializer.octetString(action.getScriptLogicalName());
				serializer.uint16(action.getScriptSelector());
			}
		}
		attDayProfileTablePassive.setRequestData(serializer.serialize());
		dlms.set(phy, attDayProfileTablePassive);
	}
	
	public CosemDateTime readActivePassiveCalendarTime(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attActivatePassiveCalendarTime);
		return new CosemParser(attActivatePassiveCalendarTime.getResponseData()).datetime();
	}
	
	public void writeActivePassiveCalendarTime(DlmsClient dlms, PhyLayer phy, CosemDateTime dt) throws PhyLayerException, DlmsException, LinkLayerException {
		attActivatePassiveCalendarTime.setRequestData(new CosemSerializer().datetime(dt).serialize());
		dlms.set(phy, attActivatePassiveCalendarTime);
	}
	
	public void activatePassive(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdActivatePassiveCalendar.setRequestData(new CosemSerializer().int8(0).serialize());
		dlms.set(phy, mtdActivatePassiveCalendar);
	}

}
