package com.network.components.accesspointnode;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class AccessPointRegistrationOutboundPort extends AbstractOutboundPort implements RegistrationCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccessPointRegistrationOutboundPort(String uri,ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
	}


	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return null;
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return ((RegistrationCI)getConnector()).registerAccessPoint(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
	}

	@Override
	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String commnicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return null; //).registerTerminalNode(address, commnicationInboundPortURI, initialPosition, initialRange);
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		((RegistrationCI)getConnector()).unregister(address);
	}



}
