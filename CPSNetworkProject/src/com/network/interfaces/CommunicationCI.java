package com.network.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
/**
 * Communication components interface
 * @author Softwarkers
 *
 */
public interface  CommunicationCI extends  OfferedCI, RequiredCI {
	/**
	 * Connect the component with a terminal node
	 * @param address node to connect with address
	 * @param communicationInboundURI node to connect with communication inbound port
	 * @exception Exception
	 */
	void connect(NodeAddressI address, String communicationInboundURI) throws Exception;
	/**
	 * Connect the component with a routing node
	 * @param address node to connect with address
	 * @param communicationInboundPortURI node to connect with communication inbound port
	 * @param routingInboundPortURI node to connect with routing inbound port
	 * @exception Exception
	 */
	void connectRouting (NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception;
	/**
	 * Transmit a message
	 * @param m the message
	 * @exception Exception
	 */
	void transmitMessage(MessageI m) throws Exception;
	/**
	 * Checks the node have a route with an address
	 * @param address address to check route to
	 * @exception Exception
	 * @return -1 if no route
	 */
	int hasRouteFor(AddressI address) throws Exception;
	void ping() throws Exception;

}
