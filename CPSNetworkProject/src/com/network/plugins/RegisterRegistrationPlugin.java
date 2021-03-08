package com.network.plugins;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;
import com.network.withplugin.components.RegisterComponent;
import com.network.withplugin.ports.RegisterServiceInboundPortPlugin;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class RegisterRegistrationPlugin extends AbstractPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1;

	private RegisterServiceInboundPortPlugin registerServiceInboundPortPlugin;
	public static String REGISTER_INBOUND_PORT_URI;

	public RegisterRegistrationPlugin() throws Exception {
		System.err.println(getOwner());

	}

	// WHEN DOING THE REGISTRATION, ONLY RETURN THE NODES THAT CAN DO ROUTING
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) {
		return ((RegisterComponent) getOwner()).registerTerminalNode(address, communicationInboundPortURI,
				initialPosition, initialRange);
	}

	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		return ((RegisterComponent) getOwner()).registerAccessPoint(address, communicationInboundPortURI,
				initialPosition, initialRange, routingInboundPortURI);
	}

	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		return ((RegisterComponent) getOwner()).registerRoutigNode(address, communicationInboundPortURI,
				initialPosition, initialRange, routingInboundPortURI);
	}

	public void unregister(NodeAddressI address) {
		((RegisterComponent) getOwner()).unregister(address);

	}

	// life cycle
	
	@Override
	public void installOn(ComponentI owner) throws Exception {
		// TODO Auto-generated method stub
		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
		System.out.println("REGISTRATION PLUGIN IS LAUNCHED !");
		addOfferedInterface(RegistrationCI.class);
		System.err.println("REGISTRATION CI IS ADDED AS AN OFFERED INTERFACE FOR THE REGISTER COMPONENT");

		registerServiceInboundPortPlugin = new RegisterServiceInboundPortPlugin(this.getOwner(), this.getPluginURI());
		registerServiceInboundPortPlugin.publishPort();
		REGISTER_INBOUND_PORT_URI = registerServiceInboundPortPlugin.getPortURI();

		
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
		registerServiceInboundPortPlugin.unpublishPort();
	}

	@Override
	public void uninstall() throws Exception {
	
		
		super.uninstall();

	}

}
