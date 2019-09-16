package yadi.dlms.classes.imagetransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Image {

	private final byte[] data;
	private final byte[] identifier;
	private int blockSize = 192;
	
	public Image(byte[] data, byte[] identifier) {
		this.data = data;
		this.identifier = identifier;
	}
	
	public Image(String filepath, byte[] identifier) throws IOException {
		this(Files.readAllBytes(Paths.get(filepath)), identifier);
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public byte[] getBlock(int blockNum) {
		int maxBlock = getNumberOfBlocks() - 1;
		
		if (blockNum < 0 || blockNum > maxBlock) {
			throw new IllegalArgumentException();
		}
		
		int from = blockNum * blockSize;
		int to = (blockNum + 1) * blockSize;
		if (to >= data.length) {
			to = data.length;
		}
		return Arrays.copyOfRange(data, from, to);
	}

	public byte[] getIdentifier() {
		return identifier;
	}

	public int getSize() {
		return data.length;
	}
	
	public int getNumberOfBlocks() {
		return (int)Math.ceil((double)data.length / blockSize);
	}
	
}
