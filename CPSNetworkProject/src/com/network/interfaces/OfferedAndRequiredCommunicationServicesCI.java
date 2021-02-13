package com.network.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface OfferedAndRequiredCommunicationServicesCI extends OfferedCI, RequiredCI {
	
	void connect (NodeAddressI address, String communicationInboundPortURI) throws Exception;
	void connectRouting(NodeAddressI address, String communicationInboundPortURI, 
			String routingInboundPortURI) throws Exception;
	void transmitMessage(MessageI m) throws Exception;
	boolean hasRouteFor(AddressI address) throws Exception;
	void ping() throws Exception;
}
