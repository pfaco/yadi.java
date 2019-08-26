package yadi.dlms.classes.profilegeneric;

import java.time.LocalDateTime;
import yadi.dlms.Obis;
import yadi.dlms.cosem.CosemSerializer;

public class DatetimeSelector implements SelectiveAccess {
	
	private byte[] data;
	
	public DatetimeSelector(LocalDateTime dtFrom, LocalDateTime dtTo) {
		CosemSerializer serializer = new CosemSerializer();
		serializer.rawByte(0x01)
				  .structure(4)
				  	.structure(4)
				  		.uint16(8)
				  		.octetString(new Obis("0.0.1.0.0.255").getValue())
				  		.int8(2)
				  		.uint16(0)
				  	.datetime(dtFrom)
				  	.datetime(dtTo)
				  	.array(0);

		data = serializer.serialize();
	}

	@Override
	public byte[] getDescriptorData() {
		return data;
	}
	
}
