package com.network.components.terminalnode;

import java.util.Set;

import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface TerminalNodeServiceCI  extends RequiredCI {
	
	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, 
			String communicationInboundPortURI, 
			PositionI initialPosition, 
			double initialRange) throws Exception;

}
