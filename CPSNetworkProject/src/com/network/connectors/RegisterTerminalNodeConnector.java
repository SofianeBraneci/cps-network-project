package com.network.connectors;

import java.util.Set;

import com.network.components.terminalnode.TerminalNodeServiceCI;
import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RegisterTerminalNodeConnector extends AbstractConnector implements TerminalNodeServiceCI {

	public RegisterTerminalNodeConnector() {
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		// TODO Auto-generated method stub
		return ((RegistrationCI) this.offering).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange);
	}

}
