package yadi.dlms.classes.imagetransfer;

import yadi.dlms.DlmsException;
import yadi.dlms.DlmsException.DlmsExceptionReason;

public enum ImageTransferStatus {
	NOT_INITIATED(0),
	INITIATED(1),
	VERIFICATION_INITIATED(2),
	VERIFICATION_SUCCESSFUL(3),
	VERIFICATION_FAILED(4),
	ACTIVATION_INITIATED(5),
	ACTIVATION_SUCCESSFUL(6),
	ACTIVATION_FAILED(7);
	
	int value;
	
	ImageTransferStatus(int value) {
		this.value = value;
	}
	
	static public ImageTransferStatus fromValue(int value) throws DlmsException {
		for (ImageTransferStatus e : ImageTransferStatus.values()) {
			if (e.value == value) {
				return e;
			}
		}
		throw new DlmsException(DlmsExceptionReason.INVALID_DATA);
	}
}
