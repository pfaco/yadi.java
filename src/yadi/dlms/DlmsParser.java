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
package yadi.dlms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import yadi.dlms.DlmsException.DlmsExceptionReason;

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
	public static DlmsItem getDlmsItem(byte[] data) throws DlmsException {
		verify(data);
		DlmsType type = DlmsType.fromTag(data[0]);
		DlmsItem item = new DlmsItem(type, getString(data));
		int numberOfItems = getNumberOfItems(type,data);
		for (int i = 0; i < numberOfItems; ++i) {
			data = parseItems(item, getNextData(data));
		}
		return item;
	}

	private static byte[] parseItems(DlmsItem parent, byte[] data) throws DlmsException {
		if (data.length == 0) {
			return data;
		}
		DlmsType type = DlmsType.fromTag(data[0]);
		DlmsItem item = new DlmsItem(type, getString(data));
		parent.addChildren(item);
		int numberOfItems = getNumberOfItems(type,data);
		for (int i = 0; i < numberOfItems; ++i) {
			data = parseItems(item, getNextData(data));
		}
		return data;
	}
	
	private static byte[] getNextData(byte[] data) throws DlmsException {
		if (data == null || data.length == 0) {
			return new byte[0];
		}
		DlmsType type = DlmsType.fromTag(data[0]);
		if (getNumberOfItems(type,data) == 0) {
			int offset = type.size == 0 ? getSize(data) + getOffset(data) : type.size + 1;
			return Arrays.copyOfRange(data, offset, data.length);
		}
		int offset = getOffset(data);
		return Arrays.copyOfRange(data, offset, data.length);
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
	
	public static String getString(DlmsType type, ByteArrayInputStream is) throws DlmsException {
		verify(is);
		return DlmsParser.getStringValue(type, getPayload(type, is));
	}

	/**
	 * Retrieves the String representation of the element in the array of bytes
	 * @param type the DlmsType of the byte array
	 * @param data array of bytes containing data result from a dlms-get
	 * @return A Strig that represents the element in the data
	 * @throws DlmsException
	 */
	public static String getString(DlmsType type, byte[] payload) throws DlmsException {
		return DlmsParser.getStringValue(type, payload);
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
		return DlmsParser.getDateTimeStringValue(getPayload(type, data));
	}
	
	/**
	 * Retrieves the Date String representation of the element in the array of bytes
	 * @param data array of bytes containing data result from a dlms-get
	 * @return A Strig that represents the date in the data
	 * @throws DlmsException
	 */
	public static String getDateString(byte[] data) throws DlmsException {
		verify(data);
		DlmsType type = DlmsType.fromTag(data[0]);
		return DlmsParser.getDateStringValue(getPayload(type, data));
	}
	
	/**
	 * Retrieves the Time String representation of the element in the array of bytes
	 * @param data array of bytes containing data result from a dlms-get
	 * @return A Strig that represents the time in the data
	 * @throws DlmsException
	 */
	public static String getTimeString(byte[] data) throws DlmsException {
		verify(data);
		DlmsType type = DlmsType.fromTag(data[0]);
		return DlmsParser.getTimeStringValue(getPayload(type, data));
	}
	
	private static int getNumberOfItems(DlmsType type, byte[] data) {
		if (type.equals(DlmsType.ARRAY) ||type.equals(DlmsType.STRUCTURE)) {
			return getSize(data);
		}
		return 0;
	}

	private static void verify(byte[] data) throws DlmsException {
		if (data == null || data.length < 2) {
			throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
		}
	}
	
	private static void verify(ByteArrayInputStream is) throws DlmsException {
		if (is == null || is.available() < 2) {
			throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
		}
	}
	
	public static byte[] getPayload(DlmsType type, byte[] data) {
		int offset = type.size == 0 ? getOffset(data) : 1;
		int size = type.size == 0 ? getSize(data) : type.size;
		return Arrays.copyOfRange(data, offset, offset+size);
	}
	
	public static byte[] getPayload(DlmsType type, ByteArrayInputStream is) throws DlmsException {
		try {
			int size = type.size == 0 ? getSize(is) : type.size;
			byte[] data = new byte[size];
			is.read(data);
			return data;
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
		}
	}
	
	public static byte[] pack(DlmsType type, byte[] data) {
		int size = type.size == 0 ? data.length : type.size; //TODO size > 1 byte
		int offset = type.size == 0 ? 1 : 0;
		byte[] retval = new byte[data.length + 1 + offset];
		retval[0] = type.tag;
		if (offset != 0) {
			retval[1] = (byte)data.length;
		}
		System.arraycopy(data, 0, retval, offset+1, data.length);
		return retval;
	}
	

	public static int getInteger(byte[] data) {
		int val = 0;
		for (int i = 1; i < data.length; ++i) {
			val <<= 8;
			val += (data[i] & 0xFF);
		}
		
		return val;
	}
	
	public static boolean getBoolean(byte[] responseData) throws DlmsException {
		if (responseData.length != 2) {
			throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
		}
		if (responseData[0] != DlmsType.BOOLEAN.tag) {
			throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
		}
		return 0 != responseData[1];
	}

	private static String getStringValue(DlmsType type, byte[] payload) throws DlmsException {
		switch (type) {
		case ARRAY:
			return bytesToHex(payload);
		case BCD:
			return bytesToHex(payload);
		case BITSTRING:
			return bytesToHex(payload);
		case BOOLEAN:
			return bytesToHex(payload);
		case DATE:
			return getDateStringValue(payload);
		case DATE_TIME:
			return getDateTimeStringValue(payload);
		case ENUM:
			return bytesToHex(payload);
		case FLOAT32:
			return Float.toString(ByteBuffer.wrap(payload).getFloat());
		case FLOAT64:
			return Double.toString(ByteBuffer.wrap(payload).getDouble());
		case INT16:
			return Integer.toString(ByteBuffer.wrap(payload).getShort());
		case INT32:
			return Integer.toString(ByteBuffer.wrap(payload).getInt());
		case INT64:
			return Long.toString(ByteBuffer.wrap(payload).getLong());
		case INT8:
			return Integer.toString(payload[0]);
		case OCTET_STRING:
			return bytesToHex(payload);
		case STRING:
			return new String(payload, Charset.forName("US-ASCII"));
		case STRUCTURE:
			return bytesToHex(payload);
		case TIME:
			return getTimeStringValue(payload);
		case UINT16:
			return Integer.toString(ByteBuffer.wrap(payload).getShort() & 0xFFFF);
		case UINT32:
			return Integer.toUnsignedString(ByteBuffer.wrap(payload).getInt());
		case UINT64:
			return Long.toUnsignedString(ByteBuffer.wrap(payload).getLong());
		case UINT8:
			return Integer.toString(payload[0] & 0xFF);
		case UTF8_STRING:
			return new String(payload, Charset.forName("UTF-8"));
		}
		throw new DlmsException(DlmsExceptionReason.NO_SUCH_TYPE);
	}
	
	public static byte[] getByteValue(DlmsType type, String value) throws DlmsException {
		switch (type) {
		case ARRAY:
		case BCD:
		case BITSTRING:
		case BOOLEAN:
		case ENUM:
		case OCTET_STRING:
		case STRUCTURE:
			return pack(type, hexStringToBytes(value));
		case DATE:
			//TODO return getDateStringValue(payload);
		case DATE_TIME:
			return pack(type, getDateAndTimeBytes(value));
		case FLOAT32:
			//TODO return pack(type, );
		case FLOAT64:
			//TODO return Double.toString(ByteBuffer.wrap(payload).getDouble());
		case INT16:
		case INT32:
		case INT64:
		case INT8:
			return pack(type, getIntegerBytes(type,value));
		case STRING:
			return pack(type, value.getBytes());
		case TIME:
			//TODO return getTimeStringValue(payload);
		case UINT16:
		case UINT32:
		case UINT64:
		case UINT8:
			return pack(type, getIntegerBytes(type,value));
		case UTF8_STRING:
			return pack(type, value.getBytes());
		}
		throw new DlmsException(DlmsExceptionReason.NO_SUCH_TYPE);
	}

	private static int getOffset(byte[] data) {
		if ((data[1] & 0xFF) <= 0x80) {
			return 2;
		}
		return (data[1] & 0x0F) + 2;
	}
	
	private static int getOffset(ByteArrayInputStream is) {
		int value = is.read();
		if (value <= 0x80) {
			return 2;
		}
		return value + 2;
	}

	private static int getSize(byte[] data) {
		if ((data[1] & 0xFF) <= 0x80) {
			return data[1] & 0xFF;
		}
		if (data[1] == (byte)0x81) {
			return data[2] & 0xFF;
		}
		if (data[1] == (byte)0x82) {
			return ByteBuffer.wrap(data, 2, 2).getShort();
		}
		if (data[1] == (byte)0x83) {
			return ByteBuffer.allocate(4).put((byte)0x00).put(data, 2, 3).getInt(0);
		}
		if (data[1] == (byte)0x84) {
			return ByteBuffer.wrap(data, 2, 4).getInt();
		}
		throw new IllegalArgumentException();
	}
	
	private static int getSize(ByteArrayInputStream is) {
		int size = is.read();
		if (size <= 0x80) {
			return size;
		}
		if (size == 0x81) {
			return is.read();
		}
		if (size== 0x82) {
			size = is.read();
			size <<= 8;
			size |= is.read();
			return size;
		}
		if (size == 0x83) {
			size = is.read();
			size <<= 8;
			size |= is.read();
			size <<= 8;
			size |= is.read();
			return size;
		}
		if (size == 0x84) {
			size = is.read();
			size <<= 8;
			size |= is.read();
			size <<= 8;
			size |= is.read();
			size <<= 8;
			size |= is.read();
			return size;
		}
		throw new IllegalArgumentException();
	}
	
	private static String bytesToHex(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}
	
	private static byte[] hexStringToBytes(String s) {
		s = s.replaceAll(" ", "");
	    int len = s.length();
	    if((len & 0x01) != 0) {
	    	s = "0"+s;
	    	++len;
	    }
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	private static String getTimeStringValue(byte[] bytes) {
		if (bytes.length < 4) {
			throw new IllegalArgumentException();
		}
		String hour = getDateValue(bytes[0], "HH");
		String min = getDateValue(bytes[1], "mm");
		String sec = getDateValue(bytes[2], "SS");
		return hour+":"+min+":"+sec;
	}
	
	private static String getDateStringValue(byte[] bytes) {
		if (bytes.length < 5) {
			throw new IllegalArgumentException();
		}
		String year = getYear(bytes);
		String month = getDateValue(bytes[2], "MM");
		String day = getDateValue(bytes[3], "DD");
		return year+"/"+month+"/"+day;
	}
	
	private static String getDateTimeStringValue(byte[] bytes) {
		if (bytes.length < 8) {
			throw new IllegalArgumentException();
		}
		String year = getYear(bytes);
		String month = getDateValue(bytes[2], "MM");
		String day = getDateValue(bytes[3], "DD");
		String hour = getDateValue(bytes[5], "HH");
		String min = getDateValue(bytes[6], "mm");
		String sec = getDateValue(bytes[7], "SS");
		return year+"/"+month+"/"+day+" "+hour+":"+min+":"+sec;
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
	
	private static byte[] getIntegerBytes(DlmsType type, String str) {
		byte[] bytes = new byte[type.size];
		bytes[0] = type.tag;
		int val = 0;
		try {
			val = Integer.parseInt(str);
		} catch (Exception e) {
			val = 0;
		}
		for(int i = 0; i < type.size; ++i) {
			bytes[type.size-i] = (byte)(val & 0x00FF); 
			val >>>= 8;
		}
		return bytes;
	}
	
	private static byte[] getDateAndTimeBytes(String str) {
		str = str.replaceAll(" ", "");
		str = str.replaceAll("/", "");
		str = str.replaceAll(":", "");
		String aux;
		byte[] bytes = new byte[12];
		aux = str.substring(0, 2);
		try {
			int index = 0;
			int day = Integer.parseInt(aux);
			aux = str.substring(2, 4);
			int month = Integer.parseInt(aux);
			aux = str.substring(4, 8);
			int year = Integer.parseInt(aux);
			aux = str.substring(8, 10);
			int hour = Integer.parseInt(aux);
			aux = str.substring(10, 12);
			int min = Integer.parseInt(aux);
			aux = str.substring(12, 14);
			int sec = Integer.parseInt(aux);
			bytes[index++] = (byte)((year & 0xFF00) >>> 8);
			bytes[index++] = (byte)(year & 0xFF);
			bytes[index++] = (byte)month;
			bytes[index++] = (byte)day;
			bytes[index++] = 0x00; //day of week
			bytes[index++] = (byte)hour;
			bytes[index++] = (byte)min;
			bytes[index++] = (byte)sec;
			bytes[index++] = 0x00; //millis
			bytes[index++] = (byte)0x80; //deviation MSB
			bytes[index++] = (byte)0x00; //deviation LSB
			bytes[index++] = (byte)0x00; //clock status
		} catch (Exception e) {
			System.out.println("DateTime malformed");
		}
		return bytes;
	}

	public static byte[] getDateTimeByteValue(String string) {
		byte[] data = new byte[14];
		byte[] datatime = getDateAndTimeBytes(string);
		data[0] = DlmsType.OCTET_STRING.tag;
		data[1] = 12;
		System.arraycopy(datatime, 0, data, 2, datatime.length);
		return data;
	}

}
