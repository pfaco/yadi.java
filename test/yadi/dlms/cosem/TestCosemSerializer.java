package yadi.dlms.cosem;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

import yadi.dlms.cosem.CosemSerializer;

public class TestCosemSerializer {

	@Test
	void testCosemSerializeArray() {
		assertArrayEquals(new CosemSerializer().array(0).serialize(), new byte[] {0x01, 0x00});
		assertArrayEquals(new CosemSerializer().array(1).serialize(), new byte[] {0x01, 0x01});
		assertArrayEquals(new CosemSerializer().array(2).serialize(), new byte[] {0x01, 0x02});
		assertArrayEquals(new CosemSerializer().array(127).serialize(), new byte[] {0x01, (byte)0x7F});
		assertArrayEquals(new CosemSerializer().array(128).serialize(), new byte[] {0x01, (byte)0x80});
		assertArrayEquals(new CosemSerializer().array(129).serialize(), new byte[] {0x01, (byte)0x81, (byte)0x81});
		assertArrayEquals(new CosemSerializer().array(254).serialize(), new byte[] {0x01, (byte)0x81, (byte)0xFE});
		assertArrayEquals(new CosemSerializer().array(255).serialize(), new byte[] {0x01, (byte)0x81, (byte)0xFF});
		assertArrayEquals(new CosemSerializer().array(256).serialize(), new byte[] {0x01, (byte)0x82, (byte)0x01, 0x00});
		assertArrayEquals(new CosemSerializer().array(65534).serialize(), new byte[] {0x01, (byte)0x82, (byte)0xFF, (byte)0xFE});
		assertArrayEquals(new CosemSerializer().array(65535).serialize(), new byte[] {0x01, (byte)0x82, (byte)0xFF, (byte)0xFF});
		assertArrayEquals(new CosemSerializer().array(65536).serialize(), new byte[] {0x01, (byte)0x83, (byte)0x01, 0x00, 0x00});
	}
	
	@Test
	void testCosemSerializeStrcuture() {
		assertArrayEquals(new CosemSerializer().structure(0).serialize(), new byte[] {0x02, 0x00});
		assertArrayEquals(new CosemSerializer().structure(1).serialize(), new byte[] {0x02, 0x01});
		assertArrayEquals(new CosemSerializer().structure(2).serialize(), new byte[] {0x02, 0x02});
		assertArrayEquals(new CosemSerializer().structure(127).serialize(), new byte[] {0x02, (byte)0x7F});
		assertArrayEquals(new CosemSerializer().structure(128).serialize(), new byte[] {0x02, (byte)0x80});
		assertArrayEquals(new CosemSerializer().structure(129).serialize(), new byte[] {0x02, (byte)0x81, (byte)0x81});
		assertArrayEquals(new CosemSerializer().structure(254).serialize(), new byte[] {0x02, (byte)0x81, (byte)0xFE});
		assertArrayEquals(new CosemSerializer().structure(255).serialize(), new byte[] {0x02, (byte)0x81, (byte)0xFF});
		assertArrayEquals(new CosemSerializer().structure(256).serialize(), new byte[] {0x02, (byte)0x82, (byte)0x01, 0x00});
		assertArrayEquals(new CosemSerializer().structure(65534).serialize(), new byte[] {0x02, (byte)0x82, (byte)0xFF, (byte)0xFE});
		assertArrayEquals(new CosemSerializer().structure(65535).serialize(), new byte[] {0x02, (byte)0x82, (byte)0xFF, (byte)0xFF});
		assertArrayEquals(new CosemSerializer().structure(65536).serialize(), new byte[] {0x02, (byte)0x83, (byte)0x01, 0x00, 0x00});
	}
	
	@Test
	void testCosemSerializeBitstring() {
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {}).serialize(), new byte[] {0x04, 0x00});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {false}).serialize(), new byte[] {0x04, 0x01, 0x00});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {true}).serialize(), new byte[] {0x04, 0x01, 0x01});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {false, false}).serialize(), new byte[] {0x04, 0x02, 0x00});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {false, true}).serialize(), new byte[] {0x04, 0x02, 0x02});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {true, false}).serialize(), new byte[] {0x04, 0x02, 0x01});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {true, true}).serialize(), new byte[] {0x04, 0x02, 0x03});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {true, true, true, true}).serialize(), new byte[] {0x04, 0x04, 0x0F});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {true, true, true, true, true, true, true, true}).serialize(), new byte[] {0x04, 0x08, (byte)0xFF});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {false, true, false, true, false, true, false, true}).serialize(), new byte[] {0x04, 0x08, (byte)0xAA});
		assertArrayEquals(new CosemSerializer().bitstring(new boolean[] {true, false, true, false, true, false, true, false}).serialize(), new byte[] {0x04, 0x08, (byte)0x55});
	}
	
	@Test
	void testCosemSerializeBoolean() {
		assertArrayEquals(new CosemSerializer().bool(false).serialize(), new byte[] {0x03, 0x00});
		assertArrayEquals(new CosemSerializer().bool(true).serialize(), new byte[] {0x03, 0x01});
	}
	
	@Test
	void testCosemSerializeInt8() {
		assertArrayEquals(new CosemSerializer().int8(0).serialize(), new byte[] {0x0F, 0x00});
		assertArrayEquals(new CosemSerializer().int8(1).serialize(), new byte[] {0x0F, 0x01});
		assertArrayEquals(new CosemSerializer().int8(10).serialize(), new byte[] {0x0F, 0x0A});
		assertArrayEquals(new CosemSerializer().int8(100).serialize(), new byte[] {0x0F, 0x64});
		assertArrayEquals(new CosemSerializer().int8(127).serialize(), new byte[] {0x0F, 0x7F});
		assertArrayEquals(new CosemSerializer().int8(-1).serialize(), new byte[] {0x0F, (byte)0xFF});
		assertArrayEquals(new CosemSerializer().int8(-10).serialize(), new byte[] {0x0F, (byte)0xF6});
		assertArrayEquals(new CosemSerializer().int8(-100).serialize(), new byte[] {0x0F, (byte)0x9C});
		assertArrayEquals(new CosemSerializer().int8(-128).serialize(), new byte[] {0x0F, (byte)0x80});
	}
}
