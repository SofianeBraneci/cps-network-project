package com.network.components.accesspointnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.NodeComponentInformationWrapper;
import com.network.common.Position;
import com.network.common.RegistrationOutboundPort;
import com.network.common.RouteInfo;
import com.network.common.UriPortsBaseNames;
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
	private final String ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI ;
	private final String ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI ;
	private final String ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI;
	private final String ACCESS_POINT_ROUTING_INBOUND_PORT_URI;

	private static Object mutex = new Object();
	private static int componentCounter = 1;
	private int componentId; 
	
	private AccessPointCommunicationInbountPort accessPointCommunicationInbountPort;
	private RegistrationOutboundPort registrationOutboundPort;
	private AccessPointRoutingInboundPort accessPointRoutingInboundPort;
	private Map<NodeAddressI, NodeComponentInformationWrapper> connections;
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnectionPorts;

	private Map<NodeAddressI, Set<RouteInfo>> routes;
	// for hops 
	private Map<NodeAddressI, Integer> accessPointsMap;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	private int outBoundPortsCounter = 0;

	protected AccessPointComponent(NodeAddressI address, PositionI initialPosition, double initialRange)
			throws Exception {
		super(1, 0);
		synchronized (mutex) {
			componentId = componentCounter ++; 
			ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI  = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_REGISTRATION_OUTBOUND_PORT_URI + "-" + componentId;
			ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI  = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_COMMUNICATION_INBOUND_PORT_URI + "-" + componentId;
			ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_COMMUNICATION_OUTBOUND_PORT_URI + "-" + componentId;
			ACCESS_POINT_ROUTING_INBOUND_PORT_URI = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_ROUTING_INBOUND_PORT_URI + "-" + componentId;
			
		}
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;
		this.connections = new HashMap<>();
		this.communicationConnectionPorts = new HashMap<>();
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.accessPointCommunicationInbountPort = new AccessPointCommunicationInbountPort(
				ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI, this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(ACCESS_POINT_ROUTING_INBOUND_PORT_URI,
				this);
		this.registrationOutboundPort = new RegistrationOutboundPort(ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI, this);
		this.accessPointCommunicationInbountPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		toggleLogging();
		toggleTracing();
	}

	protected AccessPointComponent() throws Exception {
		super(1, 0);
		synchronized (mutex) {
			componentId = componentCounter ++; 
			ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI  = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_REGISTRATION_OUTBOUND_PORT_URI + "-" + componentId;
			ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI  = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_COMMUNICATION_INBOUND_PORT_URI + "-" + componentId;
			ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_COMMUNICATION_OUTBOUND_PORT_URI + "-" + componentId;
			ACCESS_POINT_ROUTING_INBOUND_PORT_URI = UriPortsBaseNames.BASE_ACCESS_POINT_NODE_ROUTING_INBOUND_PORT_URI + "-" + componentId;
			
		}
		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;
		this.connections = new HashMap<>();
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.communicationConnectionPorts = new HashMap<>();
		
		this.accessPointCommunicationInbountPort = new AccessPointCommunicationInbountPort(
				ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI, this);
		this.accessPointRoutingInboundPort = new AccessPointRoutingInboundPort(ACCESS_POINT_ROUTING_INBOUND_PORT_URI,
				this);
		this.registrationOutboundPort = new RegistrationOutboundPort(ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI, this);
		this.accessPointCommunicationInbountPort.publishPort();
		this.accessPointRoutingInboundPort.publishPort();
		this.registrationOutboundPort.publishPort();
		toggleLogging();
		toggleTracing();
	}

	@Override
	public synchronized void execute() throws Exception {
		// TODO Auto-generated method stub
		doPortConnection(ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI, RegisterComponent.REGISTER_INBOUND_PORT_URI,RegistrationConnector.class.getCanonicalName());
		
		Set<ConnectionInfo> connectionInfos = registrationOutboundPort.registerAccessPoint(address,
				ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI, initialPosition, initialRange,
				ACCESS_POINT_ROUTING_INBOUND_PORT_URI);
		logMessage("Access Point node connections = " + connectionInfos.size());
		
		for(ConnectionInfo connectionInfo: connectionInfos) {
			logMessage(connectionInfo.getCommunicationInboudPort());
		}

		
		super.execute();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {

			this.accessPointCommunicationInbountPort.unpublishPort();
			this.accessPointRoutingInboundPort.unpublishPort();
			this.registrationOutboundPort.unpublishPort();
			for(CommunicationOutBoundPort port : communicationConnectionPorts.values()) port.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(ACCESS_POINT_REGISTRATION_OUTBOUND_PORT_URI);
		for(CommunicationOutBoundPort port: communicationConnectionPorts.values()) doPortDisconnection(port.getPortURI());
		super.finalise();
	}

	void connect(NodeAddressI address, String communicationInboudURI) {
		logMessage("FROM ACCESS POINT NODE, CONNECTION WAS INVOKED !!!!! " + communicationInboudURI);
		// create a new communication out bound port
		// register it, and do a simple port connection

		String newCommuicationOutboundPortURI = ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI + "-" + (++outBoundPortsCounter);
		try {
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(newCommuicationOutboundPortURI, this);
			port.publishPort();
			doPortConnection(newCommuicationOutboundPortURI, communicationInboudURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnectionPorts.put(address, port);
			logMessage("ACCESS POINT NODE : A NEW CONNECTION WAS ESTABLISHED !!!");

		} catch (Exception e) {
			// TODO: handle exceptions
			e.printStackTrace();
		}
	}

	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {

		// TODO : FIGURE OUT WHAT TO DO WITH ROUTNING PORT
		// TECHNECALLY IT SHOULD BE USED TO LINK WITH THE NODE'S ROUTING PORT TOO TO
		// ENSURE ROUTING...
		// FOR NOW DO THE BASIC : LIKE ABOVE

		String newCommuicationOutboundPortURI = ACCESS_POINT_COMMUNICATION_OUTBOUND_PORT_URI + "-" + (++outBoundPortsCounter);
		try {
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(newCommuicationOutboundPortURI, this);
			port.publishPort();
			doPortConnection(newCommuicationOutboundPortURI, communicationInboudPortURI, CommunicationConnector.class.getCanonicalName());
			port.connectRouting(address, ACCESS_POINT_COMMUNICATION_INBOUND_PORT_URI, ACCESS_POINT_ROUTING_INBOUND_PORT_URI);
			communicationConnectionPorts.put(address, port);
		} catch (Exception e) {
			// TODO: handle exception
		}
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
