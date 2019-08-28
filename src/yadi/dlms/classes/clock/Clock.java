package yadi.dlms.classes.clock;


import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.cosem.CosemParser;
import yadi.dlms.cosem.CosemSerializer;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class Clock {

	private final LnDescriptor attTime;
	private final LnDescriptor attTimeZone;
	private final LnDescriptor attStatus;
	private final LnDescriptor attDaylightBegin;
	private final LnDescriptor attDaylightEnd;
	private final LnDescriptor attDaylightDeviation;
	private final LnDescriptor attDaylightEnabled;
	private final LnDescriptor attClockBase;
	private final LnDescriptor mtdAdjustToQuater;
	private final LnDescriptor mtdAdjustToMeasurePeriod;
	private final LnDescriptor mtdAdjustToMinute;
	private final LnDescriptor mtdAdjustToPresetTime;
	private final LnDescriptor mtdPresetAdjustingTime;
	private final LnDescriptor mtdShiftTime;
	
	public Clock() {
		this(new Obis("0.0.1.0.0.255"));
	}
	
	public Clock(Obis obis) {
		attTime = new LnDescriptor(8, obis, 2);
		attTimeZone = new LnDescriptor(8, obis, 3);
		attStatus = new LnDescriptor(8, obis, 4);
		attDaylightBegin = new LnDescriptor(8, obis, 2);
		attDaylightEnd = new LnDescriptor(8, obis, 2);
		attDaylightDeviation = new LnDescriptor(8, obis, 2);
		attDaylightEnabled = new LnDescriptor(8, obis, 2);
		attClockBase = new LnDescriptor(8, obis, 2);
		mtdAdjustToQuater = new LnDescriptor(8, obis, 2);
		mtdAdjustToMeasurePeriod = new LnDescriptor(8, obis, 2);
		mtdAdjustToMinute = new LnDescriptor(8, obis, 2);
		mtdAdjustToPresetTime = new LnDescriptor(8, obis, 2);
		mtdPresetAdjustingTime = new LnDescriptor(8, obis, 2);
		mtdShiftTime = new LnDescriptor(8, obis, 2);
	}
	
	public CosemDateTime readDateTime(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		return readLocalDateTime(dlms, phy, attTime);
	}
	
	public void writeDateTime(DlmsClient dlms, PhyLayer phy, CosemDateTime dt) throws PhyLayerException, DlmsException, LinkLayerException {
		writeLocalDateTime(dlms, phy, dt, attTime);
	}
	
	public int readTimeZone(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attTimeZone);
		return new CosemParser(attTimeZone.getResponseData()).int16();
	}
	
	public void writeTimeZone(DlmsClient dlms, PhyLayer phy, int deviation) throws PhyLayerException, DlmsException, LinkLayerException {
		attTimeZone.setRequestData(new CosemSerializer().int16(deviation).serialize());
		dlms.set(phy, attTimeZone);
	}
	
	public ClockStatus readClockStatus(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attStatus);
		return new ClockStatus(new CosemParser(attStatus.getResponseData()).uint8());
	}
	
	public CosemDateTime readDaylightSavingBegin(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		return readLocalDateTime(dlms, phy, attDaylightBegin);
	}
	
	public void writeDaylightSavingBegin(DlmsClient dlms, PhyLayer phy, CosemDateTime dt) throws PhyLayerException, DlmsException, LinkLayerException {
		writeLocalDateTime(dlms, phy, dt, attDaylightBegin);
	}
	
	public CosemDateTime readDaylightSavingEnd(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		return readLocalDateTime(dlms, phy, attDaylightEnd);
	}
	
	public void writeDaylightSavingEnd(DlmsClient dlms, PhyLayer phy, CosemDateTime dt) throws PhyLayerException, DlmsException, LinkLayerException {
		writeLocalDateTime(dlms, phy, dt, attDaylightEnd);
	}
	
	public int readDaylightSavingDeviation(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attDaylightDeviation);
		return new CosemParser(attDaylightDeviation.getResponseData()).int8();
	}
	
	public void writeDaylightSavingDeviation(DlmsClient dlms, PhyLayer phy, int deviation) throws PhyLayerException, DlmsException, LinkLayerException {
		attDaylightDeviation.setRequestData(new CosemSerializer().int8(deviation).serialize());
		dlms.set(phy, attDaylightDeviation);
	}
	
	public boolean isDaylightSavingEnabled(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attDaylightEnabled);
		return new CosemParser(attDaylightEnabled.getResponseData()).bool();
	}
	
	public void setDaylightSavingEnabled(DlmsClient dlms, PhyLayer phy, boolean enabled) throws PhyLayerException, DlmsException, LinkLayerException {
		attDaylightEnabled.setRequestData(new CosemSerializer().bool(enabled).serialize());
		dlms.set(phy, attDaylightEnabled);
	}
	
	public ClockBase readClockBase(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attClockBase);
		return ClockBase.fromValue(new CosemParser(attClockBase.getResponseData()).enumeration());
	}
	
	private CosemDateTime readLocalDateTime(DlmsClient dlms, PhyLayer phy, LnDescriptor desc) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, desc);
		return new CosemParser(desc.getResponseData()).datetime();
	}
	
	public void adjustToQuarter(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdAdjustToQuater.setRequestData(new CosemSerializer().int8(0).serialize());
		dlms.action(phy, mtdAdjustToQuater);
	}
	
	public void adjustToMeasuringPeriod(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdAdjustToMeasurePeriod.setRequestData(new CosemSerializer().int8(0).serialize());
		dlms.action(phy, mtdAdjustToMeasurePeriod);
	}
	
	public void adjustToMinute(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdAdjustToMinute.setRequestData(new CosemSerializer().int8(0).serialize());
		dlms.action(phy, mtdAdjustToMinute);
	}
	
	public void adjustToPresetTime(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdAdjustToPresetTime.setRequestData(new CosemSerializer().int8(0).serialize());
		dlms.action(phy, mtdAdjustToPresetTime);
	}
	
	public void presetAdjustingTime(DlmsClient dlms, PhyLayer phy, PresetTime pt) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdPresetAdjustingTime.setRequestData(new CosemSerializer()
												.structure(3)
												.datetime(pt.getPresetTime())
												.datetime(pt.getValidityStart())
												.datetime(pt.getValidityEnd())
												.serialize());
		dlms.action(phy, mtdPresetAdjustingTime);
	}
	
	public void shiftTime(DlmsClient dlms, PhyLayer phy, int shiftSeconds) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdShiftTime.setRequestData(new CosemSerializer().int16(shiftSeconds).serialize());
		dlms.action(phy, mtdShiftTime);
	}
	
	private void writeLocalDateTime(DlmsClient dlms, PhyLayer phy, CosemDateTime dt, LnDescriptor desc) throws PhyLayerException, DlmsException, LinkLayerException {
		desc.setRequestData(new CosemSerializer().datetime(dt).serialize());
		dlms.set(phy, desc);
	}
}
