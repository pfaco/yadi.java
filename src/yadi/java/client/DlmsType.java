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
package yadi.java.client;

import yadi.java.client.DlmsException.DlmsExceptionReason;

public enum DlmsType {
	ARRAY(1,0),
	STRUCTURE(2,0),
	BITSTRING(4,0),
	BOOLEAN(3,1),
	DATE(26,0),
	ENUM(22,0),
	FLOAT32(23,4),
	INT16(18,2),
	INT32(5,4),
	INT64(20,8),
	INT8(15,1),
	OCTET_STRING(9,0),
	STRING(10,0),
	TIME(27,0),
	UINT8(17,1),
	UINT16(18,2),
	UINT32(6,4),
	UINT64(20,8);
	
	public final byte tag;
	final int size;
	
	DlmsType(int tag, int size) {
		this.tag = (byte)tag;
		this.size = size;
	}
	
	static DlmsType fromTag(byte tag) throws DlmsException {
		for (DlmsType type: values()) {
			if (type.tag == tag) {
				return type;
			}
		}
		throw new DlmsException(DlmsExceptionReason.NO_SUCH_TYPE);
	}
}
