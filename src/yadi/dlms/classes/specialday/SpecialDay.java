package yadi.dlms.classes.specialday;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.cosem.CosemParser;
import yadi.dlms.cosem.CosemSerializer;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class SpecialDay {
	private final LnDescriptor attEntries;
	private final LnDescriptor mtdInsert;
	private final LnDescriptor mtdDelete;
	
	public static SpecialDay fromObis(String obis) {
		return new SpecialDay(new Obis(obis));
	}
	
	public SpecialDay(Obis obis) {
		attEntries = new LnDescriptor(11, obis, 2);
		mtdInsert = new LnDescriptor(11, obis, 1);
		mtdDelete = new LnDescriptor(11, obis, 2);
	}
	
	public SpecialDayEntry[] getEntries(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		dlms.get(phy, attEntries);
		CosemParser parser = new CosemParser(attEntries.getResponseData());
		int size = parser.parseArraySize();
		SpecialDayEntry[] retval = new SpecialDayEntry[size];
		for (int i = 0; i < size; ++i) {
			parser.verifyStructureSize(3);
			retval[i] = new SpecialDayEntry(parser.uint16(), parser.date(), parser.uint8());
		}
		return retval;
	}
	
	public void setEntries(DlmsClient dlms, PhyLayer phy, SpecialDayEntry[] entries) throws DlmsException, PhyLayerException, LinkLayerException {
		CosemSerializer serializer = new CosemSerializer();
		serializer.array(entries.length);
		for (SpecialDayEntry entry : entries) {
			serializer.structure(3)
						.uint16(entry.getIndex())
						.date(entry.getDate())
						.uint8(entry.getDayId());
		}
		attEntries.setRequestData(serializer.serialize());
		dlms.set(phy, attEntries);
	} 
	
	public void insert(DlmsClient dlms, PhyLayer phy, SpecialDayEntry entry) throws DlmsException, PhyLayerException, LinkLayerException {
		mtdInsert.setRequestData(new CosemSerializer()
									.structure(3)
									.uint16(entry.getIndex())
									.date(entry.getDate())
									.uint8(entry.getDayId())
									.serialize());
		dlms.action(phy, mtdInsert);
	}
	
	public void delete(DlmsClient dlms, PhyLayer phy, int index) throws DlmsException, PhyLayerException, LinkLayerException {
		mtdDelete.setRequestData(new CosemSerializer().uint16(index).serialize());
		dlms.action(phy, mtdDelete);
	}
}
