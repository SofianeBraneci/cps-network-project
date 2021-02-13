package com.network.connectors;

import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class CommunicationCIConnector extends AbstractConnector implements CommunicationCI{

	public CommunicationCIConnector() {
		super();
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		((CommunicationCI)this.offering).connect(address, communicationInboudURI);
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transmitMessag(MessageI m) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasRouteFor(NodeAddressI address) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
