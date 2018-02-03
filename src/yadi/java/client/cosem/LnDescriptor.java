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
package yadi.java.client.cosem;

import java.nio.ByteBuffer;

import yadi.java.client.Obis;

public class LnDescriptor {

	private final byte[] classId;
	private final short index;
	private final byte[] obis;
	private byte[] requestData = new byte[0];
	private byte[] responseData = new byte[0];
	
	/**
	 * Creates a descriptor for a DLMS object
	 * @param classId the object class_id
	 * @param index the index of the attribute/method to be accessed
	 * @param obis the obis of the object
	 */
	public LnDescriptor(int classId, int index, Obis obis) {
		this.obis = obis.getValue();
		this.classId = ByteBuffer.allocate(2).putShort((short)classId).array();
		this.index = (short)index;
	}
	
	/**
	 * Creates a descriptor for a DLMS object
	 * @param classId the object class_id
	 * @param index the index of the attribute/method to be accessed
	 * @param obis the obis of the object
	 * @param requestData the data to be used in the request
	 */
	public LnDescriptor(int classId, int index, Obis obis, byte[] requestData) {
		this(classId, index, obis);
		setRequestData(requestData);
	}

	/**
	 * Retrieves the classId of the descriptor
	 * @return byte array representing the classId
	 */
	public byte[] getClassId() {
		return classId;
	}

	/**
	 * Retrieves the index of the descriptor
	 * @return index value
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Retrieves the OBIS of the descriptor
	 * @return byte array representing the OBIS
	 */
	public byte[] getObis() {
		return obis;
	}

	/**
	 * Retrieves the request data of the descriptor
	 * @return byte array representing the request data
	 */
	public byte[] getRequestData() {
		return requestData;
	}
	
	/**
	 * Retrieves the data received after an successful operation was performed
	 * @return byte array returned from the last operation successfully performed
	 */
	public byte[] getResponseData() {
		return responseData;
	}
	
	/**
	 * Sets the data to be used in next operations
	 * @param data byte array of the data to be used
	 */
	public void setRequestData(byte[] data) {
		if (data == null) {
			this.requestData = new byte[0];
		} else {
			this.requestData = data;
		}
	}

	/**
	 * Sets the response data of the descriptor
	 * @return byte array representing the response data
	 */
	public void setResponseData(byte[] data) {
		if (data == null) {
			this.responseData = new byte[0];
		} else {
			this.responseData = data;
		}
	}
}
