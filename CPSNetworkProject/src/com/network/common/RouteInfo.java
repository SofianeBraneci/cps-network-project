package com.network.common;

import com.network.interfaces.NodeAddressI;

/**
 * Class for Route Information
 * @author Softwarkers
 *
 */
public class RouteInfo {
	
	/** destination address */
	private NodeAddressI destination;
	/** number of hops to get to the destination */
	private int numberOfHops;

	/**
	 * Create and initialize a route info
	 * @param destination destination address
	 * @param numberOfHops number of hops to get to the destination
	 */
	public RouteInfo(NodeAddressI destination, int numberOfHops) {
		this.destination = destination;
		this.numberOfHops = numberOfHops;
	}
	
	/**
	 * @return destination address 
	 */
	public NodeAddressI getDestination() {
		return destination;
	}
	
	/**
	 * @return number of hops to get to the destination
	 */
	public int getNumberOfHops() {
		return numberOfHops;
	}

}
