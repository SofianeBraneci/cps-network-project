package com.network.withplugin.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.common.RoutingOutboundPort;
import com.network.common.RouteInfo;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.plugins.CommunicationPlugin;
import com.network.plugins.NodesRegistrationPlugin;
import com.network.plugins.RoutingPlugin;

import fr.sorbonne_u.components.AbstractComponent;

public class RoutingNodeComponent extends AbstractComponent {
	private Map<NodeAddressI, Set<RouteInfo>> routes;

	private Map<NodeAddressI, Integer> accessPointsMap;
	
	private Map<NodeAddressI, RoutingOutboundPort> routingTableMap;
	
	private NodeAddressI address, sendingAddressI;
	private PositionI initialPosition;
	private double initialRange;
	private CommunicationPlugin communicationPlugin;
	private RoutingPlugin routingPlugin;
	private NodesRegistrationPlugin nodesRegistrationPlugin;
	private final String COMMUNICATION_PLUGIN_URI = "ROUTING_COMMUNICATION_PLUGIN_URI";
	private final String ROUTING_PLUGIN_URI = "ROUTING_NODE_ROUTING_PLUGIN_URI";
	private final String REGISTRATION_PLUGING_URI = "REGISTRATION_PLUGING_URI";
	
	private int indexExecutor;

	protected RoutingNodeComponent(NodeAddressI address, PositionI initiaPosition, double initialRange)
			throws Exception {
		super(1, 0);
		this.address = address;
		this.initialPosition = initiaPosition;
		this.initialRange = initialRange;

		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.routingTableMap = new HashMap<NodeAddressI, RoutingOutboundPort>();

		this.communicationPlugin = new CommunicationPlugin(address, accessPointsMap);
		this.communicationPlugin.setPluginURI(COMMUNICATION_PLUGIN_URI);
		this.installPlugin(communicationPlugin);

		this.routingPlugin = new RoutingPlugin(accessPointsMap, routes, routingTableMap);
		this.routingPlugin.setPluginURI(ROUTING_PLUGIN_URI);
		this.installPlugin(routingPlugin);

		this.nodesRegistrationPlugin = new NodesRegistrationPlugin();
		this.nodesRegistrationPlugin.setPluginURI(REGISTRATION_PLUGING_URI);
		this.installPlugin(nodesRegistrationPlugin);
		this.indexExecutor = createNewExecutorService("Routing node excutor service", 1, false);
	}

	protected RoutingNodeComponent() throws Exception {
		super(1, 0);

		this.address = new NodeAddress("some ip");
		this.initialPosition = new Position(12, 23);
		this.initialRange = 120;
		this.routes = new HashMap<>();
		this.accessPointsMap = new HashMap<>();
		this.routingTableMap = new HashMap<NodeAddressI, RoutingOutboundPort>();

		this.communicationPlugin = new CommunicationPlugin(address, null);
		this.communicationPlugin.setPluginURI(COMMUNICATION_PLUGIN_URI);
		this.installPlugin(communicationPlugin);

		this.routingPlugin = new RoutingPlugin(accessPointsMap, routes, routingTableMap);
		this.routingPlugin.setPluginURI(ROUTING_PLUGIN_URI);
		this.installPlugin(routingPlugin);

		this.nodesRegistrationPlugin = new NodesRegistrationPlugin();
		this.nodesRegistrationPlugin.setPluginURI(REGISTRATION_PLUGING_URI);
		this.installPlugin(nodesRegistrationPlugin);
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
		communicationPlugin.connectRouting(address, communicationInboudPortURI, routingInboudPortURI);
		routingPlugin.addEntryTotheTable(address, routingInboudPortURI);
		System.out.println(routingTableMap.size());
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
		communicationPlugin.transmitMessage(m);

	}

	void ping() {

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

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		Set<ConnectionInfo> connectionInfos = nodesRegistrationPlugin.registerAccessPoint(address,
				communicationPlugin.getInboundPortForPluginURI(), initialPosition, initialRange,
				routingPlugin.getRoutingInboundPortURI());

		System.out.println("Routing Point node connections = " + connectionInfos.size());

		for (ConnectionInfo connectionInfo : connectionInfos) {
			if (!connectionInfo.getCommunicationInboudPort().startsWith("TEST")) {

				connectRouting(address, connectionInfo.getCommunicationInboudPort(),
						connectionInfo.getRoutingInboundPortURI());
			}

			getExecutorService(indexExecutor).execute(() -> {

				try {
					while (true) {
						for (RoutingOutboundPort neighborBoundPort : routingTableMap.values()) {
							for (Entry<NodeAddressI, Set<RouteInfo>> entry : routes.entrySet()) {
								neighborBoundPort.updateRouting(entry.getKey(), entry.getValue());
							}
							// for the other known access points

							for (Entry<NodeAddressI, Integer> entry : accessPointsMap.entrySet()) {

								neighborBoundPort.updateAccessPoint(entry.getKey(), entry.getValue() + 1);
							}
							Thread.sleep(200L);

						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			});

		}

	}

}
