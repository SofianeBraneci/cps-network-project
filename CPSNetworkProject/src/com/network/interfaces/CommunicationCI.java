package com.network.interfaces;

public interface CommunicationCI {
	
	void connect(NodeAddressI address, String communicationInboudURI) throws Exception;
	void connectRouting (NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) throws Exception;
	void transmitMessag(MessageI m);
	boolean hasRouteFor(NodeAddressI address);
	void ping();

}
