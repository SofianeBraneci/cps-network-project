package com.network.common;

import java.util.Set;

import com.network.interfaces.NodeAddressI;
import com.network.interfaces.RoutingCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RoutingOutboundPort extends AbstractOutboundPort implements RoutingCI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoutingOutboundPort(ComponentI owner) throws Exception {
		// TODO Auto-generated constructor stub
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
