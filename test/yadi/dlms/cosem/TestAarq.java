package yadi.dlms.cosem;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import app.HexString;
import yadi.dlms.DlmsException;

public class TestAarq {
	
	@Test
	void testAarqPublic() throws IOException, DlmsException {
		byte[] expected = new byte[] {};
		CosemParameters params = new CosemParameters();
		CosemConnection connection = new CosemConnection();
		byte[] result = Aarq.request(params, connection);
		assertArrayEquals(expected, Aarq.request(params, connection));
	}
}
