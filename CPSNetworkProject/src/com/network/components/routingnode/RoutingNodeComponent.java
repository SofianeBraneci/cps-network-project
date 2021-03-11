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

@RequiredInterfaces(required = { RegistrationCI.class, CommunicationCI.class, RoutingCI.class })
@OfferedInterfaces(offered = { CommunicationCI.class, RoutingCI.class })
public class RoutingNodeComponent extends AbstractComponent {

	private RoutingNodeCommunicationInboundPort routingNodeCommunicationInboundPort;
	private RegistrationOutboundPort routingNodeRegistrationOutboundPort;
	private RoutingNodeRoutingInboundPort routingInboundPort;

	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnectionPorts;

	private Map<NodeAddressI, RoutingOutboundPort> routingOutboundPorts;

	private Map<NodeAddressI, Set<RouteInfo>> routes;

	private Map<NodeAddressI, Integer> accessPointsMap;

	private NodeAddressI address, sendingAddressI;
	private PositionI initialPosition;
	private double initialRange;

	protected RoutingNodeComponent(NodeAddressI address, PositionI initiaPosition, double initialRange)
			throws Exception {
		super(1, 0);
		this.address = address;
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
	}

	protected RoutingNodeComponent() throws Exception {
		super(1, 0);

		this.address = new NodeAddress("some ip");
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

		doPortConnection(routingNodeRegistrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());

		Set<ConnectionInfo> connectionInfos = routingNodeRegistrationOutboundPort.registerRoutigNode(address,
				routingNodeCommunicationInboundPort.getPortURI(), initialPosition, initialRange,
				routingInboundPort.getPortURI());
		System.out.println("Routing node connections = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {
			if (connectionInfo.getCommunicationInboudPort().startsWith("TEST")) {
				continue;
			} else {
				this.connectRouting(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort(),
						connectionInfo.getRoutingInboundPortURI());
			}
		}
		routingNodeRegistrationOutboundPort.unregister(address);
		super.execute();
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

	void connect(NodeAddressI address, String communicationInboudURI) {
//		logMessage("FROM Routing NODE, CONNECTION WAS INVOKED !!!!! " + communicationInboudURI);
//		// create a new communication out bound port
//		// register it, and do a simple port connection

//		try {
//			if (communicationConnectionPorts.containsKey(address))
//				return;
//			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
//			RoutingOutboundPort routingPort = new RoutingOutboundPort(this);
//			
//			routingPort.publishPort();
//			port.publishPort();
//			
//			doPortConnection(port.getPortURI(), communicationInboudURI,
//					CommunicationConnector.class.getCanonicalName());
//			doPortConnection(routingPort.getPortURI(), communicationInboudURI,
//					RoutingConnector.class.getCanonicalName());
//
//			routingOutboundPorts.put(address, routingPort);
//			communicationConnectionPorts.put(address, port);
//		
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

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
			doPortConnection(port.getPortURI(), routingNodeCommunicationInboundPort.getPortURI(),
					CommunicationConnector.class.getCanonicalName());

			communicationConnectionPorts.put(address, port);
			routingOutboundPorts.put(address, routingOutboundPort);
			System.out.println("ROUTING NODE A NEW CONNECTION WAS ESTABLISHED !!!");
			// ajouter update routing

			for (Entry<NodeAddressI, Set<RouteInfo>> entry : routes.entrySet()) {
				routingOutboundPort.updateRouting(entry.getKey(), entry.getValue());
			}

			Set<RouteInfo> routeInfos = new HashSet<>();
			RouteInfo info = new RouteInfo(address, 1);
			routeInfos.add(info);
			routes.put(address, routeInfos);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	NodeAddressI getClosestAccessPoint() {

		int min = 99999;
		NodeAddressI closestAddressI = null;
		for (Entry<NodeAddressI, Integer> entry : accessPointsMap.entrySet()) {
			if (entry.getValue() > min) {
				min = entry.getValue();
				closestAddressI = entry.getKey();
			}
		}
		return closestAddressI;
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
				NodeAddressI accessPointAddressI = getClosestAccessPoint();
				if (accessPointAddressI != null) {
					communicationConnectionPorts.get(accessPointAddressI).transmitMessage(m);
					return;
				}
		
			}
			if(m.getAddress() instanceof NodeAddressI) {
				
			}

			int route = hasRouteFor(m.getAddress());
			if (route != -1) {
				communicationConnectionPorts.get(sendingAddressI).transmitMessage(m);
				return;
			}
			// inondation
			else {
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
		try {
			int min = -1;
			for (Entry<NodeAddressI, CommunicationOutBoundPort> e : communicationConnectionPorts.entrySet()) {
				int tmp = e.getValue().hasRouteFor(address);
				if (min == -1 || (tmp >= 0 && tmp < min)) {
					min = tmp;
					sendingAddressI = e.getKey();
				}

			}

			return min >= 0 ? min + 1 : -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	void ping() {

	}

	void updateRouting(NodeAddressI address, Set<RouteInfo> routes) {
		if (!this.routes.containsKey(address))
			this.routes.put(address, routes);
		// if (this.routes.get(address).size() > routes.size()) this.routes.put(address,
		// routes);
		Set<RouteInfo> currentInfos = this.routes.get(address);
		currentInfos.addAll(routes);
		this.routes.put(address, currentInfos);
	}

	void updateAccessPoint(NodeAddressI address, int numberOfHops) {
		if (!accessPointsMap.containsKey(address))
			accessPointsMap.put(address, numberOfHops);
		if (accessPointsMap.get(address) > numberOfHops)
			accessPointsMap.put(address, numberOfHops);
	}
}
