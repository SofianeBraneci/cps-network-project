package com.network.components.terminalnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.NodeComponentInformationWrapper;
import com.network.common.Position;
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
	public static final String TERMINAL_NODE_REGISTRATION_OUTBOUND_PORT_URI = "TERMINAL_NODE_REGISTRATION_OUTBOUND_PORT_URI";
	public static final String TERMINAL_NODE_COMMUNICATION_INBOUND_PORT_URI = "TERMINAL_NODE_COMMUNICATION_INBOUND_PORT_URI";
	public static final String TERMINAL_NODE_COMMINICATION_OUTBOUND_PORT_URI = "TERMINAL_NODE_COMMUNICATION_OUTBOUND_PORT_URI";

	protected TerminalNodeRegistrationOutboundPort terminalNodeRegistrationOutboundPort;
	protected TerminalNodeCommunicationInboundPort terminalNodeCommunicationInboundPort;
	protected TerminalNodeCommunicationOutboundPort terminalNodeCommunicationOutboundPort;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	private Map<NodeAddressI, NodeComponentInformationWrapper> connections;

	protected TerminalNodeComponenet(NodeAddressI address, PositionI initialPosition, double initialRange) {
		super(10, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;
		this.connections = new HashMap<>();
		try {
			this.terminalNodeRegistrationOutboundPort = new TerminalNodeRegistrationOutboundPort(
					TERMINAL_NODE_REGISTRATION_OUTBOUND_PORT_URI, this);
			this.terminalNodeCommunicationInboundPort = new TerminalNodeCommunicationInboundPort(
					TERMINAL_NODE_COMMUNICATION_INBOUND_PORT_URI, this);
			this.terminalNodeCommunicationOutboundPort = new TerminalNodeCommunicationOutboundPort(
					TERMINAL_NODE_COMMINICATION_OUTBOUND_PORT_URI, this);
			this.terminalNodeRegistrationOutboundPort.publishPort();
			this.terminalNodeCommunicationInboundPort.publishPort();
			this.terminalNodeCommunicationOutboundPort.publishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
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
		this.connections = new HashMap<>();
		try {
			this.terminalNodeRegistrationOutboundPort = new TerminalNodeRegistrationOutboundPort(
					TERMINAL_NODE_REGISTRATION_OUTBOUND_PORT_URI, this);
			this.terminalNodeCommunicationInboundPort = new TerminalNodeCommunicationInboundPort(
					TERMINAL_NODE_COMMUNICATION_INBOUND_PORT_URI, this);
			this.terminalNodeCommunicationOutboundPort = new TerminalNodeCommunicationOutboundPort(
					TERMINAL_NODE_COMMINICATION_OUTBOUND_PORT_URI, this);
			this.terminalNodeRegistrationOutboundPort.publishPort();
			this.terminalNodeCommunicationInboundPort.publishPort();
			this.terminalNodeCommunicationOutboundPort.publishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println(e.getMessage());
		}
		this.toggleLogging();
		this.toggleTracing();
	}

	void connect(NodeAddressI address, String communicationInboudURI) {
		connections.put(address, new NodeComponentInformationWrapper(communicationInboudURI));
		logMessage("Terminal Node Connected");

	}

	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {
		connections.put(address, new NodeComponentInformationWrapper(communicationInboudPortURI, routingInboudPortURI));
	}

	void transmitMessag(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		if (hasRouteFor((NodeAddressI) m.getAddress())) {

		}

	}

	boolean hasRouteFor(NodeAddressI address) {
		return connections.containsKey(address);
	}

	void ping() {

	}

	@Override
	public synchronized void execute() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("ssdfsdfsdfdsfsd");
		Set<ConnectionInfo> connectionInfos = terminalNodeRegistrationOutboundPort.registerTerminalNode(address,
				TERMINAL_NODE_COMMUNICATION_INBOUND_PORT_URI, initialPosition, initialRange);
		this.logMessage("connection size = " + connectionInfos.size());
		for(ConnectionInfo connectionInfo : connectionInfos) {
			logMessage(connectionInfo.getCommunicationInboudPort());
		}
		super.execute();

	}

	@Override
	public synchronized void finalise() throws Exception {
		// TODO Auto-generated method stub
		doPortDisconnection(TERMINAL_NODE_REGISTRATION_OUTBOUND_PORT_URI);
		// doPortDisconnection(TERMINAL_NODE_CONNECTION_INBOUND_PORT_URI);
		doPortDisconnection(TERMINAL_NODE_COMMINICATION_OUTBOUND_PORT_URI);
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		// TODO Auto-generated method stub
		try {
			terminalNodeRegistrationOutboundPort.unpublishPort();
			terminalNodeCommunicationInboundPort.unpublishPort();
			terminalNodeCommunicationOutboundPort.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
