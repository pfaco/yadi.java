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

import java.nio.ByteBuffer;

public class LnDescriptor {

	private final byte[] classId;
	private final short index;
	private final byte[] obis;
	private byte[] requestData = new byte[0];
	private byte[] responseData = new byte[0];
	
	public LnDescriptor(int classId, int index, Obis obis) {
		this.obis = obis.getValue();
		this.classId = ByteBuffer.allocate(2).putShort((short)classId).array();
		this.index = (short)index;
	}
	
	public LnDescriptor(int classId, int index, Obis obis, byte[] requestData) {
		this(classId, index, obis);
		setRequestData(requestData);
	}
	
	public byte[] getReceivedData() {
		return responseData;
	}
	
	public void setRequestData(byte[] data) {
		if (data == null) {
			this.requestData = new byte[0];
		} else {
			this.requestData = data;
		}
	}

	public byte[] getClassId() {
		return classId;
	}

	public int getIndex() {
		return index;
	}

	public byte[] getObis() {
		return obis;
	}

	public byte[] getRequestData() {
		return requestData;
	}

	public void setResponseData(byte[] data) {
		if (data == null) {
			this.responseData = new byte[0];
		} else {
			this.responseData = data;
		}
	}
}
