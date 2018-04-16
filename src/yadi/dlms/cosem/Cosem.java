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
package yadi.dlms.cosem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Arrays;

import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.DlmsException.DlmsExceptionReason;
import yadi.dlms.cosem.CosemParameters.AuthenticationType;
import yadi.dlms.cosem.CosemParameters.SecurityType;

public class Cosem {
	
	private enum ConnectionState {
		DISCONNECTED, CONNECTED, AUTHENTICATED
	}
	
	private final CosemParameters params;
	private final CosemConnection connection;
	private ConnectionState state = ConnectionState.DISCONNECTED;

	/**
	 * Creates a Cosem object
	 */
	public Cosem() {
		this(new CosemParameters());
	}
	
	/**
	 * Creates a Cosem object
	 * @param params CosemParameters for this Cosem object
	 */
	public Cosem(CosemParameters params) {
		this.params = params;
		this.connection = new CosemConnection();
	}
	
	public CosemParameters getParameters() {
		return params;
	}
	
	/**
	 * Resets the internal connection state, must be called before each connection attempt
	 */
	public void reset() {
		state = ConnectionState.DISCONNECTED;
	}

	/**
	 * Generates the next APDU for establishment of a association with the metering devices
	 * The number and content of the APDU will vary according to the HdlcParams configuration
	 * @return the array of bytes that represents the next APDU to be sent for connection establishment
	 * @throws DlmsException
	 */
	public byte[] connectionRequest() throws DlmsException {
		try {
			switch (state) {
			case DISCONNECTED:
				connection.reset();
				return Aarq.request(params, connection);
			case CONNECTED:
				LnDescriptor att = new LnDescriptor(CosemClasses.ASSOCIATION_LN.id, new Obis("0.0.40.0.0.255"), 1);
				byte[] data = Security.processChallanger(params, connection);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				stream.write(Constants.DataType.OCTET_STRING);
				stream.write(data.length);
				stream.write(data);
				att.setRequestData(stream.toByteArray());
				return requestAction(att);
			case AUTHENTICATED:
				throw new IllegalStateException();
			default:
				throw new IllegalStateException();
			}
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Parses the response of the last connection APDU sent
	 * @param data array of bytes with the APDU received from the metering device
	 * @return true if the connection is established and false if more steps are necessary
	 * @throws DlmsException
	 */
	public boolean parseConnectionResponse(byte[] data) throws DlmsException {
		switch (state) {
		case DISCONNECTED:
			Aare.parseResponse(params, connection, data);
			state = ConnectionState.CONNECTED;
			return params.authenticationType == AuthenticationType.PUBLIC || params.authenticationType == AuthenticationType.LLS;
		case CONNECTED:
			LnDescriptor att = new LnDescriptor(CosemClasses.ASSOCIATION_LN.id, new Obis("0.0.40.0.0.255"), 1);
			parseActionResponse(att, data);
			byte[] receivedData = att.getResponseData();
			if (receivedData == null || receivedData.length < 3 || receivedData[0] != Constants.DataType.OCTET_STRING) {
				throw new DlmsException(DlmsExceptionReason.CONNECTION_REJECTED);
			}
			if (receivedData[1] != receivedData.length-2) {
				throw new DlmsException(DlmsExceptionReason.CONNECTION_REJECTED);
			}
			if (!Security.verifyChallenger(params, connection, Arrays.copyOfRange(receivedData, 2, receivedData.length))) {
				throw new DlmsException(DlmsExceptionReason.FAIL_TO_AUTHENTICATE_SERVER);
			}
			state = ConnectionState.AUTHENTICATED;
			return true;
		case AUTHENTICATED:
			throw new IllegalStateException();
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Generates the APDU for a GET request
	 * @param att LnDescriptor describing the object to be accessed
	 * @return byte array representation of the APDU
	 * @throws DlmsException
	 */
	public byte[] requestGet(LnDescriptor att) throws DlmsException {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(Constants.xDlmsApdu.NoCiphering.GET_REQUEST);
			stream.write(connection.datablock.blockNum == 0 ? 1 : 2);
			stream.write(params.priority | params.serviceClass | Constants.INVOKE_ID);
			if (connection.datablock.blockNum != 0) {
				stream.write(ByteBuffer.allocate(4).putInt(connection.datablock.blockNum).array());
			} else {
				stream.write(att.getClassId());
				stream.write(att.getObis());
				stream.write(att.getIndex());
				stream.write(att.getRequestData().length == 0 ? 0 : 1);
				stream.write(att.getRequestData());
			}
			return packFrame(Constants.xDlmsApdu.GlobalCiphering.GET_REQUEST, stream.toByteArray());
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Generates the APDU for a SET request
	 * @param att LnDescriptor describing the object to be accessed
	 * @return byte array representation of the APDU
	 * @throws DlmsException
	 */
	public byte[] requestSet(LnDescriptor att) throws DlmsException {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(Constants.xDlmsApdu.NoCiphering.SET_REQUEST);
			byte[] data = att.getRequestData();
			if (data.length > (connection.maxPduSize - 20)) {
				if (connection.datablock.nextBlockNum == 1) {
					connection.datablock.setData(data, connection.maxPduSize - 20);
					//Set-Request-With-First-Datablock
					stream.write(2);
					stream.write(params.priority | params.serviceClass | Constants.INVOKE_ID);
					stream.write(att.getClassId());
					stream.write(att.getObis());
					stream.write(att.getIndex());
					stream.write(0);
					stream.write(0);
					stream.write(connection.datablock.getNextBlockNum());
					byte[] blockdata = connection.datablock.getNextBlock();
					if (blockdata.length > 128) {
						stream.write(0x81);
					}
					stream.write(blockdata.length);
					stream.write(blockdata);
				} else {
					//Set-Request-With-Datablock
					stream.write(3);
					stream.write(params.priority | params.serviceClass | Constants.INVOKE_ID);
					if (connection.datablock.thisIsLast()) {
						stream.write(1);
					} else {
						stream.write(0);
					}
					stream.write(connection.datablock.getNextBlockNum());
					byte[] blockdata = connection.datablock.getNextBlock();
					if (blockdata.length > 128) {
						stream.write(0x81);
					}
					stream.write(blockdata.length);
					stream.write(blockdata);
				}
			} else {
				//Set-Request-Normal
				stream.write(1);
				stream.write(params.priority | params.serviceClass | Constants.INVOKE_ID);
				stream.write(att.getClassId());
				stream.write(att.getObis());
				stream.write(att.getIndex());
				stream.write(0);
				stream.write(att.getRequestData());
			}
			
			return packFrame(Constants.xDlmsApdu.GlobalCiphering.SET_REQUEST, stream.toByteArray());
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Generates the APDU for a ACTION request
	 * @param att LnDescriptor describing the object to be accessed
	 * @return byte array representation of the APDU
	 * @throws DlmsException
	 */
	public byte[] requestAction(LnDescriptor att) throws DlmsException {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(Constants.xDlmsApdu.NoCiphering.ACTION_REQUEST);
			stream.write(1);
			stream.write(params.priority | params.serviceClass | Constants.INVOKE_ID);
			stream.write(att.getClassId());
			stream.write(att.getObis());
			stream.write(att.getIndex());
			stream.write(att.getRequestData().length == 0 ? 0 : 1);
			stream.write(att.getRequestData());
			return packFrame(Constants.xDlmsApdu.GlobalCiphering.ACTION_REQUEST, stream.toByteArray());
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Parses the APDU of a GET response
	 * @param att LnDescriptor describing the object accessed
	 * @return true if the parse if finished, false if more apdu's are necessary (data block transfer)
	 * @throws DlmsException
	 */
	public boolean parseGetResponse(LnDescriptor att, byte[] data) throws DlmsException {
		try {
			data = unpackFrame(Constants.xDlmsApdu.NoCiphering.GET_RESPONSE,
					           Constants.xDlmsApdu.GlobalCiphering.GET_RESPONSE, data);
	
			if (data.length < 4) {
				throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_GET_RESPONSE);
			}
			
			if (data[0] == Constants.GetResponse.NORMAL) {
				verifyDataAccessResult(data[2]);
				connection.datablock.lastBlock = true;
				data = Arrays.copyOfRange(data, 3, data.length);
			} else if (data[0] == Constants.GetResponse.DATA_BLOCK) {
				if (data.length < 10 || data[7] != 0) { //TODO only supports raw-data for now
					throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_GET_RESPONSE);
				}
				connection.datablock.lastBlock = data[2] != 0;
				connection.datablock.blockNum = ByteBuffer.allocate(4).put(data,3,4).getInt(0);
				data = getPayload(data, 8); 
			} else {
				throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_GET_RESPONSE);
			}
			
			connection.datablock.data.write(data);
			
			if (connection.datablock.lastBlock) {
				att.setResponseData(connection.datablock.data.toByteArray());
				connection.datablock.reset();
				return true;
			}
			
			return false;
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}

	/**
	 * Parses the APDU of a SET response
	 * @param att LnDescriptor describing the object accessed
	 * @return true if the parse if finished, false if more apdu's are necessary (data block transfer)
	 * @throws DlmsException
	 */
	public boolean parseSetResponse(LnDescriptor att, byte[] data) throws DlmsException {
		data = unpackFrame(Constants.xDlmsApdu.NoCiphering.SET_RESPONSE,
		                   Constants.xDlmsApdu.GlobalCiphering.SET_RESPONSE, data);

		if (data.length < 3) {
			throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_SET_RESPONSE);
		}
		
		if (data[0] == Constants.SetResponse.NORMAL) {
			verifyDataAccessResult(data[2]);
			return true;
		} else if (data[0] == Constants.SetResponse.DATA_BLOCK) {
			if (data.length < 6) {
				throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_SET_RESPONSE);
			}
			connection.datablock.ackBlock(ByteBuffer.allocate(4).put(data,2,4).getInt(0));
			if (connection.datablock.thisIsLast()) {
				connection.datablock.reset();
				return true;
			}
			return false;
		} else if (data[0] == Constants.SetResponse.LAST_DATA_BLOCK) {
			if (data.length < 7) {
				throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_SET_RESPONSE);
			}
			if (ByteBuffer.allocate(4).put(data,3,4).getInt(0) != connection.datablock.nextBlockNum) {
				throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_SET_RESPONSE);
			}
			connection.datablock.ackBlock(ByteBuffer.allocate(4).put(data,3,4).getInt(0));
			verifyDataAccessResult(data[2]);
			return true;
		}
		throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_SET_RESPONSE);
	}

