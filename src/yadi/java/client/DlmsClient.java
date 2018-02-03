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

import yadi.java.client.cosem.Cosem;
import yadi.java.client.cosem.CosemParameters;
import yadi.java.client.cosem.LnDescriptor;
import yadi.java.client.linklayer.LinkLayer;
import yadi.java.client.linklayer.LinkLayerException;
import yadi.java.client.phylayer.PhyLayer;
import yadi.java.client.phylayer.PhyLayerException;

public class DlmsClient {
	
	private final Cosem cosem;
	private final LinkLayer link;
	
	/**
	 * Creates a new Dlms instance, a facade to facilitate the usage of the Cosem, LinkLayer and PhyLayer.
	 * @param link link layer object
	 * @param settings cosem settings
	 */
	public DlmsClient(LinkLayer link, CosemParameters params) {
		this.link = link;
		this.cosem = new Cosem(params);
	}
	
	/**
	 * Connects to the server
	 * @param phy PhyLayer to transmit / receive bytes
	 * @throws PhyLayerException
	 * @throws DlmsException 
	 * @throws LinkLayerException 
	 */
	public void connect(PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		cosem.reset();
		link.connect(phy);
		do {
			link.send(phy, cosem.connectionRequest());
		} while (!cosem.parseConnectionResponse(link.read(phy)));
	}

	
	/**
	 * 
	 * @param phy PhyLayer to transmit / receive bytes
	 * @throws PhyLayerException 
	 * @throws DlmsException 
	 * @throws LinkLayerException 
	 */
	public void disconnect(PhyLayer phy) throws PhyLayerException, DlmsException, LinkLayerException {
		link.disconnect(phy);
	}
	
	/**
	 * Performs a GET operation
	 * @param phy PhyLayer to transmit / receive bytes
	 * @param obj Long-name descriptor of the objected to be accessed
	 * @throws PhyLayerException 
	 * @throws DlmsException 
	 * @throws LinkLayerException 
	 */
	public void get(PhyLayer phy, LnDescriptor obj) throws PhyLayerException, DlmsException, LinkLayerException {
		do {
			link.send(phy, cosem.requestGet(obj));
		} while(!cosem.parseGetResponse(obj, link.read(phy)));
	}
	
	/**
	 * Performs a SET operation
	 * @param phy PhyLayer to transmit / receive bytes
	 * @param obj Long-name descriptor of the objected to be accessed
	 * @throws PhyLayerException 
	 * @throws DlmsException 
	 * @throws LinkLayerException 
	 */
	public void set(PhyLayer phy, LnDescriptor obj) throws PhyLayerException, DlmsException, LinkLayerException {
		do {
			link.send(phy, cosem.requestSet(obj));
		} while (!cosem.parseSetResponse(obj, link.read(phy)));
	}
	
	/**
	 * Performs a ACTION operation
	 * @param phy PhyLayer to transmit / receive bytes
	 * @param obj Long-name descriptor of the objected to be accessed
	 * @throws PhyLayerException 
	 * @throws DlmsException 
	 * @throws LinkLayerException 
	 */
	public void action(PhyLayer phy, LnDescriptor obj) throws PhyLayerException, DlmsException, LinkLayerException {
		do {
			link.send(phy, cosem.requestAction(obj));
		} while (!cosem.parseActionResponse(obj, link.read(phy)));
	}
}
