package yadi.dlms.classes.disconnector;

public enum ControlState {
	DISCONNECTED(0),
	CONNECTED(1),
	READY_FOR_RECONNECTION(2);
	
	int value;
	
	ControlState(int value) {
		this.value = value;
	}
	
	public static ControlState fromValue(int value) {
		for (ControlState state : ControlState.values()) {
			if (state.value == value) {
				return state;
			}
		}
		throw new IllegalArgumentException();
	}
}
