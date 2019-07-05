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
package yadi.dlms.linklayer;

public class LinkLayerException extends Exception {

	public enum LinkLayerExceptionReason {
		INTERNAL_ERROR,
		RECEIVED_INVALID_ADDRESS,
		RECEIVED_INVALID_FRAME_NUMBER,
		RECEIVED_INVALID_FRAME_FORMAT,
		RECEIVED_INVALID_CHECK_SEQUENCE,
		SERVER_REPORTS_FRAME_REJECTED,
		RECEIVED_INVALID_LLC_BYTES
	}

	private static final long serialVersionUID = 1491728405848088633L;
	private final LinkLayerExceptionReason reason;
	
	public LinkLayerException(LinkLayerExceptionReason reason) {
		this.reason = reason;
	}
	
	public LinkLayerExceptionReason getReason() {
		return reason;
	}

}
