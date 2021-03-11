package com.network.withplugin.ports;

import java.util.Set;

import com.network.common.ConnectionInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.PositionI;
import com.network.interfaces.RegistrationCI;
import com.network.plugins.RegisterRegistrationPlugin;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegisterServiceInboundPortPlugin extends AbstractInboundPort implements RegistrationCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegisterServiceInboundPortPlugin(ComponentI owner, String pluginURI) throws Exception {
		// TODO Auto-generated constructor stub
		
		super(RegistrationCI.class, owner, pluginURI, null);
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return getOwner().handleRequest(new AbstractComponent.AbstractService<Set<ConnectionInfo>>(this.getPluginURI()) {
			@Override
			public Set<ConnectionInfo> call() throws Exception {
				return ((RegisterRegistrationPlugin) this.getServiceProviderReference()).registerTerminalNode(address,
						communicationInboundPortURI, initialPosition, initialRange);
			}

		});
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return getOwner().handleRequest(new AbstractComponent.AbstractService<Set<ConnectionInfo>>(this.getPluginURI()) {
			@Override
			public Set<ConnectionInfo> call() throws Exception {
				// TODO Auto-generated method stub
				return ((RegisterRegistrationPlugin) this.getServiceProviderReference()).registerAccessPoint(address,
						communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);

			}

		});
	}

	@Override
	public Set<ConnectionInfo> registerRoutigNode(NodeAddressI address, String commnicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		return getOwner().handleRequest(new AbstractComponent.AbstractService<Set<ConnectionInfo>>(this.getPluginURI()) {
			@Override
			
			public Set<ConnectionInfo> call() throws Exception {
				// TODO Auto-generated method stub
				return ((RegisterRegistrationPlugin) this.getServiceProviderReference()).registerRoutigNode(address, commnicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
			}
			
		});
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		 getOwner().handleRequest(new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {
			@Override
			public Void call() throws Exception {
				// TODO Auto-generated method stub
				((RegisterRegistrationPlugin) this.getServiceProviderReference()).unregister(address);
				return null;
			}
			
		});

	}

}
