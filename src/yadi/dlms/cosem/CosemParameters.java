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

public class CosemParameters {
	
	class DataBlock {
		boolean lastBlock;
		int blockNum;
		final ByteArrayOutputStream data;
		
		DataBlock() {
			data = new ByteArrayOutputStream();
		}
		
		void reset() {
			lastBlock = false;
			blockNum = 0;
			data.reset();
		}
	}
	
	/**
	 * COSEM connection parameters
	 */
	class Connection {
		DataBlock datablock = new DataBlock();
		byte[] challengeServerToClient = new byte[0];
		byte[] challengeClientToServer = new byte[0];
		byte[] proposedContextName = new byte[0];
		byte[] conformanceBlock = new byte[0];
		byte[] serverSysTitle = new byte[0];
		int maxPduSize;
		int serverInvocationCounter;
		
		void reset() {
			challengeServerToClient = new byte[0];
			challengeClientToServer = new byte[0];
			proposedContextName = new byte[0];
			conformanceBlock = new byte[0];
			serverSysTitle = new byte[0];
			maxPduSize = 0;
			serverInvocationCounter = 0;
			datablock.reset();
		}
	}
	
	/**
	 * Possible types of attributes references
	 */
	public enum ReferenceType {
		LOGICAL_NAME, SHORT_NAME;
	}

	/**
	 * Possible types of security
	 */
	public enum SecurityType {
		NONE, AUTHENTICATION, ENCRYPTION, AUTHENTICATION_ENCRYPTION;
	}
	
	/**
	 * Authentication types
	 */
	public enum AuthenticationType {
		PUBLIC(0), LLS(1), HLS(2), HLS_MD5(3), HLS_SHA1(4), HLS_GMAC(5);
		int value;
		AuthenticationType(int value) {
			this.value = value;
		}
	}
	
	/**
	 * Priorities
	 */
	public enum PriorityType {
		NORMAL, HIGH;
	}
	
	/**
	 * Service class type
	 */
	public enum ServiceClassType {
		UNCONFIRMED, CONFIRMED;
	}
	
	///Connection connection = new Connection();
	AuthenticationType authenticationType = AuthenticationType.PUBLIC;
	SecurityType securityType = SecurityType.NONE;
	ReferenceType referenceType = ReferenceType.LOGICAL_NAME;
	
	int invocationCounter = 0;
	int challengerSize = 8;
	int priority = Constants.PRIORITY_HIGH;
	int serviceClass = Constants.SERVICE_CLASS_CONFIRMED;
	short maxPduSize = (short)0xFFFF;
	
	byte[] llsHlsSecret = new byte[8];
	byte[] systemTitle = new byte[] {0x48, 0x45, 0x43, 0x00, 0x05, 0x00, 0x00, 0x01};
	byte[] ak = new byte[16];
	byte[] ek = new byte[16];
	
	/**
	 * Configures the number of bytes for the generated challengers
	 * @param size - number of bytes for generated challengers
	 */
	public void setChallengerSize(int size) {
		if (size < 8 || size > 64) {
			throw new IllegalArgumentException("Challenger must be between 8 and 64 bytes long");
		}
		this.challengerSize = size;
	}
	
	/**
	 * Sets the invocation counter value, it starts at zero by default.
	 * @param counter - invocation counter value
	 */
	public void setInvocationCounter(int counter) {
		this.invocationCounter = counter;
	}
	
	/**
	 * Sets the secret used for LLS and HLS authentication
	 * @param llsPassword - LLS password value
	 */
	public void setSecret(byte[] llsPassword) {
		this.llsHlsSecret = llsPassword;
	}
	
	/**
	 * Set the client system title
	 * @param systemTitle - system title value
	 */
	public void setSystemTitle(byte[] systemTitle) {
		if (systemTitle.length != 8) {
			throw new IllegalArgumentException("System Title must be 8 bytes long");
		}
		this.systemTitle = systemTitle;
	}
	
	/**
	 * Sets the authentication key
	 * @param ak - authentication key value
	 */
	public void setAk(byte[] ak) {
		if (ak.length != 16) {
			throw new IllegalArgumentException("AK must be 16 bytes long");
		}
		this.ak = ak;
	}
	
	/**
	 * Sets the encryption key
	 * @param ek - encryption key value
	 */
	public void setEk(byte[] ek) {
		if (ek.length != 16) {
			throw new IllegalArgumentException("EK must be 16 bytes long");
		}
		this.ek = ek;
	}
	
	/**
	 * Sets the security type to be used
	 * @param securityType
	 */
	public void setSecurityType(SecurityType securityType) {
		this.securityType = securityType;
	}
	
	/**
	 * Sets the authentication value to be used
	 * @param authenticationType
	 */
	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}
	
	/**
	 * Sets the attribute reference type to be used
	 * @param type
	 */
	public void setReferenceType(ReferenceType type) {
		this.referenceType = type;
	}
	
	/**
	 * Sets the maximum PDU size
	 * @param pduSize - pdu size in bytes
	 */
	public void setMaxPduSize(int pduSize) {
		this.maxPduSize = (short)pduSize;
	}
	
	/**
	 * Sets the service priority
	 * @param priority
	 */
	public void setPriority(PriorityType priority) {
		switch (priority) {
		case HIGH:
			this.priority = Constants.PRIORITY_HIGH;
			break;
		case NORMAL:
			this.priority = Constants.PRIORITY_NORMAL;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Sets the service class type
	 * @param serviceClass
	 */
	public void setServiceClass(ServiceClassType serviceClass) {
		switch (serviceClass) {
		case CONFIRMED:
			this.serviceClass = Constants.SERVICE_CLASS_CONFIRMED;
			break;
		case UNCONFIRMED:
			this.serviceClass = Constants.SERVICE_CLASS_UNCONFIRMED;
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
}
