package yadi.dlms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import yadi.dlms.DlmsType;

public class TestDlmsType {

	@Test
	void testDlmsTypeTag() {
		assertEquals( 1, DlmsType.ARRAY.tag);
		assertEquals( 2, DlmsType.STRUCTURE.tag);
		assertEquals( 3, DlmsType.BOOLEAN.tag);
		assertEquals( 4, DlmsType.BITSTRING.tag);
		assertEquals( 5, DlmsType.INT32.tag);
		assertEquals( 6, DlmsType.UINT32.tag);
		assertEquals( 9, DlmsType.OCTET_STRING.tag);
		assertEquals(10, DlmsType.STRING.tag);
		assertEquals(12, DlmsType.UTF8_STRING.tag);
		assertEquals(13, DlmsType.BCD.tag);
		assertEquals(15, DlmsType.INT8.tag);
		assertEquals(16, DlmsType.INT16.tag);
		assertEquals(17, DlmsType.UINT8.tag);
		assertEquals(18, DlmsType.UINT16.tag);
		assertEquals(20, DlmsType.INT64.tag);
		assertEquals(21, DlmsType.UINT64.tag);
		assertEquals(22, DlmsType.ENUM.tag);
		assertEquals(23, DlmsType.FLOAT32.tag);
		assertEquals(24, DlmsType.FLOAT64.tag);
		assertEquals(25, DlmsType.DATE_TIME.tag);
		assertEquals(26, DlmsType.DATE.tag);
		assertEquals(27, DlmsType.TIME.tag);
	}
	
	@Test
	void testDlmsTypeSize() {
		assertEquals(0, DlmsType.ARRAY.size);
		assertEquals(0, DlmsType.STRUCTURE.size);
		assertEquals(1, DlmsType.BOOLEAN.size);
		assertEquals(0, DlmsType.BITSTRING.size);
		assertEquals(4, DlmsType.INT32.size);
		assertEquals(4, DlmsType.UINT32.size);
		assertEquals(0, DlmsType.OCTET_STRING.size);
		assertEquals(0, DlmsType.STRING.size);
		assertEquals(0, DlmsType.UTF8_STRING.size);
		assertEquals(0, DlmsType.BCD.size);
		assertEquals(1, DlmsType.INT8.size);
		assertEquals(2, DlmsType.INT16.size);
		assertEquals(1, DlmsType.UINT8.size);
		assertEquals(2, DlmsType.UINT16.size);
		assertEquals(8, DlmsType.INT64.size);
		assertEquals(8, DlmsType.UINT64.size);
		assertEquals(1, DlmsType.ENUM.size);
		assertEquals(4, DlmsType.FLOAT32.size);
		assertEquals(8, DlmsType.FLOAT64.size);
		assertEquals(12, DlmsType.DATE_TIME.size);
		assertEquals(5, DlmsType.DATE.size);
		assertEquals(4, DlmsType.TIME.size);
	}
}
