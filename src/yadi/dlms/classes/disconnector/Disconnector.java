package yadi.dlms.classes.disconnector;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.cosem.CosemParser;
import yadi.dlms.cosem.CosemSerializer;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class Disconnector {

	private final LnDescriptor attOutputState;
	private final LnDescriptor attControlState;
	private final LnDescriptor attControlMode;
	private final LnDescriptor mtdDisconnect;
	private final LnDescriptor mtdReconnect;
	
	public Disconnector() {
		this(new Obis("0.0.96.3.10.255"));
	}
	
	public Disconnector(Obis obis) {
		attOutputState = new LnDescriptor(70, obis, 2);
		attControlState = new LnDescriptor(70, obis, 3);
		attControlMode = new LnDescriptor(70, obis, 4);
		mtdDisconnect = new LnDescriptor(70, obis, 1);
		mtdReconnect = new LnDescriptor(70, obis, 2);
	}
	
	public OutputState getOutputState(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		if (isOutputConnected(dlms, phy)) {
			return OutputState.CONNECTED;
		} else {
			return OutputState.DISCONNECTED;
		}
	}
	
	public boolean isOutputConnected(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attOutputState);
		return (new CosemParser(attOutputState.getResponseData()).bool() == true);
	}
	
	public boolean isOutputDisconnected(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attOutputState);
		return (new CosemParser(attOutputState.getResponseData()).bool() == false);
	}
	
	public ControlState getControlState(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attControlState);
		return ControlState.fromValue(new CosemParser(attControlState.getResponseData()).enumeration());
	}

	public ControlMode getControlMode(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attControlMode);
		return ControlMode.fromValue(new CosemParser(attControlMode.getResponseData()).enumeration());
	}
	
	public void disconnect(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdDisconnect.setRequestData(new CosemSerializer().uint8(0).serialize());
		dlms.action(phy, mtdDisconnect);
	}
	
	public void reconnect(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdReconnect.setRequestData(new CosemSerializer().uint8(0).serialize());
		dlms.action(phy, mtdReconnect);
	}

}
