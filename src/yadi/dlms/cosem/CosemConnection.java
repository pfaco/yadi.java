package yadi.dlms.cosem;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * COSEM connection parameters
 */
public class CosemConnection {
	
	class DataBlock {
		boolean lastBlock;
		int blockNum;
		final ByteArrayOutputStream data;
		byte[] blocks;
		int blockSize;
		int nextBlockNum;
		
		DataBlock() {
			data = new ByteArrayOutputStream();
		}
		
		void reset() {
			blockNum = 0;
			blocks = new byte[0];
			blockSize = 0;
			nextBlockNum = 1;
			data.reset();
		}

		public void setData(byte[] data, int len) {
			blocks = data;
			blockSize = len;
		}

		public byte[] getBlock(int blockNum) {
			int offset = (blockNum-1)*blockSize;
			if (offset > blocks.length) {
				return null;
			}
			int to = offset + blockSize;
			if (to > blocks.length) {
				to = blocks.length;
			}
			return Arrays.copyOfRange(blocks, offset, to);
		}
		
		public void ackBlock(int blockNum) {
			if(blockNum == nextBlockNum) {
				nextBlockNum++;
			}
		}

		public byte[] getNextBlock() {
			return getBlock(nextBlockNum);
		}

		public byte[] getNextBlockNum() {
			byte[] data = ByteBuffer.allocate(4).putInt(nextBlockNum).array();
			System.out.println("block num = "+nextBlockNum);
			StringBuilder sb = new StringBuilder();
			for (byte b : data) {
				sb.append(String.format("%02X ", b));
			}
			System.out.println(sb);
			return data;
		}
		
		public boolean thisIsLast() {
			return getBlock(nextBlockNum) == null;
		}

		public boolean nextIsLast() {
			return getBlock(nextBlockNum+1) == null;
		}
	}
	
	DataBlock datablock = new DataBlock();
	byte[] challengeServerToClient = new byte[0];
	byte[] challengeClientToServer = new byte[0];
	byte[] proposedContextName = new byte[0];
	byte[] conformanceBlock = new byte[0];
	public byte[] serverSysTitle = new byte[0];
	int maxPduSize;
	int serverInvocationCounter;
	
	void reset() {
		challengeServerToClient = new byte[0];
		challengeClientToServer = new byte[0];
		proposedContextName = new byte[0];
		conformanceBlock = new byte[0];
		serverSysTitle = new byte[0];
		maxPduSize = 0;
		serverInvocationCounter = 0;
		datablock.reset();
	}
}
