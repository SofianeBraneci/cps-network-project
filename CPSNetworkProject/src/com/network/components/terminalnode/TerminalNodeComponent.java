package com.network.components.terminalnode;

import java.rmi.ConnectException;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

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

/**
 * Class for terminal node components
 * 
 * @author Softwarkers
 *
 */
@RequiredInterfaces(required = { RegistrationCI.class, CommunicationCI.class })
@OfferedInterfaces(offered = { CommunicationCI.class })
public class TerminalNodeComponent extends AbstractComponent {
	/** the terminal node registration outbound port */
	protected RegistrationOutboundPort terminalNodeRegistrationOutboundPort;
	/** the terminal node communication inbound port */
	protected TerminalNodeCommunicationInboundPort terminalNodeCommunicationInboundPort;
	/** the terminal node address */
	private NodeAddressI address;
	/** the terminal node initial position */
	private PositionI initialPosition;
	/** the terminal node initial range */
	private double initialRange;
	/** neighbor's ports of the current node */
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnections;
	/** address to send a message to */
	private NodeAddressI sendingAddressI = null;
	/** the terminal node executor services indexes */
	private int executorServiceIndexCommunication;
	private int executorServiceIndexMessage;

	private boolean isStillOn;

	/**
	 * create and initialize terminal node
	 * 
	 * @param address         the terminal node address
	 * @param initialPosition the terminal node initial position
	 * @param initialRange    the terminal node initial range
	 * @throws Exception
	 */
	protected TerminalNodeComponent(NodeAddressI address, PositionI initialPosition, double initialRange) {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;
		this.communicationConnections = new ConcurrentHashMap<>();
		this.executorServiceIndexCommunication = createNewExecutorService(
				"TERMINAL NODE EXECUTOR SERVICE URI FOR CONNECTION", 10, false);
		this.executorServiceIndexCommunication = createNewExecutorService(
				"TERMINAL NODE EXECUTOR SERVICE URI FOR MESSAGE SENDING", 10, false);

		try {
			this.terminalNodeRegistrationOutboundPort = new RegistrationOutboundPort(this);
			this.terminalNodeCommunicationInboundPort = new TerminalNodeCommunicationInboundPort(this);
			this.terminalNodeRegistrationOutboundPort.publishPort();
			this.terminalNodeCommunicationInboundPort.publishPort();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * create and initialize a terminal node with predifined information
	 * 
	 * @throws Exception
	 */
	protected TerminalNodeComponent() {
		super(10, 0);

		this.address = new NodeAddress("Some IP");
		this.initialPosition = new Position(1, 2);
		this.initialRange = 200;
		this.communicationConnections = new ConcurrentHashMap<>();
		this.executorServiceIndexCommunication = createNewExecutorService(
				"TERMINAL NODE EXECUTOR SERVICE URI FOR CONNECTION", 10, false);
		this.executorServiceIndexCommunication = createNewExecutorService(
				"TERMINAL NODE EXECUTOR SERVICE URI FOR MESSAGE SENDING", 10, false);

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

	private void connectWithNeighbor(NodeAddressI address, String communicationInboundURI) {
		try {
			
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();

			doPortConnection(port.getPortURI(), communicationInboundURI,
					CommunicationConnector.class.getCanonicalName());

			communicationConnections.put(address, port);
			
			port.connect(this.address, terminalNodeCommunicationInboundPort.getPortURI());
			
			System.out.println("TERMINAL NODE A NEW CONNECTION WAS ESTABLISHED : " + address);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Connect the actual terminal node with another node to achieve a peer to peer
	 * connection
	 * 
	 * @param address                     node to connect with address
	 * @param communicationInboundPortURI node to connect with communication inbound
	 *                                    port uri
	 */
	void connect(NodeAddressI address, String communicationInboundURI) {

		Future<?> future = getExecutorService(executorServiceIndexCommunication).submit(() -> {
			try {
				if (communicationConnections.containsKey(address))
					return;
				CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
				port.publishPort();

				doPortConnection(port.getPortURI(), communicationInboundURI,
						CommunicationConnector.class.getCanonicalName());

				communicationConnections.put(address, port);
				System.out.println("TERMINAL NODE A NEW CONNECTION WAS ESTABLISHED : " + address);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		try {
			future.get();
		} catch (Exception e2) {
			// TODO: handle exception
		}

	}

	void connectRouting(NodeAddressI address, String communicationInboundPort, String routingInboundPort) {
		// Nothing to do
	}

	/**
	 * Transmit a message
	 * 
	 * @param m the message
	 */
	void transmitMessage(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else

		getExecutorService(executorServiceIndexMessage).execute(() -> {
			int N = 3;
			try {
				if (this.address.equals(m.getAddress())) {
					System.out.println("FROM TERMINAL NODE: A MESSAGE IS RECEIVED");
					return;
				}

				// check if a neighbor has a route
				int route = hasRouteFor(m.getAddress());
				System.out.println("ROUTE LENGTH IS " + route);
				if (route != -1) {

					System.out.println("THE SENDING ADDRESS IS " + sendingAddressI);
					communicationConnections.get(sendingAddressI).transmitMessage(m);

				} else {
					System.out.println("ALL NEIGHBORS RESPONDED WITH A -1, PROCEED WITH FLODING");
					for (CommunicationOutBoundPort port : communicationConnections.values()) {
						if (N == 0)
							break;
						port.transmitMessage(m);
						N--;
					}
					return;
				}
				// proceed by flooding the network
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

	}

	/**
	 * Ask the node neighbors if they have a route to an address
	 * 
	 * @param address
	 * @return min steps to the address if he have a route, -1 else
	 */
	int hasRouteFor(AddressI address) {
		if (this.address.equals(address))
			return 0;
		int min = Integer.MAX_VALUE;
		int counter = 0;
		int current;
		try {

			for (Entry<NodeAddressI, CommunicationOutBoundPort> entry : communicationConnections.entrySet()) {
				current = entry.getValue().hasRouteFor(address);
				if (current == -1)
					counter++;
				if (current < min) {
					sendingAddressI = entry.getKey();
					min = current;
				}
			}
			return counter == communicationConnections.size() ? -1 : min + 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

	void ping() throws ConnectException {

		if (!isStillOn)
			throw new ConnectException("The node you are trying to ping is no longer ON");

	}

	@Override
	public synchronized void execute() throws Exception {
		// do a port connection with the register
		// registration work perfectly
		doPortConnection(terminalNodeRegistrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());
		// just to wait for the routing nodes to register properly
		Thread.sleep(2000L);
		// register the node and get the neighbors
		Set<ConnectionInfo> connectionInfos = terminalNodeRegistrationOutboundPort.registerTerminalNode(address,
				terminalNodeCommunicationInboundPort.getPortURI(), initialPosition, initialRange);
		isStillOn = true;
		// connect with them
		System.out.println("TERMINAL NODE connection size = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {
			// this will invoke the connect method of the other component!!!
			connectWithNeighbor(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort());

		}

		getExecutorService(executorServiceIndexCommunication).execute(() -> {

			int randomSleepDuration = new Random().nextInt(5000) + 1000;

			try {

				Thread.sleep(randomSleepDuration);

				unregister();

				disconnectFromNeighbors();

				System.out.println("THE NODE WITH THE CURRENT ADDRESSE : " + address
						+ " HAS GONE OFF AND UNREGISTER IT SELF FROM ALL OTHER NEGIBORING NODES");

			} catch (Exception e) {
				// TODO: handle exception
			}

		});

		getExecutorService(executorServiceIndexCommunication).execute(() -> {
			boolean work = true;
			while (work) {
				pingNeighboors();
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					work = false;
				}
			}
		});

		// uncomment to test unregister
//		terminalNodeRegistrationOutboundPort.unregister(address);

		// uncomment to test message sending
		// Message message = new Message(new NodeAddress("192.168.25.6"), "Hello", 55);
		Message message = new Message(new NodeAddress("192.168.25.6"), "Hello", 56);
//		Message message = new Message(new NetworkAddress("192.168.25.6"), "Hello", 5 );
		transmitMessage(message);
		super.execute();

	}

	private void disconnectFromNeighbors() throws Exception {
		for (CommunicationOutBoundPort port : communicationConnections.values()) {
			if (port.connected()) {
				doPortDisconnection(port.getPortURI());
			}
		}
	}

	private void pingNeighboors() {
		// TODO Auto-generated method stub

		NodeAddressI currentNodeAddressI = null;
		for (Entry<NodeAddressI, CommunicationOutBoundPort> entry : communicationConnections.entrySet()) {
			try {
				currentNodeAddressI = entry.getKey();
				System.out.println("TERMINAL NODE: Pinging : " + currentNodeAddressI);
				entry.getValue().ping();

			} catch (Exception e) {
				// TODO: handle exception
				if (e instanceof ConnectException) {
					communicationConnections.remove(currentNodeAddressI);
					System.out.println("Ping address : " + address + " raised an exception");
				}
			}
		}

	}

	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(terminalNodeRegistrationOutboundPort.getPortURI());
		disconnectFromNeighbors();
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		getExecutorService(executorServiceIndexCommunication).shutdownNow();

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

	void unregister() {
		try {
			terminalNodeRegistrationOutboundPort.unregister(address);
			isStillOn = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

}
