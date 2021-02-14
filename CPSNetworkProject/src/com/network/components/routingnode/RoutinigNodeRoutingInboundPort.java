package com.network.components.routingnode;

import java.util.Set;

import com.network.common.RouteInfo;
import com.network.interfaces.NodeAddressI;

import com.network.interfaces.RoutingCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RoutinigNodeRoutingInboundPort extends AbstractInboundPort implements RoutingCI{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoutinigNodeRoutingInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RoutingCI.class, owner);
	}


	
	@Override
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		getOwner().handleRequest(c->{
			((RoutingCI)c).updateRouting(address, routes);
			return null;
		});
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		getOwner().handleRequest(c->{
			((RoutingCI)c).updateAccessPoint(neighbour, numberOfHops);
			return null;
		});
		
	}



}
