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

public class WrapperParameters {
	
	private static final short CLIENT_MANAGEMENT_PROCESS = 0x0001;
	private static final short BROADCAST = 0x007F;
	
	public short wPortSource = CLIENT_MANAGEMENT_PROCESS;
	public short wPortDestination = BROADCAST;
	public int timeoutMillis = 1500;

}
