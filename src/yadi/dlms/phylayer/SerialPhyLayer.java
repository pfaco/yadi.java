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
import java.util.ArrayList;

import jssc.*;
import yadi.dlms.phylayer.PhyLayerException.PhyLayerExceptionReason;

public final class SerialPhyLayer implements PhyLayer {
	private SerialPort serialPort;
	private final ArrayList<PhyLayerListener> listeners = new ArrayList<PhyLayerListener>();
	private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
	
	public enum DataBits {
		_5(SerialPort.DATABITS_5),
		_6(SerialPort.DATABITS_6),
		_7(SerialPort.DATABITS_7),
		_8(SerialPort.DATABITS_8);
		int dataBits;
		DataBits(int dataBits) {
			this.dataBits = dataBits;
		}
	}
	
	public enum Parity {
	    NONE(SerialPort.PARITY_NONE),
	    EVEN(SerialPort.PARITY_EVEN),
	    ODD(SerialPort.PARITY_ODD),
	    MARK(SerialPort.PARITY_MARK),
	    SPACE(SerialPort.PARITY_SPACE);
		int par;
		Parity(int par) {
			this.par = par;
		}
	}
	
	public enum StopBits {
		_1(SerialPort.STOPBITS_1),
		_1_5(SerialPort.STOPBITS_1_5),
		_2(SerialPort.STOPBITS_2);
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
		return (String[])SerialPortList.getPortNames();
	}
	
	/**
	 * Sets the RTS pin of the serial port
	 * @param enabled true if the RTS pin should be set, false if it should be cleared
	 * @throws PhyLayerException
	 */
	public void setRTS(boolean enabled) throws PhyLayerException {
		try {
			serialPort.setRTS(enabled);
			Thread.sleep(250);
		} catch (SerialPortException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
			serialPort = new SerialPort(serialName);
			serialPort.openPort();
			setRTS(true);
		} catch (SerialPortException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.BUSY_CHANNEL);
		}
	}
	
	/**
	 * Closes the serial port
	 */
	public void close() {
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			// silence disconnection
		}
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
		try {
			serialPort.setParams(baudRate, dataBits.dataBits, stopBits.stopBits, parity.par);		
		} catch (SerialPortException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Sends data through the serial port
	 * @param data array of bytes to be sent
	 */
	@Override
	public void sendData(byte[] data) throws PhyLayerException {
        try {
			serialPort.writeBytes(data);
			for (PhyLayerListener listener : listeners) {
				listener.dataSent(data);
			}
		} catch (SerialPortException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.INTERNAL_ERROR);
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
		try {
			byte[] data;
			stream.reset();
			long timeLimit = System.nanoTime() + (timeoutMillis * 1000000L);
			while (timeLimit > System.nanoTime()) {
				if ((data = serialPort.readBytes()) != null) {
					stream.write(data);
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
		} catch (SerialPortException e) {
			throw new PhyLayerException(PhyLayerExceptionReason.BUSY_CHANNEL);
		}
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