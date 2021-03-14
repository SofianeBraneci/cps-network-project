package com.network.interfaces;

import java.util.Set;

import com.network.common.RouteInfo;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
/**
 * Routing components interface
 * @author Softwarkers
 *
 */
public interface RoutingCI  extends OfferedCI, RequiredCI{
	/**
	 * Update the neighbor's route info ports map
	 * @param address address of the routing node to add
	 * @param routes routes info
	 * @exception Exception
	 */
	void updateRouting(NodeAddressI address, Set<RouteInfo> routes ) throws Exception;
	/**
	 * Update the neighbor's access points number of hops map
	 * @param neighbour address of the routing node to add
	 * @param numberOfHops the new number of hops
	 * @exception Exception
	 */
	void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception;
}
