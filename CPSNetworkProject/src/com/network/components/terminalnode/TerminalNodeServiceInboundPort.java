package com.network.components.terminalnode;

import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class TerminalNodeServiceInboundPort extends AbstractInboundPort implements CommunicationCI {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminalNodeServiceInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, CommunicationCI.class, owner);
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
		
	}

	@Override
	public void transmitMessag(MessageI m) {
		
	}

	@Override
	public boolean hasRouteFor(NodeAddressI address) {
		return false;
	}

	@Override
	public void ping() {
		
	}

}
