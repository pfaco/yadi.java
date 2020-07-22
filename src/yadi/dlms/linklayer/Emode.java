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
	public static int proposeBaudRate(PhyLayer phy) throws PhyLayerException {
		phy.sendData("/?!\r\n".getBytes());
		byte[] rxBuff = phy.readData(1000, (a) -> isFrameComplete(a));
		byte baud = (rxBuff[4] & 0xFF) > 0x35 ? 0x35 : rxBuff[4];
		phy.sendData(new byte[]{0x06, 0x32, baud, 0x32, 0x0D, 0x0A});
		try {
			/* Wait for the transmission of the last command
			 * It should take (6*8)/300 = 160ms
			 * This is necessary because some serial port adapters
			 * Will cancel the current transmission if you try to
			 * Change the serial port configuration.
			 */
			Thread.sleep(180);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return baud;
	}
	
	public static void ackNewBaud(PhyLayer phy) throws PhyLayerException {
		phy.readData(1000, (a) -> isAckReceived(a));
	}

	public static boolean isFrameComplete(byte[] data) {
		return data.length >= 15 && data[data.length-2] == 0x0D && data[data.length-1] == 0x0A;
	}
	
	public static boolean isAckReceived(byte[] data) {
		return data.length >= 6 && (data[0] == 0x06 || data[1] == 0x06);
	}
}
