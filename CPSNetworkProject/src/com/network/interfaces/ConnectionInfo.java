package com.network.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ConnectionInfo{
	
	NodeAddressI getAddress();
	String getCommunicationInboudPort();
	boolean isRouting();
	String getRoutingInboundPortURI();
	PositionI getPosition();

}
