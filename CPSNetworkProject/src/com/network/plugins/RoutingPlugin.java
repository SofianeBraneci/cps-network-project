package com.network.plugins;

import java.util.Collection;
import java.util.HashSet;
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

/**
 * Class for routing plugins
 * @author Softwarkers
 *
 */
public class RoutingPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;
	/** routing inbound port */
	private RoutingInboundPortPlugin routingInboundPortPlugin;
	/** routing table */
	private Map<NodeAddressI, RoutingOutboundPort> routingTable;
	/** access points neighbor's route info of the current node */
	private Map<NodeAddressI, Set<RouteInfo>> routes;
	/** Number of hops between the routing node and his access points neighbors*/
	private Map<NodeAddressI, Integer> accessPointsMap;

	/** Create and initialize a routing node
	 * @param accessPointsMap access point map
	 * @param routes routes map
	 * @param routingTable routing table
	 */
	public RoutingPlugin(Map<NodeAddressI, Integer> accessPointsMap, Map<NodeAddressI, Set<RouteInfo>> routes, Map<NodeAddressI, RoutingOutboundPort> routingTable) {
		super();
		this.accessPointsMap = accessPointsMap;
		this.routes = routes;
		this.routingTable = routingTable;
	}

	/**
	 * Update the neighbor's route info ports map
	 * @param address address of the routing node to add
	 * @param routes routes info
	 * @throws Exception
	 */
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		System.out.println("ROUTING NODE IS UPDATING HIS ROUTES");
		if (!this.routes.containsKey(address))
			this.routes.put(address, routes);
		Set<RouteInfo> currentInfos = this.routes.get(address);
		currentInfos.addAll(routes);
		this.routes.put(address, currentInfos);
		;

	}

	/**
	 * Update the neighbor's access points number of hops map
	 * @param address address of the routing node to add
	 * @param numberOfHops the new number of hops
	 * @throws Exception
	 */
	public void updateAccessPoint(NodeAddressI address, int numberOfHops) throws Exception {
		System.out.println("ROUTING NODE IS UPDATING ACCESS POINTS");
		if (!accessPointsMap.containsKey(address))
			accessPointsMap.put(address, numberOfHops);
		if (accessPointsMap.get(address) > numberOfHops)
			accessPointsMap.put(address, numberOfHops);

	}

	/**
	 * @return the routing table
	 */
	public Collection<RoutingOutboundPort> getRoutingTable() {
		return routingTable.values();

	}
	
	/**
	 * @return the routes map
	 */
	public Map<NodeAddressI, Set<RouteInfo>> getRoutes(){
		return routes;
	}
	
	/**
	 * @return routing inbound port
	 * @throws Exception
	 */
	public String getRoutingInboundPortURI() throws Exception {
		return routingInboundPortPlugin.getPortURI();
	}
	
	/**
	 * @return acces points map
	 */
	public Map<NodeAddressI, Integer> getAccessPoints(){
		return accessPointsMap;
	}

	/**
	 * add an address to the routing table
	 * @param address the node to add address
	 * @param routingInboundPort the node to add inbound port
	 */
	public void addEntryTotheTable(NodeAddressI address, String routingInboundPort) {

		try {
			RoutingOutboundPort port = new RoutingOutboundPort(getOwner());
			port.publishPort();
			getOwner().doPortConnection(port.getPortURI(), routingInboundPort,
					RoutingConnector.class.getCanonicalName());
			// sending all the entries contained in the current routing table to it's new
			// neighbor
			routingTable.put(address, port);
			Set<RouteInfo> routeInfos = new HashSet<>();
			RouteInfo info = new RouteInfo(address, 1);
			routeInfos.add(info);
			routes.put(address, routeInfos);

			System.out.println("A new entry was added to the routing table" + routingTable.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// life cycle
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
		addOfferedInterface(RoutingCI.class);
		addRequiredInterface(RoutingCI.class);
		routingInboundPortPlugin = new RoutingInboundPortPlugin(getOwner(), getPluginURI());
		routingInboundPortPlugin.publishPort();
	}

	@Override
	public void finalise() throws Exception {
		for (RoutingOutboundPort port : routingTable.values())
			getOwner().doPortDisconnection(port.getPortURI());
		super.finalise();
	}

	@Override
	public void uninstall() throws Exception {
		super.uninstall();
		routingInboundPortPlugin.unpublishPort();
		for (RoutingOutboundPort port : routingTable.values()) {
			port.unpublishPort();
		}
	}

}
