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
package yadi.dlms;

public class DlmsException extends Exception {

	private static final long serialVersionUID = 1L;

	public enum DlmsExceptionReason {
		INTERNAL_ERROR, 
		INVALID_SETTING, 
		INVALID_DATA_TYPE,
		INVALID_DATA, 
		RECEIVED_INVALID_COMMAND_ID, 
		NO_SUCH_TYPE,
		MALFORMED_AARE_FRAME, 
		CONNECTION_REJECTED,
		CONNECTION_REJECTED_PERMANENT,
		CONNECTION_REJECTED_TRANSIENT,
		CONNECTION_REJECTED_NULL,
		CONNECTION_REJECTED_NO_REASON, 
		CONNECTION_REJECTED_CONTEXT_NAME_NOT_SUPPORTED, 
		CONNECTION_REJECTED_AUTHENTICATION_MECHANISM_NOT_RECOGNISED, 
		CONNECTION_REJECTED_AUTHENTICATION_MECHANISM_REQUIRED, 
		CONNECTION_REJECTED_AUTHENTICATION_FAILURE,
		FAIL_TO_AUTHENTICATE_SERVER,
		STATE_ERROR_UNKNOWN,
		STATE_ERROR_SERVICE_NOT_ALLOWED,
		STATE_ERROR_SERVICE_UNKNOWN,
		SERVICE_ERROR_UNKNOWN,
		SERVICE_ERROR_OPERATION_NOT_POSSIBLE,
		SERVICE_ERROR_NOT_SUPPORTED,
		SERVICE_ERROR_OTHER_REASON,
		SERVICE_ERROR_PDU_TOO_LONG,
		SERVICE_ERROR_DECIPHERING_ERROR,
		SERVICE_ERROR_INVOCATION_COUNTER_ERROR,
		SECURITY_FAIL,
		RECEIVED_INVALID_GET_RESPONSE,
		RECEIVED_INVALID_SET_RESPONSE,
		UNKNOWN_ACCESS_RESULT_FAILURE,
		ACCESS_RESULT_HARDWARE_FAULT,
		ACCESS_RESULT_TEMPORARY_FAILURE,
		ACCESS_RESULT_READ_WRITE_DENIED,
		ACCESS_RESULT_OBJECT_UNDEFINED,
		ACCESS_RESULT_OBJECT_CLASS_INCONSISTENT,
		ACCESS_RESULT_OBJECT_UNAVAILABLE,
		ACCESS_RESULT_TYPE_UNMATCHED,
		ACCESS_RESULT_SCOPE_OF_ACCESS_VIOLATED,
		ACCESS_RESULT_DATA_BLOCK_UNAVAILABLE,
		ACCESS_RESULT_LONG_GET_ABORTED,
		ACCESS_RESULT_NO_LONG_GET_IN_PROGRESS,
		ACCESS_RESULT_LONG_SET_ABORTED,
		ACCESS_RESULT_NO_LONG_SET_IN_PROGRESS,
		ACCESS_RESULT_DATA_BLOCK_NUMBER_INVALID,
		ACCESS_RESULT_OTHER_REASON,
	}
	
	private final DlmsExceptionReason[] reason;
	
	public DlmsException(DlmsExceptionReason reason) {
		this.reason = new DlmsExceptionReason[]{reason};
	}
	
	public DlmsException(DlmsExceptionReason[] reasons) {
		this.reason = reasons;
	}
	
	public DlmsException(DlmsExceptionReason reason, String message) {
		super(message);
		this.reason = new DlmsExceptionReason[]{reason};
	}
	
	public DlmsException(DlmsExceptionReason[] reasons, String message) {
		super(message);
		this.reason = reasons;
	}
	
	/**
	 * Retrieves the reason of the exception
	 * @return A DlmsExceptionReason
	 */
	public DlmsExceptionReason[] getReason() {
		return reason;
	}

}
