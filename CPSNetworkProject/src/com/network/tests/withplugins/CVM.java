package com.network.tests.withplugins;


import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.withplugin.components.*;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM{

	public CVM() throws Exception{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void deploy() throws Exception {

		
		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[]{});
		AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),	new Object[] {new NodeAddress("192.168.25.3"), new Position(3, 2), 2000.0});
		AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),	new Object[] {new NodeAddress("192.168.25.3"), new Position(3, 2), 2000.0});
		AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),	new Object[] {new NodeAddress("192.168.25.3"), new Position(3, 2), 2000.0});

		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),	new Object[] {new NodeAddress("192.168.25.3"), new Position(3, 2), 2000.0});
		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),	new Object[] {new NodeAddress("192.168.25.3"), new Position(3, 2), 2000.0});
	
		
		super.deploy();
	}

	public static void main(String[] args) {
		try{CVM c = new CVM();
		c.startStandardLifeCycle(5000L);
		System.exit(0);
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
}
}