package com.network.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.network.common.RouteInfo;
import com.network.common.RoutingOutboundPort;
import com.network.connectors.RoutingConnector;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.RoutingCI;
import com.network.withplugin.ports.RoutingInboundPortPlugin;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class RoutingPlugin extends AbstractPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoutingInboundPortPlugin routingInboundPortPlugin;
	private Map<NodeAddressI, RoutingOutboundPort> routingTable;
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	private Map<NodeAddressI, Integer> accessPointsMap;

	public RoutingPlugin(Map<NodeAddressI, Integer> accessPointsMap, Map<NodeAddressI, Set<RouteInfo>> routes) {
		super();
		this.accessPointsMap = accessPointsMap;
		this.routes = routes;
	}

	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		System.out.println("ROUTING NODE IS UPDATING HIS ROUTES");
		if (!this.routes.containsKey(address))
			this.routes.put(address, routes);
		// if (this.routes.get(address).size() > routes.size()) this.routes.put(address,
		// routes);
		Set<RouteInfo> currentInfos = this.routes.get(address);
		currentInfos.addAll(routes);
		this.routes.put(address, currentInfos);
		;

	}

	public void updateAccessPoint(NodeAddressI address, int numberOfHops) throws Exception {
		if (!accessPointsMap.containsKey(address))
			accessPointsMap.put(address, numberOfHops);
		if (accessPointsMap.get(address) > numberOfHops)
			accessPointsMap.put(address, numberOfHops);

	}

	public Map<NodeAddressI, RoutingOutboundPort> getRoutingTable() {
		return routingTable;

	}
	
	public Map<NodeAddressI, Set<RouteInfo>> getRoutes(){
		return routes;
	}

	public String getRoutingInboundPortURI() throws Exception {
		return routingInboundPortPlugin.getPortURI();
	}
	
	public Map<NodeAddressI, Integer> getAccessPoints(){
		return accessPointsMap;
	}

	public void addEntryTotheTable(NodeAddressI address, String routingInboundPort) {

		try {
			RoutingOutboundPort port = new RoutingOutboundPort(getOwner());
			port.publishPort();
			getOwner().doPortConnection(port.getPortURI(), routingInboundPort,
					RoutingConnector.class.getCanonicalName());
			// sending all the entries contained in the current routing table to it's new
			// neighbor
			routingTable.put(address, port);
			System.out.println("A new entry was added to the routing table");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// life cycle

	@Override
	public void installOn(ComponentI owner) throws Exception {
		// TODO Auto-generated method stub
		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		// TODO Auto-generated method stub
		super.initialise();
		addOfferedInterface(RoutingCI.class);
		addRequiredInterface(RoutingCI.class);
		routingInboundPortPlugin = new RoutingInboundPortPlugin(getOwner(), getPluginURI());
		routingInboundPortPlugin.publishPort();
		routingTable = new HashMap<>();

	}

	@Override
	public void finalise() throws Exception {
		// TODO Auto-generated method stub
		super.finalise();
		for (RoutingOutboundPort port : routingTable.values())
			getOwner().doPortDisconnection(port.getPortURI());
	}

	@Override
	public void uninstall() throws Exception {
		super.uninstall();
		routingInboundPortPlugin.unpublishPort();
		for (RoutingOutboundPort port : routingTable.values()) {
			port.unpublishPort();
			port.destroyPort();

		}
	}

}
