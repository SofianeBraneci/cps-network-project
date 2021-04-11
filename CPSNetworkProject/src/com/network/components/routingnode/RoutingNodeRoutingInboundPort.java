package com.network.components.routingnode;

import java.util.Set;

import com.network.common.RouteInfo;
import com.network.interfaces.NodeAddressI;

import com.network.interfaces.RoutingCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * Class for routing nodes routing inbound ports
 * 
 * @author Softwarkers
 */

public class RoutingNodeRoutingInboundPort extends AbstractInboundPort implements RoutingCI {

	private static final long serialVersionUID = 1L;

	/**
	 * create and initialize routing inbound ports for routing nodes.
	 * 
	 * @param owner access point that owns this port
	 * @exception Exception
	 */
	public RoutingNodeRoutingInboundPort(ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);
	}

	/**
	 * create and initialize routing inbound ports for routing nodes.
	 * 
	 * @param uri   unique identifier of the port.
	 * @param owner access point that owns this port
	 * @exception Exception
	 */
	public RoutingNodeRoutingInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RoutingCI.class, owner);
	}

	@Override
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		getOwner().handleRequest(RoutingNodeComponent.ROUTING_NODE_ROUTING_EXECUTOR_SERVICE_URI, c -> {
			((RoutingNodeComponent) c).updateRouting(address, routes);
			return null;
		});
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		getOwner().handleRequest(RoutingNodeComponent.ROUTING_NODE_ROUTING_EXECUTOR_SERVICE_URI, c -> {
			((RoutingNodeComponent) c).updateAccessPoint(neighbour, numberOfHops);
			return null;
		});
	}
}
