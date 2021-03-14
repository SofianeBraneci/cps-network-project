package com.network.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.network.common.CommunicationOutBoundPort;
import com.network.connectors.CommunicationConnector;
import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NetworkAddressI;
import com.network.interfaces.NodeAddressI;
import com.network.withplugin.components.AccessPointComponent;
import com.network.withplugin.components.RegisterComponent;
import com.network.withplugin.components.RoutingNodeComponent;
import com.network.withplugin.ports.CommunicationInboundPortPlugin;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

public class CommunicationPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	// this map represents the neighbors of the current node
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnections;
	private NodeAddressI sendingAddressI;
	private NodeAddressI ownerAddress;
	private Map<NodeAddressI, Integer> accessPointsMap;
	// communication Inbound port for the plugin
	private CommunicationInboundPortPlugin communicationInboundPortPlugin;

	public CommunicationPlugin(NodeAddressI ownerAddress, Map<NodeAddressI, Integer> accessPointsMap) {
		super();
		this.ownerAddress = ownerAddress;
		this.accessPointsMap = accessPointsMap;
	}

	public void connect(NodeAddressI address, String communicationInboudURI) {

		/*
		 * each time it get's called, we create a new out bound port add it to the
		 * connections table, then do a port connection
		 * 
		 **/
		try {
			if (communicationConnections.containsKey(address))
				return;
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(getOwner());
			port.publishPort();

			getOwner().doPortConnection(port.getPortURI(), communicationInboudURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnections.put(address, port);
			System.out.println("TERMINAL NODE A NEW CONNECTION WAS ESTABLISHED !!!");
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// maybe we'll delete it later it's not really required for the terminal node
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {

		try {

			if (communicationConnections.containsKey(address))
				return;

			CommunicationOutBoundPort port = new CommunicationOutBoundPort(getOwner());
			port.publishPort();

			getOwner().doPortConnection(port.getPortURI(), communicationInboudPortURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnections.put(address, port);
			System.out.println("ROUTING NODE : A NEW CONNECTION WAS ESTABLISHED !!!");

		} catch (Exception e) {
		}

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

	public void transmitMessage(MessageI m) {
		// Check if it has a route to message's address and send it via that port, else
		int N = 3;
		try {
			if (m.getAddress().equals(ownerAddress)) {
				((CommunicationCI) getOwner()).transmitMessage(m);
				return;
			}
			if (!m.stillAlive()) {
				System.out.println("MESSAGE DIED AND HAS BEEN DESTRUCTED!");
				return;
			}
			m.decrementHops();
			// this part will only be triggered if we receive a network address
			// in this case it should be routed via the classical network
			if (m.getAddress() instanceof NetworkAddressI) {
				if (getOwner() instanceof AccessPointComponent) {
					System.err.println("A MESSAGE RECEIVED FROM A NETWORK ADDRESS");
					return;

				} else if (getOwner() instanceof RegisterComponent) {
					NodeAddressI closestAddressI = getClosestAccessPoint();
					if (closestAddressI != null) {
						communicationConnections.get(closestAddressI).transmitMessage(m);
					} else {
						System.out.println("NO ACCESS POINT FOR TRANSMITION");
						return;
					}
				}
			}

			int route = hasRouteFor(m.getAddress());
			if (route != -1) {
				communicationConnections.get(sendingAddressI).transmitMessage(m);
			}

			// flooding the net
			else {
				int n = 0;
				for (CommunicationOutBoundPort cobp : communicationConnections.values()) {
					if (n == N)
						break;
					n++;
					cobp.transmitMessage(m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean containsAddress(AddressI address) {
		return communicationConnections.containsKey(address);
	}

	public int hasRouteFor(AddressI address) {
		/**
		 * should ask for all neighbors if they have a route for that address
		 */

		if (getOwner() instanceof RoutingNodeComponent || getOwner() instanceof AccessPointComponent) {
			if (communicationConnections.containsKey(address))
				return 1;
			return -1;
		}

		try {
			int min = 99999;
			int counter = 0;
			for (Entry<NodeAddressI, CommunicationOutBoundPort> e : communicationConnections.entrySet()) {
				int tmp = e.getValue().hasRouteFor(address);
				if (tmp == -1)
					counter++;
				if (tmp < min) {
					min = tmp;
					sendingAddressI = e.getKey();

				}

			}

			return counter == communicationConnections.size() ? -1 : min + 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void ping() {

	}

	public String getInboundPortForPluginURI() {
		try {
			return communicationInboundPortPlugin.getPortURI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// life cycle of the current plug in
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
	}

	@Override
	public void initialise() throws Exception {
		System.out.println("COMMUNICATION PLUGIN IS LAUNCHED");
		super.initialise();
		addOfferedInterface(CommunicationCI.class);
		addRequiredInterface(CommunicationCI.class);
		communicationConnections = new HashMap<>();
		sendingAddressI = null;
		communicationInboundPortPlugin = new CommunicationInboundPortPlugin(getOwner(), getPluginURI());
		communicationInboundPortPlugin.publishPort();
	}

	@Override
	public void finalise() throws Exception {
		super.finalise();
		for (CommunicationOutBoundPort port : communicationConnections.values())
			getOwner().doPortDisconnection(port.getPortURI());
	}

	@Override
	public void uninstall() throws Exception {
		super.uninstall();
		communicationInboundPortPlugin.unpublishPort();
		for (CommunicationOutBoundPort port : communicationConnections.values()) {
			port.unpublishPort();
			port.destroyPort();
		}
	}

}
