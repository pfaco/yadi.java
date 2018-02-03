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

public class Obis {
	
	private final byte[] value;
	
	public Obis(int A, int B, int C, int D, int E, int F) {
		value = new byte[] {(byte)A, (byte)B, (byte)C, (byte)D, (byte)E, (byte)F};
	}
	
	public Obis(String obis) {
		String[] data = obis.split("\\.");
		if (data.length != 6) {
			System.out.println(data.length);
			throw new IllegalArgumentException("OBIS must have 6 bytes");
		}
		value = new byte[] {(byte)Integer.parseInt(data[0]),
							(byte)Integer.parseInt(data[1]),
							(byte)Integer.parseInt(data[2]),
							(byte)Integer.parseInt(data[3]),
							(byte)Integer.parseInt(data[4]),
							(byte)Integer.parseInt(data[5])};
	}
	
	public byte[] getValue() {
		return value;
	}

}
