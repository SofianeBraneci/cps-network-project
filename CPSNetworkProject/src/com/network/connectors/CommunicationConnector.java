package com.network.connectors;

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
		System.err.println("normal connection");
		((CommunicationCI)offering).connect(address, communicationInboudURI);
		
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
		// TODO Auto-generated method stub
		System.err.println("connection routing ");
		((CommunicationCI)offering).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
		
	}

	@Override
	public void transmitMessag(MessageI m) throws Exception {
		((CommunicationCI)offering).transmitMessag(m);
	}

	@Override
	public int hasRouteFor(NodeAddressI address) throws Exception {
		// TODO Auto-generated method stub
		return ((CommunicationCI)offering).hasRouteFor(address);
	}

	@Override
	public void ping() throws Exception {
		((CommunicationCI)offering).ping();
	}

}
