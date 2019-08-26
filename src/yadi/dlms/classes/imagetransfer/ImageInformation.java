package yadi.dlms.classes.imagetransfer;

public class ImageInformation {
	
	private int size;
	private String identification;
	private byte[] signature;
	
	public ImageInformation(int size, String identification, byte[]  signature) {
		this.size = size;
		this.identification = identification;
		this.signature = signature;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getIdentification() {
		return identification;
	}
	
	public byte[] getSignature() {
		return signature;
	}

}
