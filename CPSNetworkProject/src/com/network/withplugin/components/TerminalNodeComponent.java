package com.network.withplugin.components;

import java.util.Set;
import java.util.Map.Entry;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.components.terminalnode.TerminalNodeCommunicationInboundPort;
import com.network.interfaces.AddressI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.plugins.CommunicationPlugin;
import com.network.plugins.NodesRegistrationPlugin;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

public class TerminalNodeComponent extends AbstractComponent {

	/**
	 * 
	 */
	protected RegistrationOutboundPort terminalNodeRegistrationOutboundPort;
	protected TerminalNodeCommunicationInboundPort terminalNodeCommunicationInboundPort;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	private NodesRegistrationPlugin terminalNodeRegistrationPlugin;
	private CommunicationPlugin terminalCommunicationPlugin;
	private final String TERMINAL_NODE_REGISTRATION_PLUGIN_URI = "TERMINAL_NODE_REGISTRATION_PLUGIN_URI";
	private final String TERMINAL_NODE_COMMUICATION_PLUGIN_URI = "TERMINAL_NODE_COMMUICATION_PLUGIN_URI";

	protected TerminalNodeComponent(NodeAddressI address, PositionI initialPosition, double initialRange) {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;

		try {
			this.terminalNodeRegistrationPlugin = new NodesRegistrationPlugin();
			this.terminalNodeRegistrationPlugin.setPluginURI(TERMINAL_NODE_REGISTRATION_PLUGIN_URI);
			this.installPlugin(terminalNodeRegistrationPlugin);
			this.terminalCommunicationPlugin = new CommunicationPlugin(address, null);
			this.terminalCommunicationPlugin.setPluginURI(TERMINAL_NODE_COMMUICATION_PLUGIN_URI);
			this.installPlugin(terminalCommunicationPlugin);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	protected TerminalNodeComponent() {
		super(10, 0);

		this.address = new NodeAddress("Some IP");
		this.initialPosition = new Position(1, 2);
		this.initialRange = 200;
		try {
			this.terminalNodeRegistrationPlugin = new NodesRegistrationPlugin();
			this.terminalNodeRegistrationPlugin.setPluginURI(TERMINAL_NODE_REGISTRATION_PLUGIN_URI);
			this.installPlugin(terminalNodeRegistrationPlugin);
			this.terminalCommunicationPlugin = new CommunicationPlugin(address, null);
			this.terminalCommunicationPlugin.setPluginURI(TERMINAL_NODE_COMMUICATION_PLUGIN_URI);
			this.installPlugin(terminalCommunicationPlugin);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
	void connect(NodeAddressI address, String communicationInboudURI) {

			terminalCommunicationPlugin.connect(address, communicationInboudURI);

	}

	// maybe we'll delete it later it's not really required for the terminal node
	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {

	}

	void transmitMessage(MessageI m) {
		terminalCommunicationPlugin.transmitMessage(m);
	}

	// no routing capability
	int hasRouteFor(AddressI address) {
		return terminalCommunicationPlugin.hasRouteFor(address);
		}

	void ping() {

	}


	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		System.out.println("EXECUTING FROM TERMINAL NODE");
		Set<ConnectionInfo> connectionInfos = terminalNodeRegistrationPlugin.registerTerminalNode(address,
				terminalCommunicationPlugin.getInboundPortForPluginURI(), initialPosition, initialRange);
		System.out.println("TERMINAL NODE HAS A TOTAL OF " + connectionInfos.size() + " ROUTING NODES");

		for (ConnectionInfo info : connectionInfos) {

			if (!info.getCommunicationInboudPort().startsWith("TEST")) {
				connect(info.getAddress(), info.getCommunicationInboudPort());
				System.out.println("TERMINAL NODE: A CONNECTION WAS ESTABLISHED!");
			}
							

		}

	}

	@Override
	public synchronized void finalise() throws Exception {
		// TODO Auto-generated method stub
		// doPortDisconnection(terminalNodeRegistrationOutboundPort.getPortURI());
//		for (CommunicationOutBoundPort port : communicationConnections.values()) {
//			doPortDisconnection(port.getPortURI());
//		}
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		// TODO Auto-generated method stub
		try {
			// terminalNodeRegistrationOutboundPort.unpublishPort();
			// terminalNodeCommunicationInboundPort.unpublishPort();
//			for (CommunicationOutBoundPort port : communicationConnections.values())
//				port.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
