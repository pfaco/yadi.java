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
import java.util.ArrayList;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import yadi.dlms.phylayer.PhyLayerException.PhyLayerExceptionReason;

public final class SerialPhyLayer implements PhyLayer {
	private SerialPort serialPort;
	private final ArrayList<PhyLayerListener> listeners = new ArrayList<PhyLayerListener>();
	private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
	
	public enum DataBits {
		_5(5),
		_6(6),
		_7(7),
		_8(8);
		int dataBits;
		DataBits(int dataBits) {
			this.dataBits = dataBits;
		}
	}
	
	public enum Parity {
	    NONE(SerialPort.NO_PARITY),
	    EVEN(SerialPort.EVEN_PARITY),
	    ODD(SerialPort.ODD_PARITY),
	    MARK(SerialPort.MARK_PARITY),
	    SPACE(SerialPort.SPACE_PARITY);
		int par;
		Parity(int par) {
			this.par = par;
		}
	}
	
	public enum StopBits {
		_1(SerialPort.ONE_STOP_BIT),
		_1_5(SerialPort.ONE_POINT_FIVE_STOP_BITS),
		_2(SerialPort.TWO_STOP_BITS);
		int stopBits;
		StopBits(int stopBits) {
			this.stopBits = stopBits;
		}
	}
	
	/**
	 * Retrieves the list of serial ports in the system
	 * @return an array of Strings with the name of each serial port in the system
	 */
	public static String[] getListOfAvailableSerialPorts() {
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] retval = new String[ports.length];
		for (int i = 0; i < ports.length; ++i) {
			retval[i] = ports[i].getSystemPortName();
		}
		return retval;
	}
	
	/**
	 * Sets the RTS pin of the serial port
	 * @param enabled true if the RTS pin should be set, false if it should be cleared
	 * @throws PhyLayerException
	 */
	public void setRTS() throws PhyLayerException {
		if (!serialPort.setRTS()) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
		try {
			Thread.sleep(250);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens the serial port
	 * @param serialName name of the serial port to be opened
	 * @throws PhyLayerException
	 */
	public void open(String serialName) throws PhyLayerException {
		try {
			serialPort = SerialPort.getCommPort(serialName);
			if (!serialPort.openPort()) {
				throw new PhyLayerException(PhyLayerExceptionReason.BUSY_CHANNEL);
			}
			setRTS();
		} catch (SerialPortInvalidPortException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.BUSY_CHANNEL);
		}
	}
	
	/**
	 * Closes the serial port
	 */
	public void close() {
		serialPort.closePort();
	}
	
	/**
	 * Configures the serial port parameters
	 * @param baudRate the desired baudrate im bps
	 * @param dataBits the number of data bits in each byte
	 * @param parity the type of parity to be used
	 * @param stopBits the number of stop bits
	 * @throws PhyLayerException
	 */
	public void config(int baudRate, DataBits dataBits, Parity parity, StopBits stopBits) throws PhyLayerException  {
		if (!serialPort.setBaudRate(baudRate)) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
		if (!serialPort.setNumDataBits(dataBits.dataBits)) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
		if (!serialPort.setParity(parity.par)) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
		if (!serialPort.setNumStopBits(stopBits.stopBits)) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Sends data through the serial port
	 * @param data array of bytes to be sent
	 */
	@Override
	public void sendData(byte[] data) throws PhyLayerException {
		if (serialPort.writeBytes(data, data.length) == -1) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
		for (PhyLayerListener listener : listeners) {
			listener.dataSent(data);
		}
    }

	/**
	 * Read data from the serial port
	 * @param timeoutMillis maximum time to wait for a complete frame, in milliseconds
	 * @param parser a PhyLayerParser to determine when a complete frame was received
	 */
	@Override
	public byte[] readData(int timeoutMillis, PhyLayerParser parser) throws PhyLayerException {
		if (timeoutMillis < 0 || parser == null) {
			throw new IllegalArgumentException();
		}

		byte[] data = new byte[256];
		stream.reset();
		long timeLimit = System.nanoTime() + (timeoutMillis * 1000000L);
		
		while (timeLimit > System.nanoTime()) {
			int len = serialPort.readBytes(data, data.length);
		
			if (len == -1) {
				throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
			}
			
			if (len > 0) {
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
	}

	/**
	 * Adds a listener to the serial port.
	 * Each listener will receive an array of bytes containing each frame that is sent and received
	 * through the serial port
	 */
	@Override
	public void addListener(PhyLayerListener listener) {
		listeners.add(listener);
	}
	
}