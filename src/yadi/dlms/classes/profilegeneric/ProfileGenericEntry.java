package yadi.dlms.classes.profilegeneric;

import java.util.ArrayList;

public class ProfileGenericEntry {
	
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
