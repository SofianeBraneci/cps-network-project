package com.network.components.accesspointnode;

import java.rmi.ConnectException;
import java.security.DrbgParameters.NextBytes;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.Message;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.common.RouteInfo;
import com.network.connectors.RoutingConnector;
import com.network.common.RoutingOutboundPort;
import com.network.components.register.RegisterComponent;
import com.network.connectors.CommunicationConnector;
import com.network.connectors.RegistrationConnector;
import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NetworkAddressI;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;
import com.network.interfaces.RoutingCI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

/**
 * Class for access point components
 * 
 * @author Softwarkers
 *
 */
@OfferedInterfaces(offered = { CommunicationCI.class, RoutingCI.class })
@RequiredInterfaces(required = { CommunicationCI.class, RegistrationCI.class, RoutingCI.class })
public class AccessPointComponent extends AbstractComponent {
	/** the access point communication inbound port */
	private AccessPointCommunicationInboundPort accessPointCommunicationInboundPort;
	/** the access point registration outbound port */
	private RegistrationOutboundPort registrationOutboundPort;
	/** the access point routing inbound port */
	private AccessPointRoutingInboundPort accessPointRoutingInboundPort;

	/** neighbor's ports of the current node */
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnectionPorts;
	/** neighbor's routing ports of the current node */
	private Map<NodeAddressI, RoutingOutboundPort> routingOutboundPorts;
	/** access points neighbor's route info of the current node */
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	/** Number of hops between the access point and his neighbors */
	private Map<NodeAddressI, Integer> accessPointsMap;
	/** the access point address */
	private NodeAddressI address;
	/** the access point initial position */
	private PositionI initialPosition;
	/** the access point initial range */
	private double initialRange;
	/** the access point executor service index */
	private int executorServiceIndexCommunication;
	private int executorServiceIndexRoutage;

	private boolean isStillOn;

