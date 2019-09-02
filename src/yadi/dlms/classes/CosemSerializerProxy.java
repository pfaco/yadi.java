package yadi.dlms.classes;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.classes.clock.CosemDate;
import yadi.dlms.classes.clock.CosemDateTime;
import yadi.dlms.classes.clock.CosemTime;
import yadi.dlms.cosem.CosemSerializer;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class CosemSerializerProxy extends CosemSerializer {

	private final CosemProxyExecuter executer;
	
	public CosemSerializerProxy(CosemProxyExecuter executer) {
		this.executer = executer;
	}
	
	public void write(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		executer.execute(dlms, phy, this.serialize());
	}
	
	@Override
	public CosemSerializerProxy array(int size) {
		super.array(size);
		return this;
	}
	
	@Override
	public CosemSerializerProxy bitstring(boolean[] value) {
		super.bitstring(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy string(String str) {
		super.string(str);
		return this;
	}
	
	@Override
	public CosemSerializerProxy bool(boolean value) {
		super.bool(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy date(CosemDate date) {
		super.date(date);
		return this;
	}
	
	@Override
	public CosemSerializerProxy datetime(CosemDateTime dt) {
		super.datetime(dt);
		return this;
	}
	
	@Override
	public CosemSerializerProxy enumeration(int value) {
		super.enumeration(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy float32(float value) {
		super.float32(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy float64(double value) {
		super.float64(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy int16(int value) {
		super.int16(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy int32(int value) {
		super.int32(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy int64(long value) {
		super.int64(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy int8(int value) {
		super.int8(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy octetString(byte[] octets) {
		super.octetString(octets);
		return this;
	}
	
	@Override
	public CosemSerializerProxy rawByte(int value) {
		super.rawByte(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy structure(int size) {
		super.structure(size);
		return this;
	}
	
	@Override
	public CosemSerializerProxy time(CosemTime time) {
		super.time(time);
		return this;
	}
	
	@Override
	public CosemSerializerProxy uint16(int value) {
		super.uint16(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy uint32(int value) {
		super.uint32(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy uint64(long value) {
		super.uint64(value);
		return this;
	}
	
	@Override
	public CosemSerializerProxy uint8(int value) {
		super.uint8(value);
		return this;
	}
	
}
