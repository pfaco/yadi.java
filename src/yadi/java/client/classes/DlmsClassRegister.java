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
package yadi.java.client.classes;

import yadi.java.client.Obis;
import yadi.java.client.cosem.LnDescriptor;

public class DlmsClassRegister extends DlmsClass {
	private static final int CLASS_ID = 2;
	private final LnDescriptor attValue;
	private final LnDescriptor attScalarUnit;
	private final LnDescriptor mtdReset;
	
	/**
	 * Creates a Register class (class_id=2) object
	 * @param obis the object obis
	 */
	public DlmsClassRegister(Obis obis) {
		super(CLASS_ID, obis);
		attValue = new LnDescriptor(CLASS_ID, 2, obis);
		attScalarUnit = new LnDescriptor(CLASS_ID, 3, obis);
		mtdReset = new LnDescriptor(CLASS_ID, 1, obis);
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
