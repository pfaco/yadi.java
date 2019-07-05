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

class Constants {
	
	static final int DLMS_VERSION = 6;
	static final int INVOKE_ID = 0x01;
	static final int PRIORITY_HIGH = 1 << 7;
	static final int PRIORITY_NORMAL = 0;
	static final int SERVICE_CLASS_UNCONFIRMED = 0;
	static final int SERVICE_CLASS_CONFIRMED = 1 << 6;

	static class Ber {
		static final int CLASS_UNIVERSAL = 0x00;
		static final int CLASS_APPLICATION = 0x40;
		static final int CLASS_CONTEXT = 0x80;
		static final int CLASS_PRIVATE = 0xC0;
		static final int PRIMITIVE = 0x00;
		static final int CONSTRUCTED = 0x20;
		static final int OBJECT_IDENTIFIER = 0x06;
		static final int OCTET_STRING = 0x04;
		static final int BIT_STRING = 0x03;
		static final int INTEGER_8 = 1;
		static final int INTEGER = 2;
		static final int CONTEXT_CONSTRUCTED = CLASS_CONTEXT | CONSTRUCTED;
	}
	
	static class CiPdu {
		static final int PING_REQUEST_PDU = 25;
		static final int PING_RESPONSE_PDU = 26;
		static final int REGISTER_PDU = 28;
		static final int DISCOVER_PDU = 29;
		static final int DISCOVER_REPORT_PDU = 30;
		static final int REPEATER_CALL_PDU = 31;
		static final int CLEAR_ALARM_PDU = 57;
	}
	
	static class DataType {
		static final int OCTET_STRING = 9;
	}
	
	static class xDlmsApdu {
		
		static class NoCiphering {
			static final int INITIATE_REQUEST = 1;
			static final byte INITIATE_RESPONSE = 8;
			static final int GET_REQUEST = 192;
			static final int SET_REQUEST = 193;
			static final int EVENT_NOTIFICATION_REQUEST = 194;
			static final int ACTION_REQUEST = 195;
			static final int GET_RESPONSE = 196;
			static final int SET_RESPONSE = 197;
			static final int ACTION_RESPONSE = 199;
		}
		
		static class GlobalCiphering {
			static final int INITIATE_REQUEST = 33;
			static final byte INITIATE_RESPONSE = 40;
			static final int GET_REQUEST = 200;
			static final int SET_REQUEST = 201;
			static final int EVENT_NOTIFICATION_REQUEST = 202;
			static final int ACTION_REQUEST = 203;
			static final int GET_RESPONSE = 204;
			static final int SET_RESPONSE = 205;
			static final int ACTION_RESPONSE = 207;
		}
		
		static class DedicatedCiphering {
			static final int GET_REQUEST = 208;
			static final int SET_REQUEST = 209;
			static final int EVENT_NOTIFICATION_REQUEST = 210;
			static final int ACTION_REQUEST = 211;
			static final int GET_RESPONSE = 212;
			static final int SET_RESPONSE = 213;
			static final int ACTION_RESPONSE = 215;
		}
		
		static class Exception {
			static final int ExceptionResponse = 216;
		}
	}
	
	static class AarqApdu {
		static final int PROTOCOL_VERSION = 0;
		static final int APPLICATION_CONTEXT_NAME = 1;
		static final int CALLED_AP_TITLE = 2;
		static final int CALLED_AE_QUALIFIER = 3;
		static final int CALLED_AP_INVOCATION_ID = 4;
		static final int CALLED_AE_INVOCATION_ID = 5;
		static final int CALLING_AP_TITLE = 6;
		static final int CALLING_AE_QUALIFIER = 7;
		static final int CALLING_AP_INVOCATION_ID = 8;
		static final int CALLING_AE_INVOCATION_ID = 9;
		static final int SENDER_ACSE_REQUIREMENTS = 10;
		static final int MECHANISM_NAME = 11;
		static final int CALLING_AUTHENTICATION_VALUE = 12;
		static final int IMPLEMENTATION_INFORMATION = 29;
		static final int USER_INFORMATION = 30;
	}
	
	static class AareApdu {
		static final int APPLICATION_1 = 97;
		static final int PROTOCOL_VERSION = 0;
		static final int APPLICATION_CONTEXT_NAME = 1;
		static final int RESULT = 2;
		static final int RESULT_SOURCE_DIAGNOSTIC = 3;
		static final int RESPONDING_AP_TITLE = 4;
		static final int RESPONDING_AE_QUALIFIER = 5;
		static final int RESPONDING_AP_INVOCATION_ID = 6;
		static final int RESPONDING_AE_INVOCATION_ID = 7;
		static final int RESPONDER_ACSE_REQUIREMENTS = 8;
		static final int MECHANISM_NAME = 9;
		static final int RESPONDING_AUTHENTICATION_VALUE = 10;
		static final int IMPLEMENTATION_INFORMATION = 29;
		static final int USER_INFORMATION = 30;
	}
	
