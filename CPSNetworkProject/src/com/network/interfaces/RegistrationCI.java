package com.network.interfaces;

import java.util.Set;

import com.network.common.ConnectionInfo;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RegistrationCI extends OfferedCI, RequiredCI{
	
	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, 
			String communicationInboundPortURI, 
			PositionI initialPosition, 
			double initialRange) throws Exception;


	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, 
			String communicationInboundPortURI, 
			PositionI initialPosition, 
			double initialRange, String routingInboundPortURI) throws Exception;
	
	Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, 
			String commnicationInboundPortURI, 
			PositionI initialPosition,
			double initialRange,  String routingInboundPortURI) throws Exception;
	void unregister(NodeAddressI address) throws Exception;
}
