package yadi.java.client.linklayer;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

public class TestHdlcParameters {

	@Test
	public void testSetServerAddress() {
		HdlcParameters hdlcParams = new HdlcParameters();
		hdlcParams.setServerAddress(0x3FFE, 0x3FFF);
		assertArrayEquals(new byte[] {(byte)0xFE, (byte)0xFC, (byte)0xFE, (byte)0xFF}, hdlcParams.serverAddress);
	}
	
	@Test
	public void testSetClientAddress() {
		HdlcParameters hdlcParams = new HdlcParameters();
		hdlcParams.setClientAddress(0x01);
		assertEquals(0x03, hdlcParams.clientAddress);
	}
	
}
