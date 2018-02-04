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
package yadi.dlms.linklayer;

public class HdlcParameters {
	
	final int windowSizeTx = 1; //windowSizeTx != 1 not supported
	final int windowSizeRx = 1; //windowSizeTx != 1 not supported
	int timeoutMillis = 1500;
	int maxInformationFieldLengthTx = 128;
	int maxInformationFieldLengthRx = 128;
	byte[] serverAddress = new byte[]{0x00, 0x02, (byte)0xFE, (byte)0xFF};
	byte clientAddress = 0x03;
	
	public void setMaxInformationFieldLength(int maxLength) {
		if (maxLength < 0) {
			throw new IllegalArgumentException("Information field length must be positive");
		}
		maxInformationFieldLengthTx = maxInformationFieldLengthRx = maxLength;
	}
	
	public void setTimeout(int millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Timeout must be positive");
		}
		this.timeoutMillis = millis;
	}
	
	public void setClientAddress(int clientAddress) {
		if (clientAddress < 0 || clientAddress > 0x7F) {
			throw new IllegalArgumentException("Maximum client address is 0x7F");
		}
		this.clientAddress = (byte)((clientAddress << 1) | 0x01);
	}
	
	public void setServerAddress(int serverAddr) {
		if (serverAddr < 0 || serverAddr > 0x7F) {
			throw new IllegalArgumentException("Maximum client address is 0x7F");
		}
		this.serverAddress = new byte[] {(byte)((serverAddr << 1) | 0x01)};
	}
	
	public void setServerAddress(int upperAddress, int lowerAddress) {
		if (lowerAddress < 0 || lowerAddress > 0x3FFF || upperAddress < 0 || upperAddress > 0x3FFF) {
			throw new IllegalArgumentException("Maximum address is 0x3FFF");
		}
		this.serverAddress = new byte[]{ (byte)((upperAddress >>> 6) & 0xFE), 
				                         (byte)((upperAddress << 1)  & 0xFE),
				                         (byte)((lowerAddress >>> 6) & 0xFE),
				                         (byte)((lowerAddress << 1)  | 0x01)};
	}
	
}
