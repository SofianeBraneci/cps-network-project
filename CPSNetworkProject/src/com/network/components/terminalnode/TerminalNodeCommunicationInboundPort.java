package com.network.components.terminalnode;

import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class TerminalNodeCommunicationInboundPort extends AbstractInboundPort implements CommunicationCI{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminalNodeCommunicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
		assert owner instanceof TerminalNodeComponenet;
	}


	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		getOwner().handleRequest(c->{
			((CommunicationCI)c).connect(address, communicationInboudURI);
			return null;
		});
		
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
