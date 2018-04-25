package yadi.dlms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import yadi.dlms.DlmsException.DlmsExceptionReason;

public class SelectiveAccess {
	
	public static byte[] getEntryDescriptor(int lineFrom, int lineTo, int colFrom, int colTo) throws DlmsException {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(DlmsType.STRUCTURE.tag);
			stream.write(4);
			stream.write(DlmsType.UINT32.tag);
			stream.write(ByteBuffer.allocate(4).putInt(lineFrom).array());
			stream.write(DlmsType.UINT32.tag);
			stream.write(ByteBuffer.allocate(4).putInt(lineTo).array());
			stream.write(DlmsType.UINT16.tag);
			stream.write(ByteBuffer.allocate(2).putShort((short)colFrom).array());
			stream.write(DlmsType.UINT16.tag);
			stream.write(ByteBuffer.allocate(2).putShort((short)colTo).array());
			return stream.toByteArray();
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
}
