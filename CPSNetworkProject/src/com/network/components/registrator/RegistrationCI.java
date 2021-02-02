package com.network.components.registrator;

import java.util.Set;

import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

import fr.sorbonne_u.components.interfaces.OfferedCI;

public interface RegistrationCI extends OfferedCI{
	
	Set<ConnectionInfo> register(NodeAddressI address, 
			String communicationInboudPort, 
			PositionI init, 
			double initRange, boolean isRouting) throws Exception;


	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, 
			String communicationInboudPort, 
			PositionI init, 
			double initRange) throws Exception;
	
	void unregister(NodeAddressI address) throws Exception;
}
