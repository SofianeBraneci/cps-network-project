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

/**
 * Class for register registration plugin
 * @author Softwarkers
 *
 */
public class RegisterRegistrationPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1;
	/** Register service inbound port */
	private RegisterServiceInboundPortPlugin registerServiceInboundPortPlugin;
	/** Unique register inbound port */
	public static String REGISTER_INBOUND_PORT_URI;

	/**
	 * Create a register registration plugin
	 * @throws Exception
	 */
	public RegisterRegistrationPlugin() throws Exception {
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
		return ((RegisterComponent) getOwner()).registerTerminalNode(address, communicationInboundPortURI,
				initialPosition, initialRange);
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
		return ((RegisterComponent) getOwner()).registerAccessPoint(address, communicationInboundPortURI,
				initialPosition, initialRange, routingInboundPortURI);
	}
	
	/**
	 * Register a routing node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @param routingInboundPortURI the node routing inbound port
	 * @return Set of the new node neighbors connection info that can route
	 * @throws Exception
	 */
	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return ((RegisterComponent) getOwner()).registerRoutigNode(address, communicationInboundPortURI,
				initialPosition, initialRange, routingInboundPortURI);
	}
	
	/**
	 * Unregister a node
	 * @param address address of the node to unregister
	 * @throws Exception
	 */
	public void unregister(NodeAddressI address) throws Exception {
		((RegisterComponent) getOwner()).unregister(address);

	}

	// life cycle
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
		addOfferedInterface(RegistrationCI.class);

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
