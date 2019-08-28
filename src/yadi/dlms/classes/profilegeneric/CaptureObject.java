package yadi.dlms.classes.profilegeneric;

import yadi.dlms.Obis;

public class CaptureObject {
	private final int classId;
	private final Obis obis;
	private final int index;
	private final int dataIndex;
	
	public CaptureObject(int classId, Obis obis, int index, int dataIndex) {
		this.classId = classId;
		this.obis = obis;
		this.index = index;
		this.dataIndex = dataIndex;
	}

	public int getClassId() {
		return classId;
	}

	public Obis getObis() {
		return obis;
	}

	public int getIndex() {
		return index;
	}

	public int getDataIndex() {
		return dataIndex;
	}
}
