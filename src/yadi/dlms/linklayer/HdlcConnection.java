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

class HdlcConnection {
	int windowSizeRx = 1;
	int windowSizeTx = 1;
	int maxInformationFieldRx = 128;
	int maxInformationFieldTx = 128;
	int sequenceNumberRX = 0;
	int sequenceNumberTX = 0;
	int receivedControl;
	int receivedRrr;
	int receivedSss;
	int sss;
	boolean isFinalPoll;
	boolean lastFrameHadSss;
	byte[] receivedData;
	
	void reset() {
		sequenceNumberRX = sequenceNumberTX = 0;
		windowSizeRx = windowSizeTx = 1;
		maxInformationFieldRx = maxInformationFieldTx = 128;
		sss = receivedRrr = receivedSss = 0;
		receivedControl = 0;
		isFinalPoll = false;
		lastFrameHadSss = false;
	}
	
	void incSss()
	{
		sss += 1;
		sss &= 0x07;
	}

	public void insReceivedSss() {
		if (lastFrameHadSss) {
			receivedSss += 1;
			receivedSss &= 0x07;
		}
	}
}