	static class ConformanceBlock {
		static final int TAG = 95; //TODO where?
		static final int READ = 1 << 3;
		static final int WRITE = 1 << 4;
		static final int UNCONFIRMED_WRITE = 1 << 5;
		static final int ATTRIBUTE_0_SUPPORTED_WITH_SET = 1 << 8;
		static final int PRIORITY_MGMT_SUPPORTED = 1 << 9;
		static final int ATTRIBUTE_0_SUPPORTED_WITH_GET = 1 << 10;
		static final int BLOCK_TRANSFER_WITH_GET_OR_READ = 1 << 11;
		static final int BLOCK_TRANSFER_WITH_SET_OR_WRITE = 1 << 12;
		static final int BLOCK_TRANSFER_WITH_ACTION = 1 << 13;
		static final int MULTIPLE_REFERENCES = 1 << 14;
		static final int INFORMATION_REPORT = 1 << 15;
		static final int PARAMETERIZED_ACCESS = 1 << 18;
		static final int GET = 1 << 19;
		static final int SET = 1 << 20;
		static final int SELECTIVE_ACCESS = 1 << 21;
		static final int EVENT_NOTIFICATION = 1 << 22;
		static final int ACTION = 1 << 23;
	}
	
	static class ApplicationContextName {
		static final byte[] LOGICAL_NAME_NO_CIPHERING = {0x60, (byte) 0x85, 0x74, 0x05, 0x08, 0x01, 0x01};
		static final byte[] SHORT_NAME_NO_CIPHERING = {0x60, (byte) 0x85, 0x74, 0x05, 0x08, 0x01, 0x02};
		static final byte[] LOGICAL_NAME_WITH_CIPHERING = {0x60, (byte) 0x85, 0x74, 0x05, 0x08, 0x01, 0x03};
		static final byte[] SHORT_NAME_WITH_CIPHERING = {0x60, (byte) 0x85, 0x74, 0x05, 0x08, 0x01, 0x04};
	}
	
	static class GetRequest {
		static final int NORMAL = 1;
		static final int NEXT = 2;
		static final int WITH_LIST = 3;
	}
	
	static class GetResponse {
		static final int NORMAL = 1;
		static final int DATA_BLOCK = 2;
		static final int WITH_LIST = 3;
	}
	
	static class SetResponse {
		static final int NORMAL = 1;
		static final int DATA_BLOCK = 2;
		static final int LAST_DATA_BLOCK = 3;
		static final int LAST_DATA_BLOCK_WITH_LIST = 4;
		static final int WITH_LIST = 5;
	}
	
	static class AssociateSourceDiagnostic {
		static final int NULL = 0;
		static final int NO_REASON = 1;
		static final int CONTEXT_NAME_NOT_SUPPORTED = 2;
		static final int AUTHENTICATION_MECHANISM_NOT_RECOGNISED = 11;
		static final int AUTHENTICATION_MECHANISM_REQUIRED = 12;
		static final int AUTHENTICATION_FAILURE = 13;
		static final int AUTHENTICATION_REQUIRED = 14;
	}
	
	static enum AccessResult {
		ACCESS_RESULT_HARDWARE_FAULT(1),
		ACCESS_RESULT_TEMPORARY_FAILURE(2),
		ACCESS_RESULT_READ_WRITE_DENIED(3),
		ACCESS_RESULT_OBJECT_UNDEFINED(4),
		ACCESS_RESULT_OBJECT_CLASS_INCONSISTENT(9),
		ACCESS_RESULT_OBJECT_UNAVAILABLE(11),
		ACCESS_RESULT_TYPE_UNMATCHED(12),
		ACCESS_RESULT_SCOPE_OF_ACCESS_VIOLATED(13),
		ACCESS_RESULT_DATA_BLOCK_UNAVAILABLE(14),
		ACCESS_RESULT_LONG_GET_ABORTED(15),
		ACCESS_RESULT_NO_LONG_GET_IN_PROGRESS(16),
		ACCESS_RESULT_LONG_SET_ABORTED(17),
		ACCESS_RESULT_NO_LONG_SET_IN_PROGRESS(18),
		ACCESS_RESULT_DATA_BLOCK_NUMBER_INVALID(19),
		ACCESS_RESULT_OTHER_REASON(250);
		
		int val;
		
		AccessResult(int val) {
			this.val = val;
		}
	}

}
