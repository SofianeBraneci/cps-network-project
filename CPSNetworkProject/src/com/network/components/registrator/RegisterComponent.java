package com.network.components.registrator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.network.interfaces.AddressI;
import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NetworkAddressI;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = { RegistrationCI.class })
public class RegisterComponent extends AbstractComponent {

	protected RegisterServiceInboundPort registerPort;
	public static final String REGISTER_PORT_URI = "register-port-uri";
	// terminal nodes
	private Map<NodeAddressI, ConnectionInfo> terminalNodesTable;
	// routing nodes
	private Map<NodeAddressI, ConnectionInfo> routinNodesTable;
	// access point nodes
	private Map<NodeAddressI, ConnectionInfo> accessPointsNodesTable;
	
	protected RegisterComponent() {
		super(1, 0);
		try {
			registerPort = new RegisterServiceInboundPort(REGISTER_PORT_URI, this);
			registerPort.publishPort();
			toggleLogging();
			toggleTracing();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(REGISTER_PORT_URI);
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

	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) {
		Set<ConnectionInfo> neighbores = new HashSet<>();
//		for(NodeAddressI adr: terminalNodesTable.keySet() ) {
//			ConnectionInfo currentInfo = terminalNodesTable.get(adr);
//			if(currentInfo.getPosition().distance(initialPosition) <= initialRange && currentInfo.isRouting()) {
//				neighbores.add(currentInfo);
//			}
//		}
//		// add it the te table
//	    terminalNodesTable.put(address, new com.network.common.ConnectionInfo(address, communicationInboundPortURI, "", initialPosition, false));
		logMessage("Invoqued");
		return neighbores;
	}

	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		
		Set<ConnectionInfo> neighbores = new HashSet<>();
		for(NodeAddressI adr: accessPointsNodesTable.keySet() ) {
			ConnectionInfo currentInfo = accessPointsNodesTable.get(adr);
			if(currentInfo.getPosition().distance(initialPosition) <= initialRange) {
				neighbores.add(currentInfo);
			}
		}
		accessPointsNodesTable.put(address, new com.network.common.ConnectionInfo(address, communicationInboundPortURI, routingInboundPortURI, initialPosition, true));
		return neighbores;
	}

	Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String commnicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		Set<ConnectionInfo> neighbores = new HashSet<>();
		for(NodeAddressI adr: routinNodesTable.keySet() ) {
			ConnectionInfo currentInfo = routinNodesTable.get(adr);
			if(currentInfo.getPosition().distance(initialPosition) <= initialRange) {
				neighbores.add(currentInfo);
			}
		}
		routinNodesTable.put(address, new com.network.common.ConnectionInfo(address, commnicationInboundPortURI, routingInboundPortURI, initialPosition, true));
		return neighbores;
	}

	void unregister(NodeAddressI address) {
		
	}
}
