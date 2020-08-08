package yadi.dlms.cosem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import yadi.dlms.DlmsType;
import yadi.dlms.Obis;
import yadi.dlms.classes.clock.CosemDate;
import yadi.dlms.classes.clock.CosemDateTime;
import yadi.dlms.classes.clock.CosemTime;

public class CosemSerializer {
	
	private ByteArrayOutputStream os = new ByteArrayOutputStream();
	
	public static CosemSerializer make() {
		return new CosemSerializer();
	}
	
	public byte[] serialize() {
		return os.toByteArray();
	}
	
	public CosemSerializer serializeSize(int size) {
	    if (size <= 0x80) {
	        os.write(size);
	    } else if (size <= 0xFF) {
	    	os.write(0x81);
	    	os.write(size);
	    } else if (size <= 0xFFFF) {
	    	os.write(0x82);
	    	os.write(size >>> 8);
	    	os.write(size);
	    } else if (size <= 0xFFFFFF) {
	    	os.write(0x83);
	    	os.write(size >>> 16);
	    	os.write(size >>> 8);
	    	os.write(size);
	    } else {
	    	os.write(0x83);
	    	os.write(size >>> 24);
	    	os.write(size >>> 16);
	    	os.write(size >>> 8);
	    	os.write(size);
	    }
	    return this;
	}

	public CosemSerializer structure(int size) {
		os.write(DlmsType.STRUCTURE.tag);
		return serializeSize(size);
	}
	
	public CosemSerializer array(int size) {
		os.write(DlmsType.ARRAY.tag);
		return serializeSize(size);
	}

	public CosemSerializer string(String str) {
		os.write(DlmsType.STRING.tag);
		serializeSize(str.length());
		serializeBytes(str.getBytes());
		return this;
	}

	public CosemSerializer octetString(byte[] octets) {
		os.write(DlmsType.OCTET_STRING.tag);
		serializeSize(octets.length);
		serializeBytes(octets);
		return this;
	}
	
	public CosemSerializer bcd(byte[] octets) {
		os.write(DlmsType.BCD.tag);
		serializeSize(octets.length);
		serializeBytes(octets);
		return this;
	}
	
	public CosemSerializer bitstring(boolean[] value) {
		os.write(DlmsType.BITSTRING.tag);
		serializeSize(value.length);
		int byteSize = (int) Math.ceil((double)value.length / 8);
		byte[] byteValue = new byte[byteSize];
		int nextByte = 0;
		int nextBit = 0x80;
		for (int i = 0; i < value.length; ++i) {
			byteValue[nextByte] |= value[i] ? nextBit : 0;
			nextBit >>>= 1;
			if (nextBit == 0) {
				nextBit = 0x80;
				++nextByte;
			}
		}
		serializeBytes(byteValue);
		return this;
	}
	
	public CosemSerializer bool(boolean value) {
		os.write(DlmsType.BOOLEAN.tag);
		if (value) {
			os.write(0x01);
		} else {
			os.write(0x00);
		}
		return this;
	}
	
	public CosemSerializer date(CosemDate date) {
		os.write(DlmsType.OCTET_STRING.tag);
		serializeSize(5);
		os.write(date.getYear() >>> 8);
		os.write(date.getYear());
		os.write(date.getMonth());
		os.write(date.getMonthDay());
		os.write(date.getWeekDay());
		return this;
	}
	
	public CosemSerializer time(CosemTime time) {
		os.write(DlmsType.OCTET_STRING.tag);
		serializeSize(4);
		os.write(time.getHour());
		os.write(time.getMinute());
		os.write(time.getSeconds());
		os.write(0);
		return this;
	}
	
