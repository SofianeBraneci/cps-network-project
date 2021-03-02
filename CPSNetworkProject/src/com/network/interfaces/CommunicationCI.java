package com.network.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface  CommunicationCI extends  OfferedCI, RequiredCI {
	
	void connect(NodeAddressI address, String communicationInboudURI) throws Exception;
	void connectRouting (NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) throws Exception;
	void transmitMessage(MessageI m) throws Exception;
	int hasRouteFor(AddressI address) throws Exception;
	void ping() throws Exception;

}
