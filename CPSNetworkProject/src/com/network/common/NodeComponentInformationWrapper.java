package com.network.common;

import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

/**
 * Class for Node component information
 * @author Softwarkers
 *
 */
public class NodeComponentInformationWrapper {
	/** node communication inbound port uri*/
	private String communicationInboundPortURI;
	/** node initial position */
	private PositionI initialPosition;
	/** node routing inbound port uri*/
	private String routingInboundPortURI;
	
	/**
	 * Create Node component information wrapper
	 * @param communicationInboundPortURI node communication inbound port uri
	 * @param initialPosition node initial position
	 * @param routingInboundPortURI node routing inbound port uri
	 */
	public NodeComponentInformationWrapper(String communicationInboundPortURI,
			PositionI initialPosition, String routingInboundPortURI) {
		super();
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.initialPosition = initialPosition;
		this.routingInboundPortURI = routingInboundPortURI;
	}
	/**
	 * Create a Node component information wrapper
	 * @param communicationInboundPortURI node communication inbound port uri
	 */
	public NodeComponentInformationWrapper(String communicationInboundPortURI) {
		super();
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.routingInboundPortURI = null;
	}

	/**
	 * Create a Node component information wrapper
	 * @param communicationInboundPortURI node communication inbound port uri
	 * @param initialPosition node initial position
	 */
	public NodeComponentInformationWrapper(String communicationInboundPortURI,PositionI initialPosition) {
		super();
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.initialPosition = initialPosition;
		this.routingInboundPortURI = null;
	}
	
	/**
	 * Create a Node component information wrapper
	 * @param communicationInboundPortURI node communication inbound port uri
	 * @param routingInboundPortURI node routing inbound port uri
	 */
	public NodeComponentInformationWrapper(String communicationInboundPortURI, String routingInboundPortURI){
		super();
		this.communicationInboundPortURI = communicationInboundPortURI;
		this.routingInboundPortURI = routingInboundPortURI;
	}
	
	/**
	 * @return initial position
	 */
	public PositionI getInitialPosition() {
		return initialPosition;
	}

	/**
	 * create the Connection Info of the node component
	 * @param address the node address
	 * @param wrapper the node information wrapper
	 * @return Connection Info of the node component
	 */
	public static ConnectionInfo getConnectionInfo(NodeAddressI address, NodeComponentInformationWrapper wrapper) {
		
		return new ConnectionInfo(address, wrapper.communicationInboundPortURI, 
				wrapper.routingInboundPortURI == null? null : wrapper.routingInboundPortURI,
						wrapper.routingInboundPortURI == null? false : true);
	}
}
