package com.network.connectors;

import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class TerminalNodeAccessPointCommunicationConnector extends AbstractConnector implements CommunicationCI{

	public TerminalNodeAccessPointCommunicationConnector() {
		// TODO Auto-generated constructor stub
		super();
		System.err.println("INiit");
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		// TODO Auto-generated method stub
		System.err.println("IN CONNECT");
		((CommunicationCI)this.offering).connect(address, communicationInboudURI);
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
		// TODO Auto-generated method stub
		((CommunicationCI)this.offering).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
		
	}

	@Override
	public void transmitMessag(MessageI m) throws Exception {
		// TODO Auto-generated method stub
		((CommunicationCI)this.offering).transmitMessag(m);
		
	}

	@Override
	public boolean hasRouteFor(NodeAddressI address) throws Exception {
		// TODO Auto-generated method stub
		return ((CommunicationCI)this.offering).hasRouteFor(address);
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		((CommunicationCI)this.offering).ping();
		
	}

}
