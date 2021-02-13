package com.network.common;

import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

//TODO Auto-generated constructor stub
public class NodeComponentInformationWrapper {
	private String communicationInboundPortURI;
	private PositionI initialPosition;
	private String routingInboundPortURI;
	
	
	public NodeComponentInformationWrapper(String communicationInboundPortURI,
			PositionI initialPosition, String routingInboundPortURI) {
		super();
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.initialPosition = initialPosition;
		this.routingInboundPortURI = routingInboundPortURI;
	}


	public NodeComponentInformationWrapper(String communicationInboundPortURI,
			PositionI initialPosition) {
		super();
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.initialPosition = initialPosition;
		this.routingInboundPortURI = null;
	}
	
	public PositionI getInitialPosition() {
		return initialPosition;
	}

	public static ConnectionInfo getConnectionInfo(NodeAddressI address, NodeComponentInformationWrapper wrapper) {
		return new ConnectionInfo(address, wrapper.communicationInboundPortURI,
				wrapper.routingInboundPortURI == null ? null : wrapper.routingInboundPortURI, 
				wrapper.initialPosition, wrapper.routingInboundPortURI == null? false : true);
	}
}
