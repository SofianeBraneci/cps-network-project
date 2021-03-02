package com.network.components.routingnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.common.RouteInfo;
import com.network.common.RoutingConnector;
import com.network.common.RoutingOutboundPort;
import com.network.components.register.RegisterComponent;
import com.network.connectors.CommunicationConnector;
import com.network.connectors.RegistrationConnector;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
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
	private NodeAddressI address;
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
//
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
//			logMessage("ROUTING NODE : A NEW CONNECTION WAS ESTABLISHED !!!");
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
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	void transmitMessage(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		// kill it

	}

	int hasRouteFor(NodeAddressI address) {
		return 0;
	}

	void ping() {

	}

	void updateRouting(NodeAddressI address, Set<RouteInfo> routes) {
		this.routes.put(address, routes);

	}

	void updateAccessPoint(NodeAddressI address, int numberOfHops) {
		accessPointsMap.put(address, numberOfHops);

	}

}
