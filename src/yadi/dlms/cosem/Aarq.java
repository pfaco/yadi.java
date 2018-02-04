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

import yadi.dlms.DlmsException;
import yadi.dlms.DlmsException.DlmsExceptionReason;
import yadi.dlms.cosem.CosemParameters.AuthenticationType;
import yadi.dlms.cosem.CosemParameters.ReferenceType;
import yadi.dlms.cosem.CosemParameters.SecurityType;

class Aarq {

	static byte[] request(CosemParameters params) throws IOException, DlmsException {
		final int BASE = Constants.Ber.CLASS_CONTEXT | Constants.Ber.CONSTRUCTED;
		final byte[] applicationContextName  = generateApplicationContextName(params.referenceType, params.securityType);
		params.connection.proposedContextName = applicationContextName;
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		
		//TODO
		if(params.authenticationType == AuthenticationType.PUBLIC) {
			data.write(new byte[] {(byte)0x80, 0x02, 0x07, (byte)0x80});
		}
		
		data.write(BASE | Constants.AarqApdu.APPLICATION_CONTEXT_NAME);
		data.write(applicationContextName.length+2);
		data.write(Constants.Ber.OBJECT_IDENTIFIER);
		data.write(applicationContextName.length);
		data.write(applicationContextName);
		if (params.securityType != SecurityType.NONE || params.authenticationType == AuthenticationType.HLS_GMAC) {
			data.write(BASE | Constants.Ber.OBJECT_IDENTIFIER);
			data.write(params.systemTitle.length+2);
			data.write(Constants.Ber.OCTET_STRING);
			data.write(params.systemTitle.length);
			data.write(params.systemTitle);
		}
		if (params.authenticationType != AuthenticationType.PUBLIC) {
			data.write(Constants.Ber.CLASS_CONTEXT | Constants.AarqApdu.SENDER_ACSE_REQUIREMENTS);
			data.write(2);
			data.write(Constants.Ber.BIT_STRING | Constants.Ber.OCTET_STRING);
			data.write(0x80);
			data.write(Constants.Ber.CLASS_CONTEXT | Constants.AarqApdu.MECHANISM_NAME);
			data.write(7);
			data.write(new byte[]{0x60, (byte)0x85, 0x74, 0x05, 0x08, 0x02, (byte)params.authenticationType.value});
			data.write(BASE | Constants.AarqApdu.CALLING_AUTHENTICATION_VALUE);
			if (params.authenticationType == AuthenticationType.LLS) {
				data.write(params.llsHlsSecret.length+2);
				data.write(Constants.Ber.CLASS_CONTEXT);
				data.write(params.llsHlsSecret.length);
				data.write(params.llsHlsSecret);
			} else if (params.authenticationType != AuthenticationType.PUBLIC) {
				params.connection.challengeClientToServer = Security.generateChallanger(params);
				data.write(params.connection.challengeClientToServer.length+2);
				data.write(Constants.Ber.CLASS_CONTEXT);
				data.write(params.connection.challengeClientToServer.length);
				data.write(params.connection.challengeClientToServer);
			}
		}
		data.write(Constants.Ber.CONTEXT_CONSTRUCTED | Constants.AarqApdu.USER_INFORMATION);
		byte[] initiateRequest = generateInitiateRequest(params);
		data.write(initiateRequest.length+2);
		data.write(Constants.Ber.OCTET_STRING);
		data.write(initiateRequest.length);
		data.write(initiateRequest);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(Constants.Ber.CLASS_APPLICATION | Constants.Ber.CONSTRUCTED);
		stream.write(data.size());
		stream.write(data.toByteArray());
		return stream.toByteArray();
	}

	private static byte[] generateApplicationContextName(ReferenceType referenceType, SecurityType securityType) throws DlmsException {
		if (referenceType == ReferenceType.LOGICAL_NAME) {
			if (securityType == SecurityType.NONE ) {
				return Constants.ApplicationContextName.LOGICAL_NAME_NO_CIPHERING;
			} else {
				return Constants.ApplicationContextName.LOGICAL_NAME_WITH_CIPHERING;
			}
		}
		if (referenceType == ReferenceType.SHORT_NAME) {
			if (securityType == SecurityType.NONE ) {
				return Constants.ApplicationContextName.SHORT_NAME_NO_CIPHERING;
			} else {
				return Constants.ApplicationContextName.SHORT_NAME_WITH_CIPHERING;
			}
		}
		throw new DlmsException(DlmsExceptionReason.INVALID_SETTING);
	}
	
	private static byte[] generateInitiateRequest(CosemParameters params) throws IOException, DlmsException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(Constants.xDlmsApdu.NoCiphering.INITIATE_REQUEST);
		stream.write(0); //Dedicated key
		stream.write(0); //Response-allowed
		stream.write(0); //Proposed quality of service
		stream.write(Constants.DLMS_VERSION); //Dlms version
		stream.write(Constants.ConformanceBlock.TAG); //Conformance block tag
		byte[] conformance = generateConformanceBlock(params);
		stream.write(conformance.length);
		stream.write(conformance); //Conformance block
		stream.write(ByteBuffer.allocate(2).putShort(params.maxPduSize).array()); //Max pdu size
		ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
		if (params.securityType != SecurityType.NONE) {
			stream2.write(Constants.xDlmsApdu.GlobalCiphering.INITIATE_REQUEST);
			byte[] data = Security.authenticatedEncryption(params, stream.toByteArray());
			stream2.write(data.length);
			stream2.write(data);
		} else {
			stream2.write(stream.toByteArray());
		}
		return stream2.toByteArray();
	}
	
	private static byte[] generateConformanceBlock(CosemParameters params) {
		int conformanceBlock = 0;
		if (params.referenceType == ReferenceType.SHORT_NAME) {
			conformanceBlock |= Constants.ConformanceBlock.READ;
			conformanceBlock |= Constants.ConformanceBlock.WRITE;
		}
		if (params.referenceType == ReferenceType.LOGICAL_NAME) {
			conformanceBlock |= Constants.ConformanceBlock.GET;
			conformanceBlock |= Constants.ConformanceBlock.SET;
		}
		conformanceBlock |= Constants.ConformanceBlock.ACTION;
		conformanceBlock |= Constants.ConformanceBlock.BLOCK_TRANSFER_WITH_ACTION;
		conformanceBlock |= Constants.ConformanceBlock.BLOCK_TRANSFER_WITH_GET_OR_READ;
		conformanceBlock |= Constants.ConformanceBlock.BLOCK_TRANSFER_WITH_SET_OR_WRITE;
		conformanceBlock |= Constants.ConformanceBlock.SELECTIVE_ACCESS;
		return ByteBuffer.allocate(4).putInt(conformanceBlock).array();
	}
	
}
