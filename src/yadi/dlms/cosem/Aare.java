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

import java.nio.ByteBuffer;
import java.util.Arrays;

import yadi.dlms.DlmsException;
import yadi.dlms.DlmsException.DlmsExceptionReason;
import yadi.dlms.cosem.CosemParameters.ReferenceType;
import yadi.dlms.cosem.CosemParameters.SecurityType;

class Aare {

	static void parseResponse(CosemParameters params, CosemConnection connection, byte[] data) throws DlmsException {

		if (data == null || data.length < 4) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if ((data[0] & 0xFF) != Constants.AareApdu.APPLICATION_1) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if ((data[1] & 0xFF) != data.length-2) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if ((data[2] & 0xFF) != (Constants.Ber.CONTEXT_CONSTRUCTED | Constants.AareApdu.APPLICATION_CONTEXT_NAME)) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		int offset = (data[3] & 0xFF) + 4;
		parseContextName(Arrays.copyOfRange(data, 4, offset), connection.proposedContextName);
		if ((data[offset++] & 0xFF) != (Constants.Ber.CONTEXT_CONSTRUCTED | Constants.AareApdu.RESULT)) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		int resultLen = data[offset++] & 0xFF;
		byte[] result = Arrays.copyOfRange(data, offset, offset + resultLen);
		offset += resultLen;
		if ((data[offset++] & 0xFF) != (Constants.Ber.CONTEXT_CONSTRUCTED | Constants.AareApdu.RESULT_SOURCE_DIAGNOSTIC)) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		resultLen = data[offset++] & 0xFF;
		byte[] diagnostic = Arrays.copyOfRange(data, offset, offset + resultLen);
		offset += resultLen;
		
		parseResultAndDiagnostic(result, diagnostic);
		
		//Parse optional tags
		while (offset < data.length) {
			int tag = (data[offset++] & 0xFF);
			int len = (data[offset++] & 0xFF);
			if (data.length < (offset + len)) {
				throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
			}
			byte[] value = Arrays.copyOfRange(data, offset, offset+len);
			offset += len;
			if (tag == (Constants.Ber.CONTEXT_CONSTRUCTED | Constants.AareApdu.RESPONDING_AP_TITLE)) {
				if (value[0] != Constants.Ber.OCTET_STRING || value[1] != value.length-2) {
					throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
				}
				connection.serverSysTitle = Arrays.copyOfRange(value, 2, value.length);
				StringBuilder sb = new StringBuilder();
				for (byte b : connection.serverSysTitle) {
					sb.append(b);
				}
			}
			else if (tag == (Constants.Ber.CLASS_CONTEXT | Constants.AareApdu.RESPONDER_ACSE_REQUIREMENTS)) {
				//System.out.println("Resp ACSE Req: "+printBytes(value));
			}
			else if (tag == (Constants.Ber.CLASS_CONTEXT | Constants.AareApdu.MECHANISM_NAME)) {
				//System.out.println("Mechanism Name: "+printBytes(value));
			}
			else if (tag == (Constants.Ber.CONTEXT_CONSTRUCTED | Constants.AareApdu.RESPONDING_AUTHENTICATION_VALUE)) {
				if ( (value[0]&0xFF) != 0x80 || value[1] != value.length-2) {
					throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
				}
				connection.challengeServerToClient = Arrays.copyOfRange(value, 2, value.length);
			}
			else if (tag == (Constants.Ber.CONTEXT_CONSTRUCTED | Constants.AareApdu.USER_INFORMATION)) {
				parseUserInfo(value, params, connection);
			}
		}
	}

	private static void parseContextName(byte[] data, byte[] proposedContext) throws DlmsException {
		if (data == null || data.length != 9) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if (data[0] != Constants.Ber.OBJECT_IDENTIFIER || data[1] != proposedContext.length) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if (!Arrays.equals(Arrays.copyOfRange(data, 2, data.length), proposedContext)) {
			throw new DlmsException(DlmsExceptionReason.CONNECTION_REJECTED);
		}
	}
	
