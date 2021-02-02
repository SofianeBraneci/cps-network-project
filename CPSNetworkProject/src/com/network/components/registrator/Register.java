package com.network.components.registrator;

import java.util.Set;

import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = {RegistrationCI.class})
public class Register extends AbstractComponent {
	
	protected RegisterServiceInboundPort registerPort;
	public static final String REGISTER_PORT_URI ="register-port-uri"; 

	protected Register() {
		super(1, 0);
		try {
			registerPort  = new RegisterServiceInboundPort(REGISTER_PORT_URI, this);
			registerPort.publishPort();
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

	Set<ConnectionInfo> register(NodeAddressI address, 
			String communicationInboudPort, 
			PositionI init, 
			double initRange, boolean isRouting) {
		return null;
	}


	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, 
			String communicationInboudPort, 
			PositionI init, 
			double initRange) {
		return null;
	}
	
	void unregister(NodeAddressI address) {
		
	}


}
