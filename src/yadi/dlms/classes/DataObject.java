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
package yadi.dlms.classes;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.DlmsParser;
import yadi.dlms.Obis;
import yadi.dlms.cosem.CosemClasses;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class DataObject {

	private static final int attValue = 2;
	
	private final Obis obis;
	
	/**
	 * Creates a Data class (class_id=1) object
	 * @param obis the object obis
	 */
	public DataObject(Obis obis) {
		this.obis = obis;
	}
	
	public byte[] getValue(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		final LnDescriptor desc = new LnDescriptor(CosemClasses.DATA.id, obis, attValue);
		dlms.get(phy, desc);
		return desc.getResponseData();
	}
	
	public String getStringValue(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		final LnDescriptor desc = new LnDescriptor(CosemClasses.DATA.id, obis, attValue);
		dlms.get(phy, desc);
		return DlmsParser.getString(desc.getResponseData());
	}
	
	public int getIntegerValue(DlmsClient dlms, PhyLayer phy) throws DlmsException, PhyLayerException, LinkLayerException {
		final LnDescriptor desc = new LnDescriptor(CosemClasses.DATA.id, obis, attValue);
		dlms.get(phy, desc);
		return DlmsParser.getInteger(desc.getResponseData());
	}

}
