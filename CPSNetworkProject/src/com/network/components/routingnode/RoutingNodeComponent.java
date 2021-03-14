package com.network.components.routingnode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
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
 * Class for routing node components
 * @author Softwarkers
 *
 */
@RequiredInterfaces(required = { RegistrationCI.class, CommunicationCI.class, RoutingCI.class })
@OfferedInterfaces(offered = { CommunicationCI.class, RoutingCI.class })
public class RoutingNodeComponent extends AbstractComponent {
	/** the routing node communication inbound port*/
	private RoutingNodeCommunicationInboundPort routingNodeCommunicationInboundPort;
	/** the routing node registration outbound port*/
	private RegistrationOutboundPort routingNodeRegistrationOutboundPort;
	/** the routing node routing inbound port*/
	private RoutingNodeRoutingInboundPort routingInboundPort;
	
	/** neighbor's ports of the current node */
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnectionPorts;
	/** neighbor's routing ports of the current node */
	private Map<NodeAddressI, RoutingOutboundPort> routingOutboundPorts;
	/** access points neighbor's route info of the current node */
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	/** Number of hops between the routing node and his access points neighbors*/
	private Map<NodeAddressI, Integer> accessPointsMap;
	/** the routing node address */
	private NodeAddressI nodeAddress;
	/** the routing node initial position */
	private PositionI initialPosition;
	/** the routing node initial range */
	private double initialRange;
	/** the routing node executor index */
	private int executorIndex;

	/**
	 * create and initialize routing node
	 * @param address the routing node address
	 * @param initialPosition the routing node initial position
	 * @param initialRange the routing node initial range
	 * @throws Exception
	 */
	protected RoutingNodeComponent(NodeAddressI address, PositionI initiaPosition, double initialRange)
			throws Exception {
		super(1, 0);
		this.nodeAddress = address;
		this.initialPosition = initiaPosition;
		this.initialRange = initialRange;

		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.communicationConnectionPorts = new HashMap<>();
		this.routingOutboundPorts = new HashMap<>();

		this.routingNodeCommunicationInboundPort = new RoutingNodeCommunicationInboundPort(this);
		this.routingNodeRegistrationOutboundPort = new RegistrationOutboundPort(this);
		this.routingInboundPort = new RoutingNodeRoutingInboundPort(this);

		this.routingNodeCommunicationInboundPort.publishPort();
		this.routingNodeRegistrationOutboundPort.publishPort();
		this.routingInboundPort.publishPort();
		this.executorIndex = createNewExecutorService("ROUTING NODE EXECUTOR SERVICE", 1, false);
	}
	
