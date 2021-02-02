package com.network.components.registrator;

import java.util.Set;

import com.network.interfaces.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegisterServiceInboundPort extends AbstractInboundPort implements RegistrationCI{

	private static final long serialVersionUID = 1L;
	public RegisterServiceInboundPort( ComponentI owner)
			throws Exception {
		super(RegistrationCI.class, owner);
		assert owner instanceof Register;
	}

	public RegisterServiceInboundPort(String uri,  ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
		assert owner instanceof Register;
	}

	@Override
	public Set<ConnectionInfo> register(NodeAddressI address, String communicationInboudPort, PositionI init,
			double initRange, boolean isRouting) throws Exception {
		return this.getOwner().handleRequest(c-> ((Register)c).register(address, communicationInboudPort, init, initRange, isRouting));
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboudPort, PositionI init,
			double initRange) throws Exception {
		return getOwner().handleRequest(c->((Register)c).registerAccessPoint(address, communicationInboudPort, init, initRange));
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception{
		((Register)getOwner()).unregister(address);
		//this.getOwner().handleRequest(c -> ((Register)c).unregister(address));
		
	}


	

}
