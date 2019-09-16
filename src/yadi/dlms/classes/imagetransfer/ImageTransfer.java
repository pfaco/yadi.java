package yadi.dlms.classes.imagetransfer;

import yadi.dlms.DlmsClient;
import yadi.dlms.DlmsException;
import yadi.dlms.Obis;
import yadi.dlms.cosem.CosemParser;
import yadi.dlms.cosem.CosemSerializer;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class ImageTransfer {
	
	private static final int classId = 18; 
	
	private final LnDescriptor attImageBlockSize;
	private final LnDescriptor attImageTransferredBlockStatus;
	private final LnDescriptor attImageFirstNotTransferredBlockNumber;
	private final LnDescriptor attImageTransferEnabled;
	private final LnDescriptor attImageTransferStatus;
	private final LnDescriptor attImageToActiveInfo;
	private final LnDescriptor mtdImageTransferInitiate;
	private final LnDescriptor mtdImageBlockTransfer;
	private final LnDescriptor mtdImageVerify;
	private final LnDescriptor mtdImageActivate;
	
	public ImageTransfer() {
		this(new Obis("0.0.44.0.0.255"));
	}
	
	public ImageTransfer(Obis obis) {
		attImageBlockSize = new LnDescriptor(classId, obis, 2);
		attImageTransferredBlockStatus = new LnDescriptor(classId, obis, 3);
		attImageFirstNotTransferredBlockNumber = new LnDescriptor(classId, obis, 4);
		attImageTransferEnabled = new LnDescriptor(classId, obis, 5);
		attImageTransferStatus = new LnDescriptor(classId, obis, 6);
		attImageToActiveInfo = new LnDescriptor(classId, obis, 7);
		mtdImageTransferInitiate = new LnDescriptor(classId, obis, 1);
		mtdImageBlockTransfer = new LnDescriptor(classId, obis, 2);
		mtdImageVerify = new LnDescriptor(classId, obis, 3);
		mtdImageActivate = new LnDescriptor(classId, obis, 4);
	}
	
	public int getBlockSize(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attImageBlockSize);
		return CosemParser.make(attImageBlockSize.getResponseData()).integer();
	}
	
	public boolean[] getTransferredBlockStatus(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attImageTransferredBlockStatus);
		return CosemParser.make(attImageTransferredBlockStatus.getResponseData()).bitstring();
	}
	
	public int getFirstNotTransferredBlockNumber(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attImageFirstNotTransferredBlockNumber);
		return CosemParser.make(attImageFirstNotTransferredBlockNumber.getResponseData()).integer();
	}
	
	public boolean isTransferEnabled(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attImageTransferEnabled);
		return CosemParser.make(attImageTransferEnabled.getResponseData()).bool();
	}
	
	public ImageTransferStatus getTransferStatus(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attImageTransferStatus);
		return ImageTransferStatus.fromValue(CosemParser.make(attImageTransferStatus.getResponseData()).integer());
	}
	
	public ImageInformation getImageToActiveInformation(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.get(phy, attImageToActiveInfo);
		CosemParser parser = CosemParser.make(attImageToActiveInfo.getResponseData());
		parser.verifyArraySize(1);
		parser.verifyStructureSize(3);
		return new ImageInformation(parser.uint32(), new String(parser.octetString()), parser.octetString());
	}

	public void initiateTransfer(DlmsClient dlms, PhyLayer phy, byte[] imageIdentifier, int imageSize) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdImageTransferInitiate.setRequestData(
			new CosemSerializer()
			.structure(2)
			.octetString(imageIdentifier)
			.uint32(imageSize)
			.serialize()
		);
		dlms.action(phy, mtdImageTransferInitiate);
	}
	
	public void blockTransfer(DlmsClient dlms, PhyLayer phy, int blockNum, byte[] blockData) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdImageBlockTransfer.setRequestData(
			new CosemSerializer()
			.structure(2)
			.uint32(blockNum)
			.octetString(blockData)
			.serialize()
		);
		dlms.action(phy, mtdImageBlockTransfer);
	}
	
	public void verifyImage(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdImageVerify.setRequestData(
			new CosemSerializer()
			.int8(0)
			.serialize()
		);
		dlms.action(phy, mtdImageVerify);
	}
	
	public void activateImage(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		mtdImageActivate.setRequestData(
			new CosemSerializer()
			.int8(0)
			.serialize()
		);
		dlms.action(phy, mtdImageActivate);
	}
	
	public void executeImageTransfer(DlmsClient dlms, PhyLayer phy, Image image) throws PhyLayerException, DlmsException, LinkLayerException, ImageTransferException {
		executeImageTransfer(dlms, phy, image, (p) -> {});
	}
	
	public void executeImageTransfer(DlmsClient dlms, PhyLayer phy, Image image, ImageTransferObserver observer) throws PhyLayerException, DlmsException, LinkLayerException, ImageTransferException {
		if (!isTransferEnabled(dlms, phy)) {
			throw new ImageTransferException("Image transfer is disabled.");
		}
		
		image.setBlockSize(getBlockSize(dlms, phy));
		int numberOfBlocks = image.getNumberOfBlocks();
		initiateTransfer(dlms, phy, image.getIdentifier(), image.getSize());
		
		for (int i = 0; i < numberOfBlocks; ++i) {
			blockTransfer(dlms, phy, i, image.getBlock(i));
			observer.updateProgress((double)i/numberOfBlocks);
		}
		
		int firstNotTransferredBlockNumber = getFirstNotTransferredBlockNumber(dlms, phy);
		
		while (firstNotTransferredBlockNumber < image.getNumberOfBlocks()) {
			blockTransfer(dlms, phy, firstNotTransferredBlockNumber, image.getBlock(firstNotTransferredBlockNumber));
			firstNotTransferredBlockNumber = getFirstNotTransferredBlockNumber(dlms, phy);
		}
		
		verifyImage(dlms, phy);
		activateImage(dlms, phy);
	}

}
