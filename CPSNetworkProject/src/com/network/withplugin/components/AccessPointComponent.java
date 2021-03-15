package com.network.withplugin.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.network.common.CommunicationOutBoundPort;
import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RouteInfo;
import com.network.common.RoutingOutboundPort;
import com.network.connectors.CommunicationConnector;
import com.network.connectors.RoutingConnector;
import com.network.interfaces.AddressI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NetworkAddressI;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.plugins.CommunicationPlugin;
import com.network.plugins.NodesRegistrationPlugin;
import com.network.plugins.RoutingPlugin;

import fr.sorbonne_u.components.AbstractComponent;

public class AccessPointComponent extends AbstractComponent {

	private NodeAddressI address;
	private PositionI initialPosition;
	private double initialRange;
	private CommunicationPlugin communicationPlugin;
	private RoutingPlugin routingPlugin;
	private NodesRegistrationPlugin nodesRegistrationPlugin;
	private final String REGISTRATION_PLUGIN_URI = "REGISTRATION_PLUGIN_URI";
	private final String COMMUNICATION_PLUGIN_URI = "COMMUNICATION_PLUGIN_URI";
	private final String ROUTING_PLUGIN_URI = "ROUTING_PLUGIN_URI";
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	private Map<NodeAddressI, Integer> accessPointsMap;
	private Map<NodeAddressI, RoutingOutboundPort> routingTableMap;
	private int executorIndex;

	protected AccessPointComponent(NodeAddressI address, PositionI initialPosition, double initialRange)
			throws Exception {
		super(1, 0);
		this.address = address;
		this.initialPosition = initialPosition;
		this.initialRange = initialRange;
		this.accessPointsMap = new HashMap<NodeAddressI, Integer>();
		routingTableMap = new HashMap<NodeAddressI, RoutingOutboundPort>();
		this.routes = new HashMap<>();

		this.communicationPlugin = new CommunicationPlugin(address, accessPointsMap);
		this.communicationPlugin.setPluginURI(COMMUNICATION_PLUGIN_URI);
		this.installPlugin(communicationPlugin);

		this.routingPlugin = new RoutingPlugin(accessPointsMap, routes, routingTableMap);
		this.routingPlugin.setPluginURI(ROUTING_PLUGIN_URI);
		this.installPlugin(routingPlugin);

		this.nodesRegistrationPlugin = new NodesRegistrationPlugin();
		this.nodesRegistrationPlugin.setPluginURI(REGISTRATION_PLUGIN_URI);
		this.installPlugin(nodesRegistrationPlugin);
		this.executorIndex = createNewExecutorService("ACCESS POINT EXECUTOR SERVICE", 1, false);

	}

	protected AccessPointComponent() throws Exception {
		super(1, 0);

		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;
		this.accessPointsMap = new HashMap<NodeAddressI, Integer>();
		routingTableMap = new HashMap<NodeAddressI, RoutingOutboundPort>();
		this.routes = new HashMap<>();
		this.communicationPlugin = new CommunicationPlugin(address, accessPointsMap);
		this.communicationPlugin.setPluginURI(COMMUNICATION_PLUGIN_URI);
		this.installPlugin(communicationPlugin);

		this.routingPlugin = new RoutingPlugin(accessPointsMap, routes, routingTableMap);
		this.routingPlugin.setPluginURI(ROUTING_PLUGIN_URI);
		this.installPlugin(routingPlugin);

	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();

		Set<ConnectionInfo> connectionInfos = nodesRegistrationPlugin.registerAccessPoint(address,
				communicationPlugin.getInboundPortForPluginURI(), initialPosition, initialRange,
				routingPlugin.getRoutingInboundPortURI());

		System.out.println("Access Point node connections = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {
			if (!connectionInfo.getCommunicationInboudPort().startsWith("TEST")) {

				connectRouting(address, connectionInfo.getCommunicationInboudPort(),
						connectionInfo.getRoutingInboundPortURI());
			}

		}
		
	

	}

	void connect(NodeAddressI address, String communicationInboudURI) {
//		logMessage(this + "");
//		logMessage("FROM ACCESS POINT NODE, CONNECTION WAS INVOKED !!!!! " + communicationInboudURI);
//		// create a new communication out bound port
//		// register it, and do a simple port connection
//
//		try {
//			if (communicationConnectionPorts.containsKey(address))
//				return;
//			CommunicationOutBoundPort port = new CommunicationOutBoundPort(this);
//			port.publishPort();
//
//			doPortConnection(port.getPortURI(), communicationInboudURI,
//					CommunicationConnector.class.getCanonicalName());
//			logMessage("ACCESS POINT NODE : A NEW CONNECTION WAS ESTABLISHED !!!");
//
//		} catch (Exception e) {
//			// TODO: handle exceptions
//			e.printStackTrace();
//		}
	}

	void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {
		communicationPlugin.connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
		routingPlugin.addEntryTotheTable(address, routingInboudPortURI);
	}

	void transmitMessage(MessageI m) {
		communicationPlugin.transmitMessage(m);

	}

	int hasRouteFor(AddressI address) {
		return communicationPlugin.hasRouteFor(address);

	}

	void ping() {
		communicationPlugin.ping();
	}

	void updateRouting(NodeAddressI address, Set<RouteInfo> routes) {
		try {
			routingPlugin.updateRouting(address, routes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void updateAccessPoint(NodeAddressI address, int numberOfHops) {
		try {
			routingPlugin.updateAccessPoint(address, numberOfHops);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
