package com.network.components.terminalnode;

import java.util.Set;

import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class TerminalNodeOutboundPort extends AbstractOutboundPort implements TerminalNodeServiceCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminalNodeOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, TerminalNodeServiceCI.class, owner);
		assert owner instanceof TerminalNodeComponent;
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		 return ((RegistrationCI) getConnector()).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange);
		
	}


}
