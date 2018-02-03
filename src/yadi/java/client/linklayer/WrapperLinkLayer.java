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
package yadi.java.client.linklayer;

import java.io.ByteArrayOutputStream;
import yadi.java.client.phylayer.PhyLayer;
import yadi.java.client.phylayer.PhyLayerException;

public class WrapperLinkLayer implements LinkLayer {
	
	private static final short WRAPPER_VERSION = 1;
	private final WrapperParameters params;
	
	public WrapperLinkLayer(WrapperParameters params) {
		this.params = params;
	}

	@Override
	public void connect(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		//no connection necessary
	}

	@Override
	public void disconnect(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		//no disconnection necessary
	}

	@Override
	public void send(PhyLayer phy, byte[] data) throws PhyLayerException, LinkLayerException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(WRAPPER_VERSION >>> 8);
		stream.write(WRAPPER_VERSION);
		stream.write(params.wPortSource >>> 8);
		stream.write(params.wPortSource);
		stream.write(params.wPortDestination >>> 8);
		stream.write(params.wPortDestination);
		stream.write(data.length);
		for (byte b : data) {
			stream.write(b);
		}
		phy.sendData(stream.toByteArray());
	}

	@Override
	public byte[] read(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		byte[] data = phy.readData(params.timeoutMillis, (a) -> messageComplete(a));
		//TODO
		return data;
	}
	
	private boolean messageComplete(byte[] data) {
		if (data.length < 8) {
			return false;
		}
		short size = (short)(((short)data[0])&0xff << 8 | data[1]);

		if (data.length < size + 8) {
			return false;
		}
		
		return true;
	}

}
