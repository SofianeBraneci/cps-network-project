package com.network.common;

import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

public class ConnectionInfo implements com.network.interfaces.ConnectionInfo {
	
	private NodeAddressI address;
	private String communicationInboundPortURI, routingInboundPortURI;
	private PositionI position;
	private boolean isRoutning;

	public ConnectionInfo(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI,
			PositionI position, boolean isRoutning) {
		super();
		this.address = address;
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.routingInboundPortURI = routingInboundPortURI;
		this.position = position;
		this.isRoutning = isRoutning;
	}

	@Override
	public NodeAddressI getAddress() {
		// TODO Auto-generated method stub
		return address;
	}

	@Override
	public String getCommunicationInboudPort() {
		// TODO Auto-generated method stub
		return communicationInboundPortURI;
	}

	@Override
	public boolean isRouting() {
		// TODO Auto-generated method stub
		return isRoutning;
	}

	@Override
	public String getRoutingInboundPortURI() {
		// TODO Auto-generated method stub
		return routingInboundPortURI;
	}

	@Override
	public PositionI getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	
}
