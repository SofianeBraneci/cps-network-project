package com.network.components.accesspointnode;

import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class AccessPointCommunicationInbountPort extends AbstractInboundPort implements CommunicationCI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccessPointCommunicationInbountPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
	}

	public AccessPointCommunicationInbountPort(String uri, ComponentI owner) throws Exception {
		// TODO Auto-generated constructor stub
		super(uri,CommunicationCI.class ,owner);
	}
	
	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		getOwner().handleRequest(c -> {
			System.err.println("Idfdsfsdt");
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
