package com.network.components.terminalnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.Message;
import com.network.common.NetworkAddress;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.components.register.RegisterComponent;
import com.network.connectors.CommunicationConnector;
import com.network.connectors.RegistrationConnector;
import com.network.interfaces.AddressI;
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
public class TerminalNodeComponent extends AbstractComponent {

	protected RegistrationOutboundPort terminalNodeRegistrationOutboundPort;
	protected TerminalNodeCommunicationInboundPort terminalNodeCommunicationInboundPort;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	// for keeping track of all the nodes with routing capability
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnections;
	private NodeAddressI sendingAddressI = null;

	protected TerminalNodeComponent(NodeAddressI address, PositionI initialPosition, double initialRange) {
		super(1, 0);
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
	}

	protected TerminalNodeComponent() {
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
	}

	void connect(NodeAddressI address, String communicationInboudURI) {

		/*
		 * each time it get's called, we create a new out bound port add it to the
		 * connections table, then do a port connection
		 * 
		 **/
		try {
			if (communicationConnections.containsKey(address))
				return;
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();

			doPortConnection(port.getPortURI(), communicationInboudURI,
					CommunicationConnector.class.getCanonicalName());
			//
			communicationConnections.put(address, port);
			port.connect(this.address, terminalNodeCommunicationInboundPort.getPortURI());
			System.out.println("TERMINAL NODE A NEW CONNECTION WAS ESTABLISHED !!!" + communicationConnections.size());
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// maybe we'll delete it later it's not really required for the terminal node
	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {

	}

	void transmitMessage(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		int N = 3;
		try {
			if(this.address.equals(m.getAddress())) {
				System.out.println("FROM TERMINAL NODE: A MESSAGE IS RECEIVED");
				return;
			}
			
			// check if a neighbor has a roue
			int route = hasRouteFor(m.getAddress());
			
			if(route != -1) {
				System.out.println("ROUTE LENGTH IS " + route);
				System.out.println("THE SENDING ADDRESS IS " + sendingAddressI);
				communicationConnections.get(sendingAddressI).transmitMessage(m);
		
			}
			else {
				System.out.println("ALL NEIGHBORS RESPONDED WITH A -1, PROCEED WITH FLODING");
				for(CommunicationOutBoundPort port: communicationConnections.values()) {
					if(N == 0 ) break;
					port.transmitMessage(m);
					N--;
				}
				return;
			}
			// proceed by flooding the network
					
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// no routing capability
	int hasRouteFor(AddressI address) {
		
		// for(NodeAddressI add: communicationConnections.keySet())
		// System.out.println(add.toString());
		if(this.address.equals(address)) return 0;
		int min = 9000;
		int counter = 0;
		int current;
		try {
			
			for (Entry<NodeAddressI, CommunicationOutBoundPort> entry : communicationConnections.entrySet()) {
				System.out.println("TERMINAL CURRENT ADDRESSE " + entry.getKey());
				current = entry.getValue().hasRouteFor(address);
				System.out.println(current);
				if(current == -1) counter++;
				if (current < min) {
					sendingAddressI = entry.getKey();
					min = current;

				}
			}
			
			return counter ==  communicationConnections.size() ? -1 : min + 1;
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}

	}

	void ping() {

	}

	@Override
	public synchronized void execute() throws Exception {

		// do a port connection with the register
		// registration work perfectly
		doPortConnection(terminalNodeRegistrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());
		// just to wait for the routing nodes to register properly
		Thread.sleep(3000L);
		// register the node and get the neighbors
		Set<ConnectionInfo> connectionInfos = terminalNodeRegistrationOutboundPort.registerTerminalNode(address,
				terminalNodeCommunicationInboundPort.getPortURI(), initialPosition, initialRange);

		// connect with them
		System.out.println("TERMINAL NODE connection size = " + connectionInfos.size());
		for (ConnectionInfo connectionInfo : connectionInfos) {
			if (!connectionInfo.getCommunicationInboudPort().startsWith("TEST")) {

				connect(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort());
			}
		}

		// terminalNodeRegistrationOutboundPort.unregister(address);
		Message message = new Message(new NodeAddress("192.168.25.5"), "Hello", 12 );
		transmitMessage(message);
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
