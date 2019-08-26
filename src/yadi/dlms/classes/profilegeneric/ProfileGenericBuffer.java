package yadi.dlms.classes.profilegeneric;

import java.util.ArrayList;

public class ProfileGenericBuffer {

	private ArrayList<ProfileGenericEntry> entries = new ArrayList<ProfileGenericEntry>();
	
	public void addEntry(ProfileGenericEntry entry) {
		entries.add(entry);
	}
	
}
