package com.network.components.routingnode;

import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RoutingNodeCommunicationInboundPort extends AbstractOutboundPort implements CommunicationCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoutingNodeCommunicationInboundPort (ComponentI owner) throws Exception{
		super(CommunicationCI.class, owner);
	}
	
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
	public void transmitMessag(MessageI m) throws Exception {
		getOwner().handleRequest(c -> {
			((RoutingNodeComponent) c).transmitMessag(m);
			return null;
		});

	}

	@Override
	public boolean hasRouteFor(NodeAddressI address) throws Exception {
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
