package com.network.interfaces;

import java.util.Set;

public interface RoutingCI {
	
	void updateRouting(NodeAddressI address, Set<RouteInfo> routes ) throws Exception;
	void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception;
}
