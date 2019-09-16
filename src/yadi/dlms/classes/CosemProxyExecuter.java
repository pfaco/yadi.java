package yadi.dlms.classes;

import yadi.dlms.phylayer.PhyLayerException;
import yadi.dlms.DlmsException;
import yadi.dlms.linklayer.LinkLayerException;

public interface CosemProxyExecuter {
	void execute(byte[] data) throws DlmsException, PhyLayerException, LinkLayerException;
}
