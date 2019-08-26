package yadi.dlms.classes.imagetransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Image {

	private byte[] data;
	private int blockSize = 192;
	private String identifier;
	
	public Image(byte[] data, String identifier) {
		this.data = data;
		this.identifier = identifier;
	}
	
	public Image(String filepath, String identifier) throws IOException {
		this(Files.readAllBytes(Paths.get(filepath)), identifier);
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public byte[] getBlock(int blockNum) {
		int maxBlock = (data.length / blockSize) + 1;
		
		if (blockNum <= 0 || blockNum > maxBlock) {
			throw new IllegalArgumentException();
		}
		
		int from = (blockNum - 1) * blockSize;
		int to = blockNum * blockSize;
		if (to >= data.length) {
			to = data.length - 1;
		}
		return Arrays.copyOfRange(data, from, to);
	}

	public String getIdentifier() {
		return identifier;
	}

	public int getSize() {
		return data.length;
	}
	
	public int getNumberOfBlocks() {
		return (data.length / blockSize) + 1;
	}
	
}
