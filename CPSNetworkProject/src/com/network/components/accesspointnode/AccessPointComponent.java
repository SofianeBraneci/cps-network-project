package com.network.components.accesspointnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.NodeComponentInformationWrapper;
import com.network.common.Position;
import com.network.common.RouteInfo;
import com.network.connectors.TerminalNodeAccessPointCommunicationConnector;
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
	public static final String ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI = "ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI";
	public static final String ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI = "ACCESS_POINT_COMMNICATION_OUTBOUND_PORT_URI";
	public static final String ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI = "ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI";
	public static final String ACCESS_POINT_ROUTING_INBOUND_PORT_URI = "ACCESS_POINT_ROUTING_INBOUND_PORT_URI";

	private AccessPointCommunicationInbountPort accessPointCommunicationInbountPort;
	private AccessPointCommunicationOutBoundPort accessPointCommunicationOutBoundPort;
	private AccessPointRegistrationOutboundPort registrationOutboundPort;
	private AccessPointRoutingInboundPort accessPointRoutingInboundPort;
	private Map<NodeAddressI, NodeComponentInformationWrapper> connections;
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	private Map<NodeAddressI, Integer> accessPointsMap;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;

	protected AccessPointComponent() throws Exception {
		super(1, 0);
		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;
		this.connections = new HashMap<>();
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.accessPointCommunicationInbountPort = new AccessPointCommunicationInbountPort(
				ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI, this);
		this.accessPointCommunicationOutBoundPort = new AccessPointCommunicationOutBoundPort(
				ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI, this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(ACCESS_POINT_ROUTING_INBOUND_PORT_URI,
				this);
		this.registrationOutboundPort = new AccessPointRegistrationOutboundPort(
				ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI, this);
		this.accessPointCommunicationInbountPort.publishPort();
		this.accessPointCommunicationOutBoundPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		toggleLogging();
		toggleTracing();
	}

	@Override
	public synchronized void execute() throws Exception {
		// TODO Auto-generated method stub
		Set<ConnectionInfo> connectionInfos = registrationOutboundPort.registerAccessPoint(address,
				ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI, initialPosition, initialRange,
				ACCESS_POINT_ROUTING_INBOUND_PORT_URI);
		logMessage("Access Point node connections = " + connectionInfos.size());
//		try {
//			Thread.sleep(2000);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		// registrationOutboundPort.unregister(address);
		// logMessage("I unregistered my self" + connectionInfos.size());
		super.execute();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {

			this.accessPointCommunicationInbountPort.unpublishPort();
			this.accessPointCommunicationOutBoundPort.unpublishPort();
			this.accessPointRoutingInboundPort.unpublishPort();
			this.registrationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI);
		doPortDisconnection(ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI);
		super.finalise();
	}

	void connect(NodeAddressI address, String communicationInboudURI) {
		System.out.println("logggegegegegeged");
		connections.put(address, new NodeComponentInformationWrapper(communicationInboudURI));
		logMessage("Connecteddddd");
		try {
			doPortConnection(ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI, communicationInboudURI,
					TerminalNodeAccessPointCommunicationConnector.class.getCanonicalName());
			accessPointCommunicationOutBoundPort.connect(address, ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

	}

	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {
		connections.put(address, new NodeComponentInformationWrapper(communicationInboudPortURI, routingInboudPortURI));
	}

	void transmitMessag(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		// kill it

	}

	boolean hasRouteFor(NodeAddressI address) {
		return connections.containsKey(address);
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
