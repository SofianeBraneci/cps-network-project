package com.network.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface  CommunicationCI extends  OfferedCI, RequiredCI {
	
	void connect(NodeAddressI address, String communicationInboudURI) throws Exception;
	void connectRouting (NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) throws Exception;
	void transmitMessag(MessageI m);
	boolean hasRouteFor(NodeAddressI address);
	void ping();

}
