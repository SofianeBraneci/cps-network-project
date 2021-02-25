package com.network.components.terminalnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.components.register.RegisterComponent;
import com.network.connectors.CommunicationConnector;
import com.network.connectors.RegistrationConnector;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@RequiredInterfaces(required = { RegistrationCI.class, CommunicationCI.class })
@OfferedInterfaces(offered = { CommunicationCI.class })
public class TerminalNodeComponenet extends AbstractComponent {

	protected RegistrationOutboundPort terminalNodeRegistrationOutboundPort;
	protected TerminalNodeCommunicationInboundPort terminalNodeCommunicationInboundPort;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	// for keeping track of all the nodes with routing capability
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnections;

	protected TerminalNodeComponenet(NodeAddressI address, PositionI initialPosition, double initialRange) {
		super(10, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;
		this.communicationConnections = new HashMap<>();

		try {
			this.terminalNodeRegistrationOutboundPort = new RegistrationOutboundPort(this);
			this.terminalNodeCommunicationInboundPort = new TerminalNodeCommunicationInboundPort(this);
			this.terminalNodeRegistrationOutboundPort.publishPort();
			this.terminalNodeCommunicationInboundPort.publishPort();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		this.toggleLogging();
		this.toggleTracing();
	}

	protected TerminalNodeComponenet() {
		super(10, 0);

		this.address = new NodeAddress("Some IP");
		this.initialPosition = new Position(1, 2);
		this.initialRange = 200;
		this.communicationConnections = new HashMap<>();
		try {
			this.terminalNodeRegistrationOutboundPort = new RegistrationOutboundPort(this);
			this.terminalNodeCommunicationInboundPort = new TerminalNodeCommunicationInboundPort(this);
			this.terminalNodeRegistrationOutboundPort.publishPort();
			this.terminalNodeCommunicationInboundPort.publishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println(e.getMessage());
		}
		this.toggleLogging();
		this.toggleTracing();
	}

	void connect(NodeAddressI address, String communicationInboudURI) {

		/*
		 * each time it get's called, we create a new out bound port add it to the
		 * connections table, then do a port connection
		 * 
		 **/
		logMessage(this + " component");
		System.err.println("CONNECTING TO OTHER NODES VIA : " + communicationInboudURI);
		try {
			if (communicationConnections.containsKey(address))
				return;
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();
			
			doPortConnection(port.getPortURI(), communicationInboudURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnections.put(address, port);
			
			port.connect(this.address, terminalNodeCommunicationInboundPort.getPortURI());
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// maybe i'll delete it later it's not really required for the terminal node
	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {

	}

	void transmitMessag(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		if (hasRouteFor((NodeAddressI) m.getAddress())) {

		}

	}

	// no routing capability
	boolean hasRouteFor(NodeAddressI address) {
		return false;
	}

	void ping() {

	}

	@Override
	public synchronized void execute() throws Exception {

		// do a port connection with the register
		doPortConnection(terminalNodeRegistrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());

		// register the node and get the neighbors
		Set<ConnectionInfo> connectionInfos = terminalNodeRegistrationOutboundPort.registerTerminalNode(address,
				terminalNodeCommunicationInboundPort.getPortURI(), initialPosition, initialRange);

		// connect with them
		this.logMessage("TERMINAL NODE connection size = " + connectionInfos.size());
		for (ConnectionInfo connectionInfo : connectionInfos) {
			if (connectionInfo.getCommunicationInboudPort().startsWith("TEST"))
				continue;
			else {
				System.err.println(connectionInfo.getCommunicationInboudPort());
				this.connect(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort());
			}
		}
		super.execute();

	}

	@Override
	public synchronized void finalise() throws Exception {
		// TODO Auto-generated method stub
		doPortDisconnection(terminalNodeRegistrationOutboundPort.getPortURI());
		for (CommunicationOutBoundPort port : communicationConnections.values()) {
			doPortDisconnection(port.getPortURI());
		}
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		// TODO Auto-generated method stub
		try {
			terminalNodeRegistrationOutboundPort.unpublishPort();
			terminalNodeCommunicationInboundPort.unpublishPort();
			for (CommunicationOutBoundPort port : communicationConnections.values())
				port.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
