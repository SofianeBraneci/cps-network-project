package com.network.connectors;

import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class CommunicationConnector extends AbstractConnector implements CommunicationCI {

	public CommunicationConnector() {
		super();

	}
	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		// TODO Auto-generated method stub
		((CommunicationCI)offering).connect(address, communicationInboudURI);
		
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
		// TODO Auto-generated method stub
		((CommunicationCI)offering).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
		
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		((CommunicationCI)offering).transmitMessage(m);
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		// TODO Auto-generated method stub
		return ((CommunicationCI)offering).hasRouteFor(address);
	}

	@Override
	public void ping() throws Exception {
		((CommunicationCI)offering).ping();
	}

}
