package com.network.components.routingnode;

import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * Class for routing nodes communication inbound ports
 * 
 * @author Softwarkers
 *
 */
public class RoutingNodeCommunicationInboundPort extends AbstractInboundPort implements CommunicationCI {

	private static final long serialVersionUID = 1L;

	/**
	 * create and initialize routing node communication inbound ports.
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public RoutingNodeCommunicationInboundPort (ComponentI owner) throws Exception{
		super(CommunicationCI.class, owner);
	}
	
	/**
	 * create and initialize routing node communication inbound ports.
	 * @param uri unique identifier of the port.
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public RoutingNodeCommunicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
		assert owner instanceof RoutingNodeComponent;
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		getOwner().handleRequest(c -> {
			((RoutingNodeComponent) c).connect(address, communicationInboudURI);
			return null;
		});
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
		getOwner().handleRequest(c -> {
			((RoutingNodeComponent) c).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
			return null;
		});
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		getOwner().handleRequest(c -> {
			((RoutingNodeComponent) c).transmitMessage(m);
			return null;
		});
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		return getOwner().handleRequest(c -> ((RoutingNodeComponent) c).hasRouteFor(address));
	}

	@Override
	public void ping() throws Exception {
		getOwner().handleRequest(c -> {
			((RoutingNodeComponent) c).ping();
			return null;
		});

	}

}
