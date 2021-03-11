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

	private NodeAddressI nodeAddress, sendingAddressI;
	private PositionI initialPosition;
	private double initialRange;

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
	}

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
			if (connectionInfo.getCommunicationInboudPort().startsWith("TEST")) {
				continue;
			} else {
				this.connectRouting(connectionInfo.getAddress(), connectionInfo.getCommunicationInboudPort(),
						connectionInfo.getRoutingInboundPortURI());
			}
		}
		// routingNodeRegistrationOutboundPort.unregister(nodeAddress);

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
		/*
		 * When it's called, it should connect with the calling node to achieve a peer
		 * to peer connection
		 **/
		
		System.out.println("FROM ROUTING NODE: CONNECT METHOD IS INVOKED" + address.toString());
		try {
			if (communicationConnectionPorts.containsKey(address))
				return;

			// we creat a new out bound port for the new neighbor
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();

			doPortConnection(port.getPortURI(), communicationInboudURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnectionPorts.put(address, port);
			System.out.println("ROUTING NODE: A NEW CONNECTION WAS ESTABLISHED WITH A TERMINAL NODE");
			System.out.println("ROUITN NODE, CURRENT NEIGHBORS ARE:");
			for (NodeAddressI addressI : communicationConnectionPorts.keySet()) {
				System.out.println(addressI.toString());
			}

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

			doPortConnection(port.getPortURI(), routingNodeCommunicationInboundPort.getPortURI(),
					CommunicationConnector.class.getCanonicalName());

			communicationConnectionPorts.put(address, port);
			routingOutboundPorts.put(address, routingOutboundPort);
			System.out
					.println("ROUTING NODE A NEW CONNECTION WAS ESTABLISHED !!!" + communicationConnectionPorts.size());
			

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
			if (m.getAddress().equals(this.nodeAddress)) {
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
			if (m.getAddress() instanceof NodeAddressI) {

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
		try {
			
			
			if(communicationConnectionPorts.containsKey(address)) return 1;
			
			int min = Integer.MAX_VALUE, current = 0, counter = 0;
			
			for(Entry<NodeAddressI, CommunicationOutBoundPort> entry: communicationConnectionPorts.entrySet()) {
				System.out.println("ROUTING NODE CURRENT ADDRESSE " + entry.getKey().toString());
				current = entry.getValue().hasRouteFor(address);
				if(current == -1) counter ++;
				
				if(current < min ) {
					sendingAddressI = entry.getKey();
					min = current;
				}
			}
			
			return counter == communicationConnectionPorts.size() ? -1 : min;
			
		}catch (Exception e) {
			return -1;
		}
	}

	void ping() {

	}

	void updateRouting(NodeAddressI address, Set<RouteInfo> routes) {
		System.out.println("A NEW ENTRY WAS RECEIVED----ROUTING NODE-----: UPDATE ROUTING");
		System.out.println(address.toString());
		if (!this.routes.containsKey(address))
			this.routes.put(address, routes);
		// if (this.routes.get(address).size() > routes.size()) this.routes.put(address,
		// routes);
		Set<RouteInfo> currentInfos = this.routes.get(address);
		currentInfos.addAll(routes);
		this.routes.put(address, currentInfos);
	}

	void updateAccessPoint(NodeAddressI address, int numberOfHops) {
		System.out.println("A NEW ENTRY WAS RECEIVED----ROUTING NODE-----: UPDATE ACCESS POINT");
		System.out.println(address.toString());
		if (!accessPointsMap.containsKey(address))
			accessPointsMap.put(address, numberOfHops);
		if (accessPointsMap.get(address) > numberOfHops)
			accessPointsMap.put(address, numberOfHops);
	}
}
