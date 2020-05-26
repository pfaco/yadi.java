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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import yadi.dlms.linklayer.LinkLayerException.LinkLayerExceptionReason;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class WrapperLinkLayer implements LinkLayer {
	
	private static final short WRAPPER_VERSION = 1;
	private final WrapperParameters params;
	
	/**
	 * Creates a WrapperLinkLayer object
	 */
	public WrapperLinkLayer() {
		this (new WrapperParameters());
	}
	
	/**
	 * Creates a WrapperLinkLayer object
	 * @param params the WrapperParameters for this object
	 */
	public WrapperLinkLayer(WrapperParameters params) {
		this.params = params;
	}

	/**
	 * Wrapper doesn't have a connection procedure, this function doesn't need to be called when
	 * using the Wrapper protocol as link layer for the COSEM APDU's.
	 * @param phy the PhyLayer to transmit and receive bytes
	 */
	@Override
	public void connect(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		//no connection necessary
	}

	/**
	 * Wrapper doesn't have a disconnection procedure, this function doesn't need to be called when
	 * using the Wrapper protocol as link layer for the COSEM APDU's.
	 * @param phy the PhyLayer to transmit and receive bytes
	 */
	@Override
	public void disconnect(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		//no disconnection necessary
	}

	/**
	 * Encapsulates data inside a Wrapper frame and sends it
	 * @param phy the PhyLayer to transmit and receive bytes
	 * @param data the array of bytes to be encapsulated and transmitted
	 */
	@Override
	public void send(PhyLayer phy, byte[] data) throws PhyLayerException, LinkLayerException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(WRAPPER_VERSION >>> 8);
		stream.write(WRAPPER_VERSION);
		stream.write(params.wPortSource >>> 8);
		stream.write(params.wPortSource);
		stream.write(params.wPortDestination >>> 8);
		stream.write(params.wPortDestination);
		stream.write(data.length >>> 8);
		stream.write(data.length);
		for (byte b : data) {
			stream.write(b);
		}
		phy.sendData(stream.toByteArray());		
	}

	/**
	 * Retrieves the data encapsulated inside a Wrapper frame
	 * @param phy the PhyLayer to receive bytes
	 * @return array of bytes with the application data unit contents inside the Wrapper frame
	 */
	@Override
	public byte[] read(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		byte[] data = phy.readData(params.timeoutMillis, (a) -> messageComplete(a));
		
		short version = ByteBuffer.allocate(2).put(data,0,2).getShort(0);
		short wPortSource = ByteBuffer.allocate(2).put(data,2,2).getShort(0);
		short wPortDestination = ByteBuffer.allocate(2).put(data,4,2).getShort(0);
		short length = ByteBuffer.allocate(2).put(data,6,2).getShort(0);
		
		if (version != WRAPPER_VERSION) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_FRAME_FORMAT);
		}
		
		if (wPortSource != params.wPortDestination ||
		    wPortDestination != params.wPortSource	) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_ADDRESS);
		}
		
		if (length != data.length - 8) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_FRAME_FORMAT);
		}
		
		return Arrays.copyOfRange(data, 8, data.length);
	}
	
	private boolean messageComplete(byte[] data) {
		if (data.length < 8) {
			return false;
		}
		short size = ByteBuffer.allocate(2).put(data,0,2).getShort(0);

		if (data.length < size + 8) {
			return false;
		}
		
		return true;
	}

	public WrapperParameters getParameters() {
		return params;
	}

}