	/**
	 * create and initialize access points
	 * 
	 * @param address         the access point address
	 * @param initialPosition the access point initial position
	 * @param initialRange    the access point initial range
	 * @throws Exception
	 */
	protected AccessPointComponent(NodeAddressI address, PositionI initialPosition, double initialRange)
			throws Exception {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;

		this.communicationConnectionPorts = new ConcurrentHashMap<>();
		this.routingOutboundPorts = new ConcurrentHashMap<>();
		this.routes = new ConcurrentHashMap<>();
		this.accessPointsMap = new ConcurrentHashMap<>();

		this.accessPointCommunicationInboundPort = new AccessPointCommunicationInboundPort(this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(this);
		this.registrationOutboundPort = new RegistrationOutboundPort(this);

		this.accessPointCommunicationInboundPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		this.executorServiceIndexRoutage = createNewExecutorService("ROUTING ACCESS POINT EXECUTOR SERVICE URI", 10,
				false);
		this.executorServiceIndexCommunication = createNewExecutorService(
				"COMMUNICATION ACCESS POINT EXECUTOR SERVICE URI", 10, false);

	}

	/**
	 * create and initialize an access points with predefined information
	 * 
	 * @throws Exception
	 */
	protected AccessPointComponent() throws Exception {
		super(1, 0);

		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;

		this.communicationConnectionPorts = new ConcurrentHashMap<>();
		this.routingOutboundPorts = new ConcurrentHashMap<>();
		this.routes = new ConcurrentHashMap<>();
		this.accessPointsMap = new ConcurrentHashMap<>();

		this.accessPointCommunicationInboundPort = new AccessPointCommunicationInboundPort(this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(this);
		this.registrationOutboundPort = new RegistrationOutboundPort(this);

		this.accessPointCommunicationInboundPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();

		this.executorServiceIndexRoutage = createNewExecutorService("ROUTING ACCESS POINT EXECUTOR SERVICE URI", 10,
				false);
		this.executorServiceIndexCommunication = createNewExecutorService(
				"COMMUNICATION ACCESS POINT EXECUTOR SERVICE URI", 10, false);
		;
	}

	@Override
	public synchronized void execute() throws Exception {
		doPortConnection(registrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());

		Set<ConnectionInfo> connectionInfos = registrationOutboundPort.registerAccessPoint(address,
				accessPointCommunicationInboundPort.getPortURI(), initialPosition, initialRange,
				accessPointRoutingInboundPort.getPortURI());

		isStillOn = true;

		System.out.println("Access Point node connections = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {
			if (connectionInfo.isRouting()) {
				connectWithRoutingNode(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort(),
						connectionInfo.getRoutingInboundPortURI());

			}
			else connectWithTerminalNode(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort());


		}

		// dynamically propagate the routing table to it's neighbors

//		getExecutorService(executorServiceIndexRoutage).execute(() -> {
//			propagateRoutingTable();
//
//		});

		getExecutorService(executorServiceIndexCommunication).execute(() -> {

			int randomSleepDuration = new Random().nextInt(5000) + 1000;

			try {

				Thread.sleep(randomSleepDuration);

				unregister();

				disconnectFromNeighbors();

			} catch (Exception e) {
				// TODO: handle exception
				if (!(e instanceof InterruptedException)) {
					e.printStackTrace();
				}
			}
		});

		getExecutorService(executorServiceIndexCommunication).execute(() -> {
			boolean work = true;
			while (work) {
				pingNeighbors();
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					work = false;
				}
			}
		});

		// uncomment this to test unregister
		/*
		 * registrationOutboundPort.unregister(address);
		 * terminalNodeRegistrationOutboundPort.unregister(address);
		 */

		// uncomment this to test message sending

//		Message message = new Message(new NodeAddress("192.168.25.6"), "Hello", 5);
//		Message message = new Message(new NodeAddress("192.168.25.1"), "Hello", 5 );
//
//		Message message = new Message(new NodeAddress("192.168.25.6"), "Hello", 5 );
//		

//		getExecutorService(executorServiceIndexCommunication).execute(()->{
//			transmitMessage(message);
//		});

		super.execute();
	}

	private void propagateRoutingTable() {
		boolean work = true;
		try {

			while (work) {
				for (RoutingOutboundPort neighborBoundPort : routingOutboundPorts.values()) {
					for (Entry<NodeAddressI, Set<RouteInfo>> entry : routes.entrySet()) {
						neighborBoundPort.updateAccessPoint(this.address, 1);
						neighborBoundPort.updateRouting(entry.getKey(), entry.getValue());
					}
					// for the other known access points

					for (Entry<NodeAddressI, Integer> entry : accessPointsMap.entrySet()) {

						neighborBoundPort.updateAccessPoint(entry.getKey(), entry.getValue() + 1);
					}

				}
				Thread.sleep(1200L);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			work = false;
		}
	}

	void pingNeighbors() {
		NodeAddressI currentNodeAddress = null;
		try {
			for (Entry<NodeAddressI, CommunicationOutBoundPort> entry : communicationConnectionPorts.entrySet()) {
				currentNodeAddress = entry.getKey();
				System.out.println("ACCESS POINT NODE: Pinging : " + currentNodeAddress);

				entry.getValue().ping();

			}
		} catch (Exception e) {
			// TODO: handle exception
			if (e instanceof ConnectException) {
				communicationConnectionPorts.remove(currentNodeAddress);
				System.out.println("Ping address : " + currentNodeAddress + " raised an exception");
			}
		}
	}

	private void unregister() {
		try {
			registrationOutboundPort.unregister(address);
			isStillOn = false;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	void disconnectFromNeighbors() {
		try {
			for (CommunicationOutBoundPort port : communicationConnectionPorts.values()) {
				if (port.connected()) {
					doPortDisconnection(port.getPortURI());
				}
			}
			for (RoutingOutboundPort port : routingOutboundPorts.values()) {
				if (port.connected()) {
					doPortDisconnection(port.getPortURI());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		getExecutorService(executorServiceIndexCommunication).shutdownNow();
		getExecutorService(executorServiceIndexRoutage).shutdownNow();
		try {

			this.accessPointCommunicationInboundPort.unpublishPort();
			this.accessPointRoutingInboundPort.unpublishPort();
			this.registrationOutboundPort.unpublishPort();
			for (CommunicationOutBoundPort port : communicationConnectionPorts.values())
				port.unpublishPort();
			for (RoutingOutboundPort routing : routingOutboundPorts.values())
				routing.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(registrationOutboundPort.getPortURI());
		for (CommunicationOutBoundPort port : communicationConnectionPorts.values())
			if (port.connected()) {
				doPortDisconnection(port.getPortURI());
			}
		for (RoutingOutboundPort port : routingOutboundPorts.values())
			if (port.connected()) {
				doPortDisconnection(port.getPortURI());
			}
		super.finalise();
	}

	/**
	 * Connect the actual access point with a terminal node to achieve a peer to
	 * peer connection
	 * 
	 * @param address                     terminal node to connect with address
	 * @param communicationInboundPortURI terminal node to connect with
	 *                                    communication inbound port uri
	 */
	public void connect(NodeAddressI address, String communicationInboundPortURI) {
		getExecutorService(executorServiceIndexCommunication).execute(() -> {
			try {
				if (communicationConnectionPorts.containsKey(address))
					return;

				// we create a new outbound port for the new neighbor
				CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
				port.publishPort();

				doPortConnection(port.getPortURI(), communicationInboundPortURI,
						CommunicationConnector.class.getCanonicalName());
				communicationConnectionPorts.put(address, port);
				System.out.println("ACCESS POINT: A NEW CONNECTION WAS ESTABLISHED WITH A TERMINAL NODE " + address);

			} catch (Exception e) {
				e.printStackTrace();
			}

		});
	}

	private void connectWithTerminalNode(NodeAddressI address, String communicationInboundPortURI) {
		try {

			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();
			doPortConnection(port.getPortURI(), communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());

			port.connect(this.address, accessPointCommunicationInboundPort.getPortURI());

			communicationConnectionPorts.put(address, port);

			System.out.println("ACCESS POINT NODE: CONNECTED WITH A TERMINAL NODE");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void connectWithRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			String routingInboundPortURI) {
		try {

			RoutingOutboundPort port = new RoutingOutboundPort(this);
			port.publishPort();
			doPortConnection(port.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());

			CommunicationOutBoundPort portC = new CommunicationOutBoundPort(this);
			
			portC.publishPort();
			
			doPortConnection(portC.getPortURI(), communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());

			portC.connectRouting(this.address, accessPointCommunicationInboundPort.getPortURI(),
					accessPointRoutingInboundPort.getPortURI());

			communicationConnectionPorts.put(address, portC);
			routingOutboundPorts.put(address, port);

			Set<RouteInfo> routeInfos = new HashSet<>();
			RouteInfo info = new RouteInfo(address, 1);
			routeInfos.add(info);
			routes.put(address, routeInfos);

			System.out.println("ACCESS POINT NODE: CONNECTED A ROUITNG NODE");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Connect the actual access point with a routing node to achieve a peer to peer
	 * connection
	 * 
	 * @param address                    routing node to connect with address
	 * @param communicationInboudPortURI routing node to connect with communication
	 *                                   inbound port uri
	 * @param routingInboudPortURI       routing node to connect with routing
	 *                                   inbound port uri
	 */
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) {

		getExecutorService(executorServiceIndexCommunication).execute(() -> {

			try {
				if (communicationConnectionPorts.containsKey(address)) {
					return;
				}

				CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
				port.publishPort();
				RoutingOutboundPort routingOutboundPort = new RoutingOutboundPort(this);

				routingOutboundPort.publishPort();

				doPortConnection(routingOutboundPort.getPortURI(), routingInboundPortURI,
						RoutingConnector.class.getCanonicalName());

				doPortConnection(port.getPortURI(), communicationInboundPortURI,
						CommunicationConnector.class.getCanonicalName());
				communicationConnectionPorts.put(address, port);
				routingOutboundPorts.put(address, routingOutboundPort);
				System.out.println("ACCESS POINT : A NEW CONNECTION WAS ESTABLISHED WITH A ROUTING NODE : " + address);
				Set<RouteInfo> routeInfos = new HashSet<>();
				RouteInfo info = new RouteInfo(address, 1);
				routeInfos.add(info);
				routes.put(address, routeInfos);
				System.out.println("ACCESS POINT: total neighbors  = " + communicationConnectionPorts.size()
						+ ", routing neighbors = " + routes.size() + ", other access points = "
						+ accessPointsMap.size());
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

	}

	/**
	 * Transmit a message
	 * 
	 * @param m the message
	 */
	public void transmitMessage(MessageI m) {
		int N = 3;
		try {
			// Check if it has a route to message's address and send it via that port
			if (m.getAddress().equals(this.address)) {
				System.out.println("MESSAGE ARRIVED TO HIS DESTINATION !");
				return;
			}
			// Check if the message is still alive, else kill it
			if (!m.stillAlive()) {
				System.out.println("MESSAGE DIED AND HAS BEEN DESTRUCTED!");
				m = null;
				return;
			}

			m.decrementHops();
			if (m.getAddress() instanceof NetworkAddressI) {
				System.err.println("A MESSAGE RECEIVED FROM A NETWORK ADDRESS");
				return;

			}
			// Check if it has a route to message's address and send it via that port
			CommunicationOutBoundPort sendingPort = communicationConnectionPorts.get(m.getAddress());
			if (sendingPort != null) {
				System.out.println("ACCESS POINT HAS  ROUTE FOR THE CURRENT NODE ADDRESS");
				sendingPort.transmitMessage(m);
			}

			// inondation
			else {
				System.out.println("ACCESS POINT NO ENTRY FOR THE CURRENT ADDRESS");
				int n = 0;
				for (CommunicationOutBoundPort cobp : communicationConnectionPorts.values()) {
					if (n == N)
						break;
					n++;
					cobp.transmitMessage(m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Ask all neighbors if they have a route for an address
	 * 
	 * @param address
	 * @return 1 if a neighbors is the address, -1 else
	 */
	int hasRouteFor(AddressI address) {
		System.out.println("ACCESS POINT HAS " + address);
		if (communicationConnectionPorts.containsKey(address))
			return 1;
		return -1;
	}

	void ping() throws ConnectException {
		if (!isStillOn)
			throw new ConnectException("The node you are trying to ping is no longer ON");
	}

	/**
	 * Update the neighbor's route info ports map
	 * 
	 * @param address address of the routing node to add
	 * @param routes  routes info
	 */
	public synchronized void updateRouting(NodeAddressI address, Set<RouteInfo> routes) {
		if (this.address.equals(address))
			return;
		if (!this.routes.containsKey(address))
			this.routes.put(address, routes);
		Set<RouteInfo> currentInfos = this.routes.get(address);
		currentInfos.addAll(routes);

		System.out.println("ACCESS POINT UPDATE ROUTING: A NEW ENTRY WAS RECEIVED");

		this.routes.put(address, currentInfos);
	}

	/**
	 * Update the neighbor's access points number of hops map
	 * 
	 * @param address      address of the routing node to add
	 * @param numberOfHops the new number of hops
	 */
	public synchronized void updateAccessPoint(NodeAddressI address, int numberOfHops) {
		System.out.println("ACCESS POINT UPDATE ACCESS POINTS: A NEW ENTRY WAS RECEIVED");
		if (this.address.equals(address))
			return;
		if (!accessPointsMap.containsKey(address))
			accessPointsMap.put(address, numberOfHops);
		if (accessPointsMap.get(address) > numberOfHops)
			accessPointsMap.put(address, numberOfHops);
	}

}
