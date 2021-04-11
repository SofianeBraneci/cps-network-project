package com.network.components.register;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * Class for register service inbound ports
 * 
 * @author Softwarkers
 *
 */
public class RegisterServiceInboundPort extends AbstractInboundPort implements RegistrationCI {

	private static final long serialVersionUID = 1L;

	/**
	 * create and initialize register service communication inbound ports.
	 * 
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public RegisterServiceInboundPort(ComponentI owner) throws Exception {
		super(RegistrationCI.class, owner);
		assert owner instanceof RegisterComponent;
	}

	/**
	 * create and initialize register service communication inbound ports.
	 * 
	 * @param uri   unique identifier of the port.
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public RegisterServiceInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
		assert owner instanceof RegisterComponent;
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return getOwner().handleRequest(RegisterComponent.REGISTETR_EXECUTOR_SERVICE_URI, c -> ((RegisterComponent) c)
				.registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange));
//		return getOwner().handleRequest(c->((RegisterComponent)c).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange));

	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return getOwner().handleRequest(RegisterComponent.REGISTETR_EXECUTOR_SERVICE_URI,
				c -> ((RegisterComponent) c).registerAccessPoint(address, communicationInboundPortURI, initialPosition,
						initialRange, routingInboundPortURI));
//		return getOwner().handleRequest(c->((RegisterComponent)c).registerAccessPoint(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI));
	}

	@Override
	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String commnicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return getOwner().handleRequest(RegisterComponent.REGISTETR_EXECUTOR_SERVICE_URI,
				c -> ((RegisterComponent) c).registerRoutingNode(address, commnicationInboundPortURI, initialPosition,
						initialRange, routingInboundPortURI));
//		return getOwner().handleRequest(c->((RegisterComponent)c).registerRoutingNode(address, commnicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI));
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		getOwner().handleRequest(c -> {
			((RegisterComponent) c).unregister(address);
			return null;
		});
	}
}