	private static void parseResultAndDiagnostic(byte[] result, byte[] diag) throws DlmsException {
		if (result == null || result.length != 3) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if (result[0] != Constants.Ber.INTEGER || result[1] != 1) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if (diag == null || diag.length != 5) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		
		if (diag[0] != (byte)0xA1 || diag[1] != 3 || diag[2] != Constants.Ber.INTEGER || diag[3] != 1) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (result[2] == 1) {
			DlmsExceptionReason[] reasons = new DlmsExceptionReason[2];
			reasons[0] = DlmsExceptionReason.CONNECTION_REJECTED_PERMANENT;
			reasons[1] = getDiagnosticReason(diag[4]);
			throw new DlmsException(reasons);
		}
		else if (result[2] == 2) {
			DlmsExceptionReason[] reasons = new DlmsExceptionReason[2];
			reasons[0] = DlmsExceptionReason.CONNECTION_REJECTED_TRANSIENT;
			reasons[1] = getDiagnosticReason(diag[4]);
			throw new DlmsException(reasons);
		}
		else if (result[2] != 0) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
	}

	private static void parseUserInfo(byte[] data, CosemParameters params, CosemConnection connection) throws DlmsException {
		if (data == null || data.length < 16) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (data[0] != Constants.Ber.OCTET_STRING) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (data[1] < 14 || data[1] != data.length-2) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (params.securityType != SecurityType.NONE) {
			if (data[2] != Constants.xDlmsApdu.GlobalCiphering.INITIATE_RESPONSE) {
				throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
			}
			if (data[3] != data.length-4) {
				throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
			}
			byte[] encrypted = new byte[data[3] & 0xFF];
			System.arraycopy(data, 4, encrypted, 0, encrypted.length);
			data = Security.reverseAuthenticatedEncryption(params, connection, encrypted);
		} else {
			data = Arrays.copyOfRange(data, 2, data.length);
		}
		if (data[0] != Constants.xDlmsApdu.NoCiphering.INITIATE_RESPONSE) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (data[1] != 0 || data[2] != Constants.DLMS_VERSION) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (data[3] != Constants.ConformanceBlock.TAG) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (data[4] != 0x1F || data[5] != 0x04 || data[6] != 0x00) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		connection.conformanceBlock = Arrays.copyOfRange(data, 7, 10);
		int vaa = ByteBuffer.allocate(2).put(Arrays.copyOfRange(data, 12, data.length)).getShort(0);
		if (params.referenceType == ReferenceType.LOGICAL_NAME && vaa != 0x0007) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		if (params.referenceType == ReferenceType.SHORT_NAME && vaa != 0xFA00) {
			throw new DlmsException(DlmsExceptionReason.MALFORMED_AARE_FRAME);
		}
		connection.maxPduSize = ((data[10] & 0xFF) << 8) | (data[11] & 0xFF);
	}
	
	private static DlmsExceptionReason getDiagnosticReason(int diagnostic) {
		switch (diagnostic) {
		case Constants.AssociateSourceDiagnostic.NULL:
		    return DlmsExceptionReason.CONNECTION_REJECTED_NULL;
		case Constants.AssociateSourceDiagnostic.NO_REASON:
			return DlmsExceptionReason.CONNECTION_REJECTED_NO_REASON;
		case Constants.AssociateSourceDiagnostic.CONTEXT_NAME_NOT_SUPPORTED:
			return DlmsExceptionReason.CONNECTION_REJECTED_CONTEXT_NAME_NOT_SUPPORTED;
		case Constants.AssociateSourceDiagnostic.AUTHENTICATION_MECHANISM_NOT_RECOGNISED:
			return DlmsExceptionReason.CONNECTION_REJECTED_AUTHENTICATION_MECHANISM_NOT_RECOGNISED;
		case Constants.AssociateSourceDiagnostic.AUTHENTICATION_MECHANISM_REQUIRED:
			return DlmsExceptionReason.CONNECTION_REJECTED_AUTHENTICATION_MECHANISM_REQUIRED;
		case Constants.AssociateSourceDiagnostic.AUTHENTICATION_FAILURE:
			return DlmsExceptionReason.CONNECTION_REJECTED_AUTHENTICATION_FAILURE;
		case Constants.AssociateSourceDiagnostic.AUTHENTICATION_REQUIRED:
			return DlmsExceptionReason.CONNECTION_REJECTED_AUTHENTICATION_FAILURE;
		default:
			return DlmsExceptionReason.CONNECTION_REJECTED_NULL;
		}
	}
	
}
