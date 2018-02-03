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
package yadi.java.client.phylayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import yadi.java.client.phylayer.PhyLayerException.PhyLayerExceptionReason;

public class TcpPhyLayer implements PhyLayer {
	private final ArrayList<PhyLayerListener> listeners = new ArrayList<PhyLayerListener>();
	private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private Socket socket;
		
	public void connect(String ip, int port) throws PhyLayerException {
		try {
			socket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INVALID_CHANNEL);
		} catch (IOException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			// silence disconnection
		}
	}

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

	@Override
	public void addListener(PhyLayerListener listener) {
		listeners.add(listener);
	}

}
