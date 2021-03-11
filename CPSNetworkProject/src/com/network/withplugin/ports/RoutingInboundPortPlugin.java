package com.network.withplugin.ports;

import java.util.Set;

import com.network.common.RouteInfo;
import com.network.interfaces.NodeAddressI;
import com.network.interfaces.RoutingCI;
import com.network.plugins.RoutingPlugin;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RoutingInboundPortPlugin extends AbstractInboundPort implements RoutingCI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoutingInboundPortPlugin(ComponentI owner, String pluginURI) throws Exception {
		// TODO Auto-generated constructor stub
		super(RoutingCI.class, owner, pluginURI, null);
	}
	


	@Override
	public void updateRouting(NodeAddressI address, Set<RouteInfo> routes) throws Exception {
		// TODO Auto-generated method stub
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {

			@Override
			public Void call() throws Exception {
				// TODO Auto-generated method stub
				((RoutingPlugin) getServiceProviderReference()).updateRouting(address, routes);
				return null;
			}
			
		});
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		// TODO Auto-generated method stub
		getOwner().handleRequest(new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {

			@Override
			public Void call() throws Exception {
				// TODO Auto-generated method stub
				((RoutingPlugin) getServiceProviderReference()).updateAccessPoint(neighbour, numberOfHops);
				return null;
			}
			
		});
	}

}
