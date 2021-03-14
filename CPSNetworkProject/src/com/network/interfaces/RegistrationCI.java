package com.network.interfaces;

import java.util.Set;

import com.network.common.ConnectionInfo;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
/**
 * Registration components interface
 * @author Softwarkers
 *
 */
public interface RegistrationCI extends OfferedCI, RequiredCI{
	/**
	 * Register a terminal node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @return Set of the new nodes neighbors connection info that can route
	 * @exception Exception
	 */
	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, 
			String communicationInboundPortURI, 
			PositionI initialPosition, 
			double initialRange) throws Exception;

	/**
	 * Register an access point
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @param routingInboundPortURI routing inbound port
	 * @return Set of the new nodes neighbors connection info that can route
	 * @exception Exception
	 */
	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, 
			String communicationInboundPortURI, 
			PositionI initialPosition, 
			double initialRange, String routingInboundPortURI) throws Exception;
	
	/**
	 * Register a routing node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @param routingInboundPortURI routing inbound port
	 * @return Set of the new nodes neighbors connection info that can route
	 * @exception Exception
	 */
	Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, 
			String communicationInboundPortURI, 
			PositionI initialPosition,
			double initialRange,  String routingInboundPortURI) throws Exception;
	void unregister(NodeAddressI address) throws Exception;
}
