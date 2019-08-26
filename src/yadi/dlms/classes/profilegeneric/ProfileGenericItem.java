package yadi.dlms.classes.profilegeneric;

public class ProfileGenericItem {
	private byte[] data;
	
	public ProfileGenericItem(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
}
