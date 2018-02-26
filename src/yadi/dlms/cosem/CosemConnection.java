package yadi.dlms.cosem;

import java.io.ByteArrayOutputStream;

/**
 * COSEM connection parameters
 */
class CosemConnection {
	
	class DataBlock {
		boolean lastBlock;
		int blockNum;
		final ByteArrayOutputStream data;
		
		DataBlock() {
			data = new ByteArrayOutputStream();
		}
		
		void reset() {
			lastBlock = false;
			blockNum = 0;
			data.reset();
		}
	}
	
	DataBlock datablock = new DataBlock();
	byte[] challengeServerToClient = new byte[0];
	byte[] challengeClientToServer = new byte[0];
	byte[] proposedContextName = new byte[0];
	byte[] conformanceBlock = new byte[0];
	byte[] serverSysTitle = new byte[0];
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
