package yadi.dlms.selectiveaccess;

import yadi.dlms.Obis;
import yadi.dlms.classes.clock.CosemDateTime;
import yadi.dlms.cosem.CosemSerializer;

public class DateTimeSelector implements SelectiveAccess {
	
	private byte[] data;
	
	public DateTimeSelector(CosemDateTime dtFrom, CosemDateTime dtTo) {
		this.data = new CosemSerializer()
				  .rawByte(0x01)
				  .structure(4)
				  	.structure(4)
				  		.uint16(8)
				  		.octetString(new Obis("0.0.1.0.0.255").getValue())
				  		.int8(2)
				  		.uint16(0)
				  	.datetime(dtFrom)
				  	.datetime(dtTo)
				  	.array(0)
				  	.serialize();
	}

	@Override
	public byte[] getDescriptorData() {
		return data;
	}
	
}
