package yadi.dlms.classes;

import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;
import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.linklayer.LinkLayerException;

public interface CosemProxyExecuter {
	void execute(DlmsClient dlms, PhyLayer phy, byte[] data) throws DlmsException, PhyLayerException, LinkLayerException;
}
