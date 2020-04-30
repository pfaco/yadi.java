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

import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class Emode {

	/**
	 * Performs the steps necessary for a connection in the MODE-E
	 * @param com PhyLayer object to be used for data tx/rx
	 * @throws PhyLayerException
	 */
	public static void connect(PhyLayer com) throws PhyLayerException {
		try {
			com.sendData("/?!\r\n".getBytes());
			byte[] rxBuff = com.readData(1000, (a) -> isFrameComplete(a));
			byte baud = (rxBuff[4] & 0xFF) > 0x35 ? 0x35 : rxBuff[4];
			com.sendData(new byte[]{0x06, 0x32, baud, 0x32, 0x0D, 0x0A});
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean isFrameComplete(byte[] data) {
		return data.length >= 15 && data[data.length-2] == 0x0D && data[data.length-1] == 0x0A;
	}
}
