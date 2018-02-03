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

class DlmsClass {
	private final LnDescriptor attObis;
	
	/**
	 * Creates an object of DlmsClass
	 * @param classId the id of the class
	 * @param obis the obis of the object
	 */
	public DlmsClass(int classId, Obis obis) {
		attObis = new LnDescriptor(classId, 1, obis);
	}
	
	/**
	 * Gets the attribute 1 (OBIS) of the class
	 * @return LnDescriptor with index = 1
	 */
	public LnDescriptor attObis() {
		return attObis;
	}
}
