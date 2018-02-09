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

import yadi.dlms.Obis;
import yadi.dlms.cosem.LnDescriptor;

public class DlmsClassRegister {
	private final LnDescriptor attObis;
	private final LnDescriptor attValue;
	private final LnDescriptor attScalarUnit;
	private final LnDescriptor mtdReset;
	
	/**
	 * Creates a Register class (class_id=2) object
	 * @param obis the object obis
	 */
	public DlmsClassRegister(Obis obis) {
		attObis = new LnDescriptor(DlmsClass.REGISTER.id, obis, 1);
		attValue = new LnDescriptor(DlmsClass.REGISTER.id, obis, 2);
		attScalarUnit = new LnDescriptor(DlmsClass.REGISTER.id, obis, 3);
		mtdReset = new LnDescriptor(DlmsClass.REGISTER.id, obis, 1);
	}
	
	/**
	 * Retrieves the OBIS attribute (index 1) of the object
	 * @return LnDescritptor with index = 1
	 */
	public LnDescriptor attObis() {
		return attObis;
	}
	
	public LnDescriptor getAttValue() {
		return attValue;
	}
	
	public LnDescriptor getAttScalarUnit() {
		return attScalarUnit;
	}
	
	public LnDescriptor getMtdReset() {
		return mtdReset;
	}
}
