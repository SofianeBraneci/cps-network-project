package com.network.interfaces;

import java.util.Set;

import com.network.common.RouteInfo;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RoutingCI  extends OfferedCI, RequiredCI{
	
	void updateRouting(NodeAddressI address, Set<RouteInfo> routes ) throws Exception;
	void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception;
}
