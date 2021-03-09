package com.network.withplugin.components;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.components.terminalnode.TerminalNodeCommunicationInboundPort;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
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
	private final String TERMINAL_NODE_REGISTRATION_PLUGIN_URI = "TERMINAL_NODE_REGISTRATION_PLUGIN_URI";

	protected TerminalNodeComponent(NodeAddressI address, PositionI initialPosition, double initialRange) {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;

		try {
			this.terminalNodeRegistrationPlugin = new NodesRegistrationPlugin();
			this.terminalNodeRegistrationPlugin.setPluginURI(TERMINAL_NODE_REGISTRATION_PLUGIN_URI);
			this.installPlugin(terminalNodeRegistrationPlugin);

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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		System.out.println("EXECUTING FROM TERMINAL NODE");
		Set<ConnectionInfo> connectionInfos = terminalNodeRegistrationPlugin.registerTerminalNode(address,
				"SOME URI", initialPosition, initialRange);
		System.out.println("TERMINAL NODE HAS A TOTAL OF " + connectionInfos.size()  + " ROUTING NODES");
		
	}

	@Override
	public synchronized void finalise() throws Exception {
		// TODO Auto-generated method stub
		//doPortDisconnection(terminalNodeRegistrationOutboundPort.getPortURI());
//		for (CommunicationOutBoundPort port : communicationConnections.values()) {
//			doPortDisconnection(port.getPortURI());
//		}
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		// TODO Auto-generated method stub
		try {
		//	terminalNodeRegistrationOutboundPort.unpublishPort();
			//terminalNodeCommunicationInboundPort.unpublishPort();
//			for (CommunicationOutBoundPort port : communicationConnections.values())
//				port.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}
