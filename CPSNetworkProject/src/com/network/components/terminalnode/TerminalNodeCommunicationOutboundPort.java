package com.network.components.terminalnode;

import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class TerminalNodeCommunicationOutboundPort extends AbstractOutboundPort implements CommunicationCI {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminalNodeCommunicationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		((CommunicationCI)getConnector()).connect(address, communicationInboudURI);
		
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
			((CommunicationCI) getConnector()).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
	}

	@Override
	public void transmitMessag(MessageI m) throws Exception {
		((CommunicationCI)getConnector()).transmitMessag(m);
	}

	@Override
	public boolean hasRouteFor(NodeAddressI address) throws Exception {
		return ((CommunicationCI)getConnector()).hasRouteFor(address);
	}

	@Override
	public void ping() throws Exception {
		((CommunicationCI)getConnector()).ping();
	}

}
