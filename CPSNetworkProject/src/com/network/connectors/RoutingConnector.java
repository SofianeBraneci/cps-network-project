package com.network.connectors;

import java.util.Set;

import com.network.common.RouteInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.RoutingCI;

import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * Class for a routing connector
 * @author Softwarkers
 */
public class RoutingConnector extends AbstractConnector implements RoutingCI {

	public RoutingConnector() {
		super();
	}

	@Override
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		((RoutingCI) offering).updateRouting(address, routes);
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		((RoutingCI) offering).updateAccessPoint(neighbour, numberOfHops);

	}

}
