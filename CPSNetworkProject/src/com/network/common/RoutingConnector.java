package com.network.common;

import java.util.Set;

import com.network.interfaces.NodeAddressI;
import com.network.interfaces.RoutingCI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RoutingConnector extends AbstractConnector implements RoutingCI{

	public RoutingConnector() {
	}

	@Override
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		((RoutingCI)offering).updateRouting(address, routes);
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		((RoutingCI)offering).updateAccessPoint(neighbour, numberOfHops);
		
	}

}
