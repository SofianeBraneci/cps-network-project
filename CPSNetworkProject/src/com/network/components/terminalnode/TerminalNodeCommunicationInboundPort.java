package com.network.components.terminalnode;

import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class TerminalNodeCommunicationInboundPort extends AbstractInboundPort implements CommunicationCI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminalNodeCommunicationInboundPort(ComponentI owner) throws Exception{
		super(CommunicationCI.class, owner);
	}
	
	public TerminalNodeCommunicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
		assert owner instanceof TerminalNodeComponenet;
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		getOwner().handleRequest(c -> {
			((TerminalNodeComponenet) c).connect(address, communicationInboudURI);
			return null;
		});
		

	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {

		getOwner().handleRequest(c -> {
			((TerminalNodeComponenet) c).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
			return null;
		});

	}

	@Override
	public void transmitMessag(MessageI m) throws Exception {
		getOwner().handleRequest(c -> {
			((TerminalNodeComponenet) c).transmitMessag(m);
			return null;
		});

	}

	@Override
	public int hasRouteFor(NodeAddressI address) throws Exception {
		return getOwner().handleRequest(c -> ((TerminalNodeComponenet) c).hasRouteFor(address));
	}

	@Override
	public void ping() throws Exception {
		getOwner().handleRequest(c -> {
			((CommunicationCI) c).ping();
			return null;
		});

	}

}
