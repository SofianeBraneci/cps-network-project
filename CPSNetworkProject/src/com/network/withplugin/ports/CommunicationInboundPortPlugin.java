package com.network.withplugin.ports;

import com.network.interfaces.AddressI;
import com.network.interfaces.CommunicationCI;
import com.network.interfaces.MessageI;
import com.network.interfaces.NodeAddressI;
import com.network.plugins.CommunicationPlugin;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CommunicationInboundPortPlugin extends AbstractInboundPort implements CommunicationCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommunicationInboundPortPlugin(ComponentI owner, String pluginURI) throws Exception {
		super(CommunicationCI.class, owner, pluginURI, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboudURI) throws Exception {
		// TODO Auto-generated method stub

		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>(getPluginURI()) {

			@Override
			public Void call() throws Exception {

				((CommunicationPlugin) this.getServiceProviderReference()).connect(address, communicationInboudURI);
				return null;
			}

		});

	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboudPortURI, String routingInboudPortURI)
			throws Exception {
		// TODO Auto-generated method stub
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>(getPluginURI()) {

			@Override
			public Void call() throws Exception {

				((CommunicationPlugin) this.getServiceProviderReference()).connectRouting(address,
						communicationInboudPortURI, routingInboudPortURI);
				return null;
			}

		});

	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>(getPluginURI()) {

			@Override
			public Void call() throws Exception {

				((CommunicationPlugin) this.getServiceProviderReference()).transmitMessage(m);
				return null;
			}

		});

	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		// TODO Auto-generated method stub
		return getOwner().handleRequest(new AbstractComponent.AbstractService<Integer>(getPluginURI()) {

			@Override
			public Integer call() throws Exception {

				return ((CommunicationPlugin) this.getServiceProviderReference()).hasRouteFor(address);
			}

		});
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub

	}

}
