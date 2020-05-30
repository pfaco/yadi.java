/*
 * YADI (Yet Another DLMS Implementation)
 * Copyright (C) 2018 Paulo Faco (paulofaco@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package yadi.dlms.classes.data;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.classes.CosemSerializerProxy;
import yadi.dlms.cosem.CosemParser;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.cosem.SnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class DataObject {

	private final LnDescriptor attValue;
	
	public static CosemParser getAttribute(DlmsClient dlms, PhyLayer phy, int classId, String obis, int att) throws DlmsException, PhyLayerException, LinkLayerException {
		LnDescriptor desc = new LnDescriptor(classId, new Obis(obis), att);
		dlms.get(phy, desc);
		return new CosemParser(desc.getResponseData());
	}
	
	public static CosemParser getAttribute(DlmsClient dlms, PhyLayer phy, int classId, String obis, int att,
			byte[] selectiveAccess) throws PhyLayerException, DlmsException, LinkLayerException {
		LnDescriptor desc = new LnDescriptor(classId, new Obis(obis), att);
		desc.setRequestData(selectiveAccess);
		dlms.get(phy, desc);
		return new CosemParser(desc.getResponseData());
	}
	
	public static CosemParser readAttribute(DlmsClient dlms, PhyLayer phy, int shortName) throws PhyLayerException, LinkLayerException, DlmsException {
		SnDescriptor desc = new SnDescriptor(shortName);
		dlms.read(phy, desc);
		return new CosemParser(desc.getResponseData());
	}
	
	public static CosemSerializerProxy setAttribute(DlmsClient dlms, PhyLayer phy, int classId, String obis, int att) throws DlmsException, PhyLayerException, LinkLayerException {
		LnDescriptor desc = new LnDescriptor(classId, new Obis(obis), att);
		return new CosemSerializerProxy((data) -> {
			desc.setRequestData(data);
			dlms.set(phy, desc);
		});
	}
	
	public static CosemSerializerProxy writeAttribute(DlmsClient dlms, PhyLayer phy, int shortName) throws DlmsException, PhyLayerException, LinkLayerException {
		SnDescriptor desc = new SnDescriptor(shortName);
		return new CosemSerializerProxy((data) -> {
			desc.setRequestData(data);
			dlms.write(phy, desc);
		});
	}
	
	public static CosemSerializerProxy execAttribute(DlmsClient dlms, PhyLayer phy, int classId, String obis, int att) {
		LnDescriptor desc = new LnDescriptor(classId, new Obis(obis), att);
		return new CosemSerializerProxy((data) -> {
			desc.setRequestData(data);
			dlms.action(phy, desc);
		});
	}
	
	public static CosemParser getValue(DlmsClient dlms, PhyLayer phy, String obis) throws DlmsException, PhyLayerException, LinkLayerException {
		return new DataObject(new Obis(obis)).getValue(dlms, phy);
	}
	
	public static CosemSerializerProxy setValue(DlmsClient dlms, PhyLayer phy, String obis) throws DlmsException, PhyLayerException, LinkLayerException {
		return new DataObject(new Obis(obis)).setValue(dlms, phy);
	}
	
	public DataObject(Obis obis) {
		attValue = new LnDescriptor(1, obis, 2);
	}
	
	public CosemParser getValue(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		dlms.get(phy, attValue);
		return new CosemParser(attValue.getResponseData());
	}
	
	public CosemSerializerProxy setValue(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		return new CosemSerializerProxy((data) -> {
			attValue.setRequestData(data);
			dlms.set(phy, attValue);
		});
	}

}
