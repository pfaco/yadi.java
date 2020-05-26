package yadi.dlms.cosem;

public class SnDescriptor {

	private final int shortName;
	private byte[] requestData = new byte[0];
	private byte[] responseData = new byte[0];
	
	/**
	 * Creates a descriptor for a DLMS object
	 * @param classId the object class_id
	 * @param index the index of the attribute/method to be accessed
	 * @param obis the obis of the object
	 */
	public SnDescriptor(int shortName) {
		this.shortName = shortName;
	}
	
	/**
	 * Retrieves the OBIS of the descriptor
	 * @return byte array representing the OBIS
	 */
	public int getShortName() {
		return shortName;
	}

	/**
	 * Retrieves the request data of the descriptor
	 * @return byte array representing the request data
	 */
	public byte[] getRequestData() {
		return requestData;
	}
	
	/**
	 * Retrieves the data received after an successful operation was performed
	 * @return byte array returned from the last operation successfully performed
	 */
	public byte[] getResponseData() {
		return responseData;
	}
	
	/**
	 * Sets the data to be used in next operations
	 * @param data byte array of the data to be used
	 */
	public void setRequestData(byte[] data) {
		if (data == null) {
			this.requestData = new byte[0];
		} else {
			this.requestData = data;
		}
	}

	/**
	 * Sets the response data of the descriptor
	 * @return byte array representing the response data
	 */
	public void setResponseData(byte[] data) {
		if (data == null) {
			this.responseData = new byte[0];
		} else {
			this.responseData = data;
		}
	}
}
