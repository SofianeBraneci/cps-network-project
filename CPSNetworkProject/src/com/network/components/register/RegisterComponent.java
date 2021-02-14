package com.network.components.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.NodeComponentInformationWrapper;
import com.network.common.Position;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { RegistrationCI.class })
public class RegisterComponent extends AbstractComponent {

	protected RegisterServiceInboundPort registerPort;
	public static final String REGISTER_INBOUND_PORT_URI = "register-port-uri";
	// terminal nodes
	private Map<NodeAddressI, NodeComponentInformationWrapper> terminalNodesTable;
	// routing nodes
	private Map<NodeAddressI, NodeComponentInformationWrapper> routinNodesTable;
	// access point nodes
	private Map<NodeAddressI, NodeComponentInformationWrapper> accessPointsNodesTable;

	protected RegisterComponent() {
		super(10, 0);
		try {
			registerPort = new RegisterServiceInboundPort(REGISTER_INBOUND_PORT_URI, this);
			registerPort.publishPort();
			terminalNodesTable = new HashMap<>();
			routinNodesTable = new HashMap<>();
			accessPointsNodesTable = new HashMap<>();
			this.toggleLogging();
			this.toggleTracing();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	Set<ConnectionInfo> getNeighboors(NodeAddressI address, PositionI initialPosition, double initialRange, int table) {
		
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
			for (NodeAddressI adr : routinNodesTable.keySet()) {
				NodeComponentInformationWrapper current = routinNodesTable.get(adr);
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
		// TODO Auto-generated method stub
		try {
			registerPort.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ComponentShutdownException(e);
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		terminalNodesTable.put(new NodeAddress("IP Address"),
				new NodeComponentInformationWrapper("sd", new Position(12, 3)));
		System.err.println("Excuted\n");
	}

	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) {
		
		Set<ConnectionInfo> neighbores = getNeighboors(address, initialPosition, initialRange, 0);
		terminalNodesTable.put(address,
				new NodeComponentInformationWrapper(communicationInboundPortURI, initialPosition));
		System.err.println("current table size " + terminalNodesTable.size());
		return neighbores;
	}

	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		Set<ConnectionInfo> neighbores = getNeighboors(address, initialPosition, initialRange, 2);
		accessPointsNodesTable.put(address,
				new NodeComponentInformationWrapper(communicationInboundPortURI, initialPosition, routingInboundPortURI));
		System.err.println("current table size " + accessPointsNodesTable.size());
		return neighbores;
	}

	Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		Set<ConnectionInfo> neighbores = getNeighboors(address, initialPosition, initialRange, 2);
		routinNodesTable.put(address,
				new NodeComponentInformationWrapper(communicationInboundPortURI, initialPosition, routingInboundPortURI));
		System.err.println("current table size " + routinNodesTable.size());
		return neighbores;
	}

	void unregister(NodeAddressI address) {
		terminalNodesTable.remove(address);
		routinNodesTable.remove(address);
		accessPointsNodesTable.remove(address);
		System.err.println("Table size "+  terminalNodesTable.size());
		logMessage("A node was unregistered");
		
	}
}
