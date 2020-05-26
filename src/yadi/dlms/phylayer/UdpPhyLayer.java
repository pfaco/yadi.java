package yadi.dlms.phylayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import yadi.dlms.phylayer.PhyLayerException.PhyLayerExceptionReason;

public class UdpPhyLayer implements PhyLayer {
	
	private final ArrayList<PhyLayerListener> listeners = new ArrayList<PhyLayerListener>();
	private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private String ip;
	private int port;
	private DatagramSocket socket;
		
	/**
	 * Opens the UDP socket
	 * @param ip String representing the IP to connect to
	 * @param port Number of the port to connect to
	 * @throws PhyLayerException
	 */
	public void open(String ip, int port) throws PhyLayerException {
		try {
			this.ip = ip;
			this.port = port;
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		socket.close();
	}

	/**
	 * Sends data through the TCP socket
	 * @param data array of bytes to be sent
	 */
	@Override
	public void sendData(byte[] data) throws PhyLayerException {
		try {
			DatagramPacket request = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
			socket.send(request);
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
			DatagramPacket response = new DatagramPacket(data, data.length);
			socket.receive(response);
			long timeLimit = System.nanoTime() + (timeoutMillis * 1000000L);
			while (timeLimit > System.nanoTime()) {
				if (response.getLength() > 0) {
					int len = response.getLength();
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
