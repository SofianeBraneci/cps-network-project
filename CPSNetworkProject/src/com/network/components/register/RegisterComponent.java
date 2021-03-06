package com.network.components.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.NodeComponentInformationWrapper;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

/**
 * Class for register components
 * @author Softwarkers
 *
 */
@OfferedInterfaces(offered = { RegistrationCI.class })
public class RegisterComponent extends AbstractComponent {
	/** register port */
	protected RegisterServiceInboundPort registerPort;
	/** the unique register inbound port uri */
	public static String REGISTER_INBOUND_PORT_URI;

	/** all terminal nodes */
	private Map<NodeAddressI, NodeComponentInformationWrapper> terminalNodesTable;
	/** all routing nodes */
	private Map<NodeAddressI, NodeComponentInformationWrapper> routingNodesTable;
	/** all access points nodes */
	private Map<NodeAddressI, NodeComponentInformationWrapper> accessPointsNodesTable;

	/**
	 * Create and initialize register
	 */
	protected RegisterComponent() {
		super(10, 0);
		try {
			registerPort = new RegisterServiceInboundPort(this);
			REGISTER_INBOUND_PORT_URI = registerPort.getPortURI();
			registerPort.publishPort();
			terminalNodesTable = new HashMap<>();
			routingNodesTable = new HashMap<>();
			accessPointsNodesTable = new HashMap<>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns all the registered component neighbors in a certain range
	 * @param address the registered node address
	 * @param initialPosition the registered node initial position
	 * @param initialRange the registered node initial range
	 * @param table 0 for terminal nodes, 1 for routing nodes and 2 for access point nodes
	 * @return Set of connection info of the neighbors
	 */
	Set<ConnectionInfo> getNeighbors(NodeAddressI address, PositionI initialPosition, double initialRange, int table) {

		Set<ConnectionInfo> connectionInfos = new HashSet<>();
		switch (table) {
		case 0:
			for (NodeAddressI adr : terminalNodesTable.keySet()) {
				NodeComponentInformationWrapper current = terminalNodesTable.get(adr);
				if (current.getInitialPosition().distance(initialPosition) <= initialRange) {
					connectionInfos.add(NodeComponentInformationWrapper.getConnectionInfo(adr, current));
				}
			}
			return connectionInfos;
		case 1:
			for (NodeAddressI adr : routingNodesTable.keySet()) {
				NodeComponentInformationWrapper current = routingNodesTable.get(adr);
				if (current.getInitialPosition().distance(initialPosition) <= initialRange) {
					connectionInfos.add(NodeComponentInformationWrapper.getConnectionInfo(adr, current));
				}
			}
			return connectionInfos;
		case 2:

			for (NodeAddressI adr : accessPointsNodesTable.keySet()) {
				NodeComponentInformationWrapper current = accessPointsNodesTable.get(adr);
				if (current.getInitialPosition().distance(initialPosition) <= initialRange) {
					connectionInfos.add(NodeComponentInformationWrapper.getConnectionInfo(adr, current));
				}
			}
			return connectionInfos;
		default:
			return new HashSet<ConnectionInfo>();
		}

	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			registerPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		System.out.println("REGISTER COMPONENT IS LAUNCHED, WAITING FOR REGISTRATIONS");
	}

	/**
	 * Register a terminal node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @return Set of the new node neighbors connection info that can route
	 */
	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) {
		System.out.println("REGISTERING A NEW TERMINAL NODE IP: " + address);
		Set<ConnectionInfo> neighbores = getNeighbors(address, initialPosition, initialRange, 1);
		neighbores.addAll(getNeighbors(address, initialPosition, initialRange, 2));
		terminalNodesTable.put(address,
				new NodeComponentInformationWrapper(communicationInboundPortURI, initialPosition));
		System.out.println("current terminal nodes table size : " + terminalNodesTable.size());
		return neighbores;
	}
	/**
	 * Register an access point node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @return Set of the new node neighbors connection info that can route
	 */
	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		System.out.println("REGISTERING A NEW ACCESS POINT IP: " + address);
		Set<ConnectionInfo> neighbores = getNeighbors(address, initialPosition, Double.POSITIVE_INFINITY, 2);
		neighbores.addAll(getNeighbors(address, initialPosition, initialRange, 1));
		accessPointsNodesTable.put(address, new NodeComponentInformationWrapper(communicationInboundPortURI,
				initialPosition, routingInboundPortURI));
		System.out.println("current access points table size : " + accessPointsNodesTable.size());
		return neighbores;
	}

	/**
	 * Register a routing node
	 * @param address the node address
	 * @param communicationInboundPortURI the node communication inbound port
	 * @param initialPosition the node initial position
	 * @param initialRange the node initial range
	 * @return Set of the new node neighbors connection info that can route
	 */
	Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		System.out.println("REGISTERING A NEW ROUTING NODE IP: " + address);
		Set<ConnectionInfo> neighbores = getNeighbors(address, initialPosition, initialRange, 2);
		neighbores.addAll(getNeighbors(address, initialPosition, initialRange, 1));
		routingNodesTable.put(address, new NodeComponentInformationWrapper(communicationInboundPortURI, initialPosition,
				routingInboundPortURI));
		System.out.println("current routing nodes table size : " + routingNodesTable.size());
		return neighbores;
	}

	/**
	 * Unregister a node
	 * @param address address of the node to unregister
	 */
	void unregister(NodeAddressI address) {
		terminalNodesTable.remove(address);
		routingNodesTable.remove(address);
		accessPointsNodesTable.remove(address);
		System.out.println("A node with ip :  " + address + " was unregistered");
	}
}
