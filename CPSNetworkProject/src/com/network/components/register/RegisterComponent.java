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
		super(1, 0);
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
		terminalNodesTable.put(new NodeAddress("IP Address"), new NodeComponentInformationWrapper("sd", new Position(12,  3)));
		System.err.println("Excuted");
	}

	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) {
		Set<ConnectionInfo> neighbores = new HashSet<>();
		for(NodeAddressI adr: terminalNodesTable.keySet()){
			NodeComponentInformationWrapper current = terminalNodesTable.get(adr);
			if(current.getInitialPosition().distance(initialPosition) <= initialRange) {
				neighbores.add(NodeComponentInformationWrapper.getConnectionInfo(adr, current));
			}
		}
		terminalNodesTable.put(address, new NodeComponentInformationWrapper( communicationInboundPortURI, initialPosition));
		System.err.println("current table size " + terminalNodesTable.size());
		return neighbores;
	}

	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		
		Set<ConnectionInfo> neighbores = new HashSet<>();
				return neighbores;
	}

	Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String commnicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		Set<ConnectionInfo> neighbores = new HashSet<>();
				return neighbores;
	}

	void unregister(NodeAddressI address) {
		
	}
}
