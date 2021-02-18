package com.network.components.routingnode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.NodeComponentInformationWrapper;
import com.network.common.Position;
import com.network.common.RouteInfo;
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

@RequiredInterfaces(required = {RegistrationCI.class, CommunicationCI.class})
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
public class RoutingNodeComponent extends AbstractComponent{
	public static final String ROUTING_NODE_REGISTRATION_OUTBOUN_PORT_URI = "ROUTING_NODE_REGISTRATION_OUTBOUN_PORT_URI";
	public static final String ROUTING_NODE_COMMUNICATION_OUTBOUN_PORT_URI = "ROUTING_NODE_COMMUNICATION_OUTBOUN_PORT_URI";
	public static final String ROUTING_NODE_COMMUNICATION_INBOUN_PORT_URI = "ROUTING_NODE_COMMUNICATION_INBOUN_PORT_URI";
	public static final String ROUTING_NODE_ROUTING_INBOUND_PORT_URI = "ROUTING_NODE_ROUTING_INBOUND_PORT_URI";
	
	private RoutingNodeCommunicationInboundPort routingNodeCommunicationInboundPort;
	private RoutingNodeCommunicationOutboundPort routingNodeCommunicationOutboundPort;
	private RoutingNodeRegistrationOutboundPort routingNodeRegistrationOutboundPort;
	private RoutinigNodeRoutingInboundPort routingInboundPort;
	
	private Map<NodeAddressI, NodeComponentInformationWrapper> connections;
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	private Map<NodeAddressI, Integer> accessPointsMap;
	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	
	protected RoutingNodeComponent() throws Exception{
		super(1, 0);
		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;
		this.connections = new HashMap<>();
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		
		this.routingNodeCommunicationInboundPort = new RoutingNodeCommunicationInboundPort(ROUTING_NODE_COMMUNICATION_INBOUN_PORT_URI, this);
		this.routingNodeCommunicationOutboundPort = new RoutingNodeCommunicationOutboundPort(ROUTING_NODE_COMMUNICATION_OUTBOUN_PORT_URI, this);
		this.routingNodeRegistrationOutboundPort = new RoutingNodeRegistrationOutboundPort(ROUTING_NODE_REGISTRATION_OUTBOUN_PORT_URI, this);
		this.routingInboundPort = new RoutinigNodeRoutingInboundPort(ROUTING_NODE_ROUTING_INBOUND_PORT_URI, this);
		
		this.routingNodeCommunicationInboundPort.publishPort();
		this.routingNodeCommunicationOutboundPort.publishPort();
		this.routingNodeRegistrationOutboundPort.publishPort();
		this.routingInboundPort.publishPort();
		toggleLogging();
		toggleTracing();
	}
	
	
	
	@Override
	public synchronized void execute() throws Exception {
		Set<ConnectionInfo> connectionInfos = routingNodeRegistrationOutboundPort.registerRoutigNode(address, ROUTING_NODE_COMMUNICATION_INBOUN_PORT_URI, initialPosition, initialRange, ROUTING_NODE_ROUTING_INBOUND_PORT_URI);
		logMessage("Routing node connections = "+ connectionInfos.size());
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO: handle exception
		}
//		routingNodeRegistrationOutboundPort.unregister(address);
//		logMessage("I unregistered my self");
		super.execute();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			routingNodeRegistrationOutboundPort.unpublishPort();
			routingNodeCommunicationInboundPort.unpublishPort();
			routingNodeCommunicationOutboundPort.unpublishPort();
			routingInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		doPortDisconnection(ROUTING_NODE_REGISTRATION_OUTBOUN_PORT_URI);
		//doPortDisconnection(ROUTING_NODE_COMMUNICATION_OUTBOUN_PORT_URI);
		super.finalise();
	}
	
	
	void connect(NodeAddressI address, String communicationInboudURI) {
		connections.put(address, new NodeComponentInformationWrapper(communicationInboudURI));
		logMessage("Connected");
		
	}
	void connectRouting (NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {
		connections.put(address, new NodeComponentInformationWrapper(communicationInboudPortURI, routingInboudPortURI));
	}
	void transmitMessag(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else kill it 
		
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
