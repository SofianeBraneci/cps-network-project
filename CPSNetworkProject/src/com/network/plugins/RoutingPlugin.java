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

	public RoutingPlugin() {
		super();
	}

	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		((RoutingCI) getOwner()).updateRouting(address, routes);

	}

	public void updateAccessPoint(NodeAddressI address, int numberOfHops) throws Exception {
		((RoutingCI) getOwner()).updateAccessPoint(address, numberOfHops);

	}

	public void propagateRoutingTable(NodeAddressI to, NodeAddressI address, Set<RouteInfo> routes) {

		try {
			routingTable.get(to).updateRouting(address, routes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public String getRoutingInboundPortURI() throws Exception {
		return routingInboundPortPlugin.getPortURI();
	}

	public void propagateAccessPoint(NodeAddressI to, NodeAddressI address, int numberOfHops) {
		try {
			routingTable.get(to).updateAccessPoint(address, numberOfHops);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
