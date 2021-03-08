package com.network.withplugin.ports;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RegistrationOutboundPortPlugin extends AbstractOutboundPort implements RegistrationCI{
	private String pluginURI;

	public RegistrationOutboundPortPlugin(ComponentI owner, String pluginURI) throws Exception {
		super(RegistrationCI.class, owner);
		this.pluginURI = pluginURI;
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String commnicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
