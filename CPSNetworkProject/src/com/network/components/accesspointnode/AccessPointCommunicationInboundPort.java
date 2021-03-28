package com.network.components.accesspointnode;

import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * Class for access point communication inbound ports
 * 
 * @author Softwarkers
 *
 */
public class AccessPointCommunicationInboundPort extends AbstractInboundPort implements CommunicationCI{

	private static final long serialVersionUID = 1L;
	
	/**
	 * create and initialize access point communication inbound ports.
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public AccessPointCommunicationInboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
	}
	/**
	 * create and initialize access point communication inbound ports.
	 * @param uri unique identifier of the port.
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public AccessPointCommunicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri,CommunicationCI.class ,owner);
	}
	
	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		getOwner().handleRequest(c -> {
			((AccessPointComponent) c).connect(address, communicationInboudURI);
			return null;
		});
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {

		getOwner().handleRequest(c -> {
			((AccessPointComponent) c).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
			return null;
		});
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		getOwner().handleRequest(c -> {
			((AccessPointComponent) c).transmitMessage(m);
			return null;
		});

	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		return getOwner().handleRequest(c -> ((AccessPointComponent) c).hasRouteFor(address));
	}

	@Override
	public void ping() throws Exception {
		getOwner().handleRequest(c -> {
			((AccessPointComponent) c).ping();
			return null;
		});
	}
}
