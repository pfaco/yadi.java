package yadi.dlms.selectiveaccess;
import yadi.dlms.cosem.CosemSerializer;

public class EntrySelector implements SelectiveAccess {

	public static class Builder {
		private int fromEntry = 1;
		private int toEntry = 0;
		private int fromSelectedValue = 1;
		private int toSelectedValue = 0;
		
		public Builder fromEntry(int fromEntry) {
			this.fromEntry = fromEntry;
			return this;
		}
		
		public Builder toEntry(int toEntry) {
			this.toEntry = toEntry;
			return this;
		}
		
		public Builder fromSelectedValue(int fromSelectedValue) {
			this.fromSelectedValue = fromSelectedValue;
			return this;
		}
		
		public Builder toSelectedValue(int toSelectedValue) {
			this.toSelectedValue = toSelectedValue;
			return this;
		}
		
		public SelectiveAccess build() {
			return new EntrySelector(
					this.fromEntry,
					this.toEntry,
					this.fromSelectedValue,
					this.toSelectedValue);
		}
	}
	
	private byte[] data;
	
	public static Builder builder() {
		return new Builder();
	}
	
	private EntrySelector(int fromEntry, int toEntry, int fromSelected, int toSelected) {
		this.data = new CosemSerializer()
				  .rawByte(0x02)
				  .structure(4)
					  .uint32(fromEntry)
					  .uint32(toEntry)
					  .uint16(fromSelected)
					  .uint16(toSelected)
					  .serialize();
	}

	@Override
	public byte[] getDescriptorData() {
		return data;
	}
	
}
