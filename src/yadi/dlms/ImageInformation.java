package yadi.dlms;

public class ImageInformation {
	private int blockSize;
	private final byte[] identifier;
	private final byte[] image;
	
	public ImageInformation(int blockSize, byte[] identifier, byte[] image) {
		this.blockSize = blockSize;
		this.identifier = identifier;
		this.image = image;
	}
	
	public ImageInformation(byte[] identifier, byte[] image) {
		this(0, identifier, image);
	}
	
	public int getBlockSize() {
		return blockSize;
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public byte[] getIdentifier() {
		return identifier;
	}
	
	public byte[] getImage() {
		return image;
	}
}
