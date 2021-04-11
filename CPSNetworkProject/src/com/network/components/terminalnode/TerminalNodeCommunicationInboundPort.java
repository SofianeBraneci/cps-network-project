package com.network.components.terminalnode;

import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * Class for terminal nodes communication inbound ports
 * 
 * @author Softwarkers
 *
 */
public class TerminalNodeCommunicationInboundPort extends AbstractInboundPort implements CommunicationCI {

	private static final long serialVersionUID = 1L;

	/**
	 * create and initialize terminal node communication inbound ports.
	 * 
	 * @param owner component that owns this port.
	 * @throws Exception
	 */
	public TerminalNodeCommunicationInboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
	}

	/**
	 * create and initialize terminal node communication inbound ports.
	 * 
	 * @param uri   unique identifier of the port.
	 * @param owner component that owns this port.
	 * @throws Exception
	 */
	public TerminalNodeCommunicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
		assert owner instanceof TerminalNodeComponent;
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		getOwner().handleRequest(TerminalNodeComponent.TERMINAL_NODE_CONNECTION_EXECUTOR_SERVICE_URI, c -> {
			((TerminalNodeComponent) c).connect(address, communicationInboudURI);
			return null;
		});

//		getOwner().handleRequest(c -> {
//			((TerminalNodeComponent) c).connect(address, communicationInboudURI);
//			return null;
//		});
	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {

		getOwner().handleRequest(TerminalNodeComponent.TERMINAL_NODE_CONNECTION_EXECUTOR_SERVICE_URI, (c -> {
			((TerminalNodeComponent) c).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
			return null;
		}));

//		getOwner().handleRequest(c -> {
//			((TerminalNodeComponent) c).connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
//			return null;
//		});
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		getOwner().handleRequest(TerminalNodeComponent.TERMINAL_NODE_MESSAGING_EXECUTOR_SERVICE_URI, c -> {
			((TerminalNodeComponent) c).transmitMessage(m);
			return null;
		});
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		return getOwner().handleRequest(TerminalNodeComponent.TERMINAL_NODE_CONNECTION_EXECUTOR_SERVICE_URI,
				c -> ((TerminalNodeComponent) c).hasRouteFor(address));
	}

	@Override
	public void ping() throws Exception {
		getOwner().handleRequest(TerminalNodeComponent.TERMINAL_NODE_CONNECTION_EXECUTOR_SERVICE_URI, c -> {
			((CommunicationCI) c).ping();
			return null;
		});
	}
}
