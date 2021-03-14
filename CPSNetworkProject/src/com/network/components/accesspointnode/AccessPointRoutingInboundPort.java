package com.network.components.accesspointnode;

import java.util.Set;

import com.network.common.RouteInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.RoutingCI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * Class for access point routing inbound port
 * @author Softwarkers
 *
 */
public class AccessPointRoutingInboundPort extends AbstractInboundPort implements RoutingCI {

	private static final long serialVersionUID = 1L;

	/**
	 * create and initialize routing outbound ports for access points.
	 * @param owner access point that owns this port
	 * @throws Exception
	 */
	public AccessPointRoutingInboundPort(ComponentI owner) throws Exception{
		super(RoutingCI.class, owner);
	}
	
	/**
	 * create and initialize routing outbound ports for access points.
	 * @param uri unique identifier of the port.
	 * @param owner access point that owns this port
	 * @throws Exception
	 */
	public AccessPointRoutingInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RoutingCI.class, owner);
	}

	@Override
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		getOwner().handleRequest(c->{
			((AccessPointComponent)c).updateRouting(address, routes);
			return null;
		});
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		getOwner().handleRequest(c->{
			((AccessPointComponent)c).updateAccessPoint(neighbour, numberOfHops);
			return null;
		});
		
	}



}
