package com.network.common;

import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * Class for communication outbound ports
 * 
 * @author Softwarkers
 * 
 */
public class CommunicationOutBoundPort extends AbstractOutboundPort implements CommunicationCI{

	private static final long serialVersionUID = 1L;
	
	/**
	 * create and initialize communication outbound ports.
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public CommunicationOutBoundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
	}


	/**
	 * create and initialize communication outbound ports.
	 * @param uri unique identifier of the port.
	 * @param owner component that owns this port.
	 * @exception Exception
	 */
	public CommunicationOutBoundPort(String uri, ComponentI owner) throws Exception {
		super(uri,  CommunicationCI.class, owner);
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		((CommunicationCI)getConnector()).connect(address, communicationInboudURI);
		
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
			((CommunicationCI) getConnector()).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		((CommunicationCI)getConnector()).transmitMessage(m);
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		return ((CommunicationCI)getConnector()).hasRouteFor(address);
	}

	@Override
	public void ping() throws Exception {
		((CommunicationCI)getConnector()).ping();
	}

}
