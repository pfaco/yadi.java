package yadi.dlms.classes.profilegeneric;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.classes.profilegeneric.ProfileGenericEntry.ProfileGenericItem;
import yadi.dlms.cosem.CosemParser;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class ProfileGeneric {

	private final LnDescriptor attBuffer;
	private final LnDescriptor attCaptureObjects;
	private final LnDescriptor attCapturePeriod;
	private final LnDescriptor attSortMethod;
	private final LnDescriptor attSortObject;
	private final LnDescriptor attEntriesInUse;
	private final LnDescriptor attProfileEntries;
	
	public ProfileGeneric(Obis obis) {
		attBuffer = new LnDescriptor(7, obis, 2);
		attCaptureObjects = new LnDescriptor(7, obis, 3);
		attCapturePeriod = new LnDescriptor(7, obis, 4);
		attSortMethod = new LnDescriptor(7, obis, 5);
		attSortObject = new LnDescriptor(7, obis, 6);
		attEntriesInUse = new LnDescriptor(7, obis, 7);
		attProfileEntries = new LnDescriptor(7, obis, 8);
	}
	
	public ProfileGenericBuffer getBuffer(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attBuffer);
		return makeProfileGenericBuffer(attBuffer.getResponseData());
	}
	
	public ProfileGenericBuffer getBuffer(DlmsClient dlms, PhyLayer phy, SelectiveAccess accessSelector) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attBuffer);
		return makeProfileGenericBuffer(attBuffer.getResponseData());
	}

	public CaptureObject[] getCaptureObjects(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attCaptureObjects);
		return makeCaptureObjectList(attCaptureObjects.getResponseData());
	}
	
	public int getCapturePeriod(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attCapturePeriod);
		return CosemParser.make(attCapturePeriod.getResponseData()).integer();
	}

	public SortMethod getSortMethod(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attSortMethod);
		return SortMethod.fromValue(CosemParser.make(attSortMethod.getResponseData()).enumeration());
	}
	
	public CaptureObject getSortObject(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attSortObject);
		return makeSortObject(attSortObject.getResponseData());
	}
	
	public int getEntriesInUse(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attEntriesInUse);
		return CosemParser.make(attEntriesInUse.getResponseData()).integer();
	}
	
	public int getProfileEntries(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attProfileEntries);
		return CosemParser.make(attProfileEntries.getResponseData()).integer();
	}
	
	private ProfileGenericBuffer makeProfileGenericBuffer(byte[] data) {
		CosemParser parser = new CosemParser(data);
		ProfileGenericBuffer buffer = new ProfileGenericBuffer();
		int arraySize = parser.parseArraySize();
		
		for (int i = 0; i < arraySize; ++i) {
			int size = parser.parseStructureSize();
			ProfileGenericEntry entry = new ProfileGenericEntry(size);
			for (int j = 0; j < size; ++j) {
				ProfileGenericItem item = new ProfileGenericItem(parser.getNextItemRawData());
			}
		}
		
		return buffer;
	}

	private CaptureObject[] makeCaptureObjectList(byte[] data) {
		CosemParser parser = new CosemParser(data);
		int size = parser.parseArraySize();
		CaptureObject[] list = new CaptureObject[size];
		
		for (int i = 0; i < size; ++i) {
			parser.verifyStructureSize(4);
			list[i] = new CaptureObject();
			list[i].classId = parser.uint16();
			list[i].obis = new Obis(parser.octetString());
			list[i].index = parser.int8();
			list[i].dataIndex = parser.uint16();
		}
		
		return list;
	}
	
	private CaptureObject makeSortObject(byte[] data) {
		CaptureObject obj = new CaptureObject();
		CosemParser parser = new CosemParser(data);
		parser.verifyStructureSize(4);
		obj.classId = parser.uint16();
		obj.obis = new Obis(parser.octetString());
		obj.index = parser.int8();
		obj.dataIndex = parser.uint16();
		return obj;
	}

}
