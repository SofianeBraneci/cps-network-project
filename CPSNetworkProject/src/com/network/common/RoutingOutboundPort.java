package com.network.common;

import java.util.Set;

import com.network.interfaces.NodeAddressI;
import com.network.interfaces.RoutingCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * Class for routing outbound ports
 * 
 * @author Softwarkers
 * 
 */
public class RoutingOutboundPort extends AbstractOutboundPort implements RoutingCI{

	private static final long serialVersionUID = 1L;

	/**
	 * create and initialize routing outbound ports.
	 * @param owner component that owns this port.
	 * @throws Exception
	 */
	public RoutingOutboundPort(ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);
	}

	@Override
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		((RoutingCI)getConnector()).updateRouting(address, routes);
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		((RoutingCI)getConnector()).updateAccessPoint(neighbour, numberOfHops);
	}

}
