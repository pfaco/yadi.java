package yadi.dlms.classes.profilegeneric;

import java.util.ArrayList;

public class ProfileGenericEntry {

	public class ProfileGenericItem {
		private byte[] data;
		
		public ProfileGenericItem(byte[] data) {
			this.data = data;
		}
		
		public byte[] getData() {
			return data;
		}
	}
	
	private ArrayList<ProfileGenericItem> items;
	
	public ProfileGenericEntry(int size) {
		items = new ArrayList<ProfileGenericItem>();
	}
	
	public void addItem(ProfileGenericItem item) {
		items.add(item);
	}
	
	public ArrayList<ProfileGenericItem> getItems() {
		return items;
	}
	
}
