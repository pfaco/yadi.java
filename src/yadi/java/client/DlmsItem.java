/*
 * YADI (Yet Another DLMS Implementation)
 * Copyright (C) 2018 Paulo Faco (paulofaco@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package yadi.java.client;

import java.util.List;
import java.util.ArrayList;

public class DlmsItem {
	
	private final DlmsType type;
	private final String value;
	private final ArrayList<DlmsItem> children = new ArrayList<DlmsItem>();
	
	public DlmsItem(DlmsType type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public void addChildren(DlmsItem item) {
		children.add(item);
	}
	
	public List<DlmsItem> getChildren() {
		return children;
	}
	
	public DlmsType getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}

}
