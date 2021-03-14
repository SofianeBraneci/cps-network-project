package com.network.components.accesspointnode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.Map.Entry;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.Message;
import com.network.common.NetworkAddress;
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
import com.network.withplugin.components.RoutingNodeComponent;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { CommunicationCI.class, RoutingCI.class })
@RequiredInterfaces(required = { CommunicationCI.class, RegistrationCI.class, RoutingCI.class })
public class AccessPointComponent extends AbstractComponent {

	private AccessPointCommunicationInbountPort accessPointCommunicationInbountPort;
	private RegistrationOutboundPort registrationOutboundPort;
	private AccessPointRoutingInboundPort accessPointRoutingInboundPort;

	// this map represents the neighbors of the current node
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnectionPorts;
	// routing ports
	private Map<NodeAddressI, RoutingOutboundPort> routingOutboundPorts;
	// route info for access points
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	// for hops for access points
	private Map<NodeAddressI, Integer> accessPointsMap;
	// node attributes
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	private NodeAddressI sendingAddressI;
	private int executorServiceIndex;

	protected AccessPointComponent(NodeAddressI address, PositionI initialPosition, double initialRange)
			throws Exception {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;

		this.communicationConnectionPorts = new HashMap<>();
		this.routingOutboundPorts = new HashMap<>();
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();

		this.accessPointCommunicationInbountPort = new AccessPointCommunicationInbountPort(this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(this);
		this.registrationOutboundPort = new RegistrationOutboundPort(this);

		this.accessPointCommunicationInbountPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		this.executorServiceIndex = createNewExecutorService("ACCESS POINT EXECUTOR SERVICE URI", 1, false);

	}

	protected AccessPointComponent() throws Exception {
		super(1, 0);

		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;

		this.communicationConnectionPorts = new HashMap<>();
		this.routingOutboundPorts = new HashMap<>();
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();

		this.accessPointCommunicationInbountPort = new AccessPointCommunicationInbountPort(this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(this);
		this.registrationOutboundPort = new RegistrationOutboundPort(this);

		this.accessPointCommunicationInbountPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		;
	}

	@Override
	public synchronized void execute() throws Exception {
		// TODO Auto-generated method stub
		doPortConnection(registrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());

		Set<ConnectionInfo> connectionInfos = registrationOutboundPort.registerAccessPoint(address,
				accessPointCommunicationInbountPort.getPortURI(), initialPosition, initialRange,
				accessPointRoutingInboundPort.getPortURI());

		System.out.println("Access Point node connections = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {

			this.connectRouting(address, connectionInfo.getCommunicationInboudPort(),
					connectionInfo.getRoutingInboundPortURI());

		}

		// dynamically propagate the routing table to it's neighbors

		getExecutorService(executorServiceIndex).execute(() -> {
			try {
				while (true) {
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
					Thread.sleep(200L);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		});
		// uncomment this to test unregister
//		registrationOutboundPort.unregister(address);

//		terminalNodeRegistrationOutboundPort.unregister(address);
//		Message message = new Message(new NodeAddress("192.168.25.6"), "Hello", 5 );
//		Message message = new Message(new NodeAddress("192.168.25.1"), "Hello", 5 );
//		Message message = new Message(new NodeAddress("192.168.25.6"), "Hello", 5 );
//		transmitMessage(message);
		super.execute();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {

			this.accessPointCommunicationInbountPort.unpublishPort();
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
			doPortDisconnection(port.getPortURI());
		for (RoutingOutboundPort routing : routingOutboundPorts.values())
			doPortDisconnection(routing.getPortURI());
		super.finalise();
	}

	void connect(NodeAddressI address, String communicationInboudURI) {
		/*
		 * When it's called, it should connect with the calling node to achieve a peer
		 * to peer connection
		 **/
		try {
			if (communicationConnectionPorts.containsKey(address))
				return;

			// we creat a new out bound port for the new neighbor
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();

			doPortConnection(port.getPortURI(), communicationInboudURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnectionPorts.put(address, port);
			System.out.println("ACCESS POINT: A NEW CONNECTION WAS ESTABLISHED WITH A TERMINAL NODE  " + address);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {

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

			doPortConnection(port.getPortURI(), accessPointCommunicationInbountPort.getPortURI(),
					CommunicationConnector.class.getCanonicalName());

			communicationConnectionPorts.put(address, port);
			routingOutboundPorts.put(address, routingOutboundPort);
			System.out.println("ACCESS POINT  NEW CONNECTION WAS ESTABLISHED WITH A ROUTING NODE !!!"
					+ communicationConnectionPorts.size());
			Set<RouteInfo> routeInfos = new HashSet<>();
			RouteInfo info = new RouteInfo(address, 1);
			routeInfos.add(info);
			routes.put(address, routeInfos);
			System.out.println("ACCESS POINT: total neighbors  = " + communicationConnectionPorts.size()
					+ " routing neighbors = " + routes.size() + " other access points = " + accessPointsMap.size());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	void transmitMessage(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		// kill it

		// Check if it has a route to message's address and send it via that port, else
		int N = 3;
		try {
			if (m.getAddress().equals(this.address)) {
				System.out.println("MESSAGE ARRIVED TO HIS DESTINATION !");
				return;
			}
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

			CommunicationOutBoundPort sendingPort = communicationConnectionPorts.get(m.getAddress());
			if (sendingPort != null) {
				System.out.println("ACCESS POINT HAS  ROUTE FOR THE CURREN NODE ADDRESS");
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

	int hasRouteFor(AddressI address) {

		/**
		 * should ask for all neighbors if they have a route for that address
		 */
		System.out.println("ACCESS POINT HAS OOO" + address);
		if (communicationConnectionPorts.containsKey(address)) return 1;
		return -1;
	}

	void ping() {

	}

	void updateRouting(NodeAddressI address, Set<RouteInfo> routes) {
		if (this.address.equals(address))
			return;
		if (!this.routes.containsKey(address))
			this.routes.put(address, routes);
		Set<RouteInfo> currentInfos = this.routes.get(address);
		currentInfos.addAll(routes);

		System.out.println("ACCESS POINT UPDATE ROUTING: A NEW ENTRY WAS RECEIVED THE TOTAL SIZE OF THE ENTRY IS : "
				+ currentInfos.size());

		this.routes.put(address, currentInfos);
	}

	void updateAccessPoint(NodeAddressI address, int numberOfHops) {
		if (this.address.equals(address))
			return;
		if (!accessPointsMap.containsKey(address))
			accessPointsMap.put(address, numberOfHops);
		if (accessPointsMap.get(address) > numberOfHops)
			accessPointsMap.put(address, numberOfHops);
	}

}
