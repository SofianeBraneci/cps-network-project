package com.network.plugins;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.RegistrationOutboundPort;
import com.network.connectors.RegistrationConnector;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.AbstractPlugin;

/**
 * Class for node registration plugins
 * @author Softwarkers
 *
 */
public class NodesRegistrationPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;
	
	/** Registration outbound port */
	private RegistrationOutboundPort registrationOutboundPort;

	public NodesRegistrationPlugin() throws Exception {
		super();
	}

	/**
	 * Register a terminal node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @return Set of the new node neighbors connection info that can route
	 * @throws Exception
	 */
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return registrationOutboundPort.registerTerminalNode(address, communicationInboundPortURI, initialPosition,
				initialRange);
	}
	
	/**
	 * Register an access point
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @param routingInboundPortURI the node routing inbound port
	 * @return Set of the new nod neighbors connection info that can route
	 * @throws Exception
	 */
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return registrationOutboundPort.registerAccessPoint(address, communicationInboundPortURI, initialPosition,
				initialRange, routingInboundPortURI);
	}
	
	/**
	 * Register a routing node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @return Set of the new node neighbors connection info that can route
	 * @throws Exception
	 */
	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return registrationOutboundPort.registerRoutigNode(address, communicationInboundPortURI, initialPosition,
				initialRange, routingInboundPortURI);
	}
	
	/**
	 * Unregister a node
	 * @param address address of the node to unregister
	 * @throws Exception
	 */
	public void unregister(NodeAddressI address) throws Exception {
		registrationOutboundPort.unregister(address);
	}
	
	/** 
	 * @return registration outbound port
	 */
	public String getRegistrationOutboundURI() {
		
		try {
			return registrationOutboundPort.getPortURI();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void initialise() throws Exception {
		super.initialise();
		System.err.println("NODE REGISTRATION PLUGIN IS LAUNCHED");
		addRequiredInterface(RegistrationCI.class);
		registrationOutboundPort = new RegistrationOutboundPort(getOwner());
		registrationOutboundPort.publishPort();
		
		getOwner().doPortConnection(registrationOutboundPort.getPortURI(),
				RegisterRegistrationPlugin.REGISTER_INBOUND_PORT_URI, RegistrationConnector.class.getCanonicalName());
		
	}
	
	@Override
	public void finalise() throws Exception {
		super.finalise();
		getOwner().doPortDisconnection(registrationOutboundPort.getPortURI());
	}
	@Override
	public void uninstall() throws Exception {
		super.uninstall();
		registrationOutboundPort.unpublishPort();
		registrationOutboundPort.destroyPort();
		removeRequiredInterface(RegistrationCI.class);
	}

}
