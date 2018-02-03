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
	
	private static void verify(byte[] data) throws DlmsException {
		if (data == null || data.length < 2) {
			throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
		}
	}
	
	public static DlmsType getTypeFromRawBytes(byte[] data) throws DlmsException {
		verify(data);
		return DlmsType.fromTag(data[0]);
	}

	public static String parseRawBytes(byte[] data) throws DlmsException {
		verify(data);
		return rawBytesToString(DlmsType.fromTag(data[0]), data);
	}
	
	public static String parseRawBytes(DlmsType type, byte[] data) throws DlmsException {
		verify(data);
		return rawBytesToString(type, data);
	}
	
	public static String getStringValue(byte[] data) throws DlmsException {
		verify(data);
		DlmsType type = DlmsType.fromTag(data[0]);
		return getStringValue(type, getPayload(type, data));
	}
	
	private static byte[] getPayload(DlmsType type, byte[] data) {
		int size = type.size;
		int offset = 1;
		if (size == 0) {
			size = getSize(data);
			offset = getOffset(data);
		}
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
		int size = type.size;
		if (size == 0) {
			size = getSize(data);
		}
		byte[] payload = getPayload(type, data);
		String text = getStringValue(type, payload);
		switch (type) {
		case ARRAY:
			text = bytesToHex(payload);
			break;
		case BITSTRING:
			text = bytesToHex(payload);
			break;
		case BOOLEAN:
			text = bytesToHex(payload);
			break;
		case DATE:
			text = bytesToHex(payload);
			break;
		case ENUM:
			text = bytesToHex(payload);
			break;
		case FLOAT32:
			text = Float.toString(Float.intBitsToFloat(ByteBuffer.wrap(payload).getInt(0)));
			break;
		case INT16:
			text = Integer.toString(ByteBuffer.wrap(payload).getShort(0));
			break;
		case INT32:
			text = Integer.toString(ByteBuffer.wrap(payload).getInt(0));
			break;
		case INT64:
			text = Long.toString(ByteBuffer.wrap(payload).getLong(0));
			break;
		case INT8:
			text = Integer.toString(payload[0]);
			break;
		case OCTET_STRING:
			text = bytesToHex(payload);
			break;
		case STRING:
			text = new String(payload);
			break;
		case STRUCTURE:
			text = bytesToHex(payload);
			break;
		case TIME:
			text = bytesToHex(payload);
			break;
		case UINT16:
			text = Integer.toString(ByteBuffer.wrap(payload).getShort(0) & 0xFFFF);
			break;
		case UINT32:
			text = Integer.toString(ByteBuffer.wrap(payload).getInt(0));
			break;
		case UINT64:
			text = Long.toString(ByteBuffer.wrap(payload).getLong(0));
			break;
		case UINT8:
			text = Integer.toString(payload[0]&0xFF);
			break;
		default:
			throw new DlmsException(DlmsExceptionReason.NO_SUCH_TYPE);
		}
		return type.name()+" | Size: "+size+" | Value: "+text;
	}

	private static int getOffset(byte[] data) {
		if ((data[1] & 0xFF) < 0x80) {
			return 2;
		}
		return 2 + (data[1] & 0x7F);
	}

	private static int getSize(byte[] data) {
		if ((data[1] & 0xFF) < 0x80) {
			return data[1];
		}
		int len = data[1] & 0x7F;
		int offset = 2;
		int size = 0;
		while (len-- > 0) {
			size <<= 8;
			size |= data[offset++] & 0xFF;
		}
		return size;
	}
	
	private static String bytesToHex(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02x ", b));
		}
		return sb.toString();
	}
}
