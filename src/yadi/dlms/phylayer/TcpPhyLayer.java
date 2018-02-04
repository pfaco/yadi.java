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
package yadi.dlms.phylayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import yadi.dlms.phylayer.PhyLayerException.PhyLayerExceptionReason;

public class TcpPhyLayer implements PhyLayer {
	
	private final ArrayList<PhyLayerListener> listeners = new ArrayList<PhyLayerListener>();
	private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private Socket socket;
		
	/**
	 * Opens the TCP socket
	 * @param ip String representing the IP to connect to
	 * @param port Number of the port to connect to
	 * @throws PhyLayerException
	 */
	public void open(String ip, int port) throws PhyLayerException {
		try {
			socket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INVALID_CHANNEL);
		} catch (IOException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
	}

	/**
	 * Closes the TCP socket
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// silence disconnection
		}
	}

	/**
	 * Sends data through the TCP socket
	 * @param data array of bytes to be sent
	 */
	@Override
	public void sendData(byte[] data) throws PhyLayerException {
		try {
			socket.getOutputStream().write(data);
			socket.getOutputStream().flush();
			for (PhyLayerListener listener : listeners) {
				listener.dataSent(data);
			}
		} catch (IOException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Read data from the TCP socket
	 * @param timeoutMillis maximum time to wait for a complete frame, in milliseconds
	 * @param parser a PhyLayerParser to determine when a complete frame was received
	 */
	@Override
	public byte[] readData(int timeoutMillis, PhyLayerParser parser) throws PhyLayerException {
		if (timeoutMillis < 0 || parser == null) {
			throw new IllegalArgumentException();
		}
		try {
			byte[] data = new byte[256];
			InputStream input = socket.getInputStream();
			stream.reset();
			long timeLimit = System.nanoTime() + (timeoutMillis * 1000000L);
			while (timeLimit > System.nanoTime()) {
				if (input.available() > 0) {
					int len = input.read(data);
					stream.write(data, 0, len);
					if (parser.isFrameComplete(stream.toByteArray())) {
						for (PhyLayerListener listener : listeners) {
							listener.dataReceived(stream.toByteArray());
						}
						return stream.toByteArray();
					}
				}
			}
			throw new PhyLayerException(PhyLayerExceptionReason.TIMEOUT);
		} catch (IOException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
	}

	/**
	 * Adds a listener to the TCP socket.
	 * Each listener will receive an array of bytes containing each frame that is sent and received
	 * through the TCP socket
	 */
	@Override
	public void addListener(PhyLayerListener listener) {
		listeners.add(listener);
	}

}