	/**
	 * create and initialize a routing node with predifined information
	 * @throws Exception
	 */
	protected RoutingNodeComponent() throws Exception {
		super(1, 0);

		this.nodeAddress = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.communicationConnectionPorts = new HashMap<>();
		this.routingOutboundPorts = new HashMap<>();

		this.routingNodeCommunicationInboundPort = new RoutingNodeCommunicationInboundPort(this);
		this.routingNodeRegistrationOutboundPort = new RegistrationOutboundPort(this);
		this.routingInboundPort = new RoutingNodeRoutingInboundPort(this);

		this.routingNodeCommunicationInboundPort.publishPort();
		this.routingNodeRegistrationOutboundPort.publishPort();
		this.routingInboundPort.publishPort();
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		doPortConnection(routingNodeRegistrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());

		Set<ConnectionInfo> connectionInfos = routingNodeRegistrationOutboundPort.registerRoutigNode(nodeAddress,
				routingNodeCommunicationInboundPort.getPortURI(), initialPosition, initialRange,
				routingInboundPort.getPortURI());
		System.out.println("ROUTING NODE CONNECTIONS = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {
			this.connectRouting(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort(),
					connectionInfo.getRoutingInboundPortURI());
		}
		getExecutorService(executorIndex).execute(() -> {
			while(true) {
				try {
					
					for(RoutingOutboundPort neighboroOutboundPort: routingOutboundPorts.values()) {
						for(Entry<NodeAddressI, Set<RouteInfo>> entry : routes.entrySet()) {
							neighboroOutboundPort.updateRouting(entry.getKey(), entry.getValue());
						}
						
						for(Entry<NodeAddressI, Integer> entry : accessPointsMap.entrySet()) {
							neighboroOutboundPort.updateAccessPoint(entry.getKey(), entry.getValue() + 1);
						}
					}
					
					
					Thread.sleep(200L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// uncomment this to test unregister
/*
		routingNodeRegistrationOutboundPort.unregister(nodeAddress);
		terminalNodeRegistrationOutboundPort.unregister(address);
*/
		
		//uncomment this to test message sending
/*
		Message message = new Message(new NodeAddress("192.168.25.6"), "Hello", 5 );
		Message message = new Message(new NodeAddress("192.168.25.1"), "Hello", 5 );
		Message message = new Message(new NetworkAddress("192.168.25.6"), "Hello", 5 );
		transmitMessage(message);
*/
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			routingNodeRegistrationOutboundPort.unpublishPort();
			routingNodeCommunicationInboundPort.unpublishPort();
			routingInboundPort.unpublishPort();
			for (CommunicationOutBoundPort port : communicationConnectionPorts.values())
				port.unpublishPort();
			for (RoutingOutboundPort port : routingOutboundPorts.values())
				port.unpublishPort();

		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(routingNodeRegistrationOutboundPort.getPortURI());
		for (CommunicationOutBoundPort port : communicationConnectionPorts.values())
			doPortDisconnection(port.getPortURI());
		for (RoutingOutboundPort routing : routingOutboundPorts.values())
			doPortDisconnection(routing.getPortURI());
		super.finalise();
	}


	/**
	 * Connect the actual routing node with a terminal node to achieve a peer to peer connection
	 * @param address terminal node to connect with address
	 * @param communicationInboundPortURI terminal node to connect with communication inbound port uri
	 */
	void connect(NodeAddressI address, String communicationInboundURI) {


		System.out.println("FROM ROUTING NODE: CONNECT METHOD IS INVOKED" + address.toString());
		try {
			if (communicationConnectionPorts.containsKey(address))
				return;

			// we create a new outbound port for the new neighbor
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();

			doPortConnection(port.getPortURI(), communicationInboundURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnectionPorts.put(address, port);
			System.out.println("ROUTING NODE: A NEW CONNECTION WAS ESTABLISHED WITH A TERMINAL NODE " + address);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Connect the actual routing node with another routing node to achieve a peer to peer connection
	 * @param address routing node to connect with address
	 * @param communicationInboudPortURI routing node to connect with communication inbound port uri
	 * @param routingInboudPortURI routing node to connect with routing inbound port uri
	 */
	void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboudPortURI) {
		try {
			if (communicationConnectionPorts.containsKey(address)) {
				return;
			}

			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();
			RoutingOutboundPort routingOutboundPort = new RoutingOutboundPort(this);
			routingOutboundPort.publishPort();

			doPortConnection(routingOutboundPort.getPortURI(), routingInboudPortURI,
					RoutingConnector.class.getCanonicalName());

			doPortConnection(port.getPortURI(), communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());

			communicationConnectionPorts.put(address, port);
			routingOutboundPorts.put(address, routingOutboundPort);
			System.out
					.println("ROUTING NODE: A NEW CONNECTION WAS ESTABLISHED WITH A ROUTING NODE " + address);
			Set<RouteInfo> routeInfos = new HashSet<>();
			RouteInfo info = new RouteInfo(address, 1);
			routeInfos.add(info);
			routes.put(address, routeInfos);
			System.out.println("ROUTING NODE: total neighbors  = " + communicationConnectionPorts.size()
			+ ", routing neighbors = " + routes.size() + ", other access points = " + accessPointsMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return closest access point to the routing node
	 */
	NodeAddressI getClosestAccessPoint() {
		int min = Integer.MAX_VALUE;
		NodeAddressI closestAddress = null;
		for (Entry<NodeAddressI, Integer> entry : accessPointsMap.entrySet()) {
			if (entry.getValue() > min) {
				min = entry.getValue();
				closestAddress = entry.getKey();
			}
		}
		return closestAddress;
	}

	/**
	 * Transmit a message
	 * @param m the message
	 */
	void transmitMessage(MessageI m) {
		System.out.println("FROM TRANSMIT MESSAGE IN ROUTING NODE");
		if (!m.stillAlive()) {
			System.out.println("Message is dead");
			return;
		}

		if (nodeAddress.equals(m.getAddress())) {
			System.out.println("MESSAGE IS RECEIVED IN ROUTING NODE");
			return;
		}
		try {
			
			if(m.getAddress() instanceof NetworkAddressI) {
				System.out.println("ROUTING NODE HAS RECEIVED A NETWORK ADDRESS, REDIRECTING TO AN ACCESS POINT");
				NodeAddressI closestAccessPoint = getClosestAccessPoint();
				if (closestAccessPoint == null) {
					System.err.println("NO ACCESS POINT TO BE FOUND!");
					return;
				}
				communicationConnectionPorts.get(closestAccessPoint).transmitMessage(m);
				
			}
			
			CommunicationOutBoundPort sendingPort = communicationConnectionPorts.get(m.getAddress());
			m.decrementHops();
			if(sendingPort != null) {
				System.out.println("ROUTING NODE HAS AN ENTRY FOR THE DISTINATION");
				sendingPort.transmitMessage(m);
				
			}else {
				System.out.println("ROUTING NODE PROCEEDING WITH MESSAGE PROPAGATION");
				
				 int n = 3;
				 for(CommunicationOutBoundPort port: communicationConnectionPorts.values()) {
					 if (n==0) return;
					 port.transmitMessage(m);
					 n--;
				 }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Ask all neighbors if they have a route for an address
	 * @param address
	 * @return 1 if a neighbors is the address, -1 else
	 */
	int hasRouteFor(AddressI address) {
		System.err.println(communicationConnectionPorts.containsKey(address));
		if (communicationConnectionPorts.containsKey(address))
			return 1;
		else
			return -1;

	}

	void ping() {

	}

	/**
	 * Update the neighbor's route info ports map
	 * @param address address of the routing node to add
	 * @param routes routes info
	 */
	void updateRouting(NodeAddressI address, Set<RouteInfo> routes) {
		System.out.println("A NEW ENTRY WAS RECEIVED----ROUTING NODE-----: UPDATE ROUTING");
		if (!this.routes.containsKey(address))
			this.routes.put(address, routes);
	
		Set<RouteInfo> currentInfos = this.routes.get(address);
		currentInfos.addAll(routes);
		this.routes.put(address, currentInfos);
	}
	
	/**
	 * Update the neighbor's access points number of hops map
	 * @param address address of the routing node to add
	 * @param numberOfHops the new number of hops
	 */
	void updateAccessPoint(NodeAddressI address, int numberOfHops) {
		System.out.println("A NEW ENTRY WAS RECEIVED----ROUTING NODE-----: UPDATE ACCESS POINT");
		if (!accessPointsMap.containsKey(address))
			accessPointsMap.put(address, numberOfHops);
		if (accessPointsMap.get(address) > numberOfHops)
			accessPointsMap.put(address, numberOfHops);
	}
}
