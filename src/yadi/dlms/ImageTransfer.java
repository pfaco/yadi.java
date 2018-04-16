package yadi.dlms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import yadi.dlms.ImageTransferException.ImageTransferExceptionReason;
import yadi.dlms.cosem.CosemClasses;
import yadi.dlms.cosem.LnDescriptor;
import yadi.dlms.linklayer.LinkLayerException;
import yadi.dlms.phylayer.PhyLayer;
import yadi.dlms.phylayer.PhyLayerException;

public class ImageTransfer {

	/**
	 * Class ID
	 */
	private static final int CLASS_ID = CosemClasses.IMAGE_TRANSFER.id;
	
	/**
	 * Attributes
	 */
	private static final int attBlockSize = 2;
	private static final int attTransferredBlockStatus = 3;
	private static final int attFirstNotTransferredBlockNumber = 4;
	private static final int attTransferEnabled = 5;
	private static final int attTransferStatus = 6;
	private static final int attImageToActiveInfo = 7;
	
	/**
	 * Methods
	 */
	private static final int mtdTransferInitiate = 1;
	private static final int mtdImageBlockTransfer = 2;
	private static final int mtdImageVerify = 3;
	private static final int mtdImageActivate = 4;
	
	private final Obis obis;
	
	public ImageTransfer() {
		this(new Obis("0.0.44.0.0.255"));
	}
	
	public ImageTransfer(Obis obis) {
		this.obis = obis;
	}
	
	public boolean isTransferEnabled(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		LnDescriptor att = createDesc(attTransferEnabled);
		dlms.get(phy, att);
		return DlmsParser.getBoolean(att.getResponseData());
	}
	
	public int getImageBlockSize(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		LnDescriptor att = createDesc(attBlockSize);
		dlms.get(phy, att);
		return DlmsParser.getInteger(att.getResponseData());
	}
	
	public void initiateImageTransfer(DlmsClient dlms, PhyLayer phy, ImageInformation imageInfo) throws PhyLayerException, DlmsException, LinkLayerException, ImageTransferException {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(DlmsType.STRUCTURE.tag);
			stream.write(2); // size
			stream.write(DlmsType.OCTET_STRING.tag);
			stream.write(imageInfo.getIdentifier().length);
			stream.write(imageInfo.getIdentifier());
			stream.write(DlmsType.UINT32.tag);
			stream.write(ByteBuffer.allocate(4).putInt(imageInfo.getImage().length).array());
			dlms.action(phy, createDesc(mtdTransferInitiate, stream.toByteArray()));
		} catch (IOException e) {
			throw new ImageTransferException(ImageTransferExceptionReason.INTERNAL_ERROR);
		}
		
	}
	
	public void transferBlocks(DlmsClient dlms, PhyLayer phy, ImageInformation imageInfo) throws PhyLayerException, DlmsException, LinkLayerException, ImageTransferException {
		int offset = 0;
		int nblock = 1;
		LnDescriptor att = createDesc(mtdImageBlockTransfer);
		while (offset < imageInfo.getImage().length) {
			att.setRequestData(getTransferBlockData(nblock, imageInfo));
			dlms.action(phy, att);
			nblock++;
			offset += imageInfo.getBlockSize();
		}
	}
	
	public void checkCompleteness(DlmsClient dlms, PhyLayer phy, ImageInformation info) {
		//TODO
	}
	
	public void verifyImage(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.action(phy, createDesc(mtdImageVerify, new byte[] {0x0F, 0x00}));
	}
	
	public boolean checkImageInformation(DlmsClient dlms, PhyLayer phy, ImageInformation info) throws PhyLayerException, DlmsException, LinkLayerException {
		return false;
	}
	
	public void activateImage(DlmsClient dlms, PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		dlms.action(phy, createDesc(mtdImageActivate, new byte[] {0x0F, 0x00}));
	}
	
	public void execute(DlmsClient dlms, PhyLayer phy, ImageInformation imageInfo) throws PhyLayerException, DlmsException, LinkLayerException, ImageTransferException {
	
		/// Precondition: image transfer must be enabled
		if (!isTransferEnabled(dlms, phy)) {
			throw new ImageTransferException(ImageTransferExceptionReason.TRANSFER_DISABLED);
		}
		
		/// Step 1: if image block size is unknown, get block size
		if (imageInfo.getBlockSize() == 0) {
			imageInfo.setBlockSize(getImageBlockSize(dlms, phy));
		}
		
		/// Step 2: Initiate image transfer
		initiateImageTransfer(dlms, phy, imageInfo);
		
		/// Step 3: Transfer image blocks
		transferBlocks(dlms, phy, imageInfo);
		
		/// Step 4: Check completeness of the image
		checkCompleteness(dlms, phy, imageInfo);
		
		/// Step 5: Verifies the image
		verifyImage(dlms, phy);
		
		/// Step 6: Check information of image to activate
		if (!checkImageInformation(dlms, phy, imageInfo)) {
			throw new ImageTransferException(ImageTransferExceptionReason.INVALID_IMAGE_TO_ACTIVATE);
		}
		
		/// Step 7: Activates image
		activateImage(dlms, phy);
	}
	
	private byte[] getTransferBlockData(int nblock, ImageInformation imageInfo) throws ImageTransferException {
		try {
			int len = imageInfo.getBlockSize();
			byte[] image = imageInfo.getImage();
			int offset = (nblock-1)*len;
			if ((offset + len) > image.length) {
				len = image.length - offset;
			}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(DlmsType.STRUCTURE.tag);
			stream.write(2); // size
			stream.write(DlmsType.UINT32.tag);
			stream.write(ByteBuffer.allocate(4).putInt(nblock).array());
			stream.write(DlmsType.OCTET_STRING.tag);
			if (len >= 0x80 && len <= 0xFF) {
				stream.write(0x81);
			}
			else if (len > 0xFF) {
				throw new ImageTransferException(ImageTransferExceptionReason.INVALID_BLOCK_SIZE);
			}
			stream.write((byte)len);
			for (int i = 0; i < len; ++i) {
				stream.write(image[offset + i]);
			}
			return stream.toByteArray();
		} catch (IOException e) {
			throw new ImageTransferException(ImageTransferExceptionReason.INTERNAL_ERROR);
		}
	}

	private LnDescriptor createDesc(int index) {
		return new LnDescriptor(CLASS_ID, obis, index);
	}
	
	private LnDescriptor createDesc(int index, byte[] data) {
		LnDescriptor att = createDesc(index);
		att.setRequestData(data);
		return att;
	}
}