	/**
	 * Parses the APDU of a ACTION response
	 * @param att LnDescriptor describing the object accessed
	 * @return true if the parse if finished, false if more apdu's are necessary (data block transfer)
	 * @throws DlmsException
	 */
	public boolean parseActionResponse(LnDescriptor att, byte[] data) throws DlmsException {
		data = unpackFrame(Constants.xDlmsApdu.NoCiphering.ACTION_RESPONSE,
                           Constants.xDlmsApdu.GlobalCiphering.ACTION_RESPONSE, data);
		
		if (data.length > 6) {
			att.setResponseData(Arrays.copyOfRange(data, 5, data.length));
		}
		
		return true;
	}
	
	private void verifyDataAccessResult(byte result) throws DlmsException {
		if (result == 0) {
			return;
		}
		for (Constants.AccessResult a : Constants.AccessResult.values()) {
			if (a.val == result) {
				throw new DlmsException(DlmsExceptionReason.valueOf(a.toString()));
			}
		}
		throw new DlmsException(DlmsExceptionReason.UNKNOWN_ACCESS_RESULT_FAILURE);
	}
	
	private byte[] packFrame(int cmdGlobalCipher, byte[] payload) throws DlmsException {
		try {
			if (params.securityType != SecurityType.NONE) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				byte[] data = Security.authenticatedEncryption(params, payload);
				stream.reset();
				stream.write(cmdGlobalCipher);
				stream.write(getSizeBytes(data.length));
				stream.write(data);
				return stream.toByteArray();
			}
			return payload;
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
	private byte[] unpackFrame(int cmdNoCipher, int cmdGlobalCipher, byte[] data) throws DlmsException {
		if ( (data[0] & 0xFF) == Constants.xDlmsApdu.Exception.ExceptionResponse) {
			DlmsExceptionReason[] reasons = new DlmsExceptionReason[2];
			reasons[0] = DlmsExceptionReason.STATE_ERROR_UNKNOWN;
			reasons[1] = DlmsExceptionReason.SERVICE_ERROR_UNKNOWN;
			
			if (data[1] == 1) {
				reasons[0] = DlmsExceptionReason.STATE_ERROR_SERVICE_NOT_ALLOWED;
			}
			else if (data[1] == 2) {
				reasons[0] = DlmsExceptionReason.STATE_ERROR_SERVICE_UNKNOWN;
			}
			
			if (data[2] == 1) {
				reasons[1] = DlmsExceptionReason.SERVICE_ERROR_OPERATION_NOT_POSSIBLE;
			}
			else if (data[2] == 2) {
				reasons[1] = DlmsExceptionReason.SERVICE_ERROR_NOT_SUPPORTED;
			}
			else if (data[2] == 3) {
				reasons[1] = DlmsExceptionReason.SERVICE_ERROR_OTHER_REASON;
			}
			
			throw new DlmsException(reasons);
		}
		if (params.securityType != SecurityType.NONE) {
			if ( (data[0] & 0xff) != cmdGlobalCipher) {
				throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_COMMAND_ID);
			}
			data = getPayload(data, 1);
			data = Security.reverseAuthenticatedEncryption(params, connection, data);
		}
		if ( (data[0] & 0xFF) != cmdNoCipher) {
			throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_COMMAND_ID);
		}
		return Arrays.copyOfRange(data, 1, data.length);
	}
	
	private static byte[] getSizeBytes(int size) {
		if (size < 0x80) {
			return new byte[]{(byte)size};
		}
		if (size <= 65535) {
			return new byte[]{(byte)0x81, (byte)(size >>> 8), (byte)size};
		}
		throw new InvalidParameterException();
	}
	
	private static byte[] getPayload(byte[] data, int offset) throws DlmsException {
		int nBytes = (data[offset] & 0xff) - 0x80;
		int size = data[offset] & 0xff;
		int skip = 0;
		if ( nBytes > 0) {
			size = 0;
			skip = 1;
		}
		
		if (nBytes > (data.length - offset)) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < nBytes; ++i) {
			size <<= 8;
			size |= (data[offset + i + 1] & 0xff);
		}
		
		if (nBytes < 0) {
			nBytes = 1;
		}
		
		if (size != data.length-nBytes-offset-skip) {
			throw new DlmsException(DlmsExceptionReason.RECEIVED_INVALID_COMMAND_ID);
		}
		
		return Arrays.copyOfRange(data, offset+nBytes+skip, data.length);
	}
	
}
