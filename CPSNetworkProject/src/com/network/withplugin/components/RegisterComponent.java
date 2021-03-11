package com.network.withplugin.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.common.NodeAddress;
import com.network.common.NodeComponentInformationWrapper;
import com.network.common.Position;
import com.network.components.register.RegisterServiceInboundPort;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.plugins.RegisterRegistrationPlugin;

import fr.sorbonne_u.components.AbstractComponent;

public class RegisterComponent extends AbstractComponent {

	protected RegisterRegistrationPlugin registrationPlugin;
	public static final String REGISTER_REIGISTRATION_PLUNGIN_URI = "REGISTER_REIGISTRATION_PLUNGIN_URI";
	public static final String REGISTER_INBOUND_PORT_URI = "REGISTER_INBOUND_PORT_URI";
	protected RegisterServiceInboundPort registerPort;
	
	// terminal nodes
	private Map<NodeAddressI, NodeComponentInformationWrapper> terminalNodesTable;
	// routing nodes
	private Map<NodeAddressI, NodeComponentInformationWrapper> routinNodesTable;
	// access point nodes
	private Map<NodeAddressI, NodeComponentInformationWrapper> accessPointsNodesTable;


	protected RegisterComponent() {
		super(1, 0);

		try {
			
			
			terminalNodesTable = new HashMap<>();
			routinNodesTable = new HashMap<>();
			accessPointsNodesTable = new HashMap<>();	
			registrationPlugin = new RegisterRegistrationPlugin();
			registrationPlugin.setPluginURI(REGISTER_REIGISTRATION_PLUNGIN_URI);
			this.installPlugin(registrationPlugin);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public Set<ConnectionInfo> getNeighboors(NodeAddressI address, PositionI initialPosition, double initialRange, int table) {

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

	// WHEN DOING THE REGISTRATION, ONLY RETURN THE NODES THAT CAN DO ROUTING
		public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
				PositionI initialPosition, double initialRange) {
			Set<ConnectionInfo> neighbores = getNeighboors(address, initialPosition, initialRange, 1);
			neighbores.addAll(getNeighboors(address, initialPosition, initialRange, 2));
			terminalNodesTable.put(address,
					new NodeComponentInformationWrapper(communicationInboundPortURI, initialPosition));
			System.err.println("current terminal nodes table size " + terminalNodesTable.size());
			return neighbores;
		}

		public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
				PositionI initialPosition, double initialRange, String routingInboundPortURI) {
			Set<ConnectionInfo> neighbores = getNeighboors(address, initialPosition, Double.POSITIVE_INFINITY, 2);
			neighbores.addAll(getNeighboors(address, initialPosition, initialRange, 1));
			accessPointsNodesTable.put(address, new NodeComponentInformationWrapper(communicationInboundPortURI,
					initialPosition, routingInboundPortURI));
			System.err.println("current access points  table size " + accessPointsNodesTable.size());
			return neighbores;
		}

		public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String communicationInboundPortURI,
				PositionI initialPosition, double initialRange, String routingInboundPortURI) {
			Set<ConnectionInfo> neighbores = getNeighboors(address, initialPosition, initialRange, 2);
			neighbores.addAll(getNeighboors(address, initialPosition, initialRange, 1));
			routinNodesTable.put(address, new NodeComponentInformationWrapper(communicationInboundPortURI, initialPosition,
					routingInboundPortURI));
			System.err.println("current routing table size " + routinNodesTable.size());

			return neighbores;
		}
		
		public void unregister(NodeAddressI address) {
			terminalNodesTable.remove(address);
			routinNodesTable.remove(address);
			accessPointsNodesTable.remove(address);
			System.out.println("A node was unregistered");

		}


		@Override
		public synchronized void execute() throws Exception {
			super.execute();
			accessPointsNodesTable.put(new NodeAddress("IP"),
					new NodeComponentInformationWrapper("TEST_ACCESSPOINT_PORT_URI", new Position(12, 12), "fdfd"));
			routinNodesTable.put(new NodeAddress("IPP"),
					new NodeComponentInformationWrapper("TEST_ROUTING_PORT_URI", new Position(12, 10), "fff"));

			System.err.println("Excuted");

		}
	@Override
	public synchronized void finalise() throws Exception {
		super.finalise();

	}

}
