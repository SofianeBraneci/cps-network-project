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

/**
 * Class for communication plugins
 * @author Softwarkers
 *
 */
public class CommunicationPlugin extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	/** neighbor's ports of the current node */
	private Map<NodeAddressI, CommunicationOutBoundPort> communicationConnections;
	/** address to send a message to*/
	private NodeAddressI sendingAddressI;
	/** Owner of the communication component address */
	private NodeAddressI ownerAddress;
	/** Number of hops between the node and his access points neighbors*/
	private Map<NodeAddressI, Integer> accessPointsMap;
	/** communication inbound port for the plugin*/
	private CommunicationInboundPortPlugin communicationInboundPortPlugin;
	
	/**
	 * create and initialize communication plugin
	 * @param ownerAddress the owner address
	 * @param accessPointsMap access points map
	 */
	public CommunicationPlugin(NodeAddressI ownerAddress, Map<NodeAddressI, Integer> accessPointsMap) {
		super();
		this.ownerAddress = ownerAddress;
		this.accessPointsMap = accessPointsMap;
	}
	/**
	 * Connect the owner with a terminal node to achieve a peer to peer connection
	 * @param address terminal node to connect with address
	 * @param communicationInboundURI terminal node to connect with communication inbound port uri
	 */
	public void connect(NodeAddressI address, String communicationInboundURI) {
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

			getOwner().doPortConnection(port.getPortURI(), communicationInboundURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnections.put(address, port);
			System.out.println("TERMINAL NODE : A NEW CONNECTION WAS ESTABLISHED");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * Connect the owner with a routing node to achieve a peer to peer connection
	 * @param address routing node to connect with address
	 * @param communicationInboudPortURI routing node to connect with communication inbound port uri
	 * @param routingInboudPortURI routing node to connect with routing inbound port uri
	 */
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI) {
		try {
			if (communicationConnections.containsKey(address))
				return;
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(getOwner());
			port.publishPort();

			getOwner().doPortConnection(port.getPortURI(), communicationInboudPortURI,
					CommunicationConnector.class.getCanonicalName());
			communicationConnections.put(address, port);
			System.out.println("ROUTING NODE : A NEW CONNECTION WAS ESTABLISHED");

		} catch (Exception e) {
		}

	}
	
	/**
	 * @return closest access point to the owner
	 */
	NodeAddressI getClosestAccessPoint() {

		int min = Integer.MAX_VALUE;
		NodeAddressI closestAddressI = null;
		for (Entry<NodeAddressI, Integer> entry : accessPointsMap.entrySet()) {
			if (entry.getValue() > min) {
				min = entry.getValue();
				closestAddressI = entry.getKey();
			}
		}
		return closestAddressI;
	}

	/**
	 * Transmit a message
	 * @param m the message
	 */
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

	/**
	 * Ask all neighbors if they have a route for an address
	 * @param address address to check route to
	 * @return -1 if it doesn't
	 */
	public int hasRouteFor(AddressI address) {
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

	/**
	 * @return inbound port
	 */
	public String getInboundPortForPluginURI() {
		try {
			return communicationInboundPortPlugin.getPortURI();
		} catch (Exception e) {
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
