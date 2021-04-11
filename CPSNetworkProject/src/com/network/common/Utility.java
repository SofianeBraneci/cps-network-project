package com.network.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.network.connectors.CommunicationConnector;
import com.network.connectors.RoutingConnector;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;

import fr.sorbonne_u.components.ComponentI;

/**
 * 
 * This class will be in charge of connecting the nodes between them after
 * receiving the neighbors list and the sending of messages
 */
public class Utility {

	/**
	 * @param owener:                               the component who invoked the
	 *                                              method
	 * @param componentAddress:                     component's address i.e the one
	 *                                              that invoked the method
	 * @param componentCommunicationInboundPortURI: the URI of the communication
	 *                                              port linked to the owner
	 * @param connectionsMap:                       holds all the known neighbors of
	 *                                              the current owner
	 * @param address:                              the address of the node that we
	 *                                              will connect to
	 * @param communicationInboundPortURI:          the port URI of the new neighbor
	 **/
	public  void connectWithNeighbor(ComponentI owner, NodeAddressI componentAddress,
			String componentCommunicationInboundPortURI, Map<NodeAddressI, CommunicationOutBoundPort> connections,
			NodeAddressI address, String communicationInboundPortURI) {

		// create the outbound communication port
		try {
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(owner);
			port.publishPort();
			owner.doPortConnection(port.getPortURI(), communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());
			connections.put(address, port);
			port.connect(componentAddress, componentCommunicationInboundPortURI);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public  void connectWithRoutingNeighbor(ComponentI owner, NodeAddressI componentAddress,
			String componentCommunicationInboundPortURI, String componentRoutingInboundPortURI,
			Map<NodeAddressI, CommunicationOutBoundPort> connections,
			Map<NodeAddressI, RoutingOutboundPort> routingTable, Map<NodeAddressI, Set<RouteInfo>> routes,
			NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) {

		// create the outbound communication port
		try {
			CommunicationOutBoundPort port = new CommunicationOutBoundPort(owner);
			RoutingOutboundPort portR = new RoutingOutboundPort(owner);

			port.publishPort();
			portR.publishPort();

			owner.doPortConnection(port.getPortURI(), communicationInboundPortURI,
					CommunicationConnector.class.getCanonicalName());
			owner.doPortConnection(portR.getPortURI(), routingInboundPortURI,
					RoutingConnector.class.getCanonicalName());

			connections.put(address, port);
			routingTable.put(address, portR);

			Set<RouteInfo> infos = new HashSet<>();
			infos.add(new RouteInfo(address, 1));

			routes.put(address, infos);

			port.connectRouting(componentAddress, componentCommunicationInboundPortURI, componentRoutingInboundPortURI);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Sends a Message m, either directly i.e there is a way from the current node
	 * the destination or floods the network with the message, with the hope that
	 * the current message will somehow reach it's destination.
	 * 
	 */
	public  void sendMessage(MessageI message, Map<NodeAddressI, CommunicationOutBoundPort> connections) {
		System.err.println("called" + connections.size());
		// n will be use in the flooding part
		int n = 3, counter = 0, min = Integer.MAX_VALUE;
		NodeAddressI sendingAddress = null;
		try {
			for (Entry<NodeAddressI, CommunicationOutBoundPort> entry : connections.entrySet()) {

				int dist = entry.getValue().hasRouteFor(message.getAddress());
				if (dist == -1)
					counter++;
				if (min > dist) {
					min = dist;
					sendingAddress = entry.getKey();
				}

				// we found a route to the destination
				if (counter < connections.size()) {
					connections.get(sendingAddress).transmitMessage(message);

				}
				// no route was found, proceed with flooding
				else {
					for (CommunicationOutBoundPort port : connections.values()) {
						if (n == 0)
							break;
						n--;
						port.transmitMessage(message);

					}
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}
}