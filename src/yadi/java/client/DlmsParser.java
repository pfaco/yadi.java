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
import java.util.Arrays;

import yadi.java.client.DlmsException.DlmsExceptionReason;

public class DlmsParser {
	
	/**
	 * Retrieves the DlmsType from an array of bytes
	 * @param data array of bytes containing data result from a dlms-get
	 * @return The DlmsType of the data
	 * @throws DlmsException
	 */
	public static DlmsType getTypeFromRawBytes(byte[] data) throws DlmsException {
		verify(data);
		return DlmsType.fromTag(data[0]);
	}

	/**
	 * Retrieves the String representation of the raw bytes.
	 * The string will include the type and the size of the element parsed.
	 * @param data array of bytes containing data result from a dlms-get
	 * @return A String that represents the data
	 * @throws DlmsException
	 */
	public static String parseRawBytes(byte[] data) throws DlmsException {
		verify(data);
		return rawBytesToString(DlmsType.fromTag(data[0]), data);
	}
	
	/**
	 * Retrieves the String representation of the element in the array of bytes
	 * @param data array of bytes containing data result from a dlms-get
	 * @return A Strig that represents the element in the data
	 * @throws DlmsException
	 */
	public static String getString(byte[] data) throws DlmsException {
		verify(data);
		DlmsType type = DlmsType.fromTag(data[0]);
		return DlmsParser.getStringValue(type, getPayload(type, data));
	}
	
	/**
	 * Retrieves the DateTime String representation of the element in the array of bytes
	 * @param data array of bytes containing data result from a dlms-get
	 * @return A Strig that represents the date and time in the data
	 * @throws DlmsException
	 */
	public static String getDateTimeString(byte[] data) throws DlmsException {
		verify(data);
		DlmsType type = DlmsType.fromTag(data[0]);
		return DlmsParser.getDateAndTimeString(getPayload(type, data));
	}

	private static void verify(byte[] data) throws DlmsException {
		if (data == null || data.length < 2) {
			throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
		}
	}
	
	private static byte[] getPayload(DlmsType type, byte[] data) {
		int offset = type.size == 0 ? getOffset(data) : 1;
		return Arrays.copyOfRange(data, offset, data.length);
	}

	private static String getStringValue(DlmsType type, byte[] payload) throws DlmsException {
		switch (type) {
		case ARRAY:
			return bytesToHex(payload);
		case BITSTRING:
			return bytesToHex(payload);
		case BOOLEAN:
			return bytesToHex(payload);
		case DATE:
			return bytesToHex(payload);
		case ENUM:
			return bytesToHex(payload);
		case FLOAT32:
			return Float.toString(Float.intBitsToFloat(ByteBuffer.wrap(payload).getInt(0)));
		case INT16:
			return Integer.toString(ByteBuffer.wrap(payload).getShort(0));
		case INT32:
			return Integer.toString(ByteBuffer.wrap(payload).getInt(0));
		case INT64:
			return Long.toString(ByteBuffer.wrap(payload).getLong(0));
		case INT8:
			return Integer.toString(payload[0]);
		case OCTET_STRING:
			return bytesToHex(payload);
		case STRING:
			return new String(payload);
		case STRUCTURE:
			return bytesToHex(payload);
		case TIME:
			return bytesToHex(payload);
		case UINT16:
			return Integer.toString(ByteBuffer.wrap(payload).getShort(0) & 0xFFFF);
		case UINT32:
			return Integer.toString(ByteBuffer.wrap(payload).getInt(0));
		case UINT64:
			return Long.toString(ByteBuffer.wrap(payload).getLong(0));
		case UINT8:
			return Integer.toString(payload[0]&0xFF);
		default:
			throw new DlmsException(DlmsExceptionReason.NO_SUCH_TYPE);
		}
	}

	private static String rawBytesToString(DlmsType type, byte[] data) throws DlmsException {
		int size = type.size == 0 ? getSize(data) : type.size;
		byte[] payload = getPayload(type, data);
		String text = getStringValue(type, payload);
		if (size != 0) {
			return type.name()+" | Size: "+size+" | Value: "+text;
		} else {
			return type.name()+" | Value: "+text;
		}
	}

	private static int getOffset(byte[] data) {
		if ((data[1] & 0xFF) <= 0x80) {
			return 2;
		}
		return (data[1] & 0x0F) + 2;
	}

	private static int getSize(byte[] data) {
		if ((data[1] & 0xFF) <= 0x80) {
			return data[1] & 0xFF;
		}
		if (data[1] == (byte)0x81) {
			return data[2] & 0xFF;
		}
		if (data[1] == (byte)0x82) {
			return ByteBuffer.allocate(2).put(data, 2, 2).getShort(0);
		}
		if (data[1] == (byte)0x83) {
			return ByteBuffer.allocate(4).put((byte)0x00).put(data, 2, 3).getInt(0);
		}
		if (data[1] == (byte)0x84) {
			return ByteBuffer.allocate(4).put(data, 2, 4).getInt(0);
		}
		throw new IllegalArgumentException();
	}
	
	private static String bytesToHex(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02x ", b));
		}
		return sb.toString();
	}
	
	private static String getDateAndTimeString(byte[] bytes) {
		if (bytes.length < 8) {
			throw new IllegalArgumentException();
		}
		String year = getYear(bytes);
		String month = getDateValue(bytes[2], "MM");
		String day = getDateValue(bytes[3], "DD");
		String hour = getDateValue(bytes[5], "HH");
		String min = getDateValue(bytes[6], "mm");
		String sec = getDateValue(bytes[7], "SS");
		return day+"/"+month+"/"+year+" "+hour+":"+min+":"+sec;
	}

	private static String getYear(byte[] bytes) {
		if(bytes[0] == (byte)0xFF && bytes[1] == (byte)0xFF) {
			return "YY";
		}
		return String.format("%04d", ByteBuffer.allocate(2).put(bytes,0,2).getShort(0));
	}
	
	private static String getDateValue(byte val, String replacement ) {
		if(val == (byte)0xFF) {
			return replacement;
		}
		return String.format("%02d", val & 0xFF);
	}

}
