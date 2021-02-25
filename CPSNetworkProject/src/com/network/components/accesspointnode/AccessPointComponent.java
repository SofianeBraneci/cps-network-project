package com.network.components.accesspointnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.common.RouteInfo;
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

@OfferedInterfaces(offered = { CommunicationCI.class, RoutingCI.class })
@RequiredInterfaces(required = { CommunicationCI.class, RegistrationCI.class })
public class AccessPointComponent extends AbstractComponent {

	private AccessPointCommunicationInbountPort accessPointCommunicationInbountPort;
	private RegistrationOutboundPort registrationOutboundPort;
	private AccessPointRoutingInboundPort accessPointRoutingInboundPort;

	// this map represents the neighbors of the current node
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnectionPorts;

	private Map<NodeAddressI, Set<RouteInfo>> routes;
	// for hops
	private Map<NodeAddressI, Integer> accessPointsMap;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;

	protected AccessPointComponent(NodeAddressI address, PositionI initialPosition, double initialRange)
			throws Exception {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;
		this.communicationConnectionPorts = new HashMap<>();
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.accessPointCommunicationInbountPort = new AccessPointCommunicationInbountPort(this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(this);
		this.registrationOutboundPort = new RegistrationOutboundPort(this);
		this.accessPointCommunicationInbountPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		toggleLogging();
		toggleTracing();
	}

	protected AccessPointComponent() throws Exception {
		super(1, 0);

		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.communicationConnectionPorts = new HashMap<>();

		this.accessPointCommunicationInbountPort = new AccessPointCommunicationInbountPort(this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(this);
		this.registrationOutboundPort = new RegistrationOutboundPort(this);
		this.accessPointCommunicationInbountPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		toggleLogging();
		toggleTracing();
	}

	@Override
	public synchronized void execute() throws Exception {
		// TODO Auto-generated method stub
		doPortConnection(registrationOutboundPort.getPortURI(), RegisterComponent.REGISTER_INBOUND_PORT_URI,
				RegistrationConnector.class.getCanonicalName());

		Set<ConnectionInfo> connectionInfos = registrationOutboundPort.registerAccessPoint(address,
				accessPointCommunicationInbountPort.getPortURI(), initialPosition, initialRange,
				accessPointRoutingInboundPort.getPortURI());

		logMessage("Access Point node connections = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {
			if (!connectionInfo.getCommunicationInboudPort().startsWith("TEST")) {

				this.connectRouting(address, connectionInfo.getCommunicationInboudPort(),
						connectionInfo.getRoutingInboundPortURI());
			}

		}

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
		super.finalise();
	}

	void connect(NodeAddressI address, String communicationInboudURI) {
		logMessage(this + "");
		logMessage("FROM ACCESS POINT NODE, CONNECTION WAS INVOKED !!!!! " + communicationInboudURI);
		// create a new communication out bound port
		// register it, and do a simple port connection

		try {
			if (communicationConnectionPorts.containsKey(address))
				return;
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();

			doPortConnection(port.getPortURI(), communicationInboudURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnectionPorts.put(address, port);
			logMessage("ACCESS POINT NODE : A NEW CONNECTION WAS ESTABLISHED !!!");

		} catch (Exception e) {
			// TODO: handle exceptions
			e.printStackTrace();
		}
	}

	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {
		logMessage("FROM ACCESS POINT NODE, CONNECTION ROUTING WAS INVOKED !!!!! " + communicationInboudPortURI);
		try {

			if (communicationConnectionPorts.containsKey(address))
				return;

			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
			port.publishPort();

			this.doPortConnection(port.getPortURI(), communicationInboudPortURI,
					CommunicationConnector.class.getCanonicalName());
	
			port.connectRouting(address, accessPointCommunicationInbountPort.getPortURI(),
					accessPointRoutingInboundPort.getPortURI());
			communicationConnectionPorts.put(address, port);

			logMessage("ACCESS POINT NODE : A NEW CONNECTION WAS ESTABLISHED !!!");

		} catch (Exception e) {
		}
	}

	void transmitMessag(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		// kill it

	}

	boolean hasRouteFor(NodeAddressI address) {
		return false;
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
