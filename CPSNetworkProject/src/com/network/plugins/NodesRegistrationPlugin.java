package com.network.plugins;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.RegistrationOutboundPort;
import com.network.connectors.RegistrationConnector;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.AbstractPlugin;

public class NodesRegistrationPlugin extends AbstractPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RegistrationOutboundPort registrationOutboundPort;

	public NodesRegistrationPlugin() throws Exception {

	

	}

	// WHEN DOING THE REGISTRATION, ONLY RETURN THE NODES THAT CAN DO ROUTING
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return registrationOutboundPort.registerTerminalNode(address, communicationInboundPortURI, initialPosition,
				initialRange);
	}

	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return registrationOutboundPort.registerAccessPoint(address, communicationInboundPortURI, initialPosition,
				initialRange, routingInboundPortURI);
	}

	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return registrationOutboundPort.registerRoutigNode(address, communicationInboundPortURI, initialPosition,
				initialRange, routingInboundPortURI);
	}

	public void unregister(NodeAddressI address) throws Exception {
		registrationOutboundPort.unregister(address);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
		
		addRequiredInterface(RegistrationCI.class);
		System.err.println(getOwner().getRequiredInterfaces().length);
		registrationOutboundPort = new RegistrationOutboundPort(getOwner());
		registrationOutboundPort.publishPort();
		
		System.err.println("REGISTRATION CI IS ADDED AS A REQUIRED INTERFACE FOR THE CURRENT COMPONENT");
		getOwner().doPortConnection(registrationOutboundPort.getPortURI(),
				RegisterRegistrationPlugin.REGISTER_INBOUND_PORT_URI, RegistrationConnector.class.getCanonicalName());
		System.out.println("A NODE IS CONNECTED TO THE REGISTER!");
	}
	
	@Override
	public void finalise() throws Exception {
		// TODO Auto-generated method stub
		super.finalise();
		getOwner().doPortDisconnection(registrationOutboundPort.getPortURI());
	}
	@Override
	public void uninstall() throws Exception {
		// TODO Auto-generated method stub
		super.uninstall();
		registrationOutboundPort.unpublishPort();
		registrationOutboundPort.destroyPort();
		removeRequiredInterface(RegistrationCI.class);
		
	}

}
