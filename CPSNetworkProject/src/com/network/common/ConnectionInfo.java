package com.network.common;

import com.network.interfaces.NodeAddressI;

/**
 * Class for Connection information
 * 
 * @author Softwarkers
 * 
 */
public class ConnectionInfo {
	
	/* The component address*/
	private NodeAddressI address;
	/* The component communication inbound port uri*/
	private String communicationInboundPortURI;
	/* The component routing inbound port uri*/
	private String routingInboundPortURI;
	/* True if the component is routing, False else*/
	private boolean isRouting;
	/**
	 * Create and initialize a connection info object
	 * @param address The component address
	 * @param communicationInboundPortURI communication inbound port uri
	 * @param routingInboundPortURI routing inbound port uri
	 * @param isRouting the component routing status
	 */
	public ConnectionInfo(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI, boolean isRouting) {
		this.address = address;
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.routingInboundPortURI = routingInboundPortURI;
		this.isRouting = isRouting;
	}

	/**
	 * @return The component address
	 */
	public NodeAddressI getAddress() {
		return address;
	}
	
	/**
	 * @return communication inbound port uri
	 */
	public String getCommunicationInboudPort() {
		return communicationInboundPortURI;
	}

	/**
	 * @return True if the component is routing, False else
	 */
	public boolean isRouting() {
		return isRouting;
	}

	/**
	 * @return routing inbound port uri
	 */
	public String getRoutingInboundPortURI() {
		return routingInboundPortURI;
	}
}