	public CosemSerializer datetime(CosemDateTime dt) {
		os.write(DlmsType.OCTET_STRING.tag);
		serializeSize(12);
		os.write(dt.getDate().getYear() >>> 8);
		os.write(dt.getDate().getYear());
		os.write(dt.getDate().getMonth());
		os.write(dt.getDate().getMonthDay());
		os.write(dt.getDate().getWeekDay());
		os.write(dt.getTime().getHour());
		os.write(dt.getTime().getMinute());
		os.write(dt.getTime().getSeconds());
		os.write(0);
		os.write(0x80);
		os.write(0x00);
		os.write(0xFF);
		return this;
	}
	
	public CosemSerializer float32(float value) {
		int rawvalue = Float.floatToRawIntBits(value);
		os.write(DlmsType.FLOAT32.tag);
		os.write(rawvalue >>> 24);
    	os.write(rawvalue >>> 16);
    	os.write(rawvalue >>> 8);
    	os.write(rawvalue);
		return this;
	}
	
	public CosemSerializer float64(double value) {
		long rawvalue = Double.doubleToRawLongBits(value);
		os.write(DlmsType.FLOAT64.tag);
		os.write((int) (rawvalue >>> 56));
    	os.write((int) (rawvalue >>> 48));
    	os.write((int) (rawvalue >>> 40));
    	os.write((int) (rawvalue >>> 32));
		os.write((int) (rawvalue >>> 24));
    	os.write((int) (rawvalue >>> 16));
    	os.write((int) (rawvalue >>> 8));
    	os.write((int) rawvalue);
		return this;
	}
	
	public CosemSerializer enumeration(int value) {
		os.write(DlmsType.ENUM.tag);
		os.write(value);
		return this;
	}

	public CosemSerializer int8(int value) {
		os.write(DlmsType.INT8.tag);
		os.write(value);
		return this;
	}
	
	public CosemSerializer uint8(int value) {
		os.write(DlmsType.UINT8.tag);
		os.write(value);
		return this;
	}
	
	public CosemSerializer int16(int value) {
		os.write(DlmsType.INT16.tag);
    	os.write(value >>> 8);
    	os.write(value);
    	return this;
	}
	
	public CosemSerializer uint16(int value) {
		os.write(DlmsType.UINT16.tag);
    	os.write(value >>> 8);
    	os.write(value);
    	return this;
	}
	
	public CosemSerializer int32(int value) {
		os.write(DlmsType.INT32.tag);
		os.write(value >>> 24);
    	os.write(value >>> 16);
    	os.write(value >>> 8);
    	os.write(value);
    	return this;
	}
	
	public CosemSerializer uint32(int value) {
		os.write(DlmsType.UINT32.tag);
		os.write(value >>> 24);
    	os.write(value >>> 16);
    	os.write(value >>> 8);
    	os.write(value);
    	return this;
	}
	
	public CosemSerializer int64(long value) {
		os.write(DlmsType.INT64.tag);
		os.write((int) (value >>> 56));
    	os.write((int) (value >>> 48));
    	os.write((int) (value >>> 40));
    	os.write((int) (value >>> 32));
		os.write((int) (value >>> 24));
    	os.write((int) (value >>> 16));
    	os.write((int) (value >>> 8));
    	os.write((int) value);
    	return this;
	}
	
	public CosemSerializer uint64(long value) {
		os.write(DlmsType.UINT64.tag);
		os.write((int) (value >>> 56));
    	os.write((int) (value >>> 48));
    	os.write((int) (value >>> 40));
    	os.write((int) (value >>> 32));
		os.write((int) (value >>> 24));
    	os.write((int) (value >>> 16));
    	os.write((int) (value >>> 8));
    	os.write((int) value);
    	return this;
	}

	public CosemSerializer rawByte(int value) {
		os.write(value);
		return this;
	}
	

	public CosemSerializer rawBuffer(byte[] content) {
		try {
			os.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public CosemSerializer captureObject(int classId, String obis, int index) {
		return uint16(classId)
		.octetString(new Obis(obis).getValue())
		.int8(index)
		.uint16(0);
	}
	
	private void serializeBytes(byte[] bytes) {
		try {
			os.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
