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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import yadi.java.client.linklayer.LinkLayerException.LinkLayerExceptionReason;
import yadi.java.client.phylayer.PhyLayer;
import yadi.java.client.phylayer.PhyLayerException;

public class HdlcLinkLayer implements LinkLayer {
	
	private static final int[] tableFcs = {
	    0x0000, 0x1189, 0x2312, 0x329B, 0x4624, 0x57AD, 0x6536, 0x74BF, 
		0x8C48, 0x9DC1, 0xAF5A, 0xBED3, 0xCA6C, 0xDBE5, 0xE97E, 0xF8F7, 
		0x1081, 0x0108, 0x3393, 0x221A, 0x56A5, 0x472C, 0x75B7, 0x643E, 
		0x9CC9, 0x8D40, 0xBFDB, 0xAE52, 0xDAED, 0xCB64, 0xF9FF, 0xE876, 
		0x2102, 0x308B, 0x0210, 0x1399, 0x6726, 0x76AF, 0x4434, 0x55BD, 
		0xAD4A, 0xBCC3, 0x8E58, 0x9FD1, 0xEB6E, 0xFAE7, 0xC87C, 0xD9F5, 
		0x3183, 0x200A, 0x1291, 0x0318, 0x77A7, 0x662E, 0x54B5, 0x453C, 
		0xBDCB, 0xAC42, 0x9ED9, 0x8F50, 0xFBEF, 0xEA66, 0xD8FD, 0xC974, 
		0x4204, 0x538D, 0x6116, 0x709F, 0x0420, 0x15A9, 0x2732, 0x36BB, 
		0xCE4C, 0xDFC5, 0xED5E, 0xFCD7, 0x8868, 0x99E1, 0xAB7A, 0xBAF3, 
		0x5285, 0x430C, 0x7197, 0x601E, 0x14A1, 0x0528, 0x37B3, 0x263A, 
		0xDECD, 0xCF44, 0xFDDF, 0xEC56, 0x98E9, 0x8960, 0xBBFB, 0xAA72, 
		0x6306, 0x728F, 0x4014, 0x519D, 0x2522, 0x34AB, 0x0630, 0x17B9, 
		0xEF4E, 0xFEC7, 0xCC5C, 0xDDD5, 0xA96A, 0xB8E3, 0x8A78, 0x9BF1, 
		0x7387, 0x620E, 0x5095, 0x411C, 0x35A3, 0x242A, 0x16B1, 0x0738, 
		0xFFCF, 0xEE46, 0xDCDD, 0xCD54, 0xB9EB, 0xA862, 0x9AF9, 0x8B70, 
		0x8408, 0x9581, 0xA71A, 0xB693, 0xC22C, 0xD3A5, 0xE13E, 0xF0B7, 
		0x0840, 0x19C9, 0x2B52, 0x3ADB, 0x4E64, 0x5FED, 0x6D76, 0x7CFF, 
		0x9489, 0x8500, 0xB79B, 0xA612, 0xD2AD, 0xC324, 0xF1BF, 0xE036, 
		0x18C1, 0x0948, 0x3BD3, 0x2A5A, 0x5EE5, 0x4F6C, 0x7DF7, 0x6C7E, 
		0xA50A, 0xB483, 0x8618, 0x9791, 0xE32E, 0xF2A7, 0xC03C, 0xD1B5, 
		0x2942, 0x38CB, 0x0A50, 0x1BD9, 0x6F66, 0x7EEF, 0x4C74, 0x5DFD, 
		0xB58B, 0xA402, 0x9699, 0x8710, 0xF3AF, 0xE226, 0xD0BD, 0xC134, 
		0x39C3, 0x284A, 0x1AD1, 0x0B58, 0x7FE7, 0x6E6E, 0x5CF5, 0x4D7C, 
		0xC60C, 0xD785, 0xE51E, 0xF497, 0x8028, 0x91A1, 0xA33A, 0xB2B3, 
		0x4A44, 0x5BCD, 0x6956, 0x78DF, 0x0C60, 0x1DE9, 0x2F72, 0x3EFB, 
		0xD68D, 0xC704, 0xF59F, 0xE416, 0x90A9, 0x8120, 0xB3BB, 0xA232, 
		0x5AC5, 0x4B4C, 0x79D7, 0x685E, 0x1CE1, 0x0D68, 0x3FF3, 0x2E7A, 
		0xE70E, 0xF687, 0xC41C, 0xD595, 0xA12A, 0xB0A3, 0x8238, 0x93B1, 
		0x6B46, 0x7ACF, 0x4854, 0x59DD, 0x2D62, 0x3CEB, 0x0E70, 0x1FF9, 
		0xF78F, 0xE606, 0xD49D, 0xC514, 0xB1AB, 0xA022, 0x92B9, 0x8330, 
		0x7BC7, 0x6A4E, 0x58D5, 0x495C, 0x3DE3, 0x2C6A, 0x1EF1, 0x0F78
	};
	
