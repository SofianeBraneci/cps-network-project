package com.network.components.terminalnode;

import java.util.Set;

import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.components.register.RegisterComponent;
import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
@RequiredInterfaces(required = {TerminalNodeServiceCI.class})
public class TerminalNodeComponent extends AbstractComponent {
	public static final String TERMINAL_NODE_INBOUND_URI = "TERMINAL_NODE_INB_URI";
	protected TerminalNodeOutboundPort terminalOutboundPort;
	private Set<ConnectionInfo> connections;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	private String communicationInboundPortURI;
	protected TerminalNodeComponent() throws Exception {
		super(1, 0);
		
		this.address = new NodeAddress("sdd");
		this.initialPosition = new Position(1, 2);
		this.initialRange = 20;
		this.communicationInboundPortURI = RegisterComponent.REGISTER_PORT_URI;
		terminalOutboundPort = new TerminalNodeOutboundPort(TERMINAL_NODE_INBOUND_URI, this);
		terminalOutboundPort.publishPort();
		toggleLogging();
		toggleTracing();

	}
	
	@Override
	public synchronized void execute() throws Exception {
		//connections = terminalOutboundPort.registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange);
		logMessage("Total connections arr : " + connections.size() + "\n");
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		// TODO Auto-generated method stub
		try {
			terminalOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);

		}
	}

	@Override
	public synchronized void finalise() throws Exception {
		// TODO Auto-generated method stub
		this.doPortDisconnection(TERMINAL_NODE_INBOUND_URI);
	}
}
