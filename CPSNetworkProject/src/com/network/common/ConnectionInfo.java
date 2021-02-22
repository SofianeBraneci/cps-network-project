package com.network.common;

import com.network.interfaces.NodeAddressI;

public class ConnectionInfo {
	
	private NodeAddressI address;
	private String communicationInboundPortURI, routingInboundPortURI;
	private boolean isRoutning;

	public ConnectionInfo(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI, boolean isRoutning) {
		this.address = address;
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.routingInboundPortURI = routingInboundPortURI;
		this.isRoutning = isRoutning;
	}

	
	public NodeAddressI getAddress() {
		// TODO Auto-generated method stub
		return address;
	}

	public String getCommunicationInboudPort() {
		// TODO Auto-generated method stub
		return communicationInboundPortURI;
	}

	public boolean isRouting() {
		// TODO Auto-generated method stub
		return isRoutning;
	}

	public String getRoutingInboundPortURI() {
		// TODO Auto-generated method stub
		return routingInboundPortURI;
	}

	
	
}
