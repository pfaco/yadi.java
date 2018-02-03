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

import yadi.java.client.LnDescriptor;
import yadi.java.client.Obis;

public class DlmsClassData extends DlmsClass {
	private static final int CLASS_ID = 1;
	private final LnDescriptor attValue;
	
	public DlmsClassData(Obis obis) {
		super(CLASS_ID, obis);
		attValue = new LnDescriptor(CLASS_ID, 2, obis);
	}
	
	DlmsClassData(int classId, Obis obis) {
		super(classId, obis);
		attValue = new LnDescriptor(classId, 2, obis);
	}
	
	public LnDescriptor attValue() {
		return attValue;
	}
}
