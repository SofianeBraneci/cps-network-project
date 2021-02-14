package com.network.common;

import com.network.interfaces.NodeAddressI;

public class RouteInfo {
	
	private NodeAddressI destination;
	private int numberOfHops;

	public RouteInfo(NodeAddressI destination, int numberOfHops) {
		this.destination = destination;
		this.numberOfHops = numberOfHops;
	}
	
	public NodeAddressI getDestination() {
		return destination;
	}
	
	public int getNumberOfHops() {
		return numberOfHops;
	}

}
