package yadi.dlms;

public class ImageTransferException extends Exception {
	private static final long serialVersionUID = 6965629701894481169L;

	public enum ImageTransferExceptionReason {
		TRANSFER_DISABLED, 
		INTERNAL_ERROR, 
		INVALID_IMAGE_TO_ACTIVATE, INVALID_BLOCK_SIZE;
	}
	
	ImageTransferException(ImageTransferExceptionReason reason) {
		
	}
}