	private static final int HDLC_FLAG    = 0x7E;
	private static final int HDLC_FORMAT  = 0xA0;
	private static final int I_CONTROL    = 0x00;
	private static final int SNRM_CONTROL = 0x83;
	private static final int DISC_CONTROL = 0x43;
	private static final int UA_CONTROL   = 0x63;
	private static final int FRMR_CONTROL = 0x87;
	
	private final HdlcParameters params;
	private final ByteArrayOutputStream stream;
	private final HdlcConnection connection;
	
	/**
	 * Creates a HdlcLinkLayer object
	 * @param params the HdlcParameters for this object
	 */
	public HdlcLinkLayer(HdlcParameters params) {
		this.params = params;
		this.stream = new ByteArrayOutputStream();
		this.connection = new HdlcConnection();
	}

	/**
	 * Connects at the HDLC level
	 * @param phy the PhyLayer to transmit and receive bytes
	 */
	@Override
	public void connect(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		try {
			connection.reset();
			phy.sendData(snrmRequest());
			parseSnrmReply(readData(phy));
		} catch (IOException e) {
			throw new LinkLayerException(LinkLayerExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Disconnects at the HDLC level
	 * @param phy the PhyLayer to transmit and receive bytes
	 */
	@Override
	public void disconnect(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		initFrame(DISC_CONTROL,0);
		phy.sendData(getFrame());
		readData(phy);
		if (connection.receivedControl != UA_CONTROL) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_FRAME_FORMAT);
		}
	}

	/**
	 * Encapsulates data inside a HDLC frame and sends it
	 * @param phy the PhyLayer to transmit and receive bytes
	 * @param data the array of bytes to be encapsulated and transmitted
	 */
	@Override
	public void send(PhyLayer phy, byte[] data) throws PhyLayerException, LinkLayerException {
		int control = I_CONTROL;
		connection.insReceivedSss();
		control |= this.connection.receivedSss << 5;
		control |= this.connection.sss << 1;
		connection.incSss();
		initFrame(control, data.length+3);
		updateFrame(new byte[]{(byte)0xE6, (byte)0xE6, 0x00});
		updateFrame(data);
		phy.sendData(getFrame());
	} 
	
	/**
	 * Retrieves the data encapsulated inside a HDLC frame
	 * @param phy the PhyLayer to receive bytes
	 * @return array of bytes with the application data unit contents inside the HDLC frame
	 */
	@Override
	public byte[] read(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		byte[] data = readData(phy);
		if (data.length < 3) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_LLC_BYTES);
		}
		if (data[0] != (byte)0xE6 || data[1] != (byte)0xE7 || data[2] != 0x00) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_LLC_BYTES);
		}
		return Arrays.copyOfRange(data, 3, data.length);
	}
	
	private byte[] readData(PhyLayer phy) throws PhyLayerException, LinkLayerException {
		byte[] data = phy.readData(params.timeoutMillis, (a) -> isFrameComplete(a));
		int offset = 0;
		while (data[offset] != HDLC_FLAG) {
			offset++;
		}
		if (offset > 0) {
			data = Arrays.copyOfRange(data, offset, data.length);
		}
		verifyReceivedData(data);
		return connection.receivedData;
	}
	
	private void verifyReceivedData(byte[] data) throws LinkLayerException {
		if ((data[1] & 0xF0) != HDLC_FORMAT) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_FRAME_FORMAT);
		}
		int frameSize = ((data[1] & 0x0F) << 8) | (data[2] & 0xFF);
		if (frameSize != data.length-2) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_FRAME_FORMAT);
		}
		if (params.clientAddress != data[3]) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_ADDRESS);
		}
		int offset = 4;
		while (offset < frameSize && (data[offset++] & 0x01) != 0x01);
		//if (!Arrays.equals(params.serverAddress, Arrays.copyOfRange(data, 4, offset))) {
		//	throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_ADDRESS);
		//}
		connection.receivedControl = data[offset++] & 0xFF;
		if (data.length - offset > 3) {
			connection.receivedData = Arrays.copyOfRange(data, offset+2, data.length-3);
		} else {
			connection.receivedData = new byte[0];
		}
		
		short fcs = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).put(data, offset, 2).getShort(0);
		if (fcs != HdlcLinkLayer.calcFcs(data, 1, offset-1)) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_CHECK_SEQUENCE);
		}
		
		fcs = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).put(data, data.length-3, 2).getShort(0);
		if (fcs != HdlcLinkLayer.calcFcs(data, 1, data.length-4)) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_CHECK_SEQUENCE);
		}
		connection.isFinalPoll = (connection.receivedControl & 0x10) == 0x10;
		connection.receivedControl &= 0xEF; //remove p/f bit from control
		connection.receivedRrr = 0;
		connection.receivedSss = 0;
		if ((connection.receivedControl & 0x01) == 0x00) {
			connection.receivedRrr = (connection.receivedControl >>> 5) & 0x07;
			connection.receivedSss = (connection.receivedControl >>> 1) & 0x07;
			connection.receivedControl &= 0x01; //remove sss and rrr bits from control
			connection.lastFrameHadSss = true;
		}
		else if ((connection.receivedControl & 0x02) == 0x00) {
			connection.receivedRrr = (connection.receivedControl >>> 5) & 0x07;
			connection.receivedControl &= 0x0F; //remove rrr bits from control
		}
		if (connection.receivedControl == FRMR_CONTROL) {
			throw new LinkLayerException(LinkLayerExceptionReason.SERVER_REPORTS_FRAME_REJECTED);
		}
	}

	private byte[] snrmRequest() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		if (params.maxInformationFieldLengthTx < 128) {
			data.write(0x05);
			data.write(1);
			data.write(params.maxInformationFieldLengthTx);
		}
		else if (params.maxInformationFieldLengthTx > 128) {
			data.write(0x05);
			data.write(2);
			data.write(ByteBuffer.allocate(2).putShort((short)params.maxInformationFieldLengthTx).array());
		}
		if (params.maxInformationFieldLengthRx < 128) {
			data.write(0x06);
			data.write(1);
			data.write(params.maxInformationFieldLengthRx);
		}
		else if (params.maxInformationFieldLengthRx > 128) {
			data.write(0x06);
			data.write(2);
			data.write(ByteBuffer.allocate(2).putShort((short)params.maxInformationFieldLengthRx).array());
		}
		if (params.windowSizeTx != 1) {
			data.write(0x07);
			data.write(4);
			data.write(ByteBuffer.allocate(4).putInt(params.windowSizeTx).array());
		}
		if (params.windowSizeRx != 1) {
			data.write(0x08);
			data.write(4);
			data.write(ByteBuffer.allocate(4).putInt(params.windowSizeRx).array());
		}
		if (data.size() > 0) {
			out.write(0x80);
			out.write(0x81);
			out.write(data.size());
			out.write(data.toByteArray());
		}
		byte[] dataBytes = out.toByteArray();
		initFrame(SNRM_CONTROL, dataBytes.length);
		updateFrame(dataBytes);
		return getFrame();
	}
	
	private void parseSnrmReply(byte[] data) throws LinkLayerException {
		if (data.length == 0) {
			return;
		}
		if (data.length < 5 || data[0] != (byte)0x81 || data[1] != (byte)0x80) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_FRAME_FORMAT);
		}
		if (connection.receivedControl != UA_CONTROL) {
			throw new LinkLayerException(LinkLayerExceptionReason.RECEIVED_INVALID_FRAME_FORMAT);
		}
		int offset = 3;
		while (offset < data.length) {
			int id = data[offset++] & 0xFF;
			int len = data[offset++] & 0xFF;
			int value = 0;
			while (offset < data.length && len-- != 0) {
				value <<= 8;
				value |= data[offset++] & 0xFF;
			}
			switch (id) {
			case 5:
				connection.maxInformationFieldTx = Math.min(params.maxInformationFieldLengthTx, value);
				break;
			case 6:
				connection.maxInformationFieldRx = Math.min(params.maxInformationFieldLengthRx, value);
				break;
			case 7:
				connection.windowSizeTx = Math.min(params.windowSizeTx, value);
				break;
			case 8:
				connection.windowSizeRx = Math.min(params.windowSizeRx, value);
				break;
			}
		}
	}
	
	private void initFrame(int control, int size) {
		if (size < 128 || size < connection.maxInformationFieldTx) {
			control |= 0x10; //final bit
		}
		stream.reset();
		stream.write(HDLC_FLAG);
		stream.write(HDLC_FORMAT);
		stream.write(0x00); //size
		for (byte b : params.serverAddress) {
			stream.write(b);
		}
		stream.write(params.clientAddress);
		stream.write(control);
		if (size > 0) {
			stream.write(0x00); //hcs
			stream.write(0x00); //hcs
		}
	}
	
	private void updateFrame(byte[] val) {
		for (byte b : val) {
			stream.write(b);
		}
	}
	
	private byte[] getFrame() {
		int addressOffset = params.serverAddress.length + 1;
		stream.write(0x00); //fcs
		stream.write(0x00); //fcs
		stream.write(HDLC_FLAG);
		byte[] data = stream.toByteArray();
		data[1] = (byte) (data[1] | ((data.length-2) >>> 8));
		data[2] = (byte) (data.length-2);
		short fcs = HdlcLinkLayer.calcFcs(data, 1, addressOffset + 3);
		data[4 + addressOffset] = (byte) fcs;
		data[5 + addressOffset] = (byte) (fcs >>> 8);
		if (data.length > (addressOffset + 7)) {
			fcs = HdlcLinkLayer.calcFcs(data, 1, data.length-4);
			data[data.length-3] = (byte) fcs;
			data[data.length-2] = (byte) (fcs >>> 8);
		}
		return data;
	}

	private boolean isFrameComplete(byte[] data) {
		int offset = 0;
		while (offset < data.length && data[offset] != HDLC_FLAG) {
			++offset;
		}
		if(data.length < (offset + 9) || data[offset] != HDLC_FLAG || data[data.length - 1] != HDLC_FLAG) { 
        	return false; 
        }
        if(data.length < (((data[offset + 1] & 0x07) << 8 ) + (data[offset + 2] & 0xFF) + 2)) {
        	return false;
        }
        return true;
	}
	
	private static short calcFcs(byte[] data, int offset, int len) {
		int fcs = 0xffff;
		for(int i = 0; i < len; ++i) {
			fcs = (fcs >>> 8) ^ tableFcs[(fcs ^ data[offset+i]) & 0xff];
		}
		return (short)~fcs;
	}
	
}
